package game.logic.world.blocks.block_entity;

import game.logic.world.World;

public interface BlockEntityGenerator<T extends BlockEntity> {
    T createBlockEntity(World world, int x, int y, int z);
}
