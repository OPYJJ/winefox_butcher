package com.yourname.maidfox.expansion.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public final class ClientGeneCache {
    public record GeneData(String modelId, String voicePackId) {
    }

    private static final Map<Integer, GeneData> VILLAGER_GENES = Maps.newHashMap();
    private static final Set<Integer> BABY_MAIDS = Sets.newHashSet();
    private static final Map<Integer, Integer> BABY_REMAINING_TICKS = Maps.newHashMap();

    private ClientGeneCache() {
    }

    public static void putVillagerGene(int entityId, String modelId, String voicePackId) {
        VILLAGER_GENES.put(entityId, new GeneData(modelId, voicePackId));
    }

    public static GeneData getVillagerGene(int entityId) {
        return VILLAGER_GENES.get(entityId);
    }

    public static void setBaby(int entityId, boolean baby, int remainingTicks) {
        if (baby) {
            BABY_MAIDS.add(entityId);
            BABY_REMAINING_TICKS.put(entityId, Math.max(1, remainingTicks));
        } else {
            BABY_MAIDS.remove(entityId);
            BABY_REMAINING_TICKS.remove(entityId);
        }
    }

    public static boolean isBaby(int entityId) {
        return BABY_MAIDS.contains(entityId);
    }

    public static int getRemainingTicks(int entityId) {
        return BABY_REMAINING_TICKS.getOrDefault(entityId, 0);
    }

    /**
     * Client-side local countdown: the remaining growth time is decremented every second and the
     * entity counts as adult at zero (aligned with the server's adulthood packet).
     */
    public static void tickRemaining() {
        BABY_REMAINING_TICKS.entrySet().removeIf(entry -> entry.getValue() - 1 <= 0);
        BABY_REMAINING_TICKS.replaceAll((id, ticks) -> ticks - 1);
        BABY_MAIDS.removeIf(id -> !BABY_REMAINING_TICKS.containsKey(id));
    }

    public static void clearAll() {
        VILLAGER_GENES.clear();
        BABY_MAIDS.clear();
        BABY_REMAINING_TICKS.clear();
    }
}

