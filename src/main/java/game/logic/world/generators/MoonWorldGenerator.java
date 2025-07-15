package game.logic.world.generators;

import game.logic.util.FastNoiseLite;
import game.logic.world.blocks.Block;
import game.logic.world.blocks.Blocks;
import game.logic.world.chunk.ChunkProxy;
import org.joml.Vector3i;

import java.util.HashMap;

public class MoonWorldGenerator extends WorldGenerator {
    private FastNoiseLite noise;

    public MoonWorldGenerator(int seed) {
        this.seed = seed;

        this.noise = new FastNoiseLite(this.seed);
        noise.SetFrequency(1.1F);
        noise.SetFractalOctaves(4);
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
    }

    @Override
    public void generate(ChunkProxy chunkProxy, int chunkX, int chunkZ) {
        HashMap<Vector3i, Block> blocks = new HashMap<>(16 * 16 * 128);

        for(int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;
                int height = 60 + (int) (this.noise.GetNoise(worldX / 150F, worldZ / 150F) * 20);
                for (int y = 1; y < height; y++) {
                    blocks.put(new Vector3i(x,y,z), Blocks.MOONDUST);
                }
                blocks.put(new Vector3i(x, 0, z), Blocks.BEDROCK);
            }
        }
    }

    @Override
    public void generateFeatures(ChunkProxy chunkProxy, int chunkX, int chunkZ) {

    }

}
