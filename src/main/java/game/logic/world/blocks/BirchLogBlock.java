package game.logic.world.blocks;

import org.joml.Vector2f;

public class BirchLogBlock extends Block {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(2, 2), // TOP
                new Vector2f(2, 2), // BOTTOM
                new Vector2f(1, 2), // RIGHT
                new Vector2f(1, 2), // LEFT
                new Vector2f(1, 2), // FRONT
                new Vector2f(1, 2) // BACK
        };
    }
}
