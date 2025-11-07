package net.liukrast.multipart.example;

import net.liukrast.multipart.block.AbstractFacingMultipartBlock;

@Deprecated
public class ExampleFacingMultipartBlock extends AbstractFacingMultipartBlock {
    /**
     * Constructs a new facing multipart block.
     *
     * @param properties the block properties.
     */
    public ExampleFacingMultipartBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void defineParts(Builder builder) {
        builder.define(0,0,0); //Origin block
        builder.define(1, 0, 0); //adds a block on the right
        builder.define(0, 1, 0); //adds a block on top of the origin
    }
}
