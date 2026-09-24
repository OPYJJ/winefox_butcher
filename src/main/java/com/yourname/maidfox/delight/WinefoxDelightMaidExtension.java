package com.yourname.maidfox.delight.compat;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.client.overlay.MaidTipsOverlay;
import com.yourname.maidfox.delight.WinefoxDelight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@LittleMaidExtension
public class WinefoxDelightMaidExtension implements ILittleMaid {
    @Override
    @OnlyIn(Dist.CLIENT)
    public void addMaidTips(MaidTipsOverlay maidTipsOverlay) {
        List<Item> items = new ArrayList<>();
        for (String id : WinefoxDelightMaidEvents.FEEDABLE_FOOD_IDS) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(WinefoxDelight.MODID, id));
            if (item != null) {
                items.add(item);
            }
        }
        maidTipsOverlay.addTips("overlay.winefox_butcher.feed_maid.tips", items.toArray(Item[]::new));
    }
}

