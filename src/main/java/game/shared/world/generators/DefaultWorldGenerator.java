package game.shared.world.generators;

import game.shared.util.FastNoiseLite;
import game.shared.util.Spline;
import game.shared.world.biome.Biome;
import game.shared.world.biome.Biomes;
import game.shared.world.blocks.Block;
import game.shared.world.blocks.Blocks;
import game.shared.world.chunk.ChunkProxy;
import org.joml.Vector2f;

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
    public FastNoiseLite humidity;
    public FastNoiseLite temperature;
    public FastNoiseLite cavePositionNoise1;
    public FastNoiseLite cavePositionNoise2;
    public FastNoiseLite caveHeightNoise1;
    public FastNoiseLite caveHeightNoise2;

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
        this.undergroundDirtNoise.SetFrequency(0.02F);

        this.forestNoise = new FastNoiseLite(this.seed + 3);
        this.forestNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.forestNoise.SetFractalOctaves(4);
        this.forestNoise.SetFrequency(0.01F);

        this.humidity = new FastNoiseLite(this.seed + 100);

        this.cavePositionNoise1 = new FastNoiseLite(this.seed + 4);
        this.cavePositionNoise1.SetFractalType(FastNoiseLite.FractalType.Ridged);
        this.cavePositionNoise1.SetFrequency(0.005F);
        this.cavePositionNoise1.SetFractalOctaves(1);

        this.caveHeightNoise1 = new FastNoiseLite(this.seed + 5);
        this.caveHeightNoise1.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.caveHeightNoise1.SetFrequency(0.002F);

        this.cavePositionNoise2 = new FastNoiseLite(this.seed + 6);
        this.cavePositionNoise2.SetFractalType(FastNoiseLite.FractalType.Ridged);
        this.cavePositionNoise2.SetFrequency(0.005F);
        this.cavePositionNoise2.SetFractalOctaves(1);

        this.caveHeightNoise2 = new FastNoiseLite(this.seed + 7);
        this.caveHeightNoise2.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.caveHeightNoise2.SetFrequency(0.002F);
    }

    @Override
    public void generate(ChunkProxy chunkProxy, int chunkX, int chunkZ) {
        for(int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunkProxy.chunk.biomes[x * 16 + z] = Biomes.PLAINS;
            }
        }

        this.terrainShape(chunkProxy, chunkX, chunkZ);
        this.addSurfaceBlocks(chunkProxy, chunkX, chunkZ);
        this.addUndergroundDirt(chunkProxy, chunkX, chunkZ);
        this.carveOutCaves(chunkProxy, chunkX, chunkZ, 127, cavePositionNoise1, caveHeightNoise1);
        this.carveOutCaves(chunkProxy, chunkX, chunkZ, 80, cavePositionNoise2, caveHeightNoise2);
    }

    @Override
    public void generateFeatures(ChunkProxy chunkProxy, int chunkX, int chunkZ) {
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                chunkProxy.chunk.biomes[x * 16 + z].placeFeatures(chunkProxy, chunkX, chunkZ, x, z);
            }
        }
        addOre(chunkProxy, chunkX, chunkZ, Blocks.COAL_ORE, 50, 5, 10, 0, 127);
        addOre(chunkProxy, chunkX, chunkZ, Blocks.IRON_ORE, 30, 1, 3, 0, 127);
    }

    public void addUndergroundDirt(ChunkProxy chunkProxy, int chunkX, int chunkZ) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;
                for (int y = 0; y < 128; y++) {
                    float undergroundDirtValue = this.undergroundDirtNoise.GetNoise(worldX, y, worldZ);
                    if(chunkProxy.getRelative(x,y,z) == Blocks.STONE && undergroundDirtValue >= 0.9F) {
                        chunkProxy.setRelative(x,y,z, Blocks.DIRT);
                    }
                }
            }
        }
    }

    public void terrainShape(ChunkProxy chunkProxy, int chunkX, int chunkZ) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;

                for (int y = 1; y < Math.abs(Math.round(this.bedrockNoise.GetNoise(worldX,worldZ) * 3)); y++) {
                    chunkProxy.setRelative(x,y,z, Blocks.BEDROCK);
                }
                chunkProxy.setRelative(x,0,z, Blocks.BEDROCK);

                float islandNoise = this.islandNoise.GetNoise(worldX,worldZ) * -20;
                float hillMountainNoise = this.hillMountainSpline.calculateLinear((this.hillMountainNoise.GetNoise(worldX, worldZ) + 1) / 2);

                float islandMultiplier = Math.clamp(islandNoise * 100, 0, 1F);

                float height = (float) Math.ceil(islandNoise * (1 + (hillMountainNoise * islandMultiplier)) + 62);

                for(int y = 0; y <= height; y++) {
                    if(!chunkProxy.hasBlockAtRelative(x,y,z)) {
                        if(y < 10 || this.density.GetNoise(worldX,y,worldZ) < (1F - y * 0.0125)) {
                            chunkProxy.setRelative(x, y, z, Blocks.STONE);
                        }
                    }
                }

                for(int y = 60; y > 10; y--) {
                    if(!chunkProxy.hasBlockAtRelative(x,y,z)) {
                        chunkProxy.setRelative(x,y,z, Blocks.WATER);
                    }
                }
            }
        }
    }

    public void addSurfaceBlocks(ChunkProxy chunkProxy, int chunkX, int chunkZ) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Biome biome = chunkProxy.chunk.biomes[x * 16 + z];
                for (int y = 127; y > 0; y--) {
                    biome.placeSurfaceBlocks(chunkProxy, chunkX, chunkZ, x,y,z);
                }
            }
        }
    }

    public static void addTrees(ChunkProxy chunkProxy, int chunkX, int chunkZ, int localX, int localZ, int rarity, Block log, Block leaves) {
        long treeSeed = (((long) chunkX) << 32) + ((long) (chunkZ) << 2) + (long) localX * Short.MAX_VALUE + (long) localZ * Byte.MAX_VALUE;
        Random treeRandom = new Random(treeSeed);

        if(treeRandom.nextInt() % rarity != 0) {
            return;
        }

        for (int y = 127; y > 30; y--) {
            Block blockAtPosition = chunkProxy.getRelative(localX,y,localZ);
            if(blockAtPosition == Blocks.GRASS) {
                chunkProxy.setRelative(localX, y + 1, localZ, log);
                chunkProxy.setRelative(localX, y + 2, localZ, log);
                chunkProxy.setRelative(localX, y + 3, localZ, log);
                chunkProxy.setRelative(localX, y + 4, localZ, log);

                int yOffset = treeRandom.nextInt(0, 2);
                for(int i = 0; i < yOffset; i++) {
                    chunkProxy.setRelative(localX, y + 4 + i, localZ, log);
                }

                chunkProxy.setRelativeIfAbsent(localX, y + 5 + yOffset, localZ, leaves);
                chunkProxy.setRelativeIfAbsent(localX + 1, y + 5 + yOffset, localZ, leaves);
                chunkProxy.setRelativeIfAbsent(localX - 1, y + 5 + yOffset, localZ, leaves);
                chunkProxy.setRelativeIfAbsent(localX, y + 5 + yOffset, localZ + 1, leaves);
                chunkProxy.setRelativeIfAbsent(localX, y + 5 + yOffset, localZ - 1, leaves);

                for(int leafX = localX - 1; leafX <= localX + 1; leafX++) {
                    for(int leafZ = localZ - 1; leafZ <= localZ + 1; leafZ++) {
                        chunkProxy.setRelativeIfAbsent(leafX, y + 4 + yOffset, leafZ, leaves);
                    }
                }

                for(int leafX = localX - 2; leafX <= localX + 2; leafX++) {
                    for(int leafZ = localZ - 2; leafZ <= localZ + 2; leafZ++) {
                        chunkProxy.setRelativeIfAbsent(leafX, y + 3 + yOffset, leafZ, leaves);
                        chunkProxy.setRelativeIfAbsent(leafX, y + 2 + yOffset, leafZ, leaves);
                    }
                }

            } else if(blockAtPosition != null && blockAtPosition != Blocks.AIR && blockAtPosition != leaves && blockAtPosition != Blocks.SHORT_GRASS) {
                break;
            }
        }
    }

    public static void addOre(ChunkProxy chunkProxy, int chunkX, int chunkZ, Block oreBlock, int rarity, int minClusterSize, int maxClusterSize, int minY, int maxY) {
        long oreSeed = oreBlock.getBlockId().hashCode() + (((long) chunkX) << 32) + ((long) chunkZ << 4);

        Random oreRandom = new Random(oreSeed);
        for (int i = 0; i < rarity; i++) {
            int y = minY + oreRandom.nextInt() % (maxY - minY);
            int x = oreRandom.nextInt() % 16;
            int z = oreRandom.nextInt() % 16;
            int clusterSize = minClusterSize + oreRandom.nextInt() % (maxClusterSize - minClusterSize);
            float stretchX = 3F + oreRandom.nextFloat() * clusterSize;
            float stretchY = 3F + oreRandom.nextFloat() * clusterSize;
            float stretchZ = 3F + oreRandom.nextFloat() * clusterSize;

            for(int x1 = x - 8; x1 <= x + 8; x1++) {
                for (int z1 = z - 8; z1 <= z + 8; z1++) {
                    for (int y1 = y - 8; y1 <= y + 8; y1++) {
                        float value = (float) (Math.pow(x1 - x, 2) / stretchX
                                + Math.pow(y1 - y, 2) / stretchY
                                + Math.pow(z1 - z, 2) / stretchZ);

                        if(chunkProxy.getRelative(x1, y1, z1) == Blocks.STONE && value < 1F) {
                            chunkProxy.setRelative(x1,y1,z1, oreBlock);
                        }
                    }
                }
            }

        }

    }

    public static void spreadBlocks(ChunkProxy chunkProxy, int chunkX, int chunkZ, int localX, int localZ, Block block, int rarity, int minAmount, int maxAmount, int maxSize) {
        Random spreadRandom = new Random(block.getBlockId().hashCode() + (((long) chunkX) << 32) + ((long) (chunkZ) << 8) * 2 + (long) localX * Integer.MAX_VALUE >> 2 - (long) localZ * Short.MAX_VALUE << 2);

        if(spreadRandom.nextInt() % rarity == 0) {
            int amount = (int) (minAmount + Math.floor(spreadRandom.nextFloat() * (maxAmount - minAmount)));

            for (int i = 0; i < amount; i++) {
                int positionX = (int) (localX + Math.floor((spreadRandom.nextFloat() * 2 - 1) * maxSize));
                int positionZ = (int) (localZ + Math.floor((spreadRandom.nextFloat() * 2 - 1) * maxSize));

                for (int y = 127; y > 40; y--) {
                    if(chunkProxy.getRelative(positionX, y, positionZ) == Blocks.GRASS) {
                        chunkProxy.setRelative(positionX,y + 1,positionZ, block);
                    }
                }
            }
        }
    }

    public void carveOutCaves(ChunkProxy chunkProxy, int chunkX, int chunkZ, int maxHeight, FastNoiseLite cavePositionNoise, FastNoiseLite caveHeightNoise) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                float caveNoise = cavePositionNoise.GetNoise(chunkX * 16 + x, chunkZ * 16 + z);
                if(caveNoise > 0.85F) {
                    int caveHeight = (int) Math.floor(Math.abs(caveHeightNoise.GetNoise(chunkX * 16 + x, chunkZ * 16 + z)) * maxHeight);

                    for (int x2 = 0; x2 < 16; x2++) {
                        for (int z2 = 0; z2 < 16; z2++) {
                            for (int y = caveHeight - 10; y < caveHeight + 10; y++) {
                                float circleValue = (float) (Math.pow(x2 - x, 2) + Math.pow(y - caveHeight, 2) + Math.pow(z2 - z, 2));
                                if(circleValue < 10F) {
                                    Block blockAtPosition = chunkProxy.getRelative(x2, y, z2);
                                    if(blockAtPosition != Blocks.SAND && blockAtPosition != Blocks.WATER && blockAtPosition != Blocks.BEDROCK) {
                                        if(y < 5) {
                                            chunkProxy.setRelative(x2, y, z2, Blocks.LAVA);
                                        } else {
                                            chunkProxy.setRelative(x2, y, z2, null);
                                        }
                                    }
                                }
                            }
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
