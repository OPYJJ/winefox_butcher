package com.yourname.maidfox.client.renderer;

import com.yourname.maidfox.client.model.WinefoxCarcassGeoModel;
import com.yourname.maidfox.item.WinefoxCarcassItem;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WinefoxCarcassItemRenderer
extends GeoItemRenderer<WinefoxCarcassItem> {
    public WinefoxCarcassItemRenderer() {
        super(new WinefoxCarcassGeoModel());
    }
}

