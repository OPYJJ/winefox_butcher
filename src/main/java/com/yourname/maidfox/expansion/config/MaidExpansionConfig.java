package com.yourname.maidfox.expansion.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class MaidExpansionConfig {
    public static final ForgeConfigSpec COMMON;

    public static final ForgeConfigSpec.BooleanValue ENABLE_MAID_BREEDING;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BREED_FOOD_LIST;
    public static final ForgeConfigSpec.IntValue BABY_GROWTH_TICKS;
    public static final ForgeConfigSpec.IntValue BREEDING_DELAY_TICKS;
    public static final ForgeConfigSpec.IntValue LOVE_TIMEOUT_TICKS;
    public static final ForgeConfigSpec.DoubleValue VILLAGER_CONVERT_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> VOICE_PACK_IDS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> MUTATION_HOSTILES;
    public static final ForgeConfigSpec.DoubleValue MUTATION_CHANCE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_VILLAGER_VOICE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Maid Expansion common config").push("general");

        ENABLE_MAID_BREEDING = builder
                .comment("Whether the maid breeding system is enabled.")
                .define("enableMaidBreeding", true);

        BREED_FOOD_LIST = builder
                .comment("Items that can feed maids into love (breeding) or accelerate baby growth.",
                        "Format: registry item ids, e.g. minecraft:cake")
                .defineList("breedFoodList",
                        List.of("minecraft:cake", "minecraft:cookie", "minecraft:golden_carrot",
                                "minecraft:sugar", "minecraft:carrot"),
                        o -> o instanceof String);

        BABY_GROWTH_TICKS = builder
                .comment("Ticks a baby maid needs to grow into an adult (20 minutes = 24000 ticks).")
                .defineInRange("babyGrowthTicks", 24000, 1, Integer.MAX_VALUE);

        BREEDING_DELAY_TICKS = builder
                .comment("Ticks two in-love maids stay close together before the baby appears (3 seconds = 60 ticks).")
                .defineInRange("breedingDelayTicks", 60, 1, Integer.MAX_VALUE);

        LOVE_TIMEOUT_TICKS = builder
                .comment("Ticks a maid stays in love after being fed (30 seconds = 600 ticks).")
                .defineInRange("loveTimeoutTicks", 600, 1, Integer.MAX_VALUE);

        VILLAGER_CONVERT_CHANCE = builder
                .comment("Chance that a newly joined villager gets maid genes (visual + voice conversion). 1.0 = all villagers.")
                .defineInRange("villagerConvertChance", 1.0D, 0.0D, 1.0D);

        VOICE_PACK_IDS = builder
                .comment("Voice pack ids (SoundPackId) used for random voice genes.",
                        "Built-in packs: touhou_little_maid, littlemaid_peco")
                .defineList("voicePackIds",
                        List.of("touhou_little_maid", "littlemaid_peco"),
                        o -> o instanceof String);

        MUTATION_HOSTILES = builder
                .comment("Hostile entity types that can spawn from the 5% mutation branch.")
                .defineList("mutationHostiles",
                        List.of("minecraft:zombie", "minecraft:creeper", "minecraft:skeleton"),
                        o -> o instanceof String);

        MUTATION_CHANCE = builder
                .comment("Chance that breeding produces a hostile mutation instead of a baby maid.")
                .defineInRange("mutationChance", 0.05D, 0.0D, 1.0D);

        ENABLE_VILLAGER_VOICE = builder
                .comment("Whether converted villagers/mutations replace vanilla sounds with maid voice packs.")
                .define("enableVillagerVoice", true);

        builder.pop();
        COMMON = builder.build();
    }

    private MaidExpansionConfig() {
    }
}

