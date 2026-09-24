package com.yourname.maidfox.expansion.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class MaidGeneCapability implements IMaidGeneCapability, INBTSerializable<CompoundTag> {
    private static final String TAG_MODEL_GENE = "ModelGene";
    private static final String TAG_VOICE_GENE = "VoiceGene";
    private static final String TAG_AGE = "Age";
    private static final String TAG_LOVE_TICKS = "LoveTicks";

    private String modelGene = "";
    private String voiceGene = "";
    private int age = 0;
    private int loveTicks = 0;

    @Override
    public String getModelGene() {
        return modelGene;
    }

    @Override
    public void setModelGene(String modelGene) {
        this.modelGene = modelGene == null ? "" : modelGene;
    }

    @Override
    public String getVoiceGene() {
        return voiceGene;
    }

    @Override
    public void setVoiceGene(String voiceGene) {
        this.voiceGene = voiceGene == null ? "" : voiceGene;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public int getLoveTicks() {
        return loveTicks;
    }

    @Override
    public void setLoveTicks(int loveTicks) {
        this.loveTicks = loveTicks;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString(TAG_MODEL_GENE, modelGene);
        tag.putString(TAG_VOICE_GENE, voiceGene);
        tag.putInt(TAG_AGE, age);
        tag.putInt(TAG_LOVE_TICKS, loveTicks);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        modelGene = nbt.getString(TAG_MODEL_GENE);
        voiceGene = nbt.getString(TAG_VOICE_GENE);
        age = nbt.getInt(TAG_AGE);
        loveTicks = nbt.getInt(TAG_LOVE_TICKS);
    }
}

