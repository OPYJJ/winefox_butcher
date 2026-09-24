package com.yourname.maidfox.delight.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class WinefoxSushiRollItem extends Item {
    public WinefoxSushiRollItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 64;
    }
}

