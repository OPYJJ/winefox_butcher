package com.yourname.maidfox.delight.compat.farmersdelight;

import net.minecraft.world.level.block.state.BlockBehaviour;
import vectorwing.farmersdelight.common.block.FeastBlock;

public class WinefoxMeatPotBlock extends FeastBlock {
    public WinefoxMeatPotBlock(BlockBehaviour.Properties properties) {
        super(properties, FarmersDelightContent.WINEFOX_STEW_BOWL, true);
    }
}

