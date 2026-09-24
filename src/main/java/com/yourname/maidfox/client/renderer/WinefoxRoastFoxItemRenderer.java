package com.yourname.maidfox.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yourname.maidfox.client.model.WinefoxRoastFoxItemGeoModel;
import com.yourname.maidfox.delight.compat.farmersdelight.WinefoxRoastFoxItem;
import net.minecraft.client.renderer.MultiBufferSource;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * Item form renderer: the raw model coordinates are not near the origin, so the content is
 * centered on all three axes with fixed constants and scaled to fit an inventory slot.
 * <p>
 * Do <b>not</b> cancel the {@code translate(0.5, 0.51, 0.5)} that {@code GeoItemRenderer.preRender}
 * applies on its own: it is paired with vanilla {@code ItemRenderer}'s
 * {@code translate(-0.5, -0.5, -0.5)} and exists to put the content center at
 * (0.5, 0.5, 0.5) of the item model space. Subtracting another 0.5 shifts the content by
 * (-0.5, -0.5, -0.5) blocks, which clips the inventory icon into the bottom-left corner and
 * makes the held model sink to the waist.
 */
public class WinefoxRoastFoxItemRenderer extends GeoItemRenderer<WinefoxRoastFoxItem> {
    /** Makes the visible long axis exactly 1 block so it fits inside a single inventory slot. */
    private static final float ITEM_SCALE = 0.5016F;

    /**
     * Content center (in blocks). Tray geometry x[-7,7] y[0,2] z[-6,22] (16 units = 1 block);
     * combined with the fox (up to y 17.52) the overall center is about (0, 0.5475, 0.5).
     */
    private static final float MODEL_CENTER_X = 0.0F;
    private static final float MODEL_CENTER_Y = 0.55F;
    private static final float MODEL_CENTER_Z = 0.5F;

    /**
     * Y-axis orientation of the item form (degrees). The fox faces -Z in the model while the
     * GUI / held view direction is +Z, so rotate by 180 to make the face look at the player
     * (the display values are tuned for this orientation).
     */
    private static final float MODEL_YAW = 180.0F;

    public WinefoxRoastFoxItemRenderer() {
        super(new WinefoxRoastFoxItemGeoModel());
    }

    @Override
    public void preRender(PoseStack poseStack, WinefoxRoastFoxItem animatable, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                          int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        // Call order is matrix left-multiplication order: first bring the content center to the origin
        // of item model space; GeoItemRenderer's translate(0.5, 0.51, 0.5) and vanilla ItemRenderer's
        // translate(-0.5, -0.5, -0.5) then place the content at the center of the item box
        poseStack.mulPose(Axis.YP.rotationDegrees(MODEL_YAW));
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
        poseStack.translate(-MODEL_CENTER_X, -MODEL_CENTER_Y, -MODEL_CENTER_Z);
    }
}
