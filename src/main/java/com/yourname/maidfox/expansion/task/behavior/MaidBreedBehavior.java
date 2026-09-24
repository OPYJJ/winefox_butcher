package com.yourname.maidfox.expansion.task.behavior;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableMap;
import com.yourname.maidfox.expansion.breeding.BreedingHandler;
import com.yourname.maidfox.expansion.capability.IMaidGeneCapability;
import com.yourname.maidfox.expansion.capability.MaidGeneCapabilityManager;
import com.yourname.maidfox.expansion.config.MaidExpansionConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Always-on courtship behaviour: an adult maid in any work mode that is in the courtship state
 * looks for a nearby maid in the same state and tries to breed.
 * <p>
 * The behaviour is permanently attached to the Idle / Work / Rest activities by
 * {@code MaidExpansionExtension} through IExtraMaidBrain, so it no longer depends on a
 * "breeding" work mode.
 */
public class MaidBreedBehavior extends Behavior<EntityMaid> {
    private static final double SEARCH_RADIUS = 8.0D;
    private static final double BREED_DISTANCE = 1.8D;

    @Nullable
    private EntityMaid partner;
    private int breedHoldTicks = 0;

    public MaidBreedBehavior() {
        super(ImmutableMap.of(
                MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED
        ));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid maid) {
        return isEligible(maid) && findPartner(maid) != null;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, EntityMaid maid, long gameTime) {
        return isEligible(maid) && partner != null && partner.isAlive() && isEligible(partner);
    }

    @Override
    protected void start(ServerLevel level, EntityMaid maid, long gameTime) {
        this.breedHoldTicks = 0;
        this.partner = findPartner(maid);
        if (this.partner != null) {
            maid.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(this.partner, 0.6F, 2));
        }
    }

    @Override
    protected void tick(ServerLevel level, EntityMaid maid, long gameTime) {
        if (this.partner == null || !this.partner.isAlive()) {
            this.partner = findPartner(maid);
        }
        if (this.partner == null) {
            return;
        }
        if (maid.distanceToSqr(this.partner) <= BREED_DISTANCE * BREED_DISTANCE) {
            // Move close to each other: stand still for a while before spawning the baby (mirrors animal breeding pacing)
            // Keep refreshing the walk target: keeps the maid close and keeps WALK_TARGET set, so random-walk AI cannot lead her away from the partner
            maid.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(this.partner, 0.5F, 1));
            this.breedHoldTicks++;
            if (this.breedHoldTicks % 10 == 0) {
                level.broadcastEntityEvent(maid, (byte) 18);
                level.broadcastEntityEvent(this.partner, (byte) 18);
            }
            if (this.breedHoldTicks >= MaidExpansionConfig.BREEDING_DELAY_TICKS.get()) {
                this.breedHoldTicks = 0;
                BreedingHandler.tryBreed(maid, this.partner);
            }
        } else {
            this.breedHoldTicks = 0;
            maid.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(this.partner, 0.6F, 2));
        }
    }

    @Override
    protected void stop(ServerLevel level, EntityMaid maid, long gameTime) {
        this.breedHoldTicks = 0;
        this.partner = null;
        maid.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    private boolean isEligible(EntityMaid maid) {
        if (!MaidExpansionConfig.ENABLE_MAID_BREEDING.get()) {
            return false;
        }
        Optional<IMaidGeneCapability> cap = MaidGeneCapabilityManager.get(maid).resolve();
        return cap.isPresent() && cap.get().getAge() >= 0 && cap.get().isInLove();
    }

    @Nullable
    private EntityMaid findPartner(EntityMaid maid) {
        Optional<NearestVisibleLivingEntities> nearby = maid.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        if (nearby.isEmpty()) {
            return null;
        }
        return nearby.get()
                .findClosest(e -> e instanceof EntityMaid other && other != maid
                        && maid.distanceToSqr(other) <= SEARCH_RADIUS * SEARCH_RADIUS
                        && isEligible(other))
                .map(e -> (EntityMaid) e)
                .orElse(null);
    }
}

