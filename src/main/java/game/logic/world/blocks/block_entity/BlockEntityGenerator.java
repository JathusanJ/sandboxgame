package game.logic.world.blocks.block_entity;

public interface BlockEntityGenerator<T extends BlockEntity> {
    T createBlockEntity();
}
