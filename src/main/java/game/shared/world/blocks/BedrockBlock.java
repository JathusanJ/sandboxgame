package game.shared.world.blocks;

import game.shared.world.creature.Player;
import game.shared.world.items.ItemStack;
import org.joml.Vector2f;

public class BedrockBlock extends Block {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(2,0), // TOP
                new Vector2f(2,0), // BOTTOM
                new Vector2f(2,0), // RIGHT
                new Vector2f(2,0), // LEFT
                new Vector2f(2,0), // FRONT
                new Vector2f(2,0) // BACK
        };
    }

    @Override
    public int getBlockBreakingTicks(Player player, ItemStack heldItemStack) {
        return Integer.MAX_VALUE;
    }
}
