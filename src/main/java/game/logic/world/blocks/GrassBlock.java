package game.logic.world.blocks;

import game.logic.world.creature.Player;
import game.logic.world.items.ItemStack;
import game.logic.world.items.Items;
import org.joml.Vector2f;

public class GrassBlock extends Block {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(3, 0), // TOP
                new Vector2f(5, 0), // BOTTOM
                new Vector2f(4, 0), // RIGHT
                new Vector2f(4, 0), // LEFT
                new Vector2f(4, 0), // FRONT
                new Vector2f(4, 0) // BACK
        };
    }

    @Override
    public ItemStack getAsDroppedItem(Player player, ItemStack handItemStack) {
        return new ItemStack(Items.DIRT);
    }
}
