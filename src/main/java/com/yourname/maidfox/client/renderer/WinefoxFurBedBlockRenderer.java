package com.yourname.maidfox.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yourname.maidfox.block.WinefoxFurBedBlockEntity;
import com.yourname.maidfox.client.model.WinefoxFurBedGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.Property;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class WinefoxFurBedBlockRenderer
extends GeoBlockRenderer<WinefoxFurBedBlockEntity> {
    public WinefoxFurBedBlockRenderer() {
        super(new WinefoxFurBedGeoModel());
    }

    public void preRender(PoseStack poseStack, WinefoxFurBedBlockEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        Direction facing = (Direction)animatable.getBlockState().getValue((Property)BedBlock.FACING);
        poseStack.translate(0.5f, 0.0f, 0.5f);
        switch (facing) {
            case NORTH: {
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
                break;
            }
            case EAST: {
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
                break;
            }
            case WEST: {
                poseStack.mulPose(Axis.YP.rotationDegrees(-90.0f));
                break;
            }
        }
        poseStack.translate(0.0f, 0.0f, 1.0f);
        poseStack.translate(-0.5f, 0.0f, -0.5f);
    }

    protected Direction getFacing(WinefoxFurBedBlockEntity animatable) {
        return Direction.NORTH;
    }
}

