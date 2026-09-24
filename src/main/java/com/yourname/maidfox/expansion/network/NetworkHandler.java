package com.yourname.maidfox.expansion.network;

import com.yourname.maidfox.expansion.MaidExpansion;
import com.yourname.maidfox.expansion.network.message.PlayVillagerVoiceMessage;
import com.yourname.maidfox.expansion.network.message.SetMaidBabyMessage;
import com.yourname.maidfox.expansion.network.message.SyncVillagerGeneMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class NetworkHandler {
    private static final String VERSION = "1.0";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MaidExpansion.MOD_ID, "network"),
            () -> VERSION, VERSION::equals, VERSION::equals);

    private NetworkHandler() {
    }

    public static void init() {
        int id = 0;
        CHANNEL.registerMessage(id++, SyncVillagerGeneMessage.class,
                SyncVillagerGeneMessage::encode, SyncVillagerGeneMessage::decode, SyncVillagerGeneMessage::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++, SetMaidBabyMessage.class,
                SetMaidBabyMessage::encode, SetMaidBabyMessage::decode, SetMaidBabyMessage::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++, PlayVillagerVoiceMessage.class,
                PlayVillagerVoiceMessage::encode, PlayVillagerVoiceMessage::decode, PlayVillagerVoiceMessage::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}

