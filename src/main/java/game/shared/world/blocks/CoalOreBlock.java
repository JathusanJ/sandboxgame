package game.shared.world.blocks;

import game.shared.world.creature.Player;
import game.shared.world.items.ItemStack;
import game.shared.world.items.Items;
import game.shared.world.items.PickaxeItem;
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
