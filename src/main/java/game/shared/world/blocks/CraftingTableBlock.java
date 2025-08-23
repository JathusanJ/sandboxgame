package game.shared.world.blocks;

import game.client.SandboxGame;
import game.client.ui.screen.CraftingTableScreen;
import game.client.world.ClientWorld;
import org.joml.Vector2f;
import org.joml.Vector3i;

public class CraftingTableBlock extends Block {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(5, 1), // TOP
                new Vector2f(0, 1), // BOTTOM
                new Vector2f(4, 1), // RIGHT
                new Vector2f(4, 1), // LEFT
                new Vector2f(6, 1), // FRONT
                new Vector2f(4, 1) // BACK
        };
    }

    @Override
    public boolean onRightClick(ClientWorld world, Vector3i blockPosition) {
        SandboxGame.getInstance().getGameRenderer().setScreen(new CraftingTableScreen());
        return false;
    }
}
