package game.shared.world.blocks;

import org.joml.Vector2f;

public class OrangeTulipBlock extends CrossBlock {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(9, 0),
                new Vector2f(9, 0),
                new Vector2f(9, 0),
                new Vector2f(9, 0),
                new Vector2f(9, 0),
                new Vector2f(9, 0)
        };
    }
}
