package game.logic.world.blocks;

import org.joml.Vector2f;

public class OakPlanksBlock extends Block {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(0, 1), // TOP
                new Vector2f(0, 1), // BOTTOM
                new Vector2f(0, 1), // RIGHT
                new Vector2f(0, 1), // LEFT
                new Vector2f(0, 1), // FRONT
                new Vector2f(0, 1) // BACK
        };
    }
}
