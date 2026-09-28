package com.yourname.maidfox.guillotine.client;

import com.yourname.maidfox.guillotine.GuillotineItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GuillotineItemGeoModel extends GeoModel<GuillotineItem> {
    @Override public ResourceLocation getModelResource(GuillotineItem item) { return new ResourceLocation("winefox_butcher", "geo/guillotine.geo.json"); }
    @Override public ResourceLocation getTextureResource(GuillotineItem item) { return new ResourceLocation("winefox_butcher", "textures/block/guillotine_atlas.png"); }
    @Override public ResourceLocation getAnimationResource(GuillotineItem item) { return new ResourceLocation("winefox_butcher", "animations/guillotine.animation.json"); }
}
