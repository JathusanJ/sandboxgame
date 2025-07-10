package game.logic.world.blocks;

import game.logic.world.creature.Player;
import game.logic.world.items.ItemStack;
import game.logic.world.items.Items;
import game.logic.world.items.PickaxeItem;
import org.joml.Vector2f;

public class CoalOreBlock extends StoneBlock {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(4, 5), // TOP
                new Vector2f(4, 5), // BOTTOM
                new Vector2f(4, 5), // RIGHT
                new Vector2f(4, 5), // LEFT
                new Vector2f(4, 5), // FRONT
                new Vector2f(4, 5) // BACK
        };
    }

    @Override
    public ItemStack getAsDroppedItem(Player player, ItemStack handItemStack) {
        if(handItemStack.getItem() instanceof PickaxeItem) {
            return new ItemStack(Items.COAL);
        }

        return null;
    }
}
