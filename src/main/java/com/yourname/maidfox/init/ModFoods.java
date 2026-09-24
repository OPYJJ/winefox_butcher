package com.yourname.maidfox.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties RAW_WINEFOX_MEAT = new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).meat().build();
    public static final FoodProperties COOKED_WINEFOX_MEAT = new FoodProperties.Builder().nutrition(8).saturationMod(0.8f).meat().build();
    public static final FoodProperties CUBED_WINEFOX_MEAT = new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).meat().build();
    public static final FoodProperties COOKED_CUBED_WINEFOX_MEAT = new FoodProperties.Builder().nutrition(4).saturationMod(0.8f).meat().build();
    public static final FoodProperties WINEFOX_HEART = new FoodProperties.Builder().nutrition(2).saturationMod(0.4f).meat().build();
    public static final FoodProperties WINEFOX_MINCED_MEAT = new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).meat().build();
    public static final FoodProperties WINEFOX_MEAT_PATTY = new FoodProperties.Builder().nutrition(4).saturationMod(0.8f).meat().fast().build();
    public static final FoodProperties WINEFOX_BURGER = new FoodProperties.Builder().nutrition(11).saturationMod(0.8f).build();
    public static final FoodProperties WINEFOX_BARBECUE_STICK = new FoodProperties.Builder().nutrition(8).saturationMod(0.9f).build();
    public static final FoodProperties WINEFOX_FRIED_RICE = new FoodProperties.Builder().nutrition(15).saturationMod(0.8f).build();
    public static final FoodProperties WINEFOX_STEW = new FoodProperties.Builder().nutrition(10).saturationMod(1.2f).effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 400, 1), 1.0f).effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3), 1.0f).effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0), 1.0f).effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0), 1.0f).build();
    public static final FoodProperties WINEFOX_SUSHI_ROLL = new FoodProperties.Builder().nutrition(14).saturationMod(0.8f).build();
    public static final FoodProperties WINEFOX_SUSHI = new FoodProperties.Builder().nutrition(7).saturationMod(0.6f).fast().build();
}
