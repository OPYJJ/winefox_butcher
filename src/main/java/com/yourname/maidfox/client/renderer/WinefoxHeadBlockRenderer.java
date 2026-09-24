package com.yourname.maidfox.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yourname.maidfox.block.WinefoxHeadBlockEntity;
import com.yourname.maidfox.client.model.WinefoxHeadBlockGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class WinefoxHeadBlockRenderer
extends GeoBlockRenderer<WinefoxHeadBlockEntity> {
    private static final float MODEL_SCALE = 0.833f;
    private static final float MODEL_BOTTOM_Y = -0.013f;

    public WinefoxHeadBlockRenderer() {
        super(new WinefoxHeadBlockGeoModel());
    }

    public void preRender(PoseStack poseStack, WinefoxHeadBlockEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        BlockState state = animatable.getBlockState();
        boolean wall = state.getBlock() instanceof WallSkullBlock;
        Direction facing = wall ? (Direction)state.getValue((Property)WallSkullBlock.FACING) : Direction.NORTH;
        float centerX = wall ? 0.5f - (float)facing.getStepX() * 0.25f : 0.5f;
        float centerZ = wall ? 0.5f - (float)facing.getStepZ() * 0.25f : 0.5f;
        float baseY = wall ? 0.25f : 0.0f;
        // 0.091128 = bottom of the head model bounding box (-0.109397 blocks) * MODEL_SCALE (0.833),
        // lifting the model bottom (the lowest point of the long hair) to the block bottom at y = 0.
        // Recompute this constant whenever the model changes, otherwise the head sinks into the ground or floats.
        poseStack.translate(centerX, baseY - -0.091128f, centerZ);
        poseStack.scale(0.833f, 0.833f, 0.833f);
        poseStack.mulPose(Axis.YP.rotationDegrees(WinefoxHeadBlockRenderer.getYaw(wall, state)));
        poseStack.translate(-0.5f, 0.0f, -0.5f);
    }

    private static float getYaw(boolean wall, BlockState state) {
        if (wall) {
            return switch ((Direction)state.getValue((Property)WallSkullBlock.FACING)) {
                case SOUTH -> 180.0f;
                case WEST -> 90.0f;
                case EAST -> -90.0f;
                default -> 0.0f;
            };
        }
        return -((float)((Integer)state.getValue((Property)SkullBlock.ROTATION)).intValue() * 22.5f);
    }

    protected Direction getFacing(WinefoxHeadBlockEntity animatable) {
        return Direction.NORTH;
    }
}

