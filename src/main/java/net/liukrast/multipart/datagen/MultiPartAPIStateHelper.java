package net.liukrast.multipart.datagen;

import net.liukrast.multipart.block.AbstractFacingMultipartBlock;
import net.liukrast.multipart.block.AbstractMultipartBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import java.util.function.BiConsumer;
/**
 * Contains useful methods for data generation
 * */
@SuppressWarnings("unused")
public class MultiPartAPIStateHelper {

    private MultiPartAPIStateHelper() {}

    /**
     * Generates blockstate for a facing multipart block.
     * Models will be read from {@code assets/modid/models/yourblock/...}
     * @param blockStateProvider the block state provider of your mod
     * @param block the block
     * */
    public static void facingMultipartBlock(BlockStateProvider blockStateProvider, AbstractFacingMultipartBlock block) {
        multiPartBlock(
                blockStateProvider,
                block,
                (state, id) -> "block/" + id + "/part_" + state.getValue(block.getPartsProperty()),
                (state, builder) -> builder.rotationY(((int)state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360));
    }

    /**
     * Generates blockstate for a multipart block.
     * Models will be read from {@code assets/modid/models/yourblock/...}
     * @param blockStateProvider the block state provider of your mod
     * @param block the block
     * */
    public static void multiPartBlock(BlockStateProvider blockStateProvider, AbstractMultipartBlock block) {
        multiPartBlock(
                blockStateProvider,
                block,
                (state, id) -> "block/" + id + "/part_" + state.getValue(block.getPartsProperty()),
                (state, builder) -> {}
        );
    }

    /**
     * Generates blockstate for a multipart block.
     * @param blockStateProvider the block state provider of your mod
     * @param block the block
     * @param pathProvider Allows choosing where your models are read based on the state and id
     * @param extraData Allows adding extra data based on the state
     * */
    public static void multiPartBlock(BlockStateProvider blockStateProvider, AbstractMultipartBlock block, PathProvider pathProvider, BiConsumer<BlockState, ConfiguredModel.Builder<?>> extraData) {
        var id = BuiltInRegistries.BLOCK.getKey(block);
        blockStateProvider.getVariantBuilder(block)
                .forAllStates(state -> {
                    var builder = ConfiguredModel.builder().modelFile(new ModelFile.ExistingModelFile(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), pathProvider.provide(state, id.getPath())), blockStateProvider.models().existingFileHelper));
                    extraData.accept(state, builder);
                    return builder.build();
                });
    }

    /**
     * A simple interface used to provide the path where your file is stored based on state and id
     * */
    public interface PathProvider {
        /**
         * The method for the functional interface.
         * @param state the current state
         * @param id the block id
         * @return the file path.
         * */
        String provide(BlockState state, String id);
    }
}
