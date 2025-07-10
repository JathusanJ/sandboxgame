package game.logic.world.blocks;

import org.joml.Vector2f;

public class AcaciaPlanksBlock extends Block {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(0, 3), // TOP
                new Vector2f(0, 3), // BOTTOM
                new Vector2f(0, 3), // RIGHT
                new Vector2f(0, 3), // LEFT
                new Vector2f(0, 3), // FRONT
                new Vector2f(0, 3) // BACK
        };
    }
}
