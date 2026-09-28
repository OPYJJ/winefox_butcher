package com.yourname.maidfox.guillotine.client;

import com.yourname.maidfox.guillotine.GuillotineBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

/** Samples the supplied animation keyframes from synchronized server time, including late joiners. */
public class GuillotineGeoModel extends GeoModel<GuillotineBlockEntity> {
    private static final ResourceLocation GEO = new ResourceLocation("winefox_butcher", "geo/guillotine.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("winefox_butcher", "textures/block/guillotine_atlas.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("winefox_butcher", "animations/guillotine.animation.json");
    private static final float[] DROP_T = {0, .16F, .24F, .32F, .40F, .46F, .50F, .55F, .80F};
    private static final float[] DROP_Y = {0, 0, -2.407407F, -9.62963F, -21.666667F, -32.5F, -32F, -32.5F, -32.5F};
    private static final float[] RESET_T = {0, .15F, 1.10F, 1.25F};
    private static final float[] RESET_Y = {-32.5F, -32.5F, 0, 0};

    @Override public ResourceLocation getModelResource(GuillotineBlockEntity animatable) { return GEO; }
    @Override public ResourceLocation getTextureResource(GuillotineBlockEntity animatable) { return TEXTURE; }
    @Override public ResourceLocation getAnimationResource(GuillotineBlockEntity animatable) { return ANIMATION; }

    @Override
    public void setCustomAnimations(GuillotineBlockEntity machine, long instanceId, AnimationState<GuillotineBlockEntity> state) {
        if (machine.getLevel() == null) return;
        float seconds = Math.max(0, (machine.getLevel().getGameTime() - machine.getPhaseStarted()
                + state.getPartialTick()) / 20F);
        float y = switch (machine.getPhase()) {
            case EMPTY, BOUND -> 0;
            case EXECUTING -> sample(seconds, DROP_T, DROP_Y);
            case LOWERED -> -32.5F;
            case RESETTING -> sample(seconds, RESET_T, RESET_Y);
        };
        setY("BladeRoot", y);
        for (int i = 1; i <= 5; i++) {
            setY("ChainLeft_" + i, y * i / 5F);
            setY("ChainRight_" + i, y * i / 5F);
        }
    }

    private void setY(String name, float y) {
        getBone(name).ifPresent(bone -> bone.setPosY(y));
    }

    private static float sample(float time, float[] ticks, float[] values) {
        for (int i = 1; i < ticks.length; i++) {
            if (time <= ticks[i]) {
                float fraction = (time - ticks[i - 1]) / (ticks[i] - ticks[i - 1]);
                return values[i - 1] + fraction * (values[i] - values[i - 1]);
            }
        }
        return values[values.length - 1];
    }
}
