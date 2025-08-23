package game.shared.world.blocks;

import game.shared.world.creature.Player;
import game.shared.world.items.ItemStack;
import game.shared.world.items.Items;
import game.shared.world.items.PickaxeItem;
import org.joml.Vector2f;

public class IronOreBlock extends StoneBlock {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(5, 4), // TOP
                new Vector2f(5, 4), // BOTTOM
                new Vector2f(5, 4), // RIGHT
                new Vector2f(5, 4), // LEFT
                new Vector2f(5, 4), // FRONT
                new Vector2f(5, 4) // BACK
        };
    }

    @Override
    public ItemStack getAsDroppedItem(Player player, ItemStack handItemStack) {
        if(handItemStack.getItem() instanceof PickaxeItem pickaxeItem && pickaxeItem.tier.getStrength() >= PickaxeItem.PickaxeTier.STONE.getStrength()) {
            return new ItemStack(Items.RAW_IRON_CHUNK);
        }

        return null;
    }
}
