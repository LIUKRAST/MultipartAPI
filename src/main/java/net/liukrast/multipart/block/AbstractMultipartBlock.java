package net.liukrast.multipart.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link IMultipartBlock}
 *
 * Most cases do not need you to implement the interface, but to use this abstract class instead
 * If you want to see an example implementation, check {@link net.liukrast.multipart.example.ExampleMultipartBlock}
 */
@SuppressWarnings({"unused", "deprecation"})
public abstract class AbstractMultipartBlock extends Block implements IMultipartBlock {
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

    @Override
    public List<BlockPos> getPositions() {
        return positions;
    }

    @Override
    public void setPositions(List<BlockPos> positions) {
        this.positions = positions;
    }

    @Override
    public IntegerProperty getPartsProperty() {
        return property;
    }

    @Override
    public void setPartsProperty(IntegerProperty property) {
        this.property = property;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, LivingEntity placer, @NotNull ItemStack stack) {
        IMultipartBlock.super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        IMultipartBlock.super.createBlockStateDefinition$multipart(builder);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        return IMultipartBlock.super.canSurvive(state, level, pos);
    }

    @Override
    public void destroy(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state) {
        IMultipartBlock.super.destroy(level, pos, state);
    }

    /**
     * Builder to define relative positions of multipart block parts.
     */
    public static class Builder {
        private final List<BlockPos> positions = new ArrayList<>();
        /**
         * Main constructor for Builder.
         * You should not use this.
         * You will only care about {@link Builder#define(int, int, int)}
         * */
        @ApiStatus.Internal
        protected Builder() {}

        /**
         * Defines a new relative position for a part.
         * @param x relative X coordinate.
         * @param y relative Y coordinate.
         * @param z relative Z coordinate.
         * @return the builder instance for chaining.
         * @throws IllegalCallerException if the position is already defined.
         */
        @SuppressWarnings("UnusedReturnValue")
        public Builder define(int x, int y, int z) {
            var pos = new BlockPos(x,y,z);
            if(positions.contains(pos)) throw new IllegalCallerException(String.format("Position [%s, %s, %s] is already defined", x, y, z));
            positions.add(pos);
            return this;
        }

        /**
         * Internal code. Do not use.
         * @return a list of block positions
         * */
        @ApiStatus.Internal
        protected List<BlockPos> build() {
            if(positions.isEmpty()) throw new IllegalStateException("The multipart builder should not be empty");
            return ImmutableList.copyOf(positions);
        }
    }
}
