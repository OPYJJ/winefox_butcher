package com.yourname.maidfox.expansion.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.yourname.maidfox.expansion.MaidExpansion;
import com.yourname.maidfox.expansion.capability.MaidGeneCapabilityManager;
import com.yourname.maidfox.expansion.config.MaidExpansionConfig;
import com.yourname.maidfox.expansion.network.NetworkHandler;
import com.yourname.maidfox.expansion.network.message.SetMaidBabyMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = MaidExpansion.MOD_ID)
public final class MaidEvents {
    private MaidEvents() {
    }

    @SubscribeEvent
    @SuppressWarnings("removal")
    public static void onEntitySize(EntityEvent.Size event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityMaid) {
            MaidGeneCapabilityManager.get(entity).ifPresent(cap -> {
                if (cap.isBaby()) {
                    event.setNewSize(event.getNewSize().scale(0.5F));
                    event.setNewEyeHeight(event.getNewEyeHeight() * 0.5F);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof EntityMaid maid) || maid.level().isClientSide) {
            return;
        }
        MaidGeneCapabilityManager.get(maid).ifPresent(cap -> {
            if (cap.getAge() < 0) {
                int age = cap.getAge() + 1;
                cap.setAge(age);
                if (age >= 0) {
                    maid.refreshDimensions();
                    NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> maid),
                            new SetMaidBabyMessage(maid.getId(), false, 0));
                }
            }
            if (cap.getLoveTicks() > 0) {
                cap.setLoveTicks(cap.getLoveTicks() - 1);
            }
        });
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!MaidExpansionConfig.ENABLE_MAID_BREEDING.get()) {
            return;
        }
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        if (!(event.getTarget() instanceof EntityMaid maid)) {
            return;
        }
        Player player = (Player) event.getEntity();
        // Untamed maids are handled by the maid mod itself (cake taming flow); a tamed maid can be fed by any player, which lets maids of different owners breed
        if (!maid.isTame() || player instanceof FakePlayer) {
            return;
        }
        Item item = event.getItemStack().getItem();
        if (!isBreedFood(item)) {
            return;
        }
        boolean isClient = maid.level().isClientSide;
        MaidGeneCapabilityManager.get(maid).ifPresent(cap -> {
            if (cap.isBaby()) {
                // Baby: feeding speeds up growth (vanilla semantics: each feeding removes about 10% of the remaining time)
                if (!isClient) {
                    int remaining = -cap.getAge();
                    int reduce = Math.max(1, (int) ((remaining + 1) * 0.1F));
                    cap.setAge(Math.min(0, cap.getAge() + reduce));
                    if (!player.getAbilities().instabuild) {
                        event.getItemStack().shrink(1);
                    }
                    maid.level().broadcastEntityEvent(maid, (byte) 12);
                    maid.playSound(InitSounds.MAID_TAMED.get(), 1, 1);
                    NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> maid),
                            new SetMaidBabyMessage(maid.getId(), true, -cap.getAge()));
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            } else if (cap.getAge() >= 0 && !cap.isInLove()) {
                // Adult: feeding starts courtship
                if (!isClient) {
                    cap.setLoveTicks(MaidExpansionConfig.LOVE_TIMEOUT_TICKS.get());
                    if (!player.getAbilities().instabuild) {
                        event.getItemStack().shrink(1);
                    }
                    maid.level().broadcastEntityEvent(maid, (byte) 18);
                    maid.playSound(InitSounds.MAID_TAMED.get(), 1, 1);
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        });
    }

    public static boolean isBreedFood(Item item) {
        for (String id : MaidExpansionConfig.BREED_FOOD_LIST.get()) {
            Item food = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
            if (food != null && food == item) {
                return true;
            }
        }
        return false;
    }
}

