package game.shared.world.blocks;

import org.joml.Vector2f;

public class JungleLogBlock extends Block {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(2, 4), // TOP
                new Vector2f(2, 4), // BOTTOM
                new Vector2f(1, 4), // RIGHT
                new Vector2f(1, 4), // LEFT
                new Vector2f(1, 4), // FRONT
                new Vector2f(1, 4) // BACK
        };
    }
}
