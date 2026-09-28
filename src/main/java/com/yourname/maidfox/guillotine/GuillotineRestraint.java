package com.yourname.maidfox.guillotine;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.yourname.maidfox.init.ModBlocks;
import com.yourname.maidfox.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

/** Invisible, saved seat for the original maid entity. */
public class GuillotineRestraint extends Entity {
    /** Roll angle in degrees; entity xRot carries the pitch, yRot the yaw, so only roll needs data. */
    private static final EntityDataAccessor<Float> DATA_ROLL =
            SynchedEntityData.defineId(GuillotineRestraint.class, EntityDataSerializers.FLOAT);
    /** Bubble offsets in blocks; synced so the client renderer can place the chat bubble. */
    private static final EntityDataAccessor<Float> DATA_BUBBLE_X =
            SynchedEntityData.defineId(GuillotineRestraint.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_BUBBLE_Y =
            SynchedEntityData.defineId(GuillotineRestraint.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_BUBBLE_Z =
            SynchedEntityData.defineId(GuillotineRestraint.class, EntityDataSerializers.FLOAT);
    /**
     * Machine FACING, synced so the client renderer can resolve the machine-local axes for the debug
     * offsets and rotations (see GuillotineBlockEntity#seatPosition).
     */
    private static final EntityDataAccessor<Byte> DATA_FACING =
            SynchedEntityData.defineId(GuillotineRestraint.class, EntityDataSerializers.BYTE);
    private BlockPos anchor = BlockPos.ZERO;
    private UUID maidId;
    private boolean oldNoAi;
    private int missingTicks;

    public GuillotineRestraint(EntityType<?> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    public GuillotineRestraint(Level level, BlockPos anchor, Direction facing, Vec3 position, float yaw, EntityMaid maid) {
        this(ModEntities.GUILLOTINE_RESTRAINT.get(), level);
        this.anchor = anchor.immutable();
        this.maidId = maid.getUUID();
        this.oldNoAi = maid.isNoAi();
        setFacing(facing);
        setPos(position);
        setYRot(yaw);
    }

    public BlockPos getAnchor() { return anchor; }
    public UUID getMaidId() { return maidId; }
    public boolean getOldNoAi() { return oldNoAi; }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_ROLL, 0.0F);
        entityData.define(DATA_BUBBLE_X, 0.0F);
        entityData.define(DATA_BUBBLE_Y, 0.0F);
        entityData.define(DATA_BUBBLE_Z, 0.0F);
        entityData.define(DATA_FACING, (byte) Direction.NORTH.get3DDataValue());
    }

    /** Machine FACING; its local axes are forward = the direction itself, lateral = getClockWise(). */
    public Direction getFacing() { return Direction.from3DDataValue(entityData.get(DATA_FACING)); }

    public void setFacing(Direction facing) { entityData.set(DATA_FACING, (byte) facing.get3DDataValue()); }

    public float getBubbleX() { return entityData.get(DATA_BUBBLE_X); }
    public float getBubbleY() { return entityData.get(DATA_BUBBLE_Y); }
    public float getBubbleZ() { return entityData.get(DATA_BUBBLE_Z); }

    public void setBubble(float x, float y, float z) {
        entityData.set(DATA_BUBBLE_X, x);
        entityData.set(DATA_BUBBLE_Y, y);
        entityData.set(DATA_BUBBLE_Z, z);
    }

    public float getRoll() { return entityData.get(DATA_ROLL); }
    public void setRoll(float roll) { entityData.set(DATA_ROLL, roll); }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Anchor", Tag.TAG_COMPOUND)) anchor = NbtUtils.readBlockPos(tag.getCompound("Anchor"));
        if (tag.hasUUID("Maid")) maidId = tag.getUUID("Maid");
        oldNoAi = tag.getBoolean("OldNoAi");
        if (tag.contains("Facing")) setFacing(Direction.from3DDataValue(tag.getByte("Facing")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("Anchor", NbtUtils.writeBlockPos(anchor));
        if (maidId != null) tag.putUUID("Maid", maidId);
        tag.putBoolean("OldNoAi", oldNoAi);
        tag.putByte("Facing", (byte) getFacing().get3DDataValue());
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server)) return;
        if (!server.hasChunkAt(anchor)) return;
        if (server.getBlockState(anchor).is(ModBlocks.GUILLOTINE.get())
                && server.getBlockEntity(anchor) instanceof GuillotineBlockEntity machine
                && machine.ownsSeat(getUUID())) {
            missingTicks = 0;
            // Re-apply the whole placement every tick - not only while tuning. Position, the three axes,
            // the machine facing and the bubble offsets are all derived from the machine plus the baked
            // defaults, while the synced values (roll, facing, bubble) are NOT saved to NBT. Re-applying
            // here is therefore what keeps the pose from resetting after a world reload.
            setFacing(machine.getFacing());
                setPos(GuillotineBlockEntity.seatPosition(machine.getBlockPos(), machine.getFacing()));
                setYRot(machine.getFacing().toYRot() + 180F
                        + (float) (GuillotineDebug.DEFAULT_ROT_Y + GuillotineDebug.rotY()));
                setXRot((float) (GuillotineDebug.DEFAULT_ROT_X + GuillotineDebug.rotX()));
                setRoll((float) (GuillotineDebug.DEFAULT_ROT_Z + GuillotineDebug.rotZ()));
                setBubble((float) (GuillotineDebug.DEFAULT_BUBBLE_X + GuillotineDebug.bubX()),
                        (float) (GuillotineDebug.DEFAULT_BUBBLE_Y + GuillotineDebug.bubY()),
                        (float) (GuillotineDebug.DEFAULT_BUBBLE_Z + GuillotineDebug.bubZ()));
            if (getFirstPassenger() instanceof EntityMaid maid) {
                // Pin every rotation field, not just yRot: a NoAI rider never runs the vanilla body-yaw
                // follow, and the interpolated old values would otherwise keep the render stale.
                maid.setYRot(getYRot());
                maid.yRotO = getYRot();
                maid.setYHeadRot(getYRot());
                maid.yHeadRotO = getYRot();
                maid.yBodyRot = getYRot();
                maid.yBodyRotO = getYRot();
                maid.getNavigation().stop();
                maid.setDeltaMovement(Vec3.ZERO);
            }
            return;
        }
        if (++missingTicks < 40) return;
        if (getFirstPassenger() instanceof EntityMaid maid && maid.isAlive()) {
            maid.stopRiding();
            maid.setNoAi(oldNoAi);
            maid.getPersistentData().remove(GuillotineBlockEntity.BOUND_SEAT_TAG);
            maid.getPersistentData().remove(GuillotineBlockEntity.OLD_NO_AI_TAG);
            maid.getPersistentData().remove(GuillotineBlockEntity.MISSING_SEAT_TICKS_TAG);
        }
        discard();
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction mover) {
        if (hasPassenger(passenger)) mover.accept(passenger, getX(), getY(), getZ());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) { return false; }
    @Override
    public boolean isPickable() { return false; }
    @Override
    public boolean canCollideWith(Entity other) { return false; }
    @Override
    public void move(MoverType type, Vec3 movement) { }
    @Override
    public double getPassengersRidingOffset() { return 0; }
    @Override
    protected boolean repositionEntityAfterLoad() { return false; }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
