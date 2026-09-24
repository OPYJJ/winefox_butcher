package com.yourname.maidfox;

import com.yourname.maidfox.FoxButcherEvents;
import com.yourname.maidfox.delight.compat.farmersdelight.WinefoxDelightCompat;
import com.yourname.maidfox.expansion.capability.MaidGeneCapabilityManager;
import com.yourname.maidfox.expansion.config.MaidExpansionConfig;
import com.yourname.maidfox.expansion.network.NetworkHandler;
import com.yourname.maidfox.init.ModBlocks;
import com.yourname.maidfox.init.ModCreativeTabs;
import com.yourname.maidfox.init.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value="winefox_butcher")
public class WinefoxMaidButcher {
    public static final String MODID = "winefox_butcher";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public WinefoxMaidButcher(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        WinefoxDelightCompat.registerIfLoaded(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MaidExpansionConfig.COMMON, "winefox_butcher-common.toml");
        modEventBus.addListener(MaidGeneCapabilityManager::registerCapabilities);
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(FoxButcherEvents.class);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkHandler::init);
    }
}
