package com.yourname.maidfox.expansion.event;

import com.github.tartaricacid.touhoulittlemaid.entity.info.ServerCustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.yourname.maidfox.expansion.MaidExpansion;
import com.yourname.maidfox.expansion.capability.MaidGeneCapabilityManager;
import com.yourname.maidfox.expansion.config.MaidExpansionConfig;
import com.yourname.maidfox.expansion.network.NetworkHandler;
import com.yourname.maidfox.expansion.network.message.PlayVillagerVoiceMessage;
import com.yourname.maidfox.expansion.network.message.SetMaidBabyMessage;
import com.yourname.maidfox.expansion.network.message.SyncVillagerGeneMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = MaidExpansion.MOD_ID)
public final class VillagerEvents {
    public static final String DEFAULT_MODEL_ID = "touhou_little_maid:hakurei_reimu";

    // Lazy resolution: the registry is not populated during the CONSTRUCT phase, so the RegistryObject is get() only when the event fires
    private static final Map<String, RegistryObject<SoundEvent>> VILLAGER_SOUND_MAP = new HashMap<>();

    static {
        VILLAGER_SOUND_MAP.put(soundPath(SoundEvents.VILLAGER_AMBIENT), InitSounds.MAID_IDLE);
        VILLAGER_SOUND_MAP.put(soundPath(SoundEvents.VILLAGER_HURT), InitSounds.MAID_HURT);
        VILLAGER_SOUND_MAP.put(soundPath(SoundEvents.VILLAGER_DEATH), InitSounds.MAID_DEATH);
        // Trade / consent uses the maid taming (fed with cake) voice
        VILLAGER_SOUND_MAP.put(soundPath(SoundEvents.VILLAGER_TRADE), InitSounds.MAID_TAMED);
        VILLAGER_SOUND_MAP.put(soundPath(SoundEvents.VILLAGER_YES), InitSounds.MAID_TAMED);
        VILLAGER_SOUND_MAP.put(soundPath(SoundEvents.VILLAGER_NO), InitSounds.MAID_IDLE);
        VILLAGER_SOUND_MAP.put(soundPath(SoundEvents.VILLAGER_CELEBRATE), InitSounds.GAME_WIN);
    }

    private VillagerEvents() {
    }

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!(event.getEntity() instanceof Villager villager)) {
            return;
        }
        if (MaidExpansionConfig.VILLAGER_CONVERT_CHANCE.get() <= 0.0D) {
            return;
        }
        MaidGeneCapabilityManager.get(villager).ifPresent(cap -> {
            if (cap.hasGene()) {
                return;
            }
            if (villager.getRandom().nextDouble() >= MaidExpansionConfig.VILLAGER_CONVERT_CHANCE.get()) {
                return;
            }
            cap.setModelGene(randomModel(villager.getRandom()));
            cap.setVoiceGene(randomVoicePack(villager.getRandom()));
        });
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        Entity target = event.getTarget();
        if (target instanceof Villager villager) {
            MaidGeneCapabilityManager.get(villager).ifPresent(cap -> {
                if (cap.hasGene()) {
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                            new SyncVillagerGeneMessage(villager.getId(), cap.getModelGene(), cap.getVoiceGene()));
                }
            });
        } else if (target instanceof EntityMaid maid) {
            MaidGeneCapabilityManager.get(maid).ifPresent(cap -> {
                if (cap.isBaby()) {
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                            new SetMaidBabyMessage(maid.getId(), true, -cap.getAge()));
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayLevelSound(PlayLevelSoundEvent.AtPosition event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        SoundEvent sound = event.getSound() == null ? null : event.getSound().value();
        if (sound == null) {
            return;
        }
        String path = sound.getLocation().getPath();
        if (path.startsWith("entity.villager.")) {
            MaidExpansion.LOGGER.debug("Villager sound {} heard at {}", path, event.getPosition());
        }
        Entity entity = findGeneEntityAt(event.getLevel(), event.getPosition(), sound);
        if (entity == null) {
            if (path.startsWith("entity.villager.")) {
                MaidExpansion.LOGGER.debug("Villager sound {} at {} not replaced: no converted villager nearby",
                        path, event.getPosition());
            }
            return;
        }
        handleVillagerSound(entity, event.getSound(), event);
    }

    @SubscribeEvent
    public static void onPlayLevelSoundAtEntity(PlayLevelSoundEvent.AtEntity event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        handleVillagerSound(event.getEntity(), event.getSound(), event);
    }

    private static void handleVillagerSound(Entity entity, net.minecraft.core.Holder<SoundEvent> soundHolder, PlayLevelSoundEvent event) {
        if (entity == null || soundHolder == null || !MaidExpansionConfig.ENABLE_VILLAGER_VOICE.get()) {
            return;
        }
        SoundEvent sound = soundHolder.value();
        if (sound == null) {
            return;
        }
        SoundEvent maidSound = mapSound(entity, sound);
        if (maidSound == null) {
            return;
        }
        MaidGeneCapabilityManager.get(entity).ifPresent(cap -> {
            if (!cap.hasGene()) {
                return;
            }
            String voice = cap.getVoiceGene();
            if (voice.isEmpty()) {
                return;
            }
            event.setCanceled(true);
            MaidExpansion.LOGGER.info("Replace sound {} at {} for entity {} with voice {}",
                    sound.getLocation(), entity.blockPosition(), entity.getId(), voice);
            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),
                    new PlayVillagerVoiceMessage(entity.getId(), maidSound.getLocation(), voice));
        });
    }

    private static SoundEvent mapSound(Entity entity, SoundEvent sound) {
        if (entity instanceof Villager) {
            String path = soundPath(sound);
            RegistryObject<SoundEvent> maidSound = VILLAGER_SOUND_MAP.get(path);
            if (maidSound != null) {
                return maidSound.get();
            }
            if (path.startsWith("entity.villager.")) {
                // Fallback: every unmapped villager sound is replaced with the idle voice so that sources such as spawn eggs are not missed
                return InitSounds.MAID_IDLE.get();
            }
            return null;
        }
        String path = sound.getLocation().getPath();
        if (path.contains("hurt")) {
            return InitSounds.MAID_HURT.get();
        }
        if (path.contains("death")) {
            return InitSounds.MAID_DEATH.get();
        }
        if (path.contains("ambient") || path.contains("idle")) {
            return InitSounds.MAID_IDLE.get();
        }
        return null;
    }

    private static Entity findGeneEntityAt(Level level, Vec3 pos, SoundEvent sound) {
        AABB aabb = AABB.ofSize(pos, 1.0D, 1.0D, 1.0D);
        String path = sound.getLocation().getPath();
        if (VILLAGER_SOUND_MAP.containsKey(path) || path.startsWith("entity.villager.")) {
            List<Villager> villagers = level.getEntitiesOfClass(Villager.class, aabb, VillagerEvents::hasVoiceGene);
            if (!villagers.isEmpty()) {
                return nearest(villagers, pos);
            }
        }
        if (path.contains("hurt") || path.contains("death") || path.contains("ambient") || path.contains("idle")) {
            List<Monster> monsters = level.getEntitiesOfClass(Monster.class, aabb, VillagerEvents::hasVoiceGene);
            if (!monsters.isEmpty()) {
                return nearest(monsters, pos);
            }
        }
        return null;
    }

    private static <T extends Entity> T nearest(List<T> entities, Vec3 pos) {
        T nearest = null;
        double best = Double.MAX_VALUE;
        for (T entity : entities) {
            double distance = entity.distanceToSqr(pos.x(), pos.y(), pos.z());
            if (distance < best) {
                best = distance;
                nearest = entity;
            }
        }
        return nearest;
    }

    private static boolean hasVoiceGene(Entity entity) {
        return MaidGeneCapabilityManager.get(entity)
                .map(cap -> !cap.getVoiceGene().isEmpty())
                .orElse(false);
    }

    private static String soundPath(SoundEvent sound) {
        return sound.getLocation().getPath();
    }

    private static String randomModel(RandomSource random) {
        Set<String> modelIds = ServerCustomPackLoader.SERVER_MAID_MODELS.getModelIdSet();
        if (modelIds.isEmpty()) {
            return DEFAULT_MODEL_ID;
        }
        int skip = random.nextInt(modelIds.size());
        int i = 0;
        for (String id : modelIds) {
            if (i++ == skip) {
                return id;
            }
        }
        return DEFAULT_MODEL_ID;
    }

    private static String randomVoicePack(RandomSource random) {
        List<? extends String> packs = MaidExpansionConfig.VOICE_PACK_IDS.get();
        if (packs.isEmpty()) {
            return "";
        }
        return packs.get(random.nextInt(packs.size()));
    }
}

