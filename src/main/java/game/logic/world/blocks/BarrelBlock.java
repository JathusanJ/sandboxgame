package game.logic.world.blocks;

import game.client.SandboxGame;
import game.client.ui.screen.BarrelInventoryScreen;
import game.client.world.ClientWorld;
import game.logic.world.blocks.block_entity.BarrelBlockEntity;
import game.logic.world.blocks.block_entity.BlockEntityGenerator;
import org.joml.Vector2f;
import org.joml.Vector3i;

public class BarrelBlock extends Block implements BlockEntityGenerator<BarrelBlockEntity> {
    @Override
    public BarrelBlockEntity createBlockEntity() {
        return new BarrelBlockEntity();
    }

    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(4,2), // TOP
                new Vector2f(4,2), // BOTTOM
                new Vector2f(4,2), // RIGHT
                new Vector2f(4,2), // LEFT
                new Vector2f(4,2), // FRONT
                new Vector2f(4,2) // BACK
        };
    }

    @Override
    public boolean onRightClick(ClientWorld world, Vector3i blockPosition) {
        //SandboxGame.getInstance().getGameRenderer().setScreen(new BarrelInventoryScreen((BarrelBlockEntity) world.getBlockEntity(blockPosition)));
        return false;
    }
}
