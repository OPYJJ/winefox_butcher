package com.yourname.maidfox.expansion.capability;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.yourname.maidfox.expansion.MaidExpansion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MaidExpansion.MOD_ID)
public final class MaidGeneCapabilityManager {
    private static final ResourceLocation CAP_KEY = new ResourceLocation(MaidExpansion.MOD_ID, "maid_gene");

    private MaidGeneCapabilityManager() {
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IMaidGeneCapability.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof Villager || entity instanceof EntityMaid || entity instanceof Monster) {
            event.addCapability(CAP_KEY, new MaidGeneCapabilityProvider());
        }
    }

    public static LazyOptional<IMaidGeneCapability> get(Entity entity) {
        return entity.getCapability(MaidGeneCapabilityProvider.MAID_GENE_CAP);
    }
}

