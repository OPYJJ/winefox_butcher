package com.yourname.maidfox.expansion.client;

import com.yourname.maidfox.expansion.MaidExpansion;
import com.yourname.maidfox.expansion.client.renderer.VillagerMaidRenderer;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MaidExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientRegister {
    private ClientRegister() {
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityType.VILLAGER, ctx -> new VillagerMaidRenderer(ctx, new VillagerRenderer(ctx)));
    }
}

