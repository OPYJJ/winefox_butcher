package com.yourname.maidfox.guillotine;

import com.yourname.maidfox.init.ModBlocks;
import com.yourname.maidfox.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** The centre of a 3 by 3 by 4 structure. Only this block owns a block entity. */
public class GuillotineBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final ThreadLocal<Boolean> REMOVING = ThreadLocal.withInitial(() -> false);
    private static final VoxelShape BASE = Block.box(0, 0, 0, 16, 6, 16);
    private static final VoxelShape PILLAR = Block.box(4, 0, 4, 12, 16, 12);

    public GuillotineBlock() {
        super(Properties.copy(Blocks.OAK_PLANKS).strength(3.5F).noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GuillotineBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlocks.GUILLOTINE_BLOCK_ENTITY.get(), GuillotineBlockEntity::serverTick);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BASE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BASE;
    }

    static int index(int x, int y, int z) {
        return y * 9 + (z + 1) * 3 + x + 1;
    }

    static int localX(int index) { return index % 3 - 1; }
    static int localY(int index) { return index / 9; }
    static int localZ(int index) { return index / 3 % 3 - 1; }

    static BlockPos offset(BlockPos centre, Direction facing, int x, int y, int z) {
        Direction right = facing.getClockWise();
        return centre.offset(right.getStepX() * x + facing.getStepX() * z, y,
                right.getStepZ() * x + facing.getStepZ() * z);
    }

    static BlockPos centreFromPart(BlockPos part, BlockState state) {
        int index = state.getValue(GuillotinePartBlock.INDEX);
        return offset(part, state.getValue(GuillotinePartBlock.FACING), -localX(index), -localY(index), -localZ(index));
    }

    static VoxelShape partShape(int index, Direction facing) {
        int x = localX(index), y = localY(index), z = localZ(index);
        if (y == 0) {
            if (z == 0) return BASE;
            Direction towardCentre = z < 0 ? facing : facing.getOpposite();
            return switch (towardCentre) {
                case NORTH -> Block.box(0, 0, 0, 16, 6, 8);
                case SOUTH -> Block.box(0, 0, 8, 16, 6, 16);
                case EAST -> Block.box(8, 0, 0, 16, 6, 16);
                case WEST -> Block.box(0, 0, 0, 8, 6, 16);
                default -> BASE;
            };
        }
        if ((y == 1 || y == 2) && x != 0 && z == 0) return PILLAR;
        if (y == 3 && z == 0) {
            return facing.getAxis() == Direction.Axis.Z
                    ? Block.box(0, 10, 4, 16, 16, 12)
                    : Block.box(4, 10, 0, 12, 16, 16);
        }
        return Shapes.empty();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        if (!canPlaceStructure(context, facing)) return null;
        return defaultBlockState().setValue(FACING, facing);
    }

    private static boolean canPlaceStructure(BlockPlaceContext context, Direction facing) {
        Level level = context.getLevel();
        BlockPos centre = context.getClickedPos();
        for (int y = 0; y < 4; y++) for (int z = -1; z <= 1; z++) for (int x = -1; x <= 1; x++) {
            BlockPos p = offset(centre, facing, x, y, z);
            if (!level.isInWorldBounds(p) || !level.getWorldBorder().isWithinBounds(p) || !level.hasChunkAt(p)) return false;
            if (!level.getBlockState(p).canBeReplaced(context)) return false;
            if (y == 0 && !level.getBlockState(p.below()).isFaceSturdy(level, p.below(), Direction.UP)) return false;
        }
        return true;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide) return;
        Direction facing = state.getValue(FACING);
        for (int y = 0; y < 4; y++) for (int z = -1; z <= 1; z++) for (int x = -1; x <= 1; x++) {
            if (x == 0 && y == 0 && z == 0) continue;
            BlockPos p = offset(pos, facing, x, y, z);
            BlockState part = ModBlocks.GUILLOTINE_PART.get().defaultBlockState()
                    .setValue(GuillotinePartBlock.FACING, facing)
                    .setValue(GuillotinePartBlock.INDEX, index(x, y, z));
            if (!level.setBlock(p, part, Block.UPDATE_CLIENTS)) {
                breakStructure(level, pos, false);
                return;
            }
        }
    }

    static InteractionResult interact(Level level, BlockPos centre, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.CONSUME;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(centre) instanceof GuillotineBlockEntity machine) {
            return machine.interact(player);
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return interact(level, pos, player, hand);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (level.isClientSide) return;
        BlockPos centre = state.is(ModBlocks.GUILLOTINE_PART.get())
                ? GuillotineBlock.centreFromPart(pos, state) : pos;
        if (!(level.getBlockEntity(centre) instanceof GuillotineBlockEntity machine)) return;
        if (level.hasNeighborSignal(pos)) machine.tryExecuteByRedstone();
        else machine.tryResetByRedstone();
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) breakStructure(level, pos, !player.isCreative());
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (!state.is(next.getBlock()) && !level.isClientSide)
            breakStructure(level, pos, state.getValue(FACING), true);
        super.onRemove(state, level, pos, next, moving);
    }

    static void breakStructure(Level level, BlockPos centre, boolean drop) {
        if (!level.hasChunkAt(centre)) return;
        BlockState main = level.getBlockState(centre);
        if (!main.is(ModBlocks.GUILLOTINE.get())) return;
        breakStructure(level, centre, main.getValue(FACING), drop);
    }

    private static void breakStructure(Level level, BlockPos centre, Direction facing, boolean drop) {
        if (REMOVING.get() || !level.hasChunkAt(centre)) return;
        REMOVING.set(true);
        try {
            if (level.getBlockEntity(centre) instanceof GuillotineBlockEntity machine) machine.releaseForRemoval();
            for (int y = 0; y < 4; y++) for (int z = -1; z <= 1; z++) for (int x = -1; x <= 1; x++) {
                BlockPos p = offset(centre, facing, x, y, z);
                if (!level.hasChunkAt(p)) continue;
                BlockState s = level.getBlockState(p);
                if (p.equals(centre) ? s.is(ModBlocks.GUILLOTINE.get())
                        : s.is(ModBlocks.GUILLOTINE_PART.get()) && centreFromPart(p, s).equals(centre)) {
                    level.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
                }
            }
            if (drop) popResource(level, centre, new ItemStack(ModItems.GUILLOTINE.get()));
        } finally {
            REMOVING.set(false);
        }
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) { return PushReaction.BLOCK; }
}
