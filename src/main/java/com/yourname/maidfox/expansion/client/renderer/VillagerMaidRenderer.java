package com.yourname.maidfox.expansion.client.renderer;

import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
import com.github.tartaricacid.touhoulittlemaid.api.event.client.RenderMaidEvent;
import com.github.tartaricacid.touhoulittlemaid.client.animation.script.GlWrapper;
import com.github.tartaricacid.touhoulittlemaid.client.entity.GeckoMaidEntity;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.GeckoEntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.resource.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.models.MaidModels;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.yourname.maidfox.expansion.client.ClientGeneCache;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;
import java.util.Optional;

/**
 * Villager-to-maid renderer: without a gene it delegates to vanilla; with a gene it mirrors
 * EntityMaidRenderer and supports full animation rendering for both bedrock and gecko maid
 * models.
 */
@OnlyIn(Dist.CLIENT)
@SuppressWarnings({"rawtypes", "unchecked"})
public class VillagerMaidRenderer extends MobRenderer<Mob, BedrockModel<Mob>> {
    public static final String DEFAULT_MODEL_ID = "touhou_little_maid:hakurei_reimu";
    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation("touhou_little_maid", "textures/entity/empty.png");

    private final VillagerRenderer vanillaRenderer;
    private final GeckoEntityMaidRenderer<Mob> geckoRenderer;
    private MaidModelInfo mainInfo;
    private List<Object> mainAnimations = List.of();

    public VillagerMaidRenderer(EntityRendererProvider.Context context, VillagerRenderer vanillaRenderer) {
        super(context, new BedrockModel<>(), 0.5F);
        this.vanillaRenderer = vanillaRenderer;
        this.geckoRenderer = new GeckoEntityMaidRenderer<>(context);
    }

    @Override
    public void render(Mob entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (!(entity instanceof Villager villager)) {
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            return;
        }
        ClientGeneCache.GeneData gene = ClientGeneCache.getVillagerGene(villager.getId());
        if (gene == null || gene.modelId().isEmpty()) {
            vanillaRenderer.render(villager, entityYaw, partialTicks, poseStack, buffer, packedLight);
            return;
        }

        IMaid maid = IMaid.convert(villager);
        if (maid == null) {
            vanillaRenderer.render(villager, entityYaw, partialTicks, poseStack, buffer, packedLight);
            return;
        }

        // First clear the previous render's cached state with the default model, then load the gene model (mirrors EntityMaidRenderer)
        CustomPackLoader.MAID_MODELS.getModel(DEFAULT_MODEL_ID).ifPresent(model -> this.model = model);
        CustomPackLoader.MAID_MODELS.getInfo(DEFAULT_MODEL_ID).ifPresent(info -> this.mainInfo = info);
        CustomPackLoader.MAID_MODELS.getAnimation(DEFAULT_MODEL_ID).ifPresent(animations -> this.mainAnimations = animations);

        MaidModels.ModelData eventModelData = new MaidModels.ModelData(model, mainInfo, mainAnimations);
        if (MinecraftForge.EVENT_BUS.post(new RenderMaidEvent(maid, eventModelData))) {
            BedrockModel<Mob> overrideModel = eventModelData.getModel();
            if (overrideModel != null) {
                this.model = overrideModel;
            }
            this.mainInfo = eventModelData.getInfo();
            this.mainAnimations = eventModelData.getAnimations();
        } else {
            loadGeneModel(gene.modelId());
        }

        // Gecko models use the maid's gecko render path
        if (mainInfo != null && mainInfo.isGeckoModel()) {
            GeckoMaidEntity<Mob> animatable = geckoRenderer.getAnimatableEntity(villager);
            animatable.setMaidInfo(mainInfo);
            geckoRenderer.render(villager, entityYaw, partialTicks, poseStack, buffer, packedLight);
            return;
        }

        // Bedrock model: set up the JS animation and use the MobRenderer pipeline
        this.model.setAnimations(mainAnimations);
        GlWrapper.setPoseStack(poseStack);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        GlWrapper.clearPoseStack();
    }

    private void loadGeneModel(String modelId) {
        Optional<BedrockModel<Mob>> modelOpt = CustomPackLoader.MAID_MODELS.getModel(modelId);
        Optional<MaidModelInfo> infoOpt = CustomPackLoader.MAID_MODELS.getInfo(modelId);
        String effectiveId = modelId;
        if (infoOpt.isPresent() && infoOpt.get().isGeckoModel()) {
            // Gecko model: the model data lives in GeckoLibCache, BedrockModel keeps its defaults
            this.mainInfo = infoOpt.get();
            return;
        }
        if (modelOpt.isEmpty() || infoOpt.isEmpty()) {
            // Invalid or missing model falls back to the default model
            effectiveId = DEFAULT_MODEL_ID;
            modelOpt = CustomPackLoader.MAID_MODELS.getModel(DEFAULT_MODEL_ID);
            infoOpt = CustomPackLoader.MAID_MODELS.getInfo(DEFAULT_MODEL_ID);
        }
        modelOpt.ifPresent(model -> this.model = model);
        infoOpt.ifPresent(info -> this.mainInfo = info);
        CustomPackLoader.MAID_MODELS.getAnimation(effectiveId).ifPresent(animations -> this.mainAnimations = animations);
    }

    @Override
    protected void scale(Mob maid, PoseStack poseStack, float partialTickTime) {
        if (mainInfo != null) {
            float scale = mainInfo.getRenderEntityScale();
            poseStack.scale(scale, scale, scale);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Mob maid) {
        if (mainInfo == null) {
            return DEFAULT_TEXTURE;
        }
        return mainInfo.getTexture();
    }
}

