package com.yourname.maidfox.expansion.client;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.AbstractMaidContainer;
import com.yourname.maidfox.expansion.MaidExpansion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MaidExpansion.MOD_ID, value = Dist.CLIENT)
public final class RenderEvents {
    private RenderEvents() {
    }

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        if (event.getEntity() instanceof EntityMaid && ClientGeneCache.isBaby(event.getEntity().getId())) {
            event.getPoseStack().scale(0.5F, 0.5F, 0.5F);
        }
    }

    @SubscribeEvent
    public static void onClientLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientGeneCache.clearAll();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientGeneCache.tickRemaining();
        }
    }

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> containerScreen)) {
            return;
        }
        if (!(containerScreen.getMenu() instanceof AbstractMaidContainer maidContainer)) {
            return;
        }
        EntityMaid maid = maidContainer.getMaid();
        if (maid == null) {
            return;
        }
        int remaining = ClientGeneCache.getRemainingTicks(maid.getId());
        if (remaining <= 0) {
            return;
        }
        int seconds = Math.max(1, (int) Math.ceil(remaining / 20.0F));
        Component text = Component.translatable("gui.winefox_butcher.baby_growth_remaining", formatSeconds(seconds));
        Font font = Minecraft.getInstance().font;
        int x = (containerScreen.width - font.width(text)) / 2;
        event.getGuiGraphics().drawString(font, text, x, 10, 0xFFFF55, true);
    }

    private static String formatSeconds(int seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }
}

