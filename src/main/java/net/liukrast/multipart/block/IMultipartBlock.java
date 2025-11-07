package net.liukrast.multipart.block;

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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

/**
 * Unimplemented version of {@link AbstractMultipartBlock}. You will have to introduce
 * */
@SuppressWarnings("JavadocReference")
public interface IMultipartBlock {
    /**
     * Abstract method to define the relative positions of the block parts.
     * @param builder the builder used to define positions.
     */
    void defineParts(AbstractMultipartBlock.Builder builder);

    /**
     * Returns the direction of the block.
     * @param state the block state.
     * @return the block direction (default NORTH).
     */
    default Direction getDirection(BlockState state) {
        return Direction.NORTH;
    }

    /**
     * It should not be used anywhere, only overwritten;
     * If you're trying to access the size use {@link IMultipartBlock#size()}
     * @return the positions you internally stored. See {@link AbstractMultipartBlock#positions}
     * */
    @ApiStatus.OverrideOnly
    List<BlockPos> getPositions();

    /**
     * Internally sets the position list to your implemented code. It should not be used anywhere, only overwritten
     * @param positions positions the API is trying to set, which your code stored internally. See {@link AbstractMultipartBlock#positions}
     * */
    @ApiStatus.OverrideOnly
    void setPositions(List<BlockPos> positions);

    /**
     * Gives you the IntegerProperty of this precise multipart block
     * @return the IntegerProperty associated with the parts.
     */
    IntegerProperty getPartsProperty();

    /**
     * Sets the property.
     * You should not use this, only override it,
     * to set a variable internally like {@link AbstractMultipartBlock#property}
     * @param property the properties to be set
     * */
    @ApiStatus.OverrideOnly
    void setPartsProperty(IntegerProperty property);

    /**
     * Boilerplate method to override {@link Block#setPlacedBy(Level, BlockPos, BlockState, LivingEntity, ItemStack)}
     * @param level the level
     * @param pos the block position
     * @param state the block state
     * @param placer the block placer
     * @param stack the item stack used by the placer
     * */
    default void setPlacedBy(@NotNull Level level, BlockPos pos, @NotNull BlockState state, LivingEntity placer, @NotNull ItemStack stack) {
        var direction = getDirection(state);
        var positions = getPositions();
        var statePos = positions.get(state.getValue(getPartsProperty()));
        var origin = pos.relative(direction, statePos.getZ()).relative(Direction.UP, -statePos.getY()).relative(direction.getCounterClockWise(), -statePos.getX());
        for(int i = 0; i < positions.size(); i++) {
            var pos1 = positions.get(i);
            level.setBlock(origin.relative(direction, -pos1.getZ()).relative(Direction.UP, pos1.getY()).relative(direction.getCounterClockWise(), pos1.getX()), state.setValue(getPartsProperty(), i), 3);
        }
    }

    /**
     * Calculates the origin position of the multipart given a position and direction.
     * @param pos the reference position.
     * @param statePos the relative part position.
     * @param direction the block direction.
     * @return the origin position in the world.
     */
    default BlockPos getOrigin(BlockPos pos, BlockPos statePos, Direction direction) {
        return pos.relative(direction, statePos.getZ()).relative(Direction.UP, -statePos.getY()).relative(direction.getCounterClockWise(), -statePos.getX());
    }

    /**
     * Calculates the relative position of a part from the origin.
     * @param pos the origin position.
     * @param statePos the relative part position.
     * @param direction the block direction.
     * @return the relative position in the world.
     */
    default BlockPos getRelative(BlockPos pos, BlockPos statePos, Direction direction) {
        return pos.relative(direction, -statePos.getZ()).relative(Direction.UP, statePos.getY()).relative(direction.getCounterClockWise(), statePos.getX());
    }

    /**
     * Performs an operation on every part of the multipart block.
     * @param pos the block position.
     * @param state the block state.
     * @param consumer the operation to perform on each part position.
     */
    @SuppressWarnings("unused")
    default void forEachElement(BlockPos pos, BlockState state, Consumer<BlockPos> consumer) {
        var direction = getDirection(state);
        var statePos = getPositions().get(state.getValue(getPartsProperty()));
        var origin = getOrigin(pos, statePos, direction);
        for(BlockPos temp : getPositions()) {
            consumer.accept(getRelative(origin, temp, direction));
        }
    }

    /**
     * Boilerplate method to create the blockstate definition.
     * @param builder the block state builder
     * */
    default void createBlockStateDefinition$multipart(StateDefinition.Builder<Block, BlockState> builder) {
        var builder1 = new AbstractMultipartBlock.Builder();
        defineParts(builder1);
        setPositions(builder1.build());
        setPartsProperty(IntegerProperty.create("part", 0, size()-1));
        builder.add(getPartsProperty());
    }

    /**
     * Boilerplate method to override {@link net.minecraft.world.level.block.state.BlockBehaviour#canSurvive(BlockState, LevelReader, BlockPos)}
     * @param state the blockstate
     * @param level the level
     * @param pos the block pos
     * @return whether the block can survive or not at that position. Will check for all other blocks of the multipart
     * */
    default boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        var direction = getDirection(state);
        var statePos = getPositions().get(state.getValue(getPartsProperty()));
        var origin = getOrigin(pos, statePos, direction);
        boolean bl = true;
        for (BlockPos temp : getPositions()) {
            var pos1 = getRelative(origin, temp, direction);
            var state1 = level.getBlockState(pos1);
            if (state1.canBeReplaced()) continue;
            if(level.isClientSide()) MultipartAPI.notify(pos1);
            bl = false;
        }
        return bl;
    }

    /**
     * Boilerplate method to override {@link Block#destroy(LevelAccessor, BlockPos, BlockState)}
     * @param level the level
     * @param pos the block position
     * @param state the block state
     * */
    default void destroy(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state) {
        var direction = getDirection(state);
        var statePos = getPositions().get(state.getValue(getPartsProperty()));
        var origin = getOrigin(pos, statePos, direction);
        for (BlockPos temp : getPositions()) {
            var pos1 = getRelative(origin, temp, direction);
            if(!level.getBlockState(pos1).is(state.getBlock())) continue;
            level.destroyBlock(pos1, false);
        }
    }

    /**
     * Returns the total number of parts composing the multipart block.
     * @return the number of parts.
     */
    default int size() {
        return getPositions().size();
    }
}
