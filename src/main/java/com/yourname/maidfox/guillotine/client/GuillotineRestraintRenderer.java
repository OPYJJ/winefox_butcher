package com.yourname.maidfox.guillotine.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yourname.maidfox.guillotine.GuillotineRestraint;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GuillotineRestraintRenderer extends EntityRenderer<GuillotineRestraint> {
    private static final ResourceLocation EMPTY = new ResourceLocation("minecraft", "textures/entity/armorstand/wood.png");
    public GuillotineRestraintRenderer(EntityRendererProvider.Context context) { super(context); }
    @Override public ResourceLocation getTextureLocation(GuillotineRestraint entity) { return EMPTY; }
    @Override public void render(GuillotineRestraint entity, float yaw, float partialTick, PoseStack pose,
                                 MultiBufferSource buffers, int light) { }
}
