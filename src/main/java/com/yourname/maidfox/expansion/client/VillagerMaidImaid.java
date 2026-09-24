package com.yourname.maidfox.expansion.client;

import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * IMaid wrapper for villagers: lets the maid mod's animation engine (BedrockModel.setupAnim)
 * and the gecko renderer recognise villagers. The model id is read lazily from the client gene
 * cache at render time and does not depend on when the capability is attached.
 */
@OnlyIn(Dist.CLIENT)
public class VillagerMaidImaid implements IMaid {
    private final Villager villager;

    public VillagerMaidImaid(Villager villager) {
        this.villager = villager;
    }

    @Override
    public String getModelId() {
        ClientGeneCache.GeneData gene = ClientGeneCache.getVillagerGene(villager.getId());
        return gene != null ? gene.modelId() : "";
    }

    @Override
    public Mob asEntity() {
        return villager;
    }
}

