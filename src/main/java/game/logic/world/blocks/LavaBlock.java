package game.logic.world.blocks;

import org.joml.Vector2f;

public class LavaBlock extends WaterBlock {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(8, 9), // TOP
                new Vector2f(8, 9), // BOTTOM
                new Vector2f(8, 9), // RIGHT
                new Vector2f(8, 9), // LEFT
                new Vector2f(8, 9), // FRONT
                new Vector2f(8, 9) // BACK
        };
    }
}
