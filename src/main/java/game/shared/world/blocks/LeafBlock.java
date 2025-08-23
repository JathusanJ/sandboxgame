package game.shared.world.blocks;

import game.shared.world.World;
import game.shared.world.creature.Player;
import game.shared.world.items.ItemStack;
import game.shared.world.items.Items;

public class LeafBlock extends Block {
    @Override
    public boolean shouldCreateTopFace(World world, int x, int y, int z, Block neighboringBlock) {
        return true;
    }

    @Override
    public boolean shouldCreateBottomFace(World world, int x, int y, int z, Block neighboringBlock) {
        return true;
    }

    @Override
    public boolean shouldCreateFrontFace(World world, int x, int y, int z, Block neighboringBlock) {
        return true;
    }

    @Override
    public boolean shouldCreateBackFace(World world, int x, int y, int z, Block neighboringBlock) {
        return true;
    }

    @Override
    public boolean shouldCreateRightFace(World world, int x, int y, int z, Block neighboringBlock) {
        return true;
    }

    @Override
    public boolean shouldCreateLeftFace(World world, int x, int y, int z, Block neighboringBlock) {
        return true;
    }

    @Override
    public int getBlockBreakingTicks(Player player, ItemStack heldItemStack) {
        return 10;
    }

    @Override
    public ItemStack getAsDroppedItem(Player player, ItemStack handItemStack) {
        if(player.world.random.nextInt() % 5 == 0) {
            return new ItemStack(Items.STICK);
        }

        return null;
    }
}
