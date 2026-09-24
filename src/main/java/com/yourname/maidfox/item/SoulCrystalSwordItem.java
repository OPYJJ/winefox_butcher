package com.yourname.maidfox.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class SoulCrystalSwordItem
extends SwordItem {
    private static final double LIGHTNING_RADIUS = 6.0;
    private static final double LIGHTNING_UP = 6.0;
    private static final double LIGHTNING_DOWN = 2.0;
    private static final float LIGHTNING_DAMAGE = 5.0f;
    private static final Tier SOUL_CRYSTAL_TIER = new Tier(){

        public int getUses() {
            return 2433;
        }

        public float getSpeed() {
            return 9.0f;
        }

        public float getAttackDamageBonus() {
            return 15.0f;
        }

        public int getLevel() {
            return 4;
        }

        public int getEnchantmentValue() {
            return 15;
        }

        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    };

    public SoulCrystalSwordItem(Item.Properties properties) {
        super(SOUL_CRYSTAL_TIER, 0, -2.0f, properties.fireResistant());
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player) {
            Player player = (Player)attacker;
            this.triggerAoeLightning(player);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    private void triggerAoeLightning(Player player) {
        Level level = player.level();
        if (level.isClientSide()) {
            return;
        }
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        AABB area = new AABB(player.getX() - 6.0, player.getY() - 2.0, player.getZ() - 6.0, player.getX() + 6.0, player.getY() + 6.0, player.getZ() + 6.0);
        for (Mob mob : level.getEntitiesOfClass(Mob.class, area)) {
            if (!(mob instanceof Enemy) && mob.getTarget() != player) continue;
            LightningBolt bolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (bolt != null) {
                bolt.moveTo(mob.getX(), mob.getY(), mob.getZ());
                bolt.setVisualOnly(true);
                serverLevel.addFreshEntity((Entity)bolt);
            }
            mob.hurt(level.damageSources().lightningBolt(), 5.0f);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 1.0f, 1.0f);
    }
}

