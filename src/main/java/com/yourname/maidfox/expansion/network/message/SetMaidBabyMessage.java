package com.yourname.maidfox.expansion.network.message;

import com.yourname.maidfox.expansion.client.ClientGeneCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetMaidBabyMessage {
    private final int entityId;
    private final boolean isBaby;
    private final int remainingTicks;

    public SetMaidBabyMessage(int entityId, boolean isBaby, int remainingTicks) {
        this.entityId = entityId;
        this.isBaby = isBaby;
        this.remainingTicks = remainingTicks;
    }

    public static void encode(SetMaidBabyMessage message, FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityId);
        buf.writeBoolean(message.isBaby);
        buf.writeVarInt(message.remainingTicks);
    }

    public static SetMaidBabyMessage decode(FriendlyByteBuf buf) {
        return new SetMaidBabyMessage(buf.readVarInt(), buf.readBoolean(), buf.readVarInt());
    }

    public static void handle(SetMaidBabyMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientGeneCache.setBaby(message.entityId, message.isBaby, message.remainingTicks));
        }
        context.setPacketHandled(true);
    }
}

