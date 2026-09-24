package com.yourname.maidfox.expansion.capability;

/**
 * Gene data for maids / villagers / mutated mobs: model gene and voice gene, plus the age,
 * courtship and cooldown state required for maid breeding.
 */
public interface IMaidGeneCapability {
    String getModelGene();

    void setModelGene(String modelGene);

    String getVoiceGene();

    void setVoiceGene(String voiceGene);

    default boolean hasGene() {
        return !getModelGene().isEmpty() || !getVoiceGene().isEmpty();
    }

    int getAge();

    void setAge(int age);

    default boolean isBaby() {
        return getAge() < 0;
    }

    int getLoveTicks();

    void setLoveTicks(int loveTicks);

    default boolean isInLove() {
        return getLoveTicks() > 0;
    }
}

