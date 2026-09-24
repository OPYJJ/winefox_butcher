package com.yourname.maidfox.block;

import com.yourname.maidfox.block.WinefoxFurBedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.Property;

public class WinefoxFurBedBlock
extends BedBlock {
    public WinefoxFurBedBlock() {
        super(DyeColor.RED, BlockBehaviour.Properties.copy((BlockBehaviour)Blocks.RED_BED));
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue((Property)BedBlock.PART) == BedPart.HEAD) {
            return null;
        }
        return new WinefoxFurBedBlockEntity(pos, state);
    }
}

