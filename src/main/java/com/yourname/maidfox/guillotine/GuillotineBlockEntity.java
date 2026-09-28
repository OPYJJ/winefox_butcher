package com.yourname.maidfox.guillotine;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.IChatBubbleData;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.implement.TextChatBubbleData;
import com.yourname.maidfox.expansion.capability.IMaidGeneCapability;
import com.yourname.maidfox.expansion.capability.MaidGeneCapabilityManager;
import com.yourname.maidfox.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class GuillotineBlockEntity extends BlockEntity implements GeoBlockEntity {
    public static final String BOUND_SEAT_TAG = "winefox_guillotine_seat";
    public static final String OLD_NO_AI_TAG = "winefox_guillotine_old_no_ai";
    public static final String MISSING_SEAT_TICKS_TAG = "winefox_guillotine_missing_seat_ticks";
    public enum Phase { EMPTY, BOUND, EXECUTING, LOWERED, RESETTING }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private Phase phase = Phase.EMPTY;
    private long phaseStarted;
    private UUID targetId;
    private UUID seatId;
    private UUID operationId;
    private UUID attemptedOperationId;
    private UUID settledOperationId;
    private boolean previousNoAi;
    private int missingTargetTicks;

    public GuillotineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.GUILLOTINE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) { }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    public Phase getPhase() { return phase; }
    public long getPhaseStarted() { return phaseStarted; }
    public boolean ownsSeat(UUID id) { return seatId != null && seatId.equals(id); }

    public Direction getFacing() { return getBlockState().getValue(GuillotineBlock.FACING); }

    public InteractionResult interact(Player player) {
        if (!(level instanceof ServerLevel server) || !(player instanceof ServerPlayer serverPlayer)) return InteractionResult.SUCCESS;
        GuillotineInteraction.interact(server, this, serverPlayer);
        return InteractionResult.CONSUME;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GuillotineBlockEntity machine) {
        if (!(level instanceof ServerLevel server)) return;
        if (server.getGameTime() % 20 == 0 && !machine.intact(server)) {
            GuillotineBlock.breakStructure(server, pos, true);
            return;
        }
        if (machine.targetId != null && machine.phase != Phase.EXECUTING) machine.checkAssociation(server);
        long elapsed = server.getGameTime() - machine.phaseStarted;
        if (machine.phase == Phase.BOUND && elapsed > 0 && elapsed % 200 == 0)
            machine.speakBoundMaid(server, machine.getMaid(server));
        if (machine.phase == Phase.EXECUTING) {
            if (elapsed >= 10 && !machine.operationId.equals(machine.attemptedOperationId)) {
                machine.attemptedOperationId = machine.operationId;
                machine.sync();
                EntityMaid maid = machine.getMaid(server);
                GuillotineRestraint seat = machine.getSeat(server);
                boolean valid = maid != null && maid.isAlive() && seat != null && maid.getVehicle() == seat
                        && machine.ownsSeat(seat.getUUID());
                boolean succeeded = valid && GuillotineExecution.execute(server, machine.worldPosition,
                        machine.getFacing(), maid, machine.operationId);
                if (succeeded) {
                    machine.settledOperationId = machine.operationId;
                    machine.clearBinding(server, false, false);
                } else {
                    machine.messageOperator(server, "message.winefox_butcher.guillotine.failed");
                    if (maid != null && maid.isAlive() && maid.getVehicle() != seat) machine.clearBinding(server, true, true);
                }
                machine.sync();
            }
            if (elapsed >= 16) {
                machine.phase = Phase.LOWERED;
                machine.phaseStarted = server.getGameTime();
                machine.sync();
            }
        } else if (machine.phase == Phase.RESETTING && elapsed >= 25) {
            machine.phase = machine.targetId != null ? Phase.BOUND : Phase.EMPTY;
            machine.phaseStarted = server.getGameTime();
            machine.sync();
        }
    }

    private boolean intact(ServerLevel server) {
        Direction facing = getFacing();
        for (int y = 0; y < 4; y++) for (int z = -1; z <= 1; z++) for (int x = -1; x <= 1; x++) {
            if (x == 0 && y == 0 && z == 0) continue;
            BlockPos p = GuillotineBlock.offset(worldPosition, facing, x, y, z);
            if (!server.hasChunkAt(p)) return true;
            BlockState part = server.getBlockState(p);
            if (!part.is(ModBlocks.GUILLOTINE_PART.get())
                    || part.getValue(GuillotinePartBlock.INDEX) != GuillotineBlock.index(x, y, z)
                    || part.getValue(GuillotinePartBlock.FACING) != facing) return false;
        }
        return true;
    }

    private void checkAssociation(ServerLevel server) {
        EntityMaid maid = getMaid(server);
        GuillotineRestraint seat = getSeat(server);
        if (maid == null || seat == null) {
            if (++missingTargetTicks > 100 && server.hasChunkAt(worldPosition)) {
                // A temporary unload is allowed to reconnect; only loaded associations are resolved.
                if (seat != null && seat.getFirstPassenger() == null) clearBinding(server, false, true);
            }
            return;
        }
        missingTargetTicks = 0;
        if (!maid.isAlive() || maid.getVehicle() != seat) clearBinding(server, true, true);
    }

    /**
     * Seat position for a machine at the given centre: the stock placement plus the live debug offsets.
     * The offsets are machine-relative, not world-relative: X runs along {@code facing} (the machine's
     * back-to-front axis), Z along {@code facing.getClockWise()} (the machine's lateral axis) and Y stays
     * world-up. For {@code facing == EAST} this is bit-for-bit the old world-axis placement, because EAST
     * is +X and {@code getClockWise(EAST)} is +Z. Shared by tryBind and the seat tick so tuning shows
     * immediately.
     */
    public static Vec3 seatPosition(BlockPos centre, Direction facing) {
        Direction lateral = facing.getClockWise();
        double forward = GuillotineDebug.DEFAULT_POS_X + GuillotineDebug.posX();
        double side = GuillotineDebug.DEFAULT_POS_Z + GuillotineDebug.posZ();
        // Clean baseline: the machine centre, feet on top of the 6px (0.375) base plate, no offset.
        return Vec3.atBottomCenterOf(centre)
                .add(facing.getStepX() * forward + lateral.getStepX() * side,
                        0.375 + GuillotineDebug.DEFAULT_POS_Y + GuillotineDebug.posY(),
                        facing.getStepZ() * forward + lateral.getStepZ() * side);
    }

    public boolean tryBind(ServerPlayer player, EntityMaid maid) {
        if (!(level instanceof ServerLevel server) || phase != Phase.EMPTY || targetId != null) return false;
        if (!maid.isAlive() || maid.level() != server || maid.isPassenger() || maid.isVehicle()
                || maid.distanceToSqr(Vec3.atCenterOf(worldPosition)) > 16
                || !GuillotineInteraction.mayOperate(player, maid)) return false;
        // Baby maids never take part in butchering, so they must not be bound to the machine either.
        if (MaidGeneCapabilityManager.get(maid).map(IMaidGeneCapability::isBaby).orElse(false)) return false;
        if (maid.isLeashed() && maid.getLeashHolder() != player) return false;
        if (maid.getPersistentData().hasUUID(BOUND_SEAT_TAG)) return false;
        Direction facing = getFacing();
        Vec3 seatPos = GuillotineBlockEntity.seatPosition(worldPosition, facing);
        previousNoAi = maid.isNoAi();
        GuillotineRestraint seat = new GuillotineRestraint(server, worldPosition, facing, seatPos,
                facing.toYRot() + 180F + (float) (GuillotineDebug.DEFAULT_ROT_Y + GuillotineDebug.rotY()), maid);
        seat.setXRot((float) (GuillotineDebug.DEFAULT_ROT_X + GuillotineDebug.rotX()));
        seat.setRoll((float) (GuillotineDebug.DEFAULT_ROT_Z + GuillotineDebug.rotZ()));
        seat.setBubble((float) (GuillotineDebug.DEFAULT_BUBBLE_X + GuillotineDebug.bubX()),
                (float) (GuillotineDebug.DEFAULT_BUBBLE_Y + GuillotineDebug.bubY()),
                (float) (GuillotineDebug.DEFAULT_BUBBLE_Z + GuillotineDebug.bubZ()));
        if (!server.addFreshEntity(seat)) return false;
        if (!maid.startRiding(seat, true)) {
            seat.discard();
            return false;
        }
        maid.getNavigation().stop();
        maid.setNoAi(true);
        maid.setDeltaMovement(Vec3.ZERO);
        maid.getPersistentData().putUUID(BOUND_SEAT_TAG, seat.getUUID());
        maid.getPersistentData().putBoolean(OLD_NO_AI_TAG, previousNoAi);
        maid.getPersistentData().remove(MISSING_SEAT_TICKS_TAG);
        targetId = maid.getUUID();
        seatId = seat.getUUID();
        phase = Phase.BOUND;
        phaseStarted = server.getGameTime();
        missingTargetTicks = 0;
        sync();
        speakBoundMaid(server, maid);
        if (maid.isLeashed() && maid.getLeashHolder() == player) maid.dropLeash(true, true);
        return true;
    }

    public boolean tryExecute(ServerPlayer player) {
        if (!(level instanceof ServerLevel server) || phase != Phase.BOUND) return false;
        EntityMaid maid = getMaid(server);
        GuillotineRestraint seat = getSeat(server);
        if (maid == null || !maid.isAlive() || seat == null || maid.getVehicle() != seat
                || !GuillotineInteraction.mayOperate(player, maid)) return false;
        operationId = UUID.randomUUID();
        attemptedOperationId = null;
        settledOperationId = null;
        phase = Phase.EXECUTING;
        phaseStarted = server.getGameTime();
        sync();
        return true;
    }

    public boolean tryReset(ServerPlayer player) {
        if (!(level instanceof ServerLevel server) || phase != Phase.LOWERED) return false;
        EntityMaid maid = getMaid(server);
        if (maid != null && !GuillotineInteraction.mayOperate(player, maid)) return false;
        phase = Phase.RESETTING;
        phaseStarted = server.getGameTime();
        sync();
        return true;
    }

    /**
     * Redstone-triggered drop. Identical to the empty-hand activation but without a player, so the
     * permission check is skipped; the world itself is the operator. Only valid while a maid is bound,
     * and repeated signals are harmless because the phase leaves BOUND immediately.
     */
    public boolean tryExecuteByRedstone() {
        if (!(level instanceof ServerLevel server) || phase != Phase.BOUND) return false;
        EntityMaid maid = getMaid(server);
        GuillotineRestraint seat = getSeat(server);
        if (maid == null || !maid.isAlive() || seat == null || maid.getVehicle() != seat) return false;
        operationId = UUID.randomUUID();
        attemptedOperationId = null;
        settledOperationId = null;
        phase = Phase.EXECUTING;
        phaseStarted = server.getGameTime();
        sync();
        return true;
    }

    /**
     * Redstone-triggered reset: rewinds the blade after the signal ends so the machine can be cycled.
     */
    public boolean tryResetByRedstone() {
        if (!(level instanceof ServerLevel server) || phase != Phase.LOWERED) return false;
        phase = Phase.RESETTING;
        phaseStarted = server.getGameTime();
        sync();
        return true;
    }

    public boolean tryRelease(ServerPlayer player) {
        if (!(level instanceof ServerLevel server) || phase == Phase.EXECUTING || phase == Phase.RESETTING) return false;
        if (targetId == null) return false;
        EntityMaid maid = getMaid(server);
        if (maid == null || !maid.isAlive() || !GuillotineInteraction.mayOperate(player, maid)) return false;
        BlockPos safe = findReleasePos(server, maid);
        if (safe == null) return false;
        clearBinding(server, true, false);
        maid.teleportTo(safe.getX() + 0.5, safe.getY(), safe.getZ() + 0.5);
        if (phase != Phase.LOWERED) phase = Phase.EMPTY;
        sync();
        return true;
    }

    public void releaseForRemoval() {
        if (!(level instanceof ServerLevel server)) return;
        EntityMaid maid = getMaid(server);
        BlockPos safe = maid != null && maid.isAlive() ? findReleasePos(server, maid) : null;
        clearBinding(server, true, false);
        if (maid != null && maid.isAlive()) {
            if (safe != null) maid.teleportTo(safe.getX() + 0.5, safe.getY(), safe.getZ() + 0.5);
            else maid.teleportTo(worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5);
        }
    }

    private void clearBinding(ServerLevel server, boolean restoreAi, boolean onlyIfLoaded) {
        EntityMaid maid = getMaid(server);
        GuillotineRestraint seat = getSeat(server);
        if (maid != null) {
            if (maid.getVehicle() == seat) maid.stopRiding();
            if (restoreAi) maid.setNoAi(previousNoAi);
            maid.getPersistentData().remove(BOUND_SEAT_TAG);
            maid.getPersistentData().remove(OLD_NO_AI_TAG);
            maid.getPersistentData().remove(MISSING_SEAT_TICKS_TAG);
        }
        if (seat != null) seat.discard();
        if (!onlyIfLoaded || maid != null || seat != null) {
            targetId = null;
            seatId = null;
            if (phase == Phase.BOUND) phase = Phase.EMPTY;
            sync();
        }
    }

    @Nullable
    private BlockPos findReleasePos(ServerLevel server, EntityMaid maid) {
        BlockPos front = worldPosition.relative(getFacing(), 2);
        for (int radius = 0; radius <= 3; radius++) {
            for (int dx = -radius; dx <= radius; dx++) for (int dz = -radius; dz <= radius; dz++) {
                BlockPos p = front.offset(dx, 0, dz);
                if (!server.hasChunkAt(p) || !server.isInWorldBounds(p)) continue;
                if (server.getBlockState(p).isAir() && server.getBlockState(p.above()).isAir()
                        && server.getBlockState(p.below()).isFaceSturdy(server, p.below(), Direction.UP)) {
                    Vec3 destination = Vec3.atBottomCenterOf(p);
                    if (server.noCollision(maid, maid.getBoundingBox().move(destination.subtract(maid.position())))) return p;
                }
            }
        }
        return null;
    }

    @Nullable
    private EntityMaid getMaid(ServerLevel server) {
        return targetId != null && server.getEntity(targetId) instanceof EntityMaid maid ? maid : null;
    }

    @Nullable
    private GuillotineRestraint getSeat(ServerLevel server) {
        return seatId != null && server.getEntity(seatId) instanceof GuillotineRestraint seat ? seat : null;
    }

    /** Count of the localized plea lines ({@code plea.0 .. plea.N-1}). */
    private static final int PLEA_LINES = 5;
    /** Count of the localized idle lines ({@code idle.0 .. idle.N-1}). */
    private static final int IDLE_LINES = 10;

    private void speakBoundMaid(ServerLevel server, EntityMaid maid) {
        if (phase != Phase.BOUND || maid == null || !maid.isAlive()
                || !(maid.getVehicle() instanceof GuillotineRestraint seat)
                || !ownsSeat(seat.getUUID())) return;
        // Pick one line out of the combined plea + idle pool. The two groups share the
        // "message.winefox_butcher.guillotine." prefix but differ in their leaf prefix, so the
        // roll is split first and only then the matching prefix is appended.
        int roll = server.random.nextInt(PLEA_LINES + IDLE_LINES);
        String key = roll < PLEA_LINES
                ? "message.winefox_butcher.guillotine.plea." + roll
                : "message.winefox_butcher.guillotine.idle." + (roll - PLEA_LINES);
        Component line = Component.translatable(key);
        maid.getChatBubbleManager().addChatBubble(TextChatBubbleData.create(180, line,
                IChatBubbleData.TYPE_2, IChatBubbleData.DEFAULT_PRIORITY));
    }

    private void messageOperator(ServerLevel server, String key) {
        // The operator may have logged out; no global chat broadcast is made.
        if (lastOperator != null && server.getPlayerByUUID(lastOperator) instanceof ServerPlayer player)
            player.displayClientMessage(Component.translatable(key), true);
    }

    private UUID lastOperator;
    public void setOperator(UUID id) { lastOperator = id; setChanged(); }

    private void sync() {
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("Phase", phase.name());
        tag.putLong("PhaseStarted", phaseStarted);
        if (targetId != null) tag.putUUID("Target", targetId);
        if (seatId != null) tag.putUUID("Seat", seatId);
        if (operationId != null) tag.putUUID("Operation", operationId);
        if (attemptedOperationId != null) tag.putUUID("Attempted", attemptedOperationId);
        if (settledOperationId != null) tag.putUUID("Settled", settledOperationId);
        if (lastOperator != null) tag.putUUID("Operator", lastOperator);
        tag.putBoolean("PreviousNoAi", previousNoAi);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        try { phase = Phase.valueOf(tag.getString("Phase")); } catch (IllegalArgumentException e) { phase = Phase.EMPTY; }
        phaseStarted = tag.getLong("PhaseStarted");
        targetId = tag.hasUUID("Target") ? tag.getUUID("Target") : null;
        seatId = tag.hasUUID("Seat") ? tag.getUUID("Seat") : null;
        operationId = tag.hasUUID("Operation") ? tag.getUUID("Operation") : null;
        attemptedOperationId = tag.hasUUID("Attempted") ? tag.getUUID("Attempted") : null;
        settledOperationId = tag.hasUUID("Settled") ? tag.getUUID("Settled") : null;
        lastOperator = tag.hasUUID("Operator") ? tag.getUUID("Operator") : null;
        previousNoAi = tag.getBoolean("PreviousNoAi");
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel && (phase == Phase.EXECUTING || phase == Phase.RESETTING)) {
            phase = targetId != null ? Phase.BOUND : Phase.EMPTY;
            phaseStarted = level.getGameTime();
            sync();
        }
    }

    @Override
    public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        if (packet.getTag() != null) load(packet.getTag());
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.offset(-2, 0, -2), worldPosition.offset(2, 5, 2));
    }
}
