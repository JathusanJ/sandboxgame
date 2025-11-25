package game.shared.world.generators;

import game.shared.util.FastNoiseLite;
import game.shared.world.biome.Biomes;
import game.shared.world.blocks.Blocks;
import game.shared.world.chunk.ChunkProxy;

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
        for(int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;
                int height = 60 + (int) (this.noise.GetNoise(worldX / 150F, worldZ / 150F) * 20);
                for (int y = 1; y < height; y++) {
                    chunkProxy.setRelative(x,y,z, Blocks.MOONDUST);
                }
                chunkProxy.setRelative(x, 0, z, Blocks.BEDROCK);

                chunkProxy.chunk.biomes[x * 16 + z] = Biomes.NO_BIOME;
            }
        }
    }

    @Override
    public void generateFeatures(ChunkProxy chunkProxy, int chunkX, int chunkZ) {

    }

}
