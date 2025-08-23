package game.shared.world.blocks;

import org.joml.Vector2f;

public class YellowTulipBlock extends CrossBlock {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(9, 1),
                new Vector2f(9, 1),
                new Vector2f(9, 1),
                new Vector2f(9, 1),
                new Vector2f(9, 1),
                new Vector2f(9, 1)
        };
    }
}
