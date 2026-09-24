package com.yourname.maidfox.delight.compat.farmersdelight;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;

public final class WinefoxDelightCompat {
    public static final String FD_MODID = "farmersdelight";

    private WinefoxDelightCompat() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(FD_MODID);
    }

    public static void registerIfLoaded(IEventBus bus) {
        if (isLoaded()) {
            FarmersDelightContent.register(bus);
        }
    }

    public static Item createStewItem() {
        return FarmersDelightContent.createStewItem();
    }

    public static void addCreativeTabItems(CreativeModeTab.Output output) {
        if (isLoaded()) {
            FarmersDelightContent.addCreativeTabItems(output);
        }
    }
}
