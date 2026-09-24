package com.yourname.maidfox.client.model;

import com.yourname.maidfox.block.WinefoxFurBedBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WinefoxFurBedGeoModel
extends GeoModel<WinefoxFurBedBlockEntity> {
    public ResourceLocation getModelResource(WinefoxFurBedBlockEntity animatable) {
        return new ResourceLocation("winefox_butcher", "geo/winefox_fur_bed.geo.json");
    }

    public ResourceLocation getTextureResource(WinefoxFurBedBlockEntity animatable) {
        return new ResourceLocation("winefox_butcher", "textures/block/winefox_fur_bed.png");
    }

    public ResourceLocation getAnimationResource(WinefoxFurBedBlockEntity animatable) {
        return new ResourceLocation("winefox_butcher", "animations/winefox_fur_bed.animation.json");
    }
}

