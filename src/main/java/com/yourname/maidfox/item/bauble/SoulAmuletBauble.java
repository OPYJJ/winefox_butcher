package com.yourname.maidfox.item.bauble;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

public class SoulAmuletBauble
implements IMaidBauble {
    private static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("3f2e1a9c-5d7b-4a6e-8c1f-2b3d4e5f6a7b");
    private static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("4a3f2b1d-6e8c-5b7f-9d2e-3c4e5f6a7b8c");
    private static final UUID MAX_HEALTH_MODIFIER = UUID.fromString("5b4a3c2e-7f9d-6c8a-ae3f-4d5e6f7a8b9d");
    private static final double DOUBLE_AMOUNT = 1.0;

    public void onPutOn(EntityMaid maid, ItemStack baubleItem) {
        if (maid.level().isClientSide()) {
            return;
        }
        this.addDoublingModifier(maid, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_MODIFIER);
        this.addDoublingModifier(maid, Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER);
        this.addDoublingModifier(maid, Attributes.MAX_HEALTH, MAX_HEALTH_MODIFIER);
        maid.setHealth(maid.getMaxHealth());
    }

    public void onTakeOff(EntityMaid maid, ItemStack baubleItem) {
        if (maid.level().isClientSide()) {
            return;
        }
        this.removeModifier(maid, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_MODIFIER);
        this.removeModifier(maid, Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER);
        this.removeModifier(maid, Attributes.MAX_HEALTH, MAX_HEALTH_MODIFIER);
        if (maid.getHealth() > maid.getMaxHealth()) {
            maid.setHealth(maid.getMaxHealth());
        }
    }

    private void addDoublingModifier(EntityMaid maid, Attribute attribute, UUID uuid) {
        AttributeInstance instance = maid.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        if (instance.getModifier(uuid) == null) {
            instance.addPermanentModifier(new AttributeModifier(uuid, "Soul Amulet", 1.0, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    private void removeModifier(EntityMaid maid, Attribute attribute, UUID uuid) {
        AttributeInstance instance = maid.getAttribute(attribute);
        if (instance != null && instance.getModifier(uuid) != null) {
            instance.removeModifier(uuid);
        }
    }
}

