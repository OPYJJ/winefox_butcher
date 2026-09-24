package com.yourname.maidfox.task;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.github.tartaricacid.touhoulittlemaid.item.bauble.BaubleManager;
import com.yourname.maidfox.init.ModItems;
import com.yourname.maidfox.item.bauble.SoulAmuletBauble;
import com.yourname.maidfox.task.TaskButcher;
import net.minecraft.world.item.Item;

@LittleMaidExtension
public class MaidTasks
implements ILittleMaid {
    public void addMaidTask(TaskManager manager) {
        manager.add(new TaskButcher());
    }

    public void bindMaidBauble(BaubleManager manager) {
        manager.bind(ModItems.SOUL_AMULET.get(), new SoulAmuletBauble());
    }
}

