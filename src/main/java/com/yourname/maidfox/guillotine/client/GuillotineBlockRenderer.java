package com.yourname.maidfox.guillotine.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yourname.maidfox.guillotine.GuillotineBlock;
import com.yourname.maidfox.guillotine.GuillotineBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class GuillotineBlockRenderer extends GeoBlockRenderer<GuillotineBlockEntity> {
    public GuillotineBlockRenderer() { super(new GuillotineGeoModel()); }

    @Override
    public void preRender(PoseStack pose, GuillotineBlockEntity machine, BakedGeoModel model,
                          MultiBufferSource buffers, VertexConsumer vertices, boolean rerender,
                          float partialTick, int light, int overlay, float red, float green, float blue, float alpha) {
        super.preRender(pose, machine, model, buffers, vertices, rerender, partialTick, light, overlay, red, green, blue, alpha);
        pose.translate(.5, 0, .5);
        Direction facing = machine.getBlockState().getValue(GuillotineBlock.FACING);
        switch (facing) {
            case NORTH -> pose.mulPose(Axis.YP.rotationDegrees(180));
            case EAST -> pose.mulPose(Axis.YP.rotationDegrees(90));
            case WEST -> pose.mulPose(Axis.YP.rotationDegrees(-90));
            default -> { }
        }
        // Undo GeoBlockRenderer's own half-block translation, keeping the model at the centre block.
        pose.translate(-.5, 0, -.5);
    }

    @Override
    protected Direction getFacing(GuillotineBlockEntity animatable) { return Direction.NORTH; }
}
