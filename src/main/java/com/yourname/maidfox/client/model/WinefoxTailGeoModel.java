package com.yourname.maidfox.client.model;

import com.yourname.maidfox.item.WinefoxTailItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WinefoxTailGeoModel
extends GeoModel<WinefoxTailItem> {
    public ResourceLocation getModelResource(WinefoxTailItem animatable) {
        return new ResourceLocation("winefox_butcher", "geo/winefox_tail.geo.json");
    }

    public ResourceLocation getTextureResource(WinefoxTailItem animatable) {
        return new ResourceLocation("winefox_butcher", "textures/entity/winefox_tail.png");
    }

    public ResourceLocation getAnimationResource(WinefoxTailItem animatable) {
        return new ResourceLocation("winefox_butcher", "animations/winefox_tail.animation.json");
    }
}

