package game.logic.world.blocks;

import org.joml.Vector2f;

public class BirchPlanksBlock extends Block {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(0, 2), // TOP
                new Vector2f(0, 2), // BOTTOM
                new Vector2f(0, 2), // RIGHT
                new Vector2f(0, 2), // LEFT
                new Vector2f(0, 2), // FRONT
                new Vector2f(0, 2) // BACK
        };
    }
}
