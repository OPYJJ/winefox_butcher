package com.yourname.maidfox.task;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidCheckRateTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.lance5057.butchercraft.workstations.hook.MeatHookBlockEntity;
import com.yourname.maidfox.task.MaidButcherHelper;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class MaidButcherTask
extends MaidCheckRateTask {
    private static final int SEARCH_RANGE_XZ = 8;
    private static final int SEARCH_RANGE_Y = 4;
    private static final int SCAN_INTERVAL = 200;
    private static final double REACH_DIST_SQ = 4.0;
    private static final float WALK_SPEED = 1.0f;
    private static final TagKey<Item> CARCASS_TAG = TagKey.create(Registries.ITEM, new ResourceLocation("butchercraft", "carcass"));
    private BlockPos targetPos;
    private Mode mode;

    public MaidButcherTask() {
        super(Map.of(), 100000, 100000);
        this.setMaxCheckRate(SCAN_INTERVAL);
    }

    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid maid) {
        if (!super.checkExtraStartConditions(level, maid) || maid.isDeadOrDying()) {
            return false;
        }
        BlockPos occupied = MaidButcherTask.findHook(level, maid.blockPosition(), true);
        if (occupied != null && MaidButcherHelper.hasMatchingTool(level, occupied, maid)) {
            this.targetPos = occupied;
            this.mode = Mode.PROCESS;
            return true;
        }
        BlockPos empty = MaidButcherTask.findHook(level, maid.blockPosition(), false);
        if (empty != null && MaidButcherTask.hasCarcass(maid)) {
            this.targetPos = empty;
            this.mode = Mode.MOUNT;
            return true;
        }
        return false;
    }

    protected boolean canStillUse(ServerLevel level, EntityMaid maid, long gameTime) {
        if (this.targetPos == null || maid.isDeadOrDying()) {
            return false;
        }
        BlockEntity be = level.getBlockEntity(this.targetPos);
        if (!(be instanceof MeatHookBlockEntity)) {
            return false;
        }
        MeatHookBlockEntity hook = (MeatHookBlockEntity)be;
        if (this.mode == Mode.PROCESS) {
            return !hook.getInsertedItem().isEmpty() && MaidButcherHelper.hasMatchingTool(level, this.targetPos, maid);
        }
        return hook.getInsertedItem().isEmpty() && MaidButcherTask.hasCarcass(maid);
    }

    protected void start(ServerLevel level, EntityMaid maid, long gameTime) {
        this.moveTo(maid);
    }

    protected void tick(ServerLevel level, EntityMaid maid, long gameTime) {
        if (this.targetPos == null) {
            return;
        }
        if (!MaidButcherTask.isNear(maid, this.targetPos)) {
            this.moveTo(maid);
            return;
        }
        maid.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        maid.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.targetPos));
        if (this.mode == Mode.MOUNT) {
            if (gameTime % 10L == 0L && this.mount(level, maid)) {
                this.mode = Mode.PROCESS;
                if (!MaidButcherHelper.hasMatchingTool(level, this.targetPos, maid)) {
                    this.targetPos = null;
                    this.mode = null;
                }
            }
            return;
        }
        if (gameTime % 8L == 0L) {
            maid.swing(InteractionHand.MAIN_HAND);
            MaidButcherHelper.process(level, this.targetPos, maid);
        }
    }

    protected void stop(ServerLevel level, EntityMaid maid, long gameTime) {
        this.targetPos = null;
        this.mode = null;
    }

    private void moveTo(EntityMaid maid) {
        if (this.targetPos == null) {
            return;
        }
        BlockPos feet = new BlockPos(this.targetPos.getX(), maid.blockPosition().getY(), this.targetPos.getZ());
        BehaviorUtils.setWalkAndLookTargetMemories((LivingEntity)maid, feet, WALK_SPEED, (int)2);
    }

    private static boolean isNear(EntityMaid maid, BlockPos pos) {
        double dz;
        double dx = maid.getX() - ((double)pos.getX() + 0.5);
        return dx * dx + (dz = maid.getZ() - ((double)pos.getZ() + 0.5)) * dz <= REACH_DIST_SQ;
    }

    private boolean mount(ServerLevel level, EntityMaid maid) {
        MeatHookBlockEntity hook;
        BlockEntity be = level.getBlockEntity(this.targetPos);
        if (!(be instanceof MeatHookBlockEntity) || !(hook = (MeatHookBlockEntity)be).getInsertedItem().isEmpty()) {
            return false;
        }
        CombinedInvWrapper inv = maid.getAvailableInv(true);
        for (int i = 0; i < inv.getSlots(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty() || !stack.is(CARCASS_TAG)) continue;
            ItemStack copy = stack.copy();
            copy.setCount(1);
            FakePlayer fake = MaidButcherHelper.getBoundFakePlayer(level, maid);
            fake.setItemInHand(InteractionHand.MAIN_HAND, copy);
            hook.extractInsertItem((Player)fake, InteractionHand.MAIN_HAND);
            if (copy.isEmpty()) {
                stack.shrink(1);
                hook.updateInventory();
                return true;
            }
            fake.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
        return false;
    }

    private static boolean hasCarcass(EntityMaid maid) {
        CombinedInvWrapper inv = maid.getAvailableInv(true);
        for (int i = 0; i < inv.getSlots(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty() || !stack.is(CARCASS_TAG)) continue;
            return true;
        }
        return false;
    }

    private static BlockPos findHook(ServerLevel level, BlockPos center, boolean occupied) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        BlockPos best = null;
        double bestDistSq = Double.MAX_VALUE;
        for (int x = -SEARCH_RANGE_XZ; x <= SEARCH_RANGE_XZ; ++x) {
            for (int y = -SEARCH_RANGE_Y; y <= SEARCH_RANGE_Y; ++y) {
                for (int z = -SEARCH_RANGE_XZ; z <= SEARCH_RANGE_XZ; ++z) {
                    cursor.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    BlockEntity be = level.getBlockEntity(cursor);
                    if (!(be instanceof MeatHookBlockEntity)) continue;
                    MeatHookBlockEntity hook = (MeatHookBlockEntity)be;
                    ItemStack inserted = hook.getInsertedItem();
                    boolean hasCarcass = !inserted.isEmpty() && inserted.is(CARCASS_TAG);
                    if (!(occupied ? hasCarcass : !hasCarcass && inserted.isEmpty())) continue;
                    double dx = cursor.getX() - center.getX();
                    double dy = cursor.getY() - center.getY();
                    double dz = cursor.getZ() - center.getZ();
                    double distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq < bestDistSq) {
                        bestDistSq = distSq;
                        best = cursor.immutable();
                    }
                }
            }
        }
        return best;
    }

    private static enum Mode {
        PROCESS,
        MOUNT;

    }
}
