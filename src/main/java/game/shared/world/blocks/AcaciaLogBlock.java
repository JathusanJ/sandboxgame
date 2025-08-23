package game.shared.world.blocks;

import org.joml.Vector2f;

public class AcaciaLogBlock extends Block {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(2, 3), // TOP
                new Vector2f(2, 3), // BOTTOM
                new Vector2f(1, 3), // RIGHT
                new Vector2f(1, 3), // LEFT
                new Vector2f(1, 3), // FRONT
                new Vector2f(1, 3) // BACK
        };
    }
}
