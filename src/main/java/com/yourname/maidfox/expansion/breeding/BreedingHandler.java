package com.yourname.maidfox.expansion.breeding;

import com.github.tartaricacid.touhoulittlemaid.entity.info.ServerCustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.yourname.maidfox.expansion.capability.IMaidGeneCapability;
import com.yourname.maidfox.expansion.capability.MaidGeneCapabilityManager;
import com.yourname.maidfox.expansion.config.MaidExpansionConfig;
import com.yourname.maidfox.expansion.network.NetworkHandler;
import com.yourname.maidfox.expansion.network.message.PlayVillagerVoiceMessage;
import com.yourname.maidfox.expansion.network.message.SetMaidBabyMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Breeding resolution: 90% normal inheritance / 5% global model mutation / 5% mutation into a hostile mob.
 */
public final class BreedingHandler {
    public static final String DEFAULT_MODEL_ID = "touhou_little_maid:hakurei_reimu";

    private BreedingHandler() {
    }

    public static void tryBreed(EntityMaid maidA, EntityMaid maidB) {
        if (maidA.level().isClientSide || maidB.level().isClientSide) {
            return;
        }
        if (maidA.level() != maidB.level() || !maidA.isAlive() || !maidB.isAlive()) {
            return;
        }

        Optional<IMaidGeneCapability> capAOpt = MaidGeneCapabilityManager.get(maidA).resolve();
        Optional<IMaidGeneCapability> capBOpt = MaidGeneCapabilityManager.get(maidB).resolve();
        if (capAOpt.isEmpty() || capBOpt.isEmpty()) {
            return;
        }
        IMaidGeneCapability capA = capAOpt.get();
        IMaidGeneCapability capB = capBOpt.get();
        if (!canBreed(maidA, capA) || !canBreed(maidB, capB)) {
            return;
        }

        // Resolution: clear the courtship state (no cooldown, the maid can be fed again immediately)
        capA.setLoveTicks(0);
        capB.setLoveTicks(0);

        ServerLevel level = (ServerLevel) maidA.level();
        double x = (maidA.getX() + maidB.getX()) / 2.0D;
        double y = (maidA.getY() + maidB.getY()) / 2.0D;
        double z = (maidA.getZ() + maidB.getZ()) / 2.0D;

        double roll = level.random.nextDouble();
        double mutationChance = MaidExpansionConfig.MUTATION_CHANCE.get();
        if (roll < mutationChance) {
            // Mutation: inherit a random voice pack from one of the two parents
            String voice = randomParentVoice(level, maidA, maidB);
            spawnMutation(level, x, y, z, voice);
        } else {
            // Second roll, 5%: global model mutation; otherwise normal inheritance
            boolean globalModel = roll < mutationChance * 2.0D;
            // Voice: 90% from the parents / 10% a random voice pack
            String voice = level.random.nextDouble() < 0.1D
                    ? randomVoicePack(level)
                    : randomParentVoice(level, maidA, maidB);
            spawnBabyMaid(level, x, y, z, maidA, maidB, globalModel, voice);
        }
    }

    private static boolean canBreed(EntityMaid maid, IMaidGeneCapability cap) {
        return MaidExpansionConfig.ENABLE_MAID_BREEDING.get()
                && maid.isAlive() && cap.getAge() >= 0 && cap.isInLove();
    }

    private static String randomParentVoice(ServerLevel level, EntityMaid maidA, EntityMaid maidB) {
        return level.random.nextBoolean() ? maidA.getSoundPackId() : maidB.getSoundPackId();
    }

    private static String randomVoicePack(ServerLevel level) {
        List<? extends String> packs = MaidExpansionConfig.VOICE_PACK_IDS.get();
        if (packs.isEmpty()) {
            return "";
        }
        return packs.get(level.random.nextInt(packs.size()));
    }

    private static void spawnMutation(ServerLevel level, double x, double y, double z, String voice) {
        List<? extends String> hostiles = MaidExpansionConfig.MUTATION_HOSTILES.get();
        if (hostiles.isEmpty()) {
            return;
        }
        String id = hostiles.get(level.random.nextInt(hostiles.size()));
        EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(id));
        if (type == null) {
            return;
        }
        Entity entity = type.create(level);
        if (!(entity instanceof Mob mob)) {
            return;
        }
        mob.moveTo(x, y, z, level.random.nextFloat() * 360.0F, 0.0F);
        MaidGeneCapabilityManager.get(mob).ifPresent(cap -> cap.setVoiceGene(voice));
        level.addFreshEntity(mob);
        // Mutation sound: inherits the parents' voice pack
        NetworkHandler.CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, 64, level.dimension())),
                new PlayVillagerVoiceMessage(mob.getId(), InitSounds.MAID_HURT.get().getLocation(), voice));
    }

    private static void spawnBabyMaid(ServerLevel level, double x, double y, double z,
                                      EntityMaid maidA, EntityMaid maidB, boolean globalModel, String voice) {
        EntityMaid baby = InitEntities.MAID.get().create(level);
        if (baby == null) {
            return;
        }
        baby.moveTo(x, y, z, level.random.nextFloat() * 360.0F, 0.0F);
        String modelId = globalModel
                ? randomModel(level)
                : (level.random.nextBoolean() ? maidA.getModelId() : maidB.getModelId());
        if (!validModel(modelId)) {
            modelId = DEFAULT_MODEL_ID;
        }
        baby.setModelId(modelId);
        baby.setSoundPackId(voice);

        int growth = MaidExpansionConfig.BABY_GROWTH_TICKS.get();
        final String finalModelId = modelId;
        final String finalVoice = voice;
        MaidGeneCapabilityManager.get(baby).ifPresent(cap -> {
            cap.setModelGene(finalModelId);
            cap.setVoiceGene(finalVoice);
            cap.setAge(-growth);
        });
        level.addFreshEntity(baby);
        // Mirrors animal breeding: drops 1-7 experience points
        level.addFreshEntity(new ExperienceOrb(level, x, y, z, level.random.nextInt(7) + 1));
        NetworkHandler.CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, 64, level.dimension())),
                new SetMaidBabyMessage(baby.getId(), true, growth));
    }

    private static boolean validModel(String modelId) {
        return modelId != null && !modelId.isEmpty() && ServerCustomPackLoader.SERVER_MAID_MODELS.getInfo(modelId).isPresent();
    }

    private static String randomModel(ServerLevel level) {
        Set<String> modelIds = ServerCustomPackLoader.SERVER_MAID_MODELS.getModelIdSet();
        if (modelIds.isEmpty()) {
            return DEFAULT_MODEL_ID;
        }
        int skip = level.random.nextInt(modelIds.size());
        int i = 0;
        for (String id : modelIds) {
            if (i++ == skip) {
                return id;
            }
        }
        return DEFAULT_MODEL_ID;
    }
}

