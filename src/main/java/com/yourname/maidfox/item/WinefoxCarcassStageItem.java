package com.yourname.maidfox.item;

import com.yourname.maidfox.client.renderer.WinefoxCarcassStageRenderer;
import java.util.function.Consumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WinefoxCarcassStageItem
extends Item
implements GeoItem {
    private final int stage;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public WinefoxCarcassStageItem(Item.Properties properties, int stage) {
        super(properties);
        this.stage = stage;
    }

    public int getStage() {
        return this.stage;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions(){
            private WinefoxCarcassStageRenderer renderer;

            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new WinefoxCarcassStageRenderer();
                }
                return this.renderer;
            }
        });
    }
}

