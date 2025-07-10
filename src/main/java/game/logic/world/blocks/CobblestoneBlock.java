package game.logic.world.blocks;

import game.logic.world.creature.Player;
import game.logic.world.items.ItemStack;
import game.logic.world.items.PickaxeItem;
import org.joml.Vector2f;

public class CobblestoneBlock extends StoneBlock {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(1,0), // TOP
                new Vector2f(1,0), // BOTTOM
                new Vector2f(1,0), // RIGHT
                new Vector2f(1,0), // LEFT
                new Vector2f(1,0), // FRONT
                new Vector2f(1,0) // BACK
        };
    }
}
