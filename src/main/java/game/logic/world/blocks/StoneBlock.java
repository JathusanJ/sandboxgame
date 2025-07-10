package game.logic.world.blocks;

import game.logic.world.creature.Player;
import game.logic.world.items.ItemStack;
import game.logic.world.items.Items;
import game.logic.world.items.PickaxeItem;
import org.joml.Vector2f;

public class StoneBlock extends Block {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(6, 0), // TOP
                new Vector2f(6, 0), // BOTTOM
                new Vector2f(6, 0), // RIGHT
                new Vector2f(6, 0), // LEFT
                new Vector2f(6, 0), // FRONT
                new Vector2f(6, 0) // BACK
        };
    }

    @Override
    public int getBlockBreakingTicks(Player player, ItemStack heldItemStack) {
        if(heldItemStack.getItem() instanceof PickaxeItem pickaxe) {
            return (int) (20 / pickaxe.tier.getStrength());
        }

        return 40;
    }

    @Override
    public ItemStack getAsDroppedItem(Player player, ItemStack handItemStack) {
        if(handItemStack.getItem() instanceof PickaxeItem) {
            return new ItemStack(Items.COBBLESTONE);
        }

        return null;
    }
}
