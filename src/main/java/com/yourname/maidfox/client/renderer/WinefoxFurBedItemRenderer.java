package com.yourname.maidfox.client.renderer;

import com.yourname.maidfox.client.model.WinefoxFurBedItemGeoModel;
import com.yourname.maidfox.item.WinefoxFurBedItem;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WinefoxFurBedItemRenderer
extends GeoItemRenderer<WinefoxFurBedItem> {
    public WinefoxFurBedItemRenderer() {
        super(new WinefoxFurBedItemGeoModel());
    }
}

