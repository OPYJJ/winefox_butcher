package com.yourname.maidfox.client.model;

import com.yourname.maidfox.item.WinefoxFurBedItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WinefoxFurBedItemGeoModel
extends GeoModel<WinefoxFurBedItem> {
    public ResourceLocation getModelResource(WinefoxFurBedItem animatable) {
        return new ResourceLocation("winefox_butcher", "geo/winefox_fur_bed.geo.json");
    }

    public ResourceLocation getTextureResource(WinefoxFurBedItem animatable) {
        return new ResourceLocation("winefox_butcher", "textures/block/winefox_fur_bed.png");
    }

    public ResourceLocation getAnimationResource(WinefoxFurBedItem animatable) {
        return new ResourceLocation("winefox_butcher", "animations/winefox_fur_bed.animation.json");
    }
}

