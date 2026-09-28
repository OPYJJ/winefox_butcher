package com.yourname.maidfox.guillotine;

import com.yourname.maidfox.guillotine.client.GuillotineItemRenderer;
import com.yourname.maidfox.init.ModBlocks;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class GuillotineItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GuillotineItem() { super(ModBlocks.GUILLOTINE.get(), new Item.Properties().stacksTo(1)); }

    @Override
    public net.minecraft.world.InteractionResult place(BlockPlaceContext context) {
        var result = super.place(context);
        if (!context.getLevel().isClientSide && result.consumesAction()
                && !context.getLevel().getBlockState(context.getClickedPos()).is(ModBlocks.GUILLOTINE.get())) {
            if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild)
                context.getItemInHand().grow(1);
            return net.minecraft.world.InteractionResult.FAIL;
        }
        return result;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) { }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GuillotineItemRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new GuillotineItemRenderer();
                return renderer;
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);
        lines.add(Component.translatable("tooltip.winefox_butcher.guillotine.1"));
        lines.add(Component.translatable("tooltip.winefox_butcher.guillotine.2"));
        lines.add(Component.translatable("tooltip.winefox_butcher.guillotine.3"));
    }
}
