package com.yourname.maidfox.guillotine;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Explicit lead selection and all server-authoritative manual controls. */
public final class GuillotineInteraction {
    private static final Map<UUID, Selection> SELECTIONS = new HashMap<>();
    private record Selection(UUID maid, ResourceKey<Level> dimension, long expires) { }

    private GuillotineInteraction() { }

    @SubscribeEvent
    public static void selectMaid(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() != InteractionHand.MAIN_HAND || !event.getEntity().isShiftKeyDown()
                || !event.getEntity().getMainHandItem().is(Items.LEAD)
                || !(event.getTarget() instanceof EntityMaid maid)) return;
        if (!(event.getEntity() instanceof ServerPlayer player) || !(event.getLevel() instanceof ServerLevel level)) return;
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (!mayOperate(player, maid)) {
            tell(player, "message.winefox_butcher.guillotine.denied");
            return;
        }
        SELECTIONS.put(player.getUUID(), new Selection(maid.getUUID(), level.dimension(), level.getGameTime() + 1200));
        player.displayClientMessage(Component.translatable("message.winefox_butcher.guillotine.selected", maid.getDisplayName()), true);
    }

    @SubscribeEvent
    public static void clearSelection(PlayerEvent.PlayerLoggedOutEvent event) {
        SELECTIONS.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void restoreOrphanedMaid(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof EntityMaid maid) || maid.level().isClientSide || !maid.isAlive()) return;
        var data = maid.getPersistentData();
        if (!data.hasUUID(GuillotineBlockEntity.BOUND_SEAT_TAG)) return;
        UUID seatId = data.getUUID(GuillotineBlockEntity.BOUND_SEAT_TAG);
        if (maid.getVehicle() instanceof GuillotineRestraint seat && seat.getUUID().equals(seatId)) {
            data.remove(GuillotineBlockEntity.MISSING_SEAT_TICKS_TAG);
            return;
        }
        int missing = data.getInt(GuillotineBlockEntity.MISSING_SEAT_TICKS_TAG) + 1;
        if (missing < 100) {
            data.putInt(GuillotineBlockEntity.MISSING_SEAT_TICKS_TAG, missing);
            return;
        }
        maid.setNoAi(data.getBoolean(GuillotineBlockEntity.OLD_NO_AI_TAG));
        data.remove(GuillotineBlockEntity.BOUND_SEAT_TAG);
        data.remove(GuillotineBlockEntity.OLD_NO_AI_TAG);
        data.remove(GuillotineBlockEntity.MISSING_SEAT_TICKS_TAG);
    }

    static boolean mayOperate(ServerPlayer player, EntityMaid maid) {
        UUID owner = maid.getOwnerUUID();
        return owner == null || owner.equals(player.getUUID()) || player.isCreative() || player.hasPermissions(2);
    }

    static void interact(ServerLevel level, GuillotineBlockEntity machine, ServerPlayer player) {
        if (player.distanceToSqr(Vec3.atCenterOf(machine.getBlockPos())) > 36) return;
        var held = player.getMainHandItem();
        var phase = machine.getPhase();
        if (held.is(Items.LEAD)) {
            if (phase != GuillotineBlockEntity.Phase.EMPTY) {
                tell(player, "message.winefox_butcher.guillotine.busy");
                return;
            }
            EntityMaid candidate = chosenTarget(level, machine, player);
            if (candidate == null) return;
            if (machine.tryBind(player, candidate)) {
                SELECTIONS.remove(player.getUUID());
                tell(player, "message.winefox_butcher.guillotine.bound");
            } else {
                tell(player, "message.winefox_butcher.guillotine.cannot_bind");
            }
            return;
        }
        if (!held.isEmpty()) {
            tell(player, "message.winefox_butcher.guillotine.use_empty_hand");
            return;
        }
        if (player.isShiftKeyDown()) {
            if (machine.tryRelease(player)) tell(player, "message.winefox_butcher.guillotine.released");
            else tell(player, "message.winefox_butcher.guillotine.cannot_release");
            return;
        }
        if (phase == GuillotineBlockEntity.Phase.BOUND) {
            if (machine.tryExecute(player)) {
                machine.setOperator(player.getUUID());
                tell(player, "message.winefox_butcher.guillotine.executing");
            } else tell(player, "message.winefox_butcher.guillotine.cannot_execute");
        } else if (phase == GuillotineBlockEntity.Phase.LOWERED) {
            if (machine.tryReset(player)) tell(player, "message.winefox_butcher.guillotine.resetting");
            else tell(player, "message.winefox_butcher.guillotine.denied");
        } else if (phase == GuillotineBlockEntity.Phase.EMPTY) {
            tell(player, "message.winefox_butcher.guillotine.need_maid");
        } else {
            tell(player, "message.winefox_butcher.guillotine.busy");
        }
    }

    private static EntityMaid chosenTarget(ServerLevel level, GuillotineBlockEntity machine, ServerPlayer player) {
        Selection selection = SELECTIONS.get(player.getUUID());
        Vec3 centre = Vec3.atCenterOf(machine.getBlockPos());
        if (selection != null) {
            if (!selection.dimension.equals(level.dimension()) || selection.expires < level.getGameTime()) {
                SELECTIONS.remove(player.getUUID());
                tell(player, "message.winefox_butcher.guillotine.selection_expired");
                return null;
            }
            if (!(level.getEntity(selection.maid) instanceof EntityMaid maid) || !maid.isAlive()
                    || maid.distanceToSqr(centre) > 16 || !mayOperate(player, maid)) {
                tell(player, "message.winefox_butcher.guillotine.selection_invalid");
                return null;
            }
            return maid;
        }
        // Snapshot first: mounting and detaching a lead can mutate the level's entity collection.
        var nearby = new ArrayList<>(level.getEntitiesOfClass(EntityMaid.class,
                new AABB(machine.getBlockPos()).inflate(4)));
        EntityMaid nearest = nearby.stream()
                .filter(EntityMaid::isAlive)
                .filter(maid -> maid.distanceToSqr(centre) <= 16)
                .filter(maid -> maid.isLeashed() && maid.getLeashHolder() == player)
                .filter(maid -> mayOperate(player, maid))
                .min(Comparator.comparingDouble(maid -> maid.distanceToSqr(centre)))
                .orElse(null);
        if (nearest == null) tell(player, "message.winefox_butcher.guillotine.no_leashed_maid");
        return nearest;
    }

    private static void tell(ServerPlayer player, String key) {
        player.displayClientMessage(Component.translatable(key), true);
    }
}
