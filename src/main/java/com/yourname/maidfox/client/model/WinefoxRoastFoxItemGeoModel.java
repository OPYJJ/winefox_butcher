package com.yourname.maidfox.client.model;

import com.yourname.maidfox.delight.compat.farmersdelight.WinefoxRoastFoxItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * The item form always uses stage 0 (the complete roast fox) model and texture.
 */
public class WinefoxRoastFoxItemGeoModel extends GeoModel<WinefoxRoastFoxItem> {
    @Override
    public ResourceLocation getModelResource(WinefoxRoastFoxItem animatable) {
        return WinefoxRoastFoxGeoModel.modelForStage(0);
    }

    @Override
    public ResourceLocation getTextureResource(WinefoxRoastFoxItem animatable) {
        return WinefoxRoastFoxGeoModel.textureForStage(0);
    }

    @Override
    public ResourceLocation getAnimationResource(WinefoxRoastFoxItem animatable) {
        return WinefoxRoastFoxGeoModel.ANIMATION;
    }
}
