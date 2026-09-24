package com.yourname.maidfox.client.model;

import com.yourname.maidfox.block.WinefoxHeadBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WinefoxHeadBlockGeoModel
extends GeoModel<WinefoxHeadBlockEntity> {
    public ResourceLocation getModelResource(WinefoxHeadBlockEntity animatable) {
        return new ResourceLocation("winefox_butcher", "geo/winefox_head.geo.json");
    }

    public ResourceLocation getTextureResource(WinefoxHeadBlockEntity animatable) {
        return new ResourceLocation("winefox_butcher", "textures/entity/winefox_head.png");
    }

    public ResourceLocation getAnimationResource(WinefoxHeadBlockEntity animatable) {
        return new ResourceLocation("winefox_butcher", "animations/winefox_head.animation.json");
    }
}

