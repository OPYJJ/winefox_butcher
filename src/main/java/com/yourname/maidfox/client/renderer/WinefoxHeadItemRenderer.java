package com.yourname.maidfox.client.renderer;

import com.yourname.maidfox.client.model.WinefoxHeadGeoModel;
import com.yourname.maidfox.item.WinefoxHeadItem;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WinefoxHeadItemRenderer
extends GeoItemRenderer<WinefoxHeadItem> {
    public WinefoxHeadItemRenderer() {
        super(new WinefoxHeadGeoModel());
    }
}

