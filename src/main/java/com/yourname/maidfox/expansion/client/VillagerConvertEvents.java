package com.yourname.maidfox.expansion.client;

import com.github.tartaricacid.touhoulittlemaid.api.event.ConvertMaidEvent;
import com.yourname.maidfox.expansion.MaidExpansion;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Client-side conversion of villagers into IMaid: the maid mod itself calls IMaid.convert when
 * it attaches the gecko capability to a Mob on the client, so this has to take effect as early
 * as possible on the client (when the villager entity is constructed); the wrapper reads the
 * gene cache lazily.
 */
@Mod.EventBusSubscriber(modid = MaidExpansion.MOD_ID, value = Dist.CLIENT)
public final class VillagerConvertEvents {
    private VillagerConvertEvents() {
    }

    @SubscribeEvent
    public static void onConvertMaid(ConvertMaidEvent event) {
        if (event.getEntity() instanceof Villager villager && villager.level().isClientSide) {
            event.setMaid(new VillagerMaidImaid(villager));
        }
    }
}

