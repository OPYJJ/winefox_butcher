package com.yourname.maidfox.init;

import com.yourname.maidfox.init.ModItems;
import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public enum ModArmorMaterials implements ArmorMaterial
{
    WINEFOX_FUR_ARMOR("winefox_butcher:winefox_fur_armor", 15, new int[]{2, 5, 6, 2}, 9, SoundEvents.ARMOR_EQUIP_IRON, 1.0f, 0.0f, () -> Ingredient.of((ItemLike[])new ItemLike[]{ModItems.WINEFOX_FUR.get()})),
    WINEFOX_FUR_REINFORCED("winefox_butcher:winefox_fur_reinforced", 37, new int[]{3, 6, 8, 3}, 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 4.0f, 0.1f, () -> Ingredient.of((ItemLike[])new ItemLike[]{ModItems.WINEFOX_FUR.get()}));

    private static final int[] HEALTH_PER_SLOT;
    private final String name;
    private final int durabilityMultiplier;
    private final int[] slotProtections;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;

    private ModArmorMaterials(String name, int durabilityMultiplier, int[] slotProtections, int enchantmentValue, SoundEvent sound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.slotProtections = slotProtections;
        this.enchantmentValue = enchantmentValue;
        this.sound = sound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    public int getDurabilityForType(ArmorItem.Type type) {
        return HEALTH_PER_SLOT[type.getSlot().getIndex()] * this.durabilityMultiplier;
    }

    public int getDefenseForType(ArmorItem.Type type) {
        return this.slotProtections[type.getSlot().getIndex()];
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public SoundEvent getEquipSound() {
        return this.sound;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    public String getName() {
        return this.name;
    }

    public float getToughness() {
        return this.toughness;
    }

    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    static {
        HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
    }
}

