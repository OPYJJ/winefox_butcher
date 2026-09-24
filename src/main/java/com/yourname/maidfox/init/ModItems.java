package com.yourname.maidfox.init;

import com.yourname.maidfox.delight.compat.farmersdelight.WinefoxDelightCompat;
import com.yourname.maidfox.delight.item.WinefoxSushiRollItem;
import com.yourname.maidfox.init.ModArmorMaterials;
import com.yourname.maidfox.init.ModFoods;
import com.yourname.maidfox.item.SoulAmuletItem;
import com.yourname.maidfox.item.SoulCrystalSwordItem;
import com.yourname.maidfox.item.WinefoxCarcassItem;
import com.yourname.maidfox.item.WinefoxCarcassStageItem;
import com.yourname.maidfox.item.WinefoxFurBedItem;
import com.yourname.maidfox.item.WinefoxHeadItem;
import com.yourname.maidfox.item.WinefoxTailItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.ModList;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "winefox_butcher");
    public static final RegistryObject<Item> WINEFOX_CARCASS = ITEMS.register("winefox_carcass", () -> new WinefoxCarcassItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> WINEFOX_HEAD_ITEM = ITEMS.register("winefox_head_item", () -> new WinefoxHeadItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> WINEFOX_CARCASS_STAGE1 = ITEMS.register("winefox_carcass_stage1", () -> new WinefoxCarcassStageItem(new Item.Properties().stacksTo(1), 1));
    public static final RegistryObject<Item> WINEFOX_CARCASS_STAGE2 = ITEMS.register("winefox_carcass_stage2", () -> new WinefoxCarcassStageItem(new Item.Properties().stacksTo(1), 2));
    public static final RegistryObject<Item> WINEFOX_CARCASS_STAGE3 = ITEMS.register("winefox_carcass_stage3", () -> new WinefoxCarcassStageItem(new Item.Properties().stacksTo(1), 3));
    public static final RegistryObject<Item> WINEFOX_CARCASS_STAGE4 = ITEMS.register("winefox_carcass_stage4", () -> new WinefoxCarcassStageItem(new Item.Properties().stacksTo(1), 4));
    public static final RegistryObject<Item> WINEFOX_TAIL = ITEMS.register("winefox_tail", () -> new WinefoxTailItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> WINEFOX_FUR = ITEMS.register("winefox_fur", () -> new Item(new Item.Properties()));
    public static final RegistryObject<WinefoxFurBedItem> WINEFOX_FUR_BED_ITEM = ITEMS.register("winefox_fur_bed", () -> new WinefoxFurBedItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RAW_WINEFOX_MEAT = ITEMS.register("raw_winefox_meat", () -> new Item(new Item.Properties().food(ModFoods.RAW_WINEFOX_MEAT)));
    public static final RegistryObject<Item> COOKED_WINEFOX_MEAT = ITEMS.register("cooked_winefox_meat", () -> new Item(new Item.Properties().food(ModFoods.COOKED_WINEFOX_MEAT)));
    public static final RegistryObject<Item> CUBED_WINEFOX_MEAT = ITEMS.register("cubed_winefox_meat", () -> new Item(new Item.Properties().food(ModFoods.CUBED_WINEFOX_MEAT)));
    public static final RegistryObject<Item> COOKED_CUBED_WINEFOX_MEAT = ITEMS.register("cooked_cubed_winefox_meat", () -> new Item(new Item.Properties().food(ModFoods.COOKED_CUBED_WINEFOX_MEAT)));
    public static final RegistryObject<Item> WINEFOX_MINCED_MEAT = ITEMS.register("winefox_minced_meat", () -> new Item(new Item.Properties().food(ModFoods.WINEFOX_MINCED_MEAT)));
    public static final RegistryObject<Item> WINEFOX_MEAT_PATTY = ITEMS.register("winefox_meat_patty", () -> new Item(new Item.Properties().food(ModFoods.WINEFOX_MEAT_PATTY)));
    public static final RegistryObject<Item> WINEFOX_BURGER = ITEMS.register("winefox_burger", () -> new Item(new Item.Properties().food(ModFoods.WINEFOX_BURGER)));
    public static final RegistryObject<Item> WINEFOX_BARBECUE_STICK = ITEMS.register("winefox_barbecue_stick", () -> new Item(new Item.Properties().food(ModFoods.WINEFOX_BARBECUE_STICK)));
    public static final RegistryObject<Item> WINEFOX_FRIED_RICE = ITEMS.register("winefox_fried_rice", () -> new Item(new Item.Properties().food(ModFoods.WINEFOX_FRIED_RICE)));
    public static final RegistryObject<Item> WINEFOX_STEW = ITEMS.register("winefox_stew", () -> ModList.get().isLoaded("farmersdelight")
            ? WinefoxDelightCompat.createStewItem()
            : new Item(new Item.Properties().food(ModFoods.WINEFOX_STEW)));
    public static final RegistryObject<Item> WINEFOX_SUSHI_ROLL = ITEMS.register("winefox_sushi_roll", () -> new WinefoxSushiRollItem(new Item.Properties().food(ModFoods.WINEFOX_SUSHI_ROLL)));
    public static final RegistryObject<Item> WINEFOX_SUSHI = ITEMS.register("winefox_sushi", () -> new Item(new Item.Properties().food(ModFoods.WINEFOX_SUSHI)));
    public static final RegistryObject<Item> WINEFOX_HEART = ITEMS.register("winefox_heart", () -> new Item(new Item.Properties().food(ModFoods.WINEFOX_HEART)));
    public static final RegistryObject<Item> SPIRIT_ESSENCE = ITEMS.register("spirit_essence", () -> new Item(new Item.Properties()));
    public static final RegistryObject<SoulCrystalSwordItem> SOUL_CRYSTAL_SWORD = ITEMS.register("soul_crystal_sword", () -> new SoulCrystalSwordItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<SoulAmuletItem> SOUL_AMULET = ITEMS.register("soul_amulet", () -> new SoulAmuletItem(new Item.Properties()));
    public static final RegistryObject<Item> WINEFOX_FUR_HELMET = ITEMS.register("winefox_fur_helmet", () -> new ArmorItem(ModArmorMaterials.WINEFOX_FUR_ARMOR, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> WINEFOX_FUR_CHESTPLATE = ITEMS.register("winefox_fur_chestplate", () -> new ArmorItem(ModArmorMaterials.WINEFOX_FUR_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> WINEFOX_FUR_LEGGINGS = ITEMS.register("winefox_fur_leggings", () -> new ArmorItem(ModArmorMaterials.WINEFOX_FUR_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> WINEFOX_FUR_BOOTS = ITEMS.register("winefox_fur_boots", () -> new ArmorItem(ModArmorMaterials.WINEFOX_FUR_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties()));
    public static final RegistryObject<Item> WINEFOX_FUR_REINFORCED_HELMET = ITEMS.register("winefox_fur_reinforced_helmet", () -> new ArmorItem(ModArmorMaterials.WINEFOX_FUR_REINFORCED, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> WINEFOX_FUR_REINFORCED_CHESTPLATE = ITEMS.register("winefox_fur_reinforced_chestplate", () -> new ArmorItem(ModArmorMaterials.WINEFOX_FUR_REINFORCED, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> WINEFOX_FUR_REINFORCED_LEGGINGS = ITEMS.register("winefox_fur_reinforced_leggings", () -> new ArmorItem(ModArmorMaterials.WINEFOX_FUR_REINFORCED, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> WINEFOX_FUR_REINFORCED_BOOTS = ITEMS.register("winefox_fur_reinforced_boots", () -> new ArmorItem(ModArmorMaterials.WINEFOX_FUR_REINFORCED, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
