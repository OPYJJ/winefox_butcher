package com.yourname.maidfox.expansion.network.message;

import com.yourname.maidfox.expansion.client.sound.VillagerMaidSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class PlayVillagerVoiceMessage {
    private final int entityId;
    private final ResourceLocation soundEvent;
    private final String voicePackId;

    public PlayVillagerVoiceMessage(int entityId, ResourceLocation soundEvent, String voicePackId) {
        this.entityId = entityId;
        this.soundEvent = soundEvent;
        this.voicePackId = voicePackId;
    }

    public static void encode(PlayVillagerVoiceMessage message, FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityId);
        buf.writeResourceLocation(message.soundEvent);
        buf.writeUtf(message.voicePackId);
    }

    public static PlayVillagerVoiceMessage decode(FriendlyByteBuf buf) {
        return new PlayVillagerVoiceMessage(buf.readVarInt(), buf.readResourceLocation(), buf.readUtf());
    }

    public static void handle(PlayVillagerVoiceMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> playSound(message));
        }
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void playSound(PlayVillagerVoiceMessage message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        Entity entity = mc.level.getEntity(message.entityId);
        if (entity == null) {
            return;
        }
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(message.soundEvent);
        if (soundEvent != null) {
            mc.getSoundManager().play(new VillagerMaidSoundInstance(soundEvent, message.voicePackId, entity));
        }
    }
}

