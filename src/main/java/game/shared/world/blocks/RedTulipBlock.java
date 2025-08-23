package game.shared.world.blocks;

import org.joml.Vector2f;

public class RedTulipBlock extends CrossBlock {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(8, 0),
                new Vector2f(8, 0),
                new Vector2f(8, 0),
                new Vector2f(8, 0),
                new Vector2f(8, 0),
                new Vector2f(8, 0)
        };
    }
}
