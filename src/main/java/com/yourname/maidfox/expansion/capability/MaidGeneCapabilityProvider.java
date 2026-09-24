package com.yourname.maidfox.expansion.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class MaidGeneCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<IMaidGeneCapability> MAID_GENE_CAP = CapabilityManager.get(new CapabilityToken<>() {
    });

    private final MaidGeneCapability instance = new MaidGeneCapability();
    private final LazyOptional<IMaidGeneCapability> lazyOptional = LazyOptional.of(() -> instance);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return cap == MAID_GENE_CAP ? lazyOptional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.deserializeNBT(nbt);
    }
}

