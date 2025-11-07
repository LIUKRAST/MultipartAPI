package net.liukrast.multipart.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for multipart blocks with horizontal facing.
 * Extends {@link AbstractMultipartBlock} and adds horizontal orientation support.
 * Check {@link net.liukrast.multipart.example.ExampleFacingMultipartBlock} for an example usage
 */
@SuppressWarnings({"unused", "deprecation"})
public abstract class AbstractFacingMultipartBlock extends AbstractMultipartBlock {
    /**
     * The horizontal facing direction property (NORTH, SOUTH, EAST, WEST).
     */
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    /**
     * Constructs a new facing multipart block.
     * @param properties the block properties.
     */
    public AbstractFacingMultipartBlock(Properties properties) {
        super(properties);
    }

    /**
     * Returns the horizontal-facing direction of the block.
     * @param state the current block state.
     * @return the direction the block is facing.
     */
    @Override
    public @NotNull Direction getDirection(BlockState state) {
        return state.getValue(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    protected @NotNull BlockState mirror(@NotNull BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }
}

