package com.yourname.maidfox.client.renderer;

import com.yourname.maidfox.client.model.WinefoxCarcassStageModel;
import com.yourname.maidfox.item.WinefoxCarcassStageItem;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WinefoxCarcassStageRenderer
extends GeoItemRenderer<WinefoxCarcassStageItem> {
    public WinefoxCarcassStageRenderer() {
        super(new WinefoxCarcassStageModel());
    }
}

