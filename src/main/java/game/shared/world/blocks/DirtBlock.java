package game.shared.world.blocks;

import org.joml.Vector2f;

public class DirtBlock extends Block {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(5, 0), // TOP
                new Vector2f(5, 0), // BOTTOM
                new Vector2f(5, 0), // RIGHT
                new Vector2f(5, 0), // LEFT
                new Vector2f(5, 0), // FRONT
                new Vector2f(5, 0) // BACK
        };
    }
}
