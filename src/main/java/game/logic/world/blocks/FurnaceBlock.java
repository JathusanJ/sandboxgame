package game.logic.world.blocks;

import game.client.SandboxGame;
import game.client.ui.screen.FurnaceScreen;
import game.client.world.ClientWorld;
import game.logic.world.blocks.block_entity.BlockEntityGenerator;
import game.logic.world.blocks.block_entity.FurnaceBlockEntity;
import org.joml.Vector2f;
import org.joml.Vector3i;

public class FurnaceBlock extends Block implements BlockEntityGenerator<FurnaceBlockEntity> {
    @Override
    public FurnaceBlockEntity createBlockEntity() {
        return new FurnaceBlockEntity();
    }

    @Override
    public boolean onRightClick(ClientWorld world, Vector3i blockPosition) {
        //SandboxGame.getInstance().getGameRenderer().setScreen(new FurnaceScreen((FurnaceBlockEntity) world.getBlockEntity(blockPosition)));
        return false;
    }

    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(5,3), // TOP
                new Vector2f(6,3), // BOTTOM
                new Vector2f(4,4), // RIGHT
                new Vector2f(4,4), // LEFT
                new Vector2f(4,3), // FRONT
                new Vector2f(4,4) // BACK
        };
    }
}
