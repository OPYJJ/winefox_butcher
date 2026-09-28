package com.yourname.maidfox.guillotine;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidDeathEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.yourname.maidfox.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.Locale;
import java.util.UUID;

/** The only location that awards a head. The event references are inspected after hurt returns. */
public final class GuillotineExecution {
    public static final ResourceKey<DamageType> DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE,
            new ResourceLocation("winefox_butcher", "guillotine"));
    private static final ThreadLocal<Attempt> CURRENT = new ThreadLocal<>();

    private static final class Attempt {
        final UUID maid;
        final UUID operation;
        MaidDeathEvent maidDeath;
        LivingDeathEvent livingDeath;
        Attempt(UUID maid, UUID operation) { this.maid = maid; this.operation = operation; }
    }

    private GuillotineExecution() { }

    public static boolean isMachineDamage(DamageSource source) { return source != null && source.is(DAMAGE); }

    public static void observe(MaidDeathEvent event) {
        Attempt attempt = CURRENT.get();
        if (attempt != null && attempt.maid.equals(event.getMaid().getUUID()) && isMachineDamage(event.getSource()))
            attempt.maidDeath = event;
    }

    public static void observe(LivingDeathEvent event) {
        Attempt attempt = CURRENT.get();
        if (attempt != null && attempt.maid.equals(event.getEntity().getUUID()) && isMachineDamage(event.getSource()))
            attempt.livingDeath = event;
    }

    public static boolean execute(ServerLevel level, BlockPos anchor, Direction facing, EntityMaid maid, UUID operation) {
        if (CURRENT.get() != null || !maid.isAlive()) return false;
        boolean winefox = isCurrentWinefoxModel(maid);
        Attempt attempt = new Attempt(maid.getUUID(), operation);
        DamageSource source = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DAMAGE));
        CURRENT.set(attempt);
        try {
            maid.hurt(source, 1024.0F);
        } finally {
            CURRENT.remove();
        }
        boolean died = attempt.maidDeath != null && !attempt.maidDeath.isCanceled()
                && attempt.livingDeath != null && !attempt.livingDeath.isCanceled()
                && maid.isDeadOrDying() && !maid.isRemoved();
        if (died && winefox) {
            BlockPos front = anchor.relative(facing, 2);
            ItemEntity head = new ItemEntity(level, front.getX() + 0.5, front.getY() + 0.7,
                    front.getZ() + 0.5, new ItemStack(ModItems.WINEFOX_HEAD_ITEM.get(), 1));
            head.setDefaultPickUpDelay();
            level.addFreshEntity(head);
        }
        return died;
    }

    private static boolean isCurrentWinefoxModel(EntityMaid maid) {
        String id = maid.isYsmModel() ? maid.getYsmModelId() : maid.getModelId();
        if (id == null) return false;
        String normalized = id.toLowerCase(Locale.ROOT);
        return normalized.contains("winefox") || normalized.contains("wine_fox");
    }
}
