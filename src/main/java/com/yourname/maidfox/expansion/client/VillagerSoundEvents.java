package com.yourname.maidfox.expansion.client;

import com.yourname.maidfox.expansion.MaidExpansion;
import com.yourname.maidfox.expansion.client.sound.VillagerMaidSoundInstance;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Recreates the mechanism of the maid mod's PlayMaidSoundEvent: attaches a voice pack buffer
 * to the sound channel of villagers / mutated mobs.
 */
@Mod.EventBusSubscriber(modid = MaidExpansion.MOD_ID, value = Dist.CLIENT)
public final class VillagerSoundEvents {
    private VillagerSoundEvents() {
    }

    @SubscribeEvent
    public static void onPlaySoundSource(PlaySoundSourceEvent event) {
        if (event.getSound() instanceof VillagerMaidSoundInstance instance) {
            SoundBuffer soundBuffer = instance.getSoundBuffer();
            if (soundBuffer != null) {
                event.getChannel().attachStaticBuffer(soundBuffer);
                event.getChannel().play();
            }
        }
    }
}

