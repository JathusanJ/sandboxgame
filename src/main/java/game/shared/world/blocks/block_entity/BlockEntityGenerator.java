package game.shared.world.blocks.block_entity;

import game.shared.world.World;

public interface BlockEntityGenerator<T extends BlockEntity> {
    T createBlockEntity(World world, int x, int y, int z);
}
