package com.yourname.maidfox.expansion.client.sound;

import com.github.tartaricacid.touhoulittlemaid.client.sound.CustomSoundLoader;
import com.github.tartaricacid.touhoulittlemaid.client.sound.data.SoundCache;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Mirrors MaidSoundInstance: takes audio from the Touhou Little Maid voice pack cache and
 * plays it at the position of any entity.
 */
@OnlyIn(Dist.CLIENT)
public class VillagerMaidSoundInstance extends AbstractTickableSoundInstance {
    private final String voicePackId;
    private final Entity entity;

    public VillagerMaidSoundInstance(SoundEvent soundEvent, String voicePackId, Entity entity) {
        super(soundEvent, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.voicePackId = voicePackId;
        this.entity = entity;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }

    @Override
    public boolean canPlaySound() {
        return !this.entity.isSilent();
    }

    @Override
    public void tick() {
        if (this.entity.isRemoved()) {
            this.stop();
        } else {
            this.x = this.entity.getX();
            this.y = this.entity.getY();
            this.z = this.entity.getZ();
        }
    }

    @Nullable
    public SoundBuffer getSoundBuffer() {
        SoundCache soundCache = CustomSoundLoader.getSoundCache(voicePackId);
        if (soundCache != null) {
            return soundCache.getBuffer(this.location);
        }
        return null;
    }
}

