package com.yourname.maidfox.client.model;

import com.yourname.maidfox.delight.compat.farmersdelight.WinefoxRoastFoxBlock;
import com.yourname.maidfox.delight.compat.farmersdelight.WinefoxRoastFoxBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * Picks one of the six stage geo models and textures from the current servings value.
 */
public class WinefoxRoastFoxGeoModel extends GeoModel<WinefoxRoastFoxBlockEntity> {
    public static final String MODID = "winefox_butcher";
    public static final ResourceLocation ANIMATION =
            new ResourceLocation(MODID, "animations/winefox_roast_fox.animation.json");

    public static ResourceLocation modelForStage(int stage) {
        return new ResourceLocation(MODID, "geo/winefox_roast_fox_stage" + clamp(stage) + ".geo.json");
    }

    public static ResourceLocation textureForStage(int stage) {
        return new ResourceLocation(MODID, "textures/block/winefox_roast_fox_stage" + clamp(stage) + ".png");
    }

    public static int clamp(int stage) {
        return Math.max(0, Math.min(WinefoxRoastFoxBlock.MAX_SERVINGS, stage));
    }

    @Override
    public ResourceLocation getModelResource(WinefoxRoastFoxBlockEntity animatable) {
        return modelForStage(stageOf(animatable));
    }

    @Override
    public ResourceLocation getTextureResource(WinefoxRoastFoxBlockEntity animatable) {
        return textureForStage(stageOf(animatable));
    }

    @Override
    public ResourceLocation getAnimationResource(WinefoxRoastFoxBlockEntity animatable) {
        return ANIMATION;
    }

    private static int stageOf(WinefoxRoastFoxBlockEntity animatable) {
        return animatable.getBlockState().getValue(WinefoxRoastFoxBlock.SERVINGS);
    }
}
