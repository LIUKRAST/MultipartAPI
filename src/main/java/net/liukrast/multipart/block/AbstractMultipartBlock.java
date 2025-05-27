package net.liukrast.multipart.block;

import com.google.common.collect.ImmutableList;
import net.liukrast.multipart.MultipartAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Abstract class for multipart blocks composed of multiple parts
 * positioned relative to each other.
 */
@SuppressWarnings("unused")
public abstract class AbstractMultipartBlock extends Block {
    private List<BlockPos> positions;
    private IntegerProperty property;

    /**
     * Constructor for the block.
     * @param properties the block properties.
     */
    public AbstractMultipartBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(property, 0));
    }

    /**
     * Abstract method to define the relative positions of the block parts.
     * @param builder the builder used to define positions.
     */
    public abstract void defineParts(Builder builder);

    /**
     * Returns the direction of the block.
     * @param state the block state.
     * @return the block direction (default NORTH).
     */
    public Direction getDirection(BlockState state) {
        return Direction.NORTH;
    }

    /**
     * Returns the integer property representing the part of the block.
     * @return the IntegerProperty associated with the parts.
     */
    public IntegerProperty getPartsProperty() {
        return this.property;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, BlockPos pos, @NotNull BlockState state, LivingEntity placer, @NotNull ItemStack stack) {
        var direction = getDirection(state);
        var statePos = positions.get(state.getValue(property));
        var origin = pos.relative(direction, statePos.getZ()).relative(Direction.UP, -statePos.getY()).relative(direction.getCounterClockWise(), -statePos.getX());
        for(int i = 0; i < positions.size(); i++) {
            var pos1 = positions.get(i);
            level.setBlock(origin.relative(direction, -pos1.getZ()).relative(Direction.UP, pos1.getY()).relative(direction.getCounterClockWise(), pos1.getX()), state.setValue(property, i), 3);
        }
    }

    /**
     * Calculates the origin position of the multipart given a position and direction.
     * @param pos the reference position.
     * @param statePos the relative part position.
     * @param direction the block direction.
     * @return the origin position in the world.
     */
    public BlockPos getOrigin(BlockPos pos, BlockPos statePos, Direction direction) {
        return pos.relative(direction, statePos.getZ()).relative(Direction.UP, -statePos.getY()).relative(direction.getCounterClockWise(), -statePos.getX());
    }

    /**
     * Calculates the relative position of a part from the origin.
     * @param pos the origin position.
     * @param statePos the relative part position.
     * @param direction the block direction.
     * @return the relative position in the world.
     */
    public BlockPos getRelative(BlockPos pos, BlockPos statePos, Direction direction) {
        return pos.relative(direction, -statePos.getZ()).relative(Direction.UP, statePos.getY()).relative(direction.getCounterClockWise(), statePos.getX());
    }

    /**
     * Performs an operation on every part of the multipart block.
     * @param pos the block position.
     * @param state the block state.
     * @param consumer the operation to perform on each part position.
     */
    public void forEachElement(BlockPos pos, BlockState state, Consumer<BlockPos> consumer) {
        var direction = getDirection(state);
        var statePos = positions.get(state.getValue(property));
        var origin = getOrigin(pos, statePos, direction);
        for(BlockPos temp : positions) {
            consumer.accept(getRelative(origin, temp, direction));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        var builder1 = new Builder();
        defineParts(builder1);
        this.positions = builder1.build();
        this.property = IntegerProperty.create("part", 0, positions.size()-1);
        builder.add(this.property);
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        var direction = getDirection(state);
        var statePos = positions.get(state.getValue(property));
        var origin = getOrigin(pos, statePos, direction);
        boolean bl = true;
        for (BlockPos temp : positions) {
            var pos1 = getRelative(origin, temp, direction);
            var state1 = level.getBlockState(pos1);
            if (state1.canBeReplaced()) continue;
            if(level.isClientSide()) MultipartAPI.notify(pos1);
            bl = false;
        }
        return bl;
    }

    @Override
    public void destroy(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state) {
        var direction = getDirection(state);
        var statePos = positions.get(state.getValue(property));
        var origin = getOrigin(pos, statePos, direction);
        for (BlockPos temp : positions) {
            var pos1 = getRelative(origin, temp, direction);
            level.destroyBlock(pos1, false);
        }
    }

    /**
     * Returns the total number of parts composing the multipart block.
     * @return the number of parts.
     */
    public int size() {
        return positions.size();
    }

    /**
     * Builder to define relative positions of multipart block parts.
     */
    public static class Builder {
        private final List<BlockPos> positions = new ArrayList<>();
        private Builder() {}

        /**
         * Defines a new relative position for a part.
         * @param x relative X coordinate.
         * @param y relative Y coordinate.
         * @param z relative Z coordinate.
         * @return the builder instance for chaining.
         * @throws IllegalCallerException if the position is already defined.
         */
        public Builder define(int x, int y, int z) {
            var pos = new BlockPos(x,y,z);
            if(positions.contains(pos)) throw new IllegalCallerException(String.format("Position [%s, %s, %s] is already defined", x, y, z));
            positions.add(pos);
            return this;
        }

        private List<BlockPos> build() {
            if(positions.isEmpty()) throw new IllegalStateException("The multipart builder should not be empty");
            return ImmutableList.copyOf(positions);
        }
    }
}
