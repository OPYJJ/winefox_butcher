package com.yourname.maidfox.delight.compat.farmersdelight;

import com.yourname.maidfox.init.ModBlocks;
import com.yourname.maidfox.init.ModFoods;
import com.yourname.maidfox.init.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.item.ConsumableItem;
import vectorwing.farmersdelight.common.item.PlaceableItem;
import vectorwing.farmersdelight.common.registry.ModEffects;

public final class FarmersDelightContent {
    public static final FoodProperties WINEFOX_MEAT_SAUCE_PASTA_FOOD = new FoodProperties.Builder()
            .nutrition(12)
            .saturationMod(0.75f)
            .effect(() -> new MobEffectInstance(ModEffects.NOURISHMENT.get(), 6000, 0, false, false), 1.0f)
            .build();
    public static final FoodProperties WINEFOX_STEW_BOWL_FOOD = new FoodProperties.Builder()
            .nutrition(16)
            .saturationMod(0.625f)
            .effect(() -> new MobEffectInstance(ModEffects.NOURISHMENT.get(), 6000, 0, false, false), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 2400, 1), 1.0f)
            .build();

    public static RegistryObject<Item> WINEFOX_MEAT_SAUCE_PASTA;
    public static RegistryObject<Item> WINEFOX_STEW_BOWL;
    public static RegistryObject<Block> WINEFOX_MEAT_POT;
    public static RegistryObject<Item> WINEFOX_MEAT_POT_ITEM;
    public static RegistryObject<Block> WINEFOX_ROAST_FOX;
    public static RegistryObject<BlockEntityType<WinefoxRoastFoxBlockEntity>> WINEFOX_ROAST_FOX_BLOCK_ENTITY;
    public static RegistryObject<Item> WINEFOX_ROAST_FOX_ITEM;

    private FarmersDelightContent() {
    }

    public static void register(IEventBus bus) {
        WINEFOX_MEAT_SAUCE_PASTA = ModItems.ITEMS.register("winefox_meat_sauce_pasta",
                () -> new ConsumableItem(new Item.Properties()
                        .food(WINEFOX_MEAT_SAUCE_PASTA_FOOD)
                        .craftRemainder(Items.BOWL)
                        .stacksTo(16)));
        WINEFOX_STEW_BOWL = ModItems.ITEMS.register("winefox_stew_bowl",
                () -> new ConsumableItem(new Item.Properties()
                        .food(WINEFOX_STEW_BOWL_FOOD)
                        .craftRemainder(Items.BOWL)
                        .stacksTo(16)));
        WINEFOX_MEAT_POT = ModBlocks.BLOCKS.register("winefox_meat_pot",
                () -> new WinefoxMeatPotBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .strength(2.0f, 6.0f)));
        WINEFOX_MEAT_POT_ITEM = ModItems.ITEMS.register("winefox_meat_pot",
                () -> new PlaceableItem(WINEFOX_MEAT_POT.get(), new Item.Properties().stacksTo(1)));

        // Roast fox: a two-block cake-style feast block rendered by GeckoLib in 6 stages
        WINEFOX_ROAST_FOX = ModBlocks.BLOCKS.register("winefox_roast_fox",
                () -> new WinefoxRoastFoxBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .strength(0.5F)
                        .sound(SoundType.WOOD)
                        .noOcclusion()));
        WINEFOX_ROAST_FOX_BLOCK_ENTITY = ModBlocks.BLOCK_ENTITIES.register("winefox_roast_fox",
                () -> BlockEntityType.Builder.of(WinefoxRoastFoxBlockEntity::new, WINEFOX_ROAST_FOX.get()).build(null));
        WINEFOX_ROAST_FOX_ITEM = ModItems.ITEMS.register("winefox_roast_fox",
                () -> new WinefoxRoastFoxItem((WinefoxRoastFoxBlock) WINEFOX_ROAST_FOX.get(),
                        new Item.Properties().stacksTo(1)));
    }

    public static Item createStewItem() {
        return new ConsumableItem(new Item.Properties()
                .food(ModFoods.WINEFOX_STEW)
                .craftRemainder(Items.BOWL)
                .stacksTo(16));
    }

    public static void addCreativeTabItems(CreativeModeTab.Output output) {
        output.accept(WINEFOX_MEAT_SAUCE_PASTA.get());
        output.accept(WINEFOX_STEW_BOWL.get());
        output.accept(WINEFOX_MEAT_POT_ITEM.get());
        output.accept(WINEFOX_ROAST_FOX_ITEM.get());
    }
}
