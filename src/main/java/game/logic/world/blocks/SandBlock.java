package game.logic.world.blocks;

import org.joml.Vector2f;

public class SandBlock extends Block {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(6,2), // TOP
                new Vector2f(6,2), // BOTTOM
                new Vector2f(6,2), // RIGHT
                new Vector2f(6,2), // LEFT
                new Vector2f(6,2), // FRONT
                new Vector2f(6,2) // BACK
        };
    }
}
