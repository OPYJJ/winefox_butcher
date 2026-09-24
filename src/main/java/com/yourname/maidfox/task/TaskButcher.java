package com.yourname.maidfox.task;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.lance5057.butchercraft.ButchercraftItems;
import com.mojang.datafixers.util.Pair;
import com.yourname.maidfox.task.MaidButcherTask;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class TaskButcher
implements IMaidTask {
    private static final ResourceLocation UID = new ResourceLocation("winefox_butcher", "butcher");

    public ResourceLocation getUid() {
        return UID;
    }

    public ItemStack getIcon() {
        return new ItemStack(ButchercraftItems.BUTCHER_KNIFE.get());
    }

    @Nullable
    public SoundEvent getAmbientSound(EntityMaid maid) {
        return null;
    }

    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid maid) {
        ArrayList<Pair<Integer, BehaviorControl<? super EntityMaid>>> tasks = new ArrayList<Pair<Integer, BehaviorControl<? super EntityMaid>>>();
        tasks.add(Pair.of(5, new MaidButcherTask()));
        return tasks;
    }

    public boolean enableLookAndRandomWalk(EntityMaid maid) {
        return true;
    }
}

