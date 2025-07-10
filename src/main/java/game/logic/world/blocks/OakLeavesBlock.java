package game.logic.world.blocks;

import game.logic.world.creature.Player;
import game.logic.world.items.ItemStack;
import org.joml.Vector2f;

public class OakLeavesBlock extends LeafBlock {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(3, 1), // TOP
                new Vector2f(3, 1), // BOTTOM
                new Vector2f(3, 1), // RIGHT
                new Vector2f(3, 1), // LEFT
                new Vector2f(3, 1), // FRONT
                new Vector2f(3, 1) // BACK
        };
    }
}
