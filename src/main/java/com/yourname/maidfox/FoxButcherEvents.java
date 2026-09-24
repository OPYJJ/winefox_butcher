package com.yourname.maidfox;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidDeathEvent;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidTombstoneEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.yourname.maidfox.init.ModItems;
import com.yourname.maidfox.expansion.capability.IMaidGeneCapability;
import com.yourname.maidfox.expansion.capability.MaidGeneCapabilityManager;
import com.yourname.maidfox.item.WinefoxCarcassItem;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FoxButcherEvents {
    private static final Logger LOGGER = LoggerFactory.getLogger("WinefoxButcher");
    private static final ResourceLocation BUTCHER_KNIFE = new ResourceLocation("butchercraft", "butcher_knife");
    private static final String LOVE_LOATHE_MODID = "callresponse";
    private static final double BUTCHER_PLAYER_RADIUS = 4.0;
    private static final String KEY_BUTCHERED = "winefox_butchered";
    private static final String KEY_CARCASS_DROPPED = "winefox_carcass_dropped";
    private static final String KEY_MEAT_DROPPED = "winefox_meat_dropped";
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void onMaidDeathEarly(MaidDeathEvent event) {
        if (event.isCanceled()) {
            return;
        }
        FoxButcherEvents.handleMaidButchered(event.getMaid(), event.getSource());
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void onMaidDeath(LivingDeathEvent event) {
        if (event.isCanceled()) {
            return;
        }
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof EntityMaid) {
            EntityMaid maid = (EntityMaid)livingEntity;
            FoxButcherEvents.handleMaidButchered(maid, event.getSource());
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void onMaidTombstone(MaidTombstoneEvent event) {
        if (event.getMaid().getPersistentData().getBoolean(KEY_BUTCHERED)) {
            event.setCanceled(true);
            LOGGER.info("Cancelled tombstone for butchered winefox maid");
        }
    }

    private static void handleMaidButchered(EntityMaid maid, DamageSource source) {
        if (maid.level().isClientSide()) {
            return;
        }
        if (!FoxButcherEvents.isWinefoxMaid(maid)) {
            LOGGER.info("Maid died with modelId={} (ysm={}), not a winefox model - no carcass drop", maid.getModelId(), (maid.isYsmModel() ? maid.getYsmModelId() : "-"));
            return;
        }
        if (FoxButcherEvents.isBabyMaid(maid)) {
            LOGGER.info("Baby winefox maid died - baby maids are excluded from butchering, no carcass drop");
            return;
        }
        boolean butcherKnifeInvolved = FoxButcherEvents.isDirectButcherKnifeKill(source);
        if (FoxButcherEvents.isLoveLoatheLoaded()) {
            butcherKnifeInvolved = butcherKnifeInvolved
                    || FoxButcherEvents.isUnattributedKill(source) && FoxButcherEvents.isOwnerOrNearbyButcherKnifePlayer(maid);
        }
        if (!butcherKnifeInvolved) {
            if (source != null && source.getEntity() instanceof Player) {
                FoxButcherEvents.dropRawWinefoxMeat(maid);
            } else if (FoxButcherEvents.isLoveLoatheLoaded()) {
                LOGGER.info("Winefox maid died (Love & Loathing active) from source {} - no butcher-knife kill or owner/nearby butcher-knife player - no carcass drop", (source == null ? "null" : source.getMsgId()));
            } else {
                Entity attacker = source == null ? null : source.getEntity();
                LOGGER.info("Winefox maid killed by {} (no butcher knife) - no carcass drop", (attacker == null ? "unknown source" : attacker.toString()));
            }
            return;
        }
        maid.getPersistentData().putBoolean(KEY_BUTCHERED, true);
        if (maid.getPersistentData().getBoolean(KEY_CARCASS_DROPPED)) {
            return;
        }
        maid.getPersistentData().putBoolean(KEY_CARCASS_DROPPED, true);
        ItemStack carcassStack = WinefoxCarcassItem.createFromMaid(maid);
        Level level = maid.level();
        ItemEntity itemEntity = new ItemEntity(level, maid.getX(), maid.getY(), maid.getZ(), carcassStack);
        level.addFreshEntity(itemEntity);
        int tailCount = FoxButcherEvents.isNineTailedMaid(maid) ? 9 : 1;
        ItemStack tailStack = new ItemStack(ModItems.WINEFOX_TAIL.get());
        tailStack.setCount(tailCount);
        ItemEntity tailEntity = new ItemEntity(level, maid.getX(), maid.getY(), maid.getZ(), tailStack);
        level.addFreshEntity(tailEntity);
        LOGGER.info("Butchered winefox maid at ({}/{}/{})", new Object[]{maid.getBlockX(), maid.getBlockY(), maid.getBlockZ()});
    }

    private static boolean isHoldingButcherKnife(LivingEntity attacker) {
        return FoxButcherEvents.isButcherKnife(attacker.getMainHandItem()) || FoxButcherEvents.isButcherKnife(attacker.getItemInHand(InteractionHand.OFF_HAND));
    }

    private static boolean isUnattributedKill(DamageSource source) {
        return source != null && source.getEntity() == null && source.getDirectEntity() == null && source.is(DamageTypes.GENERIC);
    }

    private static boolean isDirectButcherKnifeKill(DamageSource source) {
        Entity attacker = source == null ? null : source.getEntity();
        return attacker instanceof LivingEntity living && FoxButcherEvents.isHoldingButcherKnife(living);
    }

    private static void dropRawWinefoxMeat(EntityMaid maid) {
        if (maid.getPersistentData().getBoolean(KEY_MEAT_DROPPED)) {
            return;
        }
        maid.getPersistentData().putBoolean(KEY_MEAT_DROPPED, true);
        Level level = maid.level();
        int count = 1 + level.random.nextInt(2);
        ItemStack meatStack = new ItemStack(ModItems.RAW_WINEFOX_MEAT.get(), count);
        ItemEntity itemEntity = new ItemEntity(level, maid.getX(), maid.getY() + 0.5, maid.getZ(), meatStack);
        level.addFreshEntity(itemEntity);
        LOGGER.info("Dropped {} raw winefox meat for non-butcher-knife player kill", count);
    }

    private static boolean isOwnerOrNearbyButcherKnifePlayer(EntityMaid maid) {
        Player owner;
        UUID ownerId = maid.getOwnerUUID();
        if (ownerId != null && (owner = maid.level().getPlayerByUUID(ownerId)) != null && FoxButcherEvents.isHoldingButcherKnife(owner)) {
            return true;
        }
        return FoxButcherEvents.isNearbyButcherKnifePlayer(maid, BUTCHER_PLAYER_RADIUS);
    }

    private static boolean isNearbyButcherKnifePlayer(EntityMaid maid, double radius) {
        double radiusSq = radius * radius;
        for (Player player : maid.level().players()) {
            if (!(player.distanceToSqr(maid) <= radiusSq) || !FoxButcherEvents.isHoldingButcherKnife(player)) continue;
            return true;
        }
        return false;
    }

    private static boolean isLoveLoatheLoaded() {
        return ModList.get().isLoaded(LOVE_LOATHE_MODID);
    }

    private static boolean isButcherKnife(ItemStack held) {
        if (held.isEmpty()) {
            return false;
        }
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(held.getItem());
        return BUTCHER_KNIFE.equals(key);
    }

    private static boolean isWinefoxMaid(EntityMaid maid) {
        if (FoxButcherEvents.containsWinefox(maid.getModelId())) {
            return true;
        }
        if (maid.isYsmModel()) {
            return FoxButcherEvents.containsWinefox(maid.getYsmModelId());
        }
        return false;
    }

    /**
     * Only adult winefox maids take part in butchering: baby maids (negative age in the gene
     * capability) are excluded, so killing them never drops a carcass, tail or raw meat.
     * Maids without the expansion capability count as adults.
     */
    private static boolean isBabyMaid(EntityMaid maid) {
        return MaidGeneCapabilityManager.get(maid).map(IMaidGeneCapability::isBaby).orElse(false);
    }

    private static boolean isNineTailedMaid(EntityMaid maid) {
        if (FoxButcherEvents.containsNineTailed(maid.getModelId())) {
            return true;
        }
        return maid.isYsmModel() && FoxButcherEvents.containsNineTailed(maid.getYsmModelId());
    }

    private static boolean containsNineTailed(String id) {
        return id != null && id.toLowerCase(Locale.ROOT).contains("nine_tailed");
    }

    private static boolean containsWinefox(String id) {
        if (id == null) {
            return false;
        }
        String lower = id.toLowerCase(Locale.ROOT);
        return lower.contains("winefox") || lower.contains("wine_fox");
    }

    @SubscribeEvent
    public static void onEntityStruckByLightning(EntityStruckByLightningEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        if (player.level().isClientSide()) {
            return;
        }
        FoxButcherEvents.convertWinefoxFurArmor(player);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Player player = event.player;
        if (player.level().isClientSide()) {
            return;
        }
        if (FoxButcherEvents.countReinforcedArmor(player) >= 4) {
            FoxButcherEvents.applySetEffect(player, MobEffects.DAMAGE_RESISTANCE, 2);
            FoxButcherEvents.applySetEffect(player, MobEffects.DAMAGE_BOOST, 1);
        } else {
            FoxButcherEvents.removeSetEffect(player, MobEffects.DAMAGE_RESISTANCE);
            FoxButcherEvents.removeSetEffect(player, MobEffects.DAMAGE_BOOST);
        }
    }

    private static void convertWinefoxFurArmor(Player player) {
        boolean converted = false;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            Item replacement = FoxButcherEvents.getReinforcedReplacement(stack.getItem());
            if (replacement == null) continue;
            ItemStack newStack = new ItemStack(replacement);
            float ratio = 1.0f - (float)stack.getDamageValue() / (float)stack.getMaxDamage();
            int newDamage = Math.round((1.0f - ratio) * (float)newStack.getMaxDamage());
            newStack.setDamageValue(Math.min(Math.max(newDamage, 0), newStack.getMaxDamage() - 1));
            EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments((ItemStack)stack), (ItemStack)newStack);
            if (stack.hasCustomHoverName()) {
                newStack.setHoverName(stack.getHoverName());
            }
            player.setItemSlot(slot, newStack);
            converted = true;
        }
        if (converted) {
            LOGGER.info("Converted winefox fur armor to reinforced for player {}", player.getGameProfile().getName());
        }
    }

    private static Item getReinforcedReplacement(Item item) {
        if (item == ModItems.WINEFOX_FUR_HELMET.get()) {
            return ModItems.WINEFOX_FUR_REINFORCED_HELMET.get();
        }
        if (item == ModItems.WINEFOX_FUR_CHESTPLATE.get()) {
            return ModItems.WINEFOX_FUR_REINFORCED_CHESTPLATE.get();
        }
        if (item == ModItems.WINEFOX_FUR_LEGGINGS.get()) {
            return ModItems.WINEFOX_FUR_REINFORCED_LEGGINGS.get();
        }
        if (item == ModItems.WINEFOX_FUR_BOOTS.get()) {
            return ModItems.WINEFOX_FUR_REINFORCED_BOOTS.get();
        }
        return null;
    }

    private static boolean isReinforcedArmor(Item item) {
        return item == ModItems.WINEFOX_FUR_REINFORCED_HELMET.get() || item == ModItems.WINEFOX_FUR_REINFORCED_CHESTPLATE.get() || item == ModItems.WINEFOX_FUR_REINFORCED_LEGGINGS.get() || item == ModItems.WINEFOX_FUR_REINFORCED_BOOTS.get();
    }

    private static int countReinforcedArmor(Player player) {
        int count = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (!FoxButcherEvents.isReinforcedArmor(player.getItemBySlot(slot).getItem())) continue;
            ++count;
        }
        return count;
    }

    private static void applySetEffect(Player player, MobEffect effect, int amplifier) {
        MobEffectInstance current = player.getEffect(effect);
        if (current == null || current.getAmplifier() < amplifier || current.getDuration() <= 20) {
            player.addEffect(new MobEffectInstance(effect, 40, amplifier, true, false, false));
        }
    }

    private static void removeSetEffect(Player player, MobEffect effect) {
        MobEffectInstance current = player.getEffect(effect);
        if (current != null && current.isAmbient()) {
            player.removeEffect(effect);
        }
    }
}
