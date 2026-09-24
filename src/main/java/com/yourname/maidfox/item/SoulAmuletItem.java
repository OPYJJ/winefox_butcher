package com.yourname.maidfox.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class SoulAmuletItem
extends Item {
    public SoulAmuletItem(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    public boolean isFoil(ItemStack stack) {
        return true;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.winefox_butcher.soul_amulet.desc").withStyle(ChatFormatting.GRAY));
    }
}

