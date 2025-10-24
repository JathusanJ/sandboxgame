package game.shared.world.blocks;

import game.shared.world.chunk.ChunkProxy;
import org.joml.Vector2f;

public class DirtBlock extends Block implements RandomTickable {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(5, 0), // TOP
                new Vector2f(5, 0), // BOTTOM
                new Vector2f(5, 0), // RIGHT
                new Vector2f(5, 0), // LEFT
                new Vector2f(5, 0), // FRONT
                new Vector2f(5, 0) // BACK
        };
    }

    @Override
    public void randomTick(ChunkProxy chunkProxy, int localX, int localY, int localZ, int worldX, int worldY, int worldZ) {
        Block blockAbove = chunkProxy.getRelative(localX, localY + 1, localZ);
        if(!(blockAbove instanceof AirBlock || blockAbove instanceof CrossBlock || blockAbove instanceof LeafBlock)) {
            return;
        }

        if(chunkProxy.chunk.world.random.nextInt() % 3 == 0) {
            boolean grassNearby = false;
            for(int x = -1; x <= 1; x++) {
                for(int y = -1; y <= 1; y++) {
                    for(int z = -1; z <= 1; z++) {
                        // Skip the block right above
                        if(x == 0 && y == 1 && z == 0) {
                            continue;
                        }
                        // Skip the block right below
                        if(x == 0 && y == -1 && z == 0) {
                            continue;
                        }

                        if(chunkProxy.getRelative(localX + x, localY + y, localZ + z) == Blocks.GRASS) {
                            grassNearby = true;
                            break;
                        }
                    }
                    if(grassNearby) {
                        break;
                    }
                }
                if(grassNearby) {
                    break;
                }
            }

            if(grassNearby) {
                chunkProxy.setRelative(localX, localY, localZ, Blocks.GRASS);
            }
        }
    }
}
