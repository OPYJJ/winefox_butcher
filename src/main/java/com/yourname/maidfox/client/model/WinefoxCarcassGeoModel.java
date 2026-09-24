package com.yourname.maidfox.client.model;

import com.yourname.maidfox.item.WinefoxCarcassItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WinefoxCarcassGeoModel
extends GeoModel<WinefoxCarcassItem> {
    public ResourceLocation getModelResource(WinefoxCarcassItem animatable) {
        return new ResourceLocation("winefox_butcher", "geo/winefox_carcass.geo.json");
    }

    public ResourceLocation getTextureResource(WinefoxCarcassItem animatable) {
        return new ResourceLocation("winefox_butcher", "textures/entity/winefox_carcass.png");
    }

    public ResourceLocation getAnimationResource(WinefoxCarcassItem animatable) {
        return new ResourceLocation("winefox_butcher", "animations/winefox_carcass.animation.json");
    }
}

