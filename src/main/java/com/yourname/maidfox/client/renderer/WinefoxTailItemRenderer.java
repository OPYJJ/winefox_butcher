package com.yourname.maidfox.client.renderer;

import com.yourname.maidfox.client.model.WinefoxTailGeoModel;
import com.yourname.maidfox.item.WinefoxTailItem;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WinefoxTailItemRenderer
extends GeoItemRenderer<WinefoxTailItem> {
    public WinefoxTailItemRenderer() {
        super(new WinefoxTailGeoModel());
    }
}

