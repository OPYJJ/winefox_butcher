package com.yourname.maidfox.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yourname.maidfox.client.model.WinefoxRoastFoxGeoModel;
import com.yourname.maidfox.delight.compat.farmersdelight.WinefoxRoastFoxBlock;
import com.yourname.maidfox.delight.compat.farmersdelight.WinefoxRoastFoxBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * Renderer for the two-block feast block.
 * <p>
 * GeckoLib's GeoBlockRenderer applies {@code translate(0.5, 0, 0.5)} by itself; here
 * {@link #getFacing} is pinned to NORTH to switch off its built-in facing rotation, and the
 * rotation is applied manually from the block's FACING value.
 * <p>
 * Position and scale use fixed constants (same approach as the head / fur bed / meat pot):
 * this kind of model parks unused parts far away, so fitting by bounding box would inflate the
 * model and make it too small. No auto-fitting here.
 * <p>
 * The effective transform chain ({@code T(0.5,0,0.5) * R_facing * T(0,0,0.5) * R_yaw * S * T(-C)})
 * places the model-space point {@code C} at the midpoint of the two blocks, at ground level;
 * therefore {@code C} must be the center of the tray itself.
 * Tray geometry (16 units = 1 block): x[-7,7] y[0,2] z[-6,22] -> center (0, 0.0625, 0.5).
 */
public class WinefoxRoastFoxBlockRenderer extends GeoBlockRenderer<WinefoxRoastFoxBlockEntity> {
    /**
     * Model scale. At 1.0 a single plate is 14/16 = 0.875 blocks, exactly matching the Farmer's
     * Delight roast chicken {@code tray} element (14x2x14 pixels); the two plates joined along the
     * long edge total 28 units = 1.75 blocks, spanning the FOOT + HEAD blocks with 0.125 blocks of
     * margin on each side.
     * <p>
     * Note: 1.0 is 20% larger than the 0.833 used by {@code WinefoxHeadBlockRenderer}, so the fox
     * head here is slightly bigger than a standalone winefox head block. That is the unavoidable
     * cost of matching the chicken tray exactly - do not revert to 0.833.
     */
    private static final float MODEL_SCALE = 1.0F;

    /**
     * Center of the tray in model space (in blocks), used for horizontal centering.
     * <p>
     * Uses the geometric center of the tray rather than the bounding-box center of the whole
     * content (standing fox head, distant parked parts), so that the tray fills both blocks
     * symmetrically.
     */
    private static final float MODEL_CENTER_X = 0.0F;
    private static final float MODEL_CENTER_Z = 0.5F;

    /** Tray bottom rests on the ground. */
    private static final float MODEL_BOTTOM_Y = 0.0F;

    /**
     * Y-axis orientation of the placed model (degrees).
     * <p>
     * The tray's long edge follows the model Z axis (28 units = 1.75 blocks), and after step 3 the
     * local +Z is the FOOT -> HEAD axis, so this may only be 0 or 180. Using +/-90 would make the
     * tray perpendicular to that axis and spill outside the blocks (the old value -90 caused that
     * display bug).
     * <ul>
     *     <li>0 (current): the fox faces local -Z, i.e. towards a player standing on the FOOT side</li>
     *     <li>180: the fox faces the HEAD end, away from the player</li>
     * </ul>
     */
    private static final float MODEL_YAW = 0.0F;

    public WinefoxRoastFoxBlockRenderer() {
        super(new WinefoxRoastFoxGeoModel());
    }

    @Override
    public void preRender(PoseStack poseStack, WinefoxRoastFoxBlockEntity animatable, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                          int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick,
                packedLight, packedOverlay, red, green, blue, alpha);

        // Call order is matrix left-multiplication order: the last call is applied to vertices first

        // 1) Move the origin to the center of the FOOT block
        poseStack.translate(0.5F, 0.0F, 0.5F);
        // 2) Make local +Z point along the block facing (GeckoLib's own rotation is disabled by getFacing = NORTH)
        Direction facing = animatable.getBlockState().getValue(WinefoxRoastFoxBlock.FACING);
        switch (facing) {
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            default -> {
                // SOUTH: the facing axis is already +Z, no rotation needed
            }
        }
        // 3) Move to the seam between the two blocks (half a block along the facing from the FOOT center)
        poseStack.translate(0.0F, 0.0F, 0.5F);
        // 4) Model orientation (0 = the fox faces the player)
        poseStack.mulPose(Axis.YP.rotationDegrees(MODEL_YAW));
        // 5) Scale (1.0 = a single plate of 0.875 blocks, matching the chicken tray)
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        // 6) Center the tray horizontally and rest its bottom on the ground
        poseStack.translate(-MODEL_CENTER_X, -MODEL_BOTTOM_Y, -MODEL_CENTER_Z);
        // 7) Cancel the implicit translate(0.5, 0, 0.5) applied inside GeoBlockRenderer.actuallyRender.
        //    It works at the innermost vertex level, so this call must be last to cancel it exactly.
        poseStack.translate(-0.5F, 0.0F, -0.5F);
    }

    @Override
    protected Direction getFacing(WinefoxRoastFoxBlockEntity animatable) {
        return Direction.NORTH;
    }
}
