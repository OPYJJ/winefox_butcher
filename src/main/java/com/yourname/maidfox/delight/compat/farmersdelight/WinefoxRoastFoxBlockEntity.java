package com.yourname.maidfox.delight.compat.farmersdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Roast fox block entity: the stage (servings) lives in the block state, so the entity itself
 * holds no extra data and only exists so that GeckoLib can render the six stage geo models.
 */
public class WinefoxRoastFoxBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public WinefoxRoastFoxBlockEntity(BlockPos pos, BlockState state) {
        super(FarmersDelightContent.WINEFOX_ROAST_FOX_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
