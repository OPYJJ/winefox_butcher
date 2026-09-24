package com.yourname.maidfox.delight.compat.farmersdelight;

import com.yourname.maidfox.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.registry.ModEffects;
import vectorwing.farmersdelight.common.registry.ModSounds;

import javax.annotation.Nullable;

/**
 * Roast fox: a Farmer's Delight style feast block.
 * <p>
 * Eaten directly with right click, cake style (no serving item is taken out). It occupies two
 * blocks and faces the direction the player was facing when placing it (same as beds).
 * <ul>
 *     <li>{@code servings} 0..5 map to six stage models: 0 = complete, 5 = only the fox head left on the tray</li>
 *     <li>right click with servings 0..4 = eat one serving (hunger +6, saturation +4.4, refresh 5 minutes of Nourishment), stage +1</li>
 *     <li>right click with servings 5 = take the winefox head; the block disappears and drops a cutting board</li>
 *     <li>breaking: with servings=0 it drops the roast fox itself, otherwise it drops a cutting board
 *         (the loot table decides by part=head + servings)</li>
 * </ul>
 */
@SuppressWarnings("deprecation")
public class WinefoxRoastFoxBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final IntegerProperty SERVINGS = IntegerProperty.create("servings", 0, 5);

    /** Number of servings, which is also the highest stage index (servings 5 = only the head left, handled as a take interaction). */
    public static final int MAX_SERVINGS = 5;

    /** Hunger restored per serving: 2 from cake + 4. */
    private static final int NUTRITION = 6;

    /**
     * Saturation restored per serving: 0.4 from cake + 4 = 4.4.
     * {@code FoodData#eat(nutrition, saturationModifier)} yields nutrition * modifier * 2.
     */
    private static final float SATURATION_MODIFIER = 4.4F / (NUTRITION * 2.0F);

    /** Duration of the Nourishment effect (Farmer's Delight): 5 minutes. */
    private static final int NOURISHMENT_TICKS = 6000;

    /** Collision box per half block: 8 pixels tall (0.5 blocks, still walkable) so the middle of the model can be clicked. */
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);

    public WinefoxRoastFoxBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, BedPart.FOOT)
                .setValue(SERVINGS, 0));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    /** Only the FOOT half holds the block entity; the renderer uses it as the anchor and draws the whole two-block model. */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == BedPart.HEAD ? null : new WinefoxRoastFoxBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSupportRigidBlock(level, pos.below());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level,
                                  BlockPos currentPos, BlockPos facingPos) {
        if (facing == getNeighbourDirection(state.getValue(PART), state.getValue(FACING))) {
            return facingState.is(this) && facingState.getValue(PART) != state.getValue(PART)
                    ? state
                    : Blocks.AIR.defaultBlockState();
        }
        return facing == Direction.DOWN && !state.canSurvive(level, currentPos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        BlockPos headPos = context.getClickedPos().relative(direction);
        Level level = context.getLevel();
        if (!level.getBlockState(headPos).canBeReplaced(context) || !level.getWorldBorder().isWithinBounds(headPos)) {
            return null;
        }
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            BlockPos headPos = pos.relative(state.getValue(FACING));
            level.setBlock(headPos, state.setValue(PART, BedPart.HEAD), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            BedPart part = state.getValue(PART);
            // In creative mode, breaking either half silently removes the other half to avoid dropping a duplicate (flag 35 includes 32 = do not drop)
            BlockPos otherPos = pos.relative(getNeighbourDirection(part, state.getValue(FACING)));
            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.is(this) && otherState.getValue(PART) != part) {
                level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, otherPos, Block.getId(otherState));
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        if (state.getValue(SERVINGS) >= MAX_SERVINGS) {
            if (!level.isClientSide) {
                takeHead(level, pos, state);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        // Same as cake: eating is blocked when the player cannot eat (Farmer's Delight's Nourishment keeps canEat true, so eating can continue after the first bite)
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            eatServing(level, pos, state, player);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void eatServing(Level level, BlockPos pos, BlockState state, Player player) {
        player.getFoodData().eat(NUTRITION, SATURATION_MODIFIER);
        level.gameEvent(player, GameEvent.EAT, pos);

        // Eating again refreshes the full 5 minutes: remove the effect first so the remaining duration does not cap it
        if (player.hasEffect(ModEffects.NOURISHMENT.get())) {
            player.removeEffect(ModEffects.NOURISHMENT.get());
        }
        player.addEffect(new MobEffectInstance(ModEffects.NOURISHMENT.get(), NOURISHMENT_TICKS, 0, false, false));

        setServings(level, pos, state, state.getValue(SERVINGS) + 1);

        level.playSound(null, pos, ModSounds.BLOCK_FOOD_TAKE_PORTION.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                    pos.getX() + 0.5D, pos.getY() + 0.3D, pos.getZ() + 0.5D,
                    3, 0.2D, 0.1D, 0.2D, 0.001D);
        }
    }

    /**
     * Stage 5, taking the head: drops one winefox head and destroys the HEAD half (the loot table
     * produces the cutting board there); the neighbour update removes the FOOT half as well
     * without dropping anything twice.
     */
    private void takeHead(Level level, BlockPos pos, BlockState state) {
        BlockPos headPos = state.getValue(PART) == BedPart.HEAD
                ? pos
                : pos.relative(getNeighbourDirection(BedPart.FOOT, state.getValue(FACING)));

        ItemStack head = new ItemStack(ModItems.WINEFOX_HEAD_ITEM.get());
        ItemEntity itemEntity = new ItemEntity(level,
                headPos.getX() + 0.5D, headPos.getY() + 0.5D, headPos.getZ() + 0.5D, head);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);

        level.playSound(null, pos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 0.8F, 0.8F);
        level.destroyBlock(headPos, true);
    }

    /** The stage is stored once and updated on both halves. */
    private void setServings(Level level, BlockPos pos, BlockState state, int servings) {
        level.setBlock(pos, state.setValue(SERVINGS, servings), 3);
        BlockPos otherPos = pos.relative(getNeighbourDirection(state.getValue(PART), state.getValue(FACING)));
        BlockState otherState = level.getBlockState(otherPos);
        if (otherState.is(this) && otherState.getValue(PART) != state.getValue(PART)) {
            level.setBlock(otherPos, otherState.setValue(SERVINGS, servings), 3);
        }
    }

    /** FOOT -> facing direction, HEAD -> opposite of the facing. */
    private static Direction getNeighbourDirection(BedPart part, Direction facing) {
        return part == BedPart.FOOT ? facing : facing.getOpposite();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, SERVINGS);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return (MAX_SERVINGS - state.getValue(SERVINGS)) * 3;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }
}
