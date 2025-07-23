package game.logic.world.blocks;

import game.logic.world.creature.Player;
import game.logic.world.items.ItemStack;
import game.logic.world.items.Items;
import game.logic.world.items.PickaxeItem;
import org.joml.Vector2f;

public class IronBlock extends StoneBlock {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(7, 4), // TOP
                new Vector2f(7, 4), // BOTTOM
                new Vector2f(7, 4), // RIGHT
                new Vector2f(7, 4), // LEFT
                new Vector2f(7, 4), // FRONT
                new Vector2f(7, 4) // BACK
        };
    }

    @Override
    public ItemStack getAsDroppedItem(Player player, ItemStack handItemStack) {
        if(handItemStack.getItem() instanceof PickaxeItem) {
            return new ItemStack(this.getAsItem());
        }

        return null;
    }
}
