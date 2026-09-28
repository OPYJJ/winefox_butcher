package com.yourname.maidfox.client;

import com.yourname.maidfox.client.renderer.WinefoxFurBedBlockRenderer;
import com.yourname.maidfox.client.renderer.WinefoxHeadBlockRenderer;
import com.yourname.maidfox.client.renderer.WinefoxRoastFoxBlockRenderer;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.yourname.maidfox.delight.compat.farmersdelight.FarmersDelightContent;
import com.yourname.maidfox.guillotine.client.GuillotineBlockRenderer;
import com.yourname.maidfox.guillotine.client.GuillotineMaidRenderer;
import com.yourname.maidfox.guillotine.client.GuillotineRestraintRenderer;
import com.yourname.maidfox.init.ModBlocks;
import com.yourname.maidfox.init.ModEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import software.bernie.geckolib.GeckoLib;

@Mod.EventBusSubscriber(modid="winefox_butcher", bus=Mod.EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        GeckoLib.initialize();
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlocks.WINEFOX_HEAD_BLOCK_ENTITY.get(), ctx -> new WinefoxHeadBlockRenderer());
        event.registerBlockEntityRenderer(ModBlocks.WINEFOX_FUR_BED_BLOCK_ENTITY.get(), ctx -> new WinefoxFurBedBlockRenderer());
        event.registerBlockEntityRenderer(ModBlocks.GUILLOTINE_BLOCK_ENTITY.get(), ctx -> new GuillotineBlockRenderer());
        event.registerEntityRenderer(ModEntities.GUILLOTINE_RESTRAINT.get(), GuillotineRestraintRenderer::new);
        event.registerEntityRenderer(EntityMaid.TYPE, GuillotineMaidRenderer::new);
        // The roast fox is only registered when Farmer's Delight is present
        if (ModList.get().isLoaded("farmersdelight")) {
            event.registerBlockEntityRenderer(FarmersDelightContent.WINEFOX_ROAST_FOX_BLOCK_ENTITY.get(),
                    ctx -> new WinefoxRoastFoxBlockRenderer());
        }
    }
}
