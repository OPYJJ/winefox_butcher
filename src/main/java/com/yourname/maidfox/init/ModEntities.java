package com.yourname.maidfox.init;

import com.yourname.maidfox.guillotine.GuillotineRestraint;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "winefox_butcher");
    public static final RegistryObject<EntityType<GuillotineRestraint>> GUILLOTINE_RESTRAINT = ENTITIES.register("guillotine_restraint",
            () -> EntityType.Builder.<GuillotineRestraint>of(GuillotineRestraint::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F).clientTrackingRange(8).updateInterval(1).build("guillotine_restraint"));

    private ModEntities() { }
    public static void register(IEventBus bus) { ENTITIES.register(bus); }
}
