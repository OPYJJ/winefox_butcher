package com.yourname.maidfox.init;

import com.yourname.maidfox.delight.compat.farmersdelight.WinefoxDelightCompat;
import com.yourname.maidfox.init.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "winefox_butcher");
    public static final RegistryObject<CreativeModeTab> WINEFOX_BUTCHER_TAB = CREATIVE_MODE_TABS.register("winefox_butcher_tab", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.winefox_butcher")).icon(() -> new ItemStack(ModItems.WINEFOX_CARCASS.get())).displayItems((params, output) -> {
        output.accept(ModItems.WINEFOX_CARCASS.get());
        output.accept(ModItems.WINEFOX_HEAD_ITEM.get());
        output.accept(ModItems.WINEFOX_HEART.get());
        output.accept(ModItems.SPIRIT_ESSENCE.get());
        output.accept(ModItems.SOUL_CRYSTAL_SWORD.get());
        output.accept(ModItems.SOUL_AMULET.get());
        output.accept(ModItems.WINEFOX_TAIL.get());
        output.accept(ModItems.WINEFOX_FUR.get());
        output.accept(ModItems.WINEFOX_FUR_HELMET.get());
        output.accept(ModItems.WINEFOX_FUR_CHESTPLATE.get());
        output.accept(ModItems.WINEFOX_FUR_LEGGINGS.get());
        output.accept(ModItems.WINEFOX_FUR_BOOTS.get());
        output.accept(ModItems.WINEFOX_FUR_REINFORCED_HELMET.get());
        output.accept(ModItems.WINEFOX_FUR_REINFORCED_CHESTPLATE.get());
        output.accept(ModItems.WINEFOX_FUR_REINFORCED_LEGGINGS.get());
        output.accept(ModItems.WINEFOX_FUR_REINFORCED_BOOTS.get());
        output.accept(ModItems.WINEFOX_FUR_BED_ITEM.get());
        output.accept(ModItems.RAW_WINEFOX_MEAT.get());
        output.accept(ModItems.COOKED_WINEFOX_MEAT.get());
        output.accept(ModItems.CUBED_WINEFOX_MEAT.get());
        output.accept(ModItems.COOKED_CUBED_WINEFOX_MEAT.get());
        if (ModList.get().isLoaded("farmersdelight")) {
            output.accept(ModItems.WINEFOX_MINCED_MEAT.get());
            output.accept(ModItems.WINEFOX_MEAT_PATTY.get());
            output.accept(ModItems.WINEFOX_BURGER.get());
            output.accept(ModItems.WINEFOX_BARBECUE_STICK.get());
            output.accept(ModItems.WINEFOX_FRIED_RICE.get());
            output.accept(ModItems.WINEFOX_STEW.get());
            output.accept(ModItems.WINEFOX_SUSHI_ROLL.get());
            output.accept(ModItems.WINEFOX_SUSHI.get());
            WinefoxDelightCompat.addCreativeTabItems(output);
        }
    }).build());

    public static void register(IEventBus bus) {
        CREATIVE_MODE_TABS.register(bus);
    }
}
