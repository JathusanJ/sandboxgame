package game.shared.world.blocks;

// Workaround to make sure BlockState accepts all block classes
public abstract class AbstractBlock {
    public abstract String getBlockId();
    public abstract BlockState<?> getDefaultBlockState();
}
