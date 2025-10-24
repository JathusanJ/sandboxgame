package game.shared.world.blocks;

public class BlockState<T extends AbstractBlock> {
    public T block;

    public T getBlock() {
        return this.block;
    }
}
