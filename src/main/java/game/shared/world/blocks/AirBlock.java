package game.shared.world.blocks;

public class AirBlock extends Block {
    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean hasCollision() {
        return false;
    }

    @Override
    public boolean canLightPassThrough() {
        return true;
    }
}
