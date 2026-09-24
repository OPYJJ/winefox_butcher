package com.yourname.maidfox.client.model;

import com.yourname.maidfox.item.WinefoxHeadItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WinefoxHeadGeoModel
extends GeoModel<WinefoxHeadItem> {
    public ResourceLocation getModelResource(WinefoxHeadItem animatable) {
        return new ResourceLocation("winefox_butcher", "geo/winefox_head.geo.json");
    }

    public ResourceLocation getTextureResource(WinefoxHeadItem animatable) {
        return new ResourceLocation("winefox_butcher", "textures/entity/winefox_head.png");
    }

    public ResourceLocation getAnimationResource(WinefoxHeadItem animatable) {
        return new ResourceLocation("winefox_butcher", "animations/winefox_head.animation.json");
    }
}

