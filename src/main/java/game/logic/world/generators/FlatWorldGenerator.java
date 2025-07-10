package game.logic.world.generators;

import game.logic.world.blocks.Block;
import game.logic.world.blocks.Blocks;
import org.joml.Vector3i;

import java.util.HashMap;

public class FlatWorldGenerator extends WorldGenerator {
    @Override
    public HashMap<Vector3i, Block> generate(int chunkX, int chunkZ) {
        HashMap<Vector3i, Block> blocks = new HashMap<>(16 * 16 * 128);

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

        return blocks;
    }
}
