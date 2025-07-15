package game.logic.world.generators;

import game.logic.world.blocks.Block;
import game.logic.world.blocks.Blocks;
import game.logic.world.chunk.ChunkProxy;
import org.joml.Vector3i;

import java.util.HashMap;

public class FlatWorldGenerator extends WorldGenerator {
    @Override
    public void generate(ChunkProxy chunkProxy, int chunkX, int chunkZ) {
        HashMap<Vector3i, Block> blocks = new HashMap<>();

        for(int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 1; y <= 56; y++) {
                    blocks.put(new Vector3i(x,y,z), Blocks.STONE);
                }
                for (int y = 57; y <= 59; y++) {
                    blocks.put(new Vector3i(x,y,z), Blocks.DIRT);
                }
                blocks.put(new Vector3i(x, 60, z), Blocks.GRASS);
                blocks.put(new Vector3i(x, 0, z), Blocks.BEDROCK);
            }
        }
    }

    @Override
    public void generateFeatures(ChunkProxy chunkProxy, int chunkX, int chunkZ) {

    }
}
