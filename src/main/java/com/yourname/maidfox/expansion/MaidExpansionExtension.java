package com.yourname.maidfox.expansion;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.api.entity.ai.IExtraMaidBrain;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.ExtraMaidBrainManager;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.util.Pair;
import com.yourname.maidfox.expansion.task.behavior.MaidBreedBehavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;

import java.util.ArrayList;
import java.util.List;

/**
 * Maid breeding expansion: breeding is always available and does not occupy a work mode.
 * <p>
 * The courtship behaviour is attached to the maid's Idle / Work / Rest activities through
 * {@link IExtraMaidBrain}, so an adult maid in any work mode starts looking for a partner as
 * soon as she is fed into the courtship state. There is no (and no need for) a dedicated
 * "breeding" work task.
 */
@LittleMaidExtension
public class MaidExpansionExtension implements ILittleMaid {
    /**
     * Courtship priority: above random walking (20), below daily behaviours such as eating
     * (5..7) and stealing food (8).
     */
    private static final int BREED_PRIORITY = 9;

    @Override
    public void addExtraMaidBrain(ExtraMaidBrainManager manager) {
        manager.addExtraMaidBrain(new IExtraMaidBrain() {
            @Override
            public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> getIdleBehaviors() {
                return breedBehaviors();
            }

            @Override
            public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> getWorkBehaviors() {
                return breedBehaviors();
            }

            @Override
            public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> getRestBehaviors() {
                return breedBehaviors();
            }
        });
    }

    /**
     * A fresh behaviour instance must be returned for every maid brain rebuild, otherwise several
     * maids would share the same courtship state.
     */
    private static List<Pair<Integer, BehaviorControl<? super EntityMaid>>> breedBehaviors() {
        List<Pair<Integer, BehaviorControl<? super EntityMaid>>> behaviors = new ArrayList<>();
        behaviors.add(Pair.of(BREED_PRIORITY, new MaidBreedBehavior()));
        return behaviors;
    }
}

