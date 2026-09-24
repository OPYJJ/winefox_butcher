package com.yourname.maidfox.client.model;

import com.yourname.maidfox.item.WinefoxCarcassStageItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WinefoxCarcassStageModel
extends GeoModel<WinefoxCarcassStageItem> {
    public ResourceLocation getModelResource(WinefoxCarcassStageItem animatable) {
        return new ResourceLocation("winefox_butcher", (String)("geo/winefox_carcass_stage" + animatable.getStage() + ".geo.json"));
    }

    public ResourceLocation getTextureResource(WinefoxCarcassStageItem animatable) {
        return new ResourceLocation("winefox_butcher", (String)("textures/entity/winefox_carcass_stage" + animatable.getStage() + ".png"));
    }

    public ResourceLocation getAnimationResource(WinefoxCarcassStageItem animatable) {
        return new ResourceLocation("winefox_butcher", "animations/winefox_carcass_stage.animation.json");
    }
}

