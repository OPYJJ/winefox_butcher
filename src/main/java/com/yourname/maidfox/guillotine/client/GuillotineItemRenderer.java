package com.yourname.maidfox.guillotine.client;

import com.yourname.maidfox.guillotine.GuillotineItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GuillotineItemRenderer extends GeoItemRenderer<GuillotineItem> {
    public GuillotineItemRenderer() {
        super(new GuillotineItemGeoModel());
        withScale(.35F);
    }
}
