package com.yourname.maidfox.item;

import com.yourname.maidfox.block.WinefoxHeadBlock;
import com.yourname.maidfox.block.WinefoxWallHeadBlock;
import com.yourname.maidfox.client.renderer.WinefoxHeadItemRenderer;
import com.yourname.maidfox.init.ModBlocks;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WinefoxHeadItem
extends Item
implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public WinefoxHeadItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResult useOn(UseOnContext context) {
        BlockPlaceContext placeContext = new BlockPlaceContext(context);
        if (!placeContext.canPlace()) {
            return InteractionResult.FAIL;
        }
        BlockState placement = WinefoxHeadItem.getPlacementState(placeContext);
        if (placement == null) {
            return InteractionResult.FAIL;
        }
        BlockPos pos = placeContext.getClickedPos();
        Level level = context.getLevel();
        if (!level.setBlock(pos, placement, 11)) {
            return InteractionResult.FAIL;
        }
        SoundType soundType = placement.getSoundType((LevelReader)level, pos, (Entity)context.getPlayer());
        level.playSound(context.getPlayer(), pos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f);
        if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.sidedSuccess((boolean)level.isClientSide);
    }

    @Nullable
    private static BlockState getPlacementState(BlockPlaceContext context) {
        BlockState standing = ((WinefoxHeadBlock)(ModBlocks.WINEFOX_HEAD_BLOCK.get())).getStateForPlacement(context);
        BlockState wall = ((WinefoxWallHeadBlock)(ModBlocks.WINEFOX_WALL_HEAD_BLOCK.get())).getStateForPlacement(context);
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        for (Direction direction : context.getNearestLookingDirections()) {
            BlockState candidate;
            if (direction == Direction.UP) continue;
            BlockState blockState = candidate = direction == Direction.DOWN ? standing : wall;
            if (candidate == null || !candidate.canSurvive((LevelReader)level, pos) || !level.isUnobstructed(candidate, pos, CollisionContext.empty())) continue;
            return candidate;
        }
        return null;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions(){
            private WinefoxHeadItemRenderer renderer;

            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new WinefoxHeadItemRenderer();
                }
                return this.renderer;
            }
        });
    }
}

