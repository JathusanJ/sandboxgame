package game.shared.world.blocks;

import org.joml.Vector2f;

public class PumpkinBlock extends Block {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(9, 5), // TOP
                new Vector2f(9, 4), // BOTTOM
                new Vector2f(8, 5), // RIGHT
                new Vector2f(8, 5), // LEFT
                new Vector2f(8, 5), // FRONT
                new Vector2f(8, 5) // BACK
        };
    }
}
