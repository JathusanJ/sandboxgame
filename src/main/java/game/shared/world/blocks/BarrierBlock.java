package game.shared.world.blocks;

import game.shared.world.creature.Player;
import game.shared.world.items.ItemStack;
import org.joml.Vector2f;

public class BarrierBlock extends Block {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(0,7), // TOP
                new Vector2f(0,7), // BOTTOM
                new Vector2f(5,2), // RIGHT
                new Vector2f(5,2), // LEFT
                new Vector2f(5,2), // FRONT
                new Vector2f(5,2)  // BACK
        };
    }

    public int getBlockBreakingTicks(Player player, ItemStack heldItemStack) {
        return Integer.MAX_VALUE;
    }

    public ItemStack getAsDroppedItem(Player player, ItemStack tool) {
        return null;
    }
}
