package com.yourname.maidfox.init;

import com.yourname.maidfox.block.WinefoxFurBedBlock;
import com.yourname.maidfox.block.WinefoxFurBedBlockEntity;
import com.yourname.maidfox.block.WinefoxHeadBlock;
import com.yourname.maidfox.block.WinefoxHeadBlockEntity;
import com.yourname.maidfox.block.WinefoxWallHeadBlock;
import com.yourname.maidfox.guillotine.GuillotineBlock;
import com.yourname.maidfox.guillotine.GuillotineBlockEntity;
import com.yourname.maidfox.guillotine.GuillotinePartBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "winefox_butcher");
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "winefox_butcher");
    public static final RegistryObject<WinefoxHeadBlock> WINEFOX_HEAD_BLOCK = BLOCKS.register("winefox_head", WinefoxHeadBlock::new);
    public static final RegistryObject<WinefoxWallHeadBlock> WINEFOX_WALL_HEAD_BLOCK = BLOCKS.register("winefox_wall_head", WinefoxWallHeadBlock::new);
    public static final RegistryObject<WinefoxFurBedBlock> WINEFOX_FUR_BED = BLOCKS.register("winefox_fur_bed", WinefoxFurBedBlock::new);
    // Guillotine: the main block sits at the centre of the 3x3x4 structure and owns the block entity
    public static final RegistryObject<GuillotineBlock> GUILLOTINE = BLOCKS.register("guillotine", GuillotineBlock::new);
    public static final RegistryObject<GuillotinePartBlock> GUILLOTINE_PART = BLOCKS.register("guillotine_part", GuillotinePartBlock::new);
    public static final RegistryObject<BlockEntityType<WinefoxHeadBlockEntity>> WINEFOX_HEAD_BLOCK_ENTITY = BLOCK_ENTITIES.register("winefox_head", () -> BlockEntityType.Builder.of(WinefoxHeadBlockEntity::new, WINEFOX_HEAD_BLOCK.get(), WINEFOX_WALL_HEAD_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<WinefoxFurBedBlockEntity>> WINEFOX_FUR_BED_BLOCK_ENTITY = BLOCK_ENTITIES.register("winefox_fur_bed", () -> BlockEntityType.Builder.of(WinefoxFurBedBlockEntity::new, WINEFOX_FUR_BED.get()).build(null));
    public static final RegistryObject<BlockEntityType<GuillotineBlockEntity>> GUILLOTINE_BLOCK_ENTITY = BLOCK_ENTITIES.register("guillotine", () -> BlockEntityType.Builder.of(GuillotineBlockEntity::new, GUILLOTINE.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
    }
}

