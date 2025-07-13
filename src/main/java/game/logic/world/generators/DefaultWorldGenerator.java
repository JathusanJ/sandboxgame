package game.logic.world.generators;

import game.logic.util.FastNoiseLite;
import game.logic.util.Spline;
import game.logic.world.blocks.Block;
import game.logic.world.blocks.Blocks;
import org.joml.Vector2f;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DefaultWorldGenerator extends WorldGenerator {
    public int seed;
    public FastNoiseLite bedrockNoise;
    public FastNoiseLite islandNoise;
    public FastNoiseLite hillMountainNoise;
    public Spline hillMountainSpline;
    public FastNoiseLite density;
    public FastNoiseLite undergroundDirtNoise;
    public FastNoiseLite forestNoise;

    public DefaultWorldGenerator(int seed) {
        this.seed = seed;

        this.bedrockNoise = new FastNoiseLite(this.seed);
        this.bedrockNoise.SetFrequency(2F);

        this.islandNoise = new FastNoiseLite(this.seed);
        this.islandNoise.SetFrequency(0.001F);
        this.islandNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.islandNoise.SetFractalOctaves(10);

        this.hillMountainNoise = new FastNoiseLite(this.seed + 1);
        this.hillMountainNoise.SetFrequency(0.001F);
        this.hillMountainNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.hillMountainNoise.SetFractalOctaves(4);
        this.hillMountainNoise.SetFractalGain(0.3F);
        this.hillMountainNoise.SetFractalWeightedStrength(-1);

        this.hillMountainSpline = new Spline(List.of(
                new Vector2f(0, 0),
                new Vector2f(0.4F, 1),
                new Vector2f(0.5F, 2),
                new Vector2f(0.7F, 3.1F),
                new Vector2f(0.8F, 3.5F),
                new Vector2f(0.95F, 3.95F),
                new Vector2f(1, 4)
        ));

        this.density = new FastNoiseLite(this.seed + 2);
        this.density.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.density.SetFrequency(0.005F);
        this.density.SetFractalOctaves(4);

        this.undergroundDirtNoise = new FastNoiseLite(this.seed - 2);
        this.undergroundDirtNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        this.undergroundDirtNoise.SetFrequency(0.02F);

        this.forestNoise = new FastNoiseLite(this.seed + 3);
        this.forestNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        this.forestNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.forestNoise.SetFractalOctaves(4);
        this.forestNoise.SetFrequency(0.01F);
    }

    @Override
    public HashMap<Vector3i, Block> generate(int chunkX, int chunkZ) {
        HashMap<Vector3i, Block> blocks = new HashMap<>();

        this.terrainShape(blocks, chunkX, chunkZ);
        this.addGrassAndDirt(blocks, chunkX, chunkZ);
        this.addUndergroundDirt(blocks, chunkX, chunkZ);
        this.addTrees(blocks, chunkX, chunkZ);
        this.addOre(blocks, chunkX, chunkZ, Blocks.COAL_ORE, 50, 5, 10, 0, 100);

        return blocks;
    }

    public void addUndergroundDirt(HashMap<Vector3i, Block> blocks, int chunkX, int chunkZ) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;
                for (int y = 0; y < 128; y++) {
                    float undergroundDirtValue = this.undergroundDirtNoise.GetNoise(worldX, y, worldZ);
                    Vector3i position = new Vector3i(x, y, z);
                    if(blocks.get(position) == Blocks.STONE && undergroundDirtValue >= 0.9F) {
                        blocks.put(new Vector3i(x, y, z), Blocks.DIRT);
                    }
                }
            }
        }
    }

    public void terrainShape(HashMap<Vector3i, Block> blocks, int chunkX, int chunkZ) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;

                for (int y = 1; y < Math.abs(Math.round(this.bedrockNoise.GetNoise(worldX,worldZ) * 3)); y++) {
                    blocks.put(new Vector3i(x,y,z), Blocks.BEDROCK);
                }
                blocks.put(new Vector3i(x,0,z), Blocks.BEDROCK);

                float islandNoise = this.islandNoise.GetNoise(worldX,worldZ) * -20;
                float hillMountainNoise = this.hillMountainSpline.calculateLinear((this.hillMountainNoise.GetNoise(worldX, worldZ) + 1) / 2);

                float islandMultiplier = Math.clamp(islandNoise * 100, 0, 1F);

                float height = (float) Math.ceil(islandNoise * (1 + (hillMountainNoise * islandMultiplier)) + 62);

                for(int y = 0; y <= height; y++) {
                    if(!blocks.containsKey(new Vector3i(x,y,z))) {
                        if(y < 10 || this.density.GetNoise(worldX,y,worldZ) < (1F - y * 0.0125)) {
                            blocks.put(new Vector3i(x, y, z), Blocks.STONE);
                        }
                    }
                }

                for(int y = 60; y > 30; y--) {
                    if(!blocks.containsKey(new Vector3i(x,y,z))) {
                        blocks.put(new Vector3i(x, y, z), Blocks.WATER);
                    }
                }
            }
        }
    }

    public void addGrassAndDirt(HashMap<Vector3i, Block> blocks, int chunkX, int chunkZ) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;

                for (int y = 127; y > 0; y--) {
                    Vector3i position = new Vector3i(x,y,z);
                    Block blockAtPosition = blocks.get(position);
                    Block blockAbove = blocks.get(new Vector3i(x,y + 1,z));
                    Block block2Above = blocks.get(new Vector3i(x,y + 2,z));
                    Block block3Above = blocks.get(new Vector3i(x,y + 3,z));
                    Block block4Above = blocks.get(new Vector3i(x,y + 4,z));

                    if(block4Above == Blocks.GRASS) {
                        continue;
                    } else if(block3Above == Blocks.GRASS) {
                        blocks.put(position, Blocks.DIRT);
                    } else if(block2Above == Blocks.GRASS) {
                        blocks.put(position, Blocks.DIRT);
                    } else if(blockAbove == Blocks.GRASS) {
                        blocks.put(position, Blocks.DIRT);
                    } else if(blockAbove == null && y >= 62 && blockAtPosition == Blocks.STONE) {
                        blocks.put(position, Blocks.GRASS);
                    }

                    if(block3Above == Blocks.SAND) {
                        continue;
                    } else if(block2Above == Blocks.SAND) {
                        blocks.put(position, Blocks.SAND);
                    } else if(blockAbove == Blocks.SAND) {
                        blocks.put(position, Blocks.SAND);
                    } else if((blockAbove == Blocks.WATER || blockAbove == null) && y < 62 && blockAtPosition == Blocks.STONE) {
                        blocks.put(position, Blocks.SAND);
                    }
                }
            }
        }
    }

    public void addTrees(HashMap<Vector3i, Block> blocks, int chunkX, int chunkZ) {
        long treeSeed = (((long) chunkX) << 32) + ((long) chunkZ << 2);
        Random treeRandom = new Random(treeSeed);

        int rarity = 500;
        if(this.forestNoise.GetNoise(chunkX, chunkZ) > 0.3F) {
            rarity = 50;
        }

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if(treeRandom.nextInt() % rarity != 0) {
                    continue;
                }

                for (int y = 127; y > 30; y--) {
                    int worldX = chunkX * 16 + x;
                    int worldZ = chunkZ * 16 + z;

                    Vector3i position = new Vector3i(x, y, z);
                    Block blockAtPosition = blocks.get(position);
                    if(blockAtPosition == Blocks.GRASS) {
                        blocks.putIfAbsent(new Vector3i(x, y + 1, z), Blocks.OAK_LOG);
                        blocks.putIfAbsent(new Vector3i(x, y + 2, z), Blocks.OAK_LOG);
                        blocks.putIfAbsent(new Vector3i(x, y + 3, z), Blocks.OAK_LOG);
                        blocks.putIfAbsent(new Vector3i(x, y + 4, z), Blocks.OAK_LOG);

                        blocks.putIfAbsent(new Vector3i(x, y + 5, z), Blocks.OAK_LEAVES);
                        blocks.putIfAbsent(new Vector3i(x + 1, y + 5, z), Blocks.OAK_LEAVES);
                        blocks.putIfAbsent(new Vector3i(x - 1, y + 5, z), Blocks.OAK_LEAVES);
                        blocks.putIfAbsent(new Vector3i(x, y + 5, z + 1), Blocks.OAK_LEAVES);
                        blocks.putIfAbsent(new Vector3i(x, y + 5, z - 1), Blocks.OAK_LEAVES);

                        for(int leafX = x - 1; leafX <= x + 1; leafX++) {
                            for(int leafZ = z - 1; leafZ <= z + 1; leafZ++) {
                                blocks.putIfAbsent(new Vector3i(leafX, y + 4, leafZ), Blocks.OAK_LEAVES);
                            }
                        }

                        for(int leafX = x - 2; leafX <= x + 2; leafX++) {
                            for(int leafZ = z - 2; leafZ <= z + 2; leafZ++) {
                                blocks.putIfAbsent(new Vector3i(leafX, y + 3, leafZ), Blocks.OAK_LEAVES);
                                blocks.putIfAbsent(new Vector3i(leafX, y + 2, leafZ), Blocks.OAK_LEAVES);
                            }
                        }

                    } else if(blockAtPosition != null && blockAtPosition != Blocks.AIR && blockAtPosition != Blocks.OAK_LEAVES) {
                        break;
                    }
                }
            }
        }
    }

    public void addOre(HashMap<Vector3i, Block> blocks, int chunkX, int chunkZ, Block oreBlock, int rarity, int minClusterSize, int maxClusterSize, int minY, int maxY) {
        long oreSeed = oreBlock.getBlockId().hashCode() + (((long) chunkX) << 32) + ((long) chunkZ << 4);

        Random oreRandom = new Random(oreSeed);
        for (int i = 0; i < rarity; i++) {
            int y = minY + oreRandom.nextInt() % (maxY - minY);
            int x = oreRandom.nextInt() % 16;
            int z = oreRandom.nextInt() % 16;
            int clusterSize = minClusterSize + oreRandom.nextInt() % (maxClusterSize - minClusterSize);
            float stretchX = 3F + oreRandom.nextFloat() * 10;
            float stretchY = 3F + oreRandom.nextFloat() * 10;
            float stretchZ = 3F + oreRandom.nextFloat() * 10;

            for(int x1 = x - 8; x1 <= x + 8; x1++) {
                for (int z1 = z - 8; z1 <= z + 8; z1++) {
                    for (int y1 = y - 8; y1 <= y + 8; y1++) {
                        float value = (float) (Math.pow(x1 - x, 2) / stretchX
                                + Math.pow(y1 - y, 2) / stretchY
                                + Math.pow(z1 - z, 2) / stretchZ);

                        Vector3i position = new Vector3i(x1, y1, z1);

                        if(blocks.get(position) == Blocks.STONE && value < 1F) {
                            blocks.put(position, oreBlock);
                        }
                    }
                }
            }

        }

    }

    public List<Float> getDebugValues(int x, int z) {
        float islandNoise = this.islandNoise.GetNoise(x,z) * -20;
        float hillMountainNoise = this.hillMountainSpline.calculateLinear((this.hillMountainNoise.GetNoise(x, z) + 1) / 2);

        float islandMultiplier = Math.clamp(islandNoise * 100, 0, 1F);

        float hillMountainMultiplier = (1 + (hillMountainNoise * islandMultiplier));
        float height = islandNoise * hillMountainMultiplier + 62;

        return List.of(islandNoise, hillMountainNoise, islandMultiplier, hillMountainMultiplier, height);
    }
}
