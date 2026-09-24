package com.yourname.maidfox.item;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.yourname.maidfox.client.renderer.WinefoxCarcassItemRenderer;
import com.yourname.maidfox.init.ModItems;
import java.util.function.Consumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WinefoxCarcassItem
extends Item
implements GeoItem {
    static final String TAG_MODEL_ID = "ModelId";
    static final String TAG_MAID_NAME = "MaidName";
    static final String TAG_MAID_UUID = "MaidUUID";
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public WinefoxCarcassItem(Item.Properties properties) {
        super(properties);
    }

    public static ItemStack createFromMaid(EntityMaid maid) {
        ItemStack stack = new ItemStack(ModItems.WINEFOX_CARCASS.get());
        CompoundTag tag = stack.getOrCreateTag();
        String modelId = maid.getModelId();
        tag.putString(TAG_MODEL_ID, modelId != null ? modelId : "unknown");
        tag.putString(TAG_MAID_NAME, maid.getName().getString());
        tag.putUUID(TAG_MAID_UUID, maid.getUUID());
        return stack;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions(){
            private WinefoxCarcassItemRenderer renderer;

            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new WinefoxCarcassItemRenderer();
                }
                return this.renderer;
            }
        });
    }
}

