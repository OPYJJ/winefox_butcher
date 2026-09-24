package com.yourname.maidfox.block;

import com.yourname.maidfox.block.WinefoxHeadBlockEntity;
import com.yourname.maidfox.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WinefoxHeadBlock
extends SkullBlock {
    public WinefoxHeadBlock() {
        super((SkullBlock.Type)SkullBlock.Types.PLAYER, BlockBehaviour.Properties.copy((BlockBehaviour)Blocks.PLAYER_HEAD));
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WinefoxHeadBlockEntity(pos, state);
    }

    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.WINEFOX_HEAD_ITEM.get());
    }
}

