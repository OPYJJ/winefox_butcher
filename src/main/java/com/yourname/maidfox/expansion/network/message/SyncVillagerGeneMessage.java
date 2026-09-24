package com.yourname.maidfox.expansion.network.message;

import com.yourname.maidfox.expansion.client.ClientGeneCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncVillagerGeneMessage {
    private final int entityId;
    private final String modelId;
    private final String voicePackId;

    public SyncVillagerGeneMessage(int entityId, String modelId, String voicePackId) {
        this.entityId = entityId;
        this.modelId = modelId;
        this.voicePackId = voicePackId;
    }

    public static void encode(SyncVillagerGeneMessage message, FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityId);
        buf.writeUtf(message.modelId);
        buf.writeUtf(message.voicePackId);
    }

    public static SyncVillagerGeneMessage decode(FriendlyByteBuf buf) {
        return new SyncVillagerGeneMessage(buf.readVarInt(), buf.readUtf(), buf.readUtf());
    }

    public static void handle(SyncVillagerGeneMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientGeneCache.putVillagerGene(message.entityId, message.modelId, message.voicePackId));
        }
        context.setPacketHandled(true);
    }
}

