package com.yourname.maidfox.task;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.lance5057.butchercraft.workstations.bases.recipes.AnimatedRecipeItemUse;
import com.lance5057.butchercraft.workstations.hook.MeatHookBlockEntity;
import com.mojang.authlib.GameProfile;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public final class MaidButcherHelper {
    private MaidButcherHelper() {
    }

    public static FakePlayer getBoundFakePlayer(ServerLevel level, EntityMaid maid) {
        GameProfile profile = new GameProfile(maid.getUUID(), "winefox_butcher_" + maid.getUUID().toString().substring(0, 8));
        FakePlayer fake = FakePlayerFactory.get((ServerLevel)level, (GameProfile)profile);
        fake.setPos(maid.getX(), maid.getY(), maid.getZ());
        return fake;
    }

    public static boolean hasMatchingTool(ServerLevel level, BlockPos hookPos, EntityMaid maid) {
        BlockEntity be = level.getBlockEntity(hookPos);
        if (!(be instanceof MeatHookBlockEntity)) {
            return false;
        }
        MeatHookBlockEntity hook = (MeatHookBlockEntity)be;
        Optional current = hook.getCurrentTool();
        if (current.isEmpty()) {
            return false;
        }
        return !MaidButcherHelper.findTool(maid, ((AnimatedRecipeItemUse)current.get()).tool).isEmpty();
    }

    private static ItemStack findTool(EntityMaid maid, Ingredient needed) {
        CombinedInvWrapper inv = maid.getAvailableInv(true);
        for (int i = 0; i < inv.getSlots(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty() || !needed.test(stack)) continue;
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static void process(ServerLevel level, BlockPos hookPos, EntityMaid maid) {
        BlockEntity be = level.getBlockEntity(hookPos);
        if (!(be instanceof MeatHookBlockEntity)) {
            return;
        }
        MeatHookBlockEntity hook = (MeatHookBlockEntity)be;
        if (hook.getInsertedItem().isEmpty()) {
            return;
        }
        Optional current = hook.getCurrentTool();
        if (current.isEmpty()) {
            return;
        }
        ItemStack knife = MaidButcherHelper.findTool(maid, ((AnimatedRecipeItemUse)current.get()).tool);
        if (knife.isEmpty()) {
            return;
        }
        FakePlayer fake = MaidButcherHelper.getBoundFakePlayer(level, maid);
        fake.setItemInHand(InteractionHand.MAIN_HAND, knife);
        hook.butcher((Player)fake, knife);
    }
}

