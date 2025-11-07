package net.liukrast.multipart.example;

import net.liukrast.multipart.block.AbstractMultipartBlock;

/**
 * Example code of a multipart block. Do not use nor extend
 * */
@Deprecated
public class ExampleMultipartBlock extends AbstractMultipartBlock {
    /**
     * Constructor for the block.
     *
     * @param properties the block properties.
     */
    public ExampleMultipartBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void defineParts(Builder builder) {
        builder.define(0,0,0); //one bock in the origin
        builder.define(0, 1, 0); //one block above!
        builder.define(1, 0, 1); //one block on x+1 from the origin!
    }
}
