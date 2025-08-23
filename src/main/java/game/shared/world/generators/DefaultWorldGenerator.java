package game.shared.world.generators;

import game.shared.util.FastNoiseLite;
import game.shared.util.Spline;
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
        this.terrainShape(chunkProxy, chunkX, chunkZ);
        this.addGrassAndDirt(chunkProxy, chunkX, chunkZ);
        this.addUndergroundDirt(chunkProxy, chunkX, chunkZ);
        this.carveOutCaves(chunkProxy, chunkX, chunkZ, 127, cavePositionNoise1, caveHeightNoise1);
        this.carveOutCaves(chunkProxy, chunkX, chunkZ, 80, cavePositionNoise2, caveHeightNoise2);
    }

    @Override
    public void generateFeatures(ChunkProxy chunkProxy, int chunkX, int chunkZ) {
        this.addTulips(chunkProxy, chunkX, chunkZ, Blocks.RED_TULIP, 2000, 3, 10, 10);
        this.addTulips(chunkProxy, chunkX, chunkZ, Blocks.ORANGE_TULIP, 2000, 3, 10, 10);
        this.addTulips(chunkProxy, chunkX, chunkZ, Blocks.YELLOW_TULIP, 2000, 3, 10, 10);
        this.addTulips(chunkProxy, chunkX, chunkZ, Blocks.SHORT_GRASS, 200, 3, 10, 10);
        this.addTrees(chunkProxy, chunkX, chunkZ);
        this.addOre(chunkProxy, chunkX, chunkZ, Blocks.COAL_ORE, 50, 5, 10, 0, 127);
        this.addOre(chunkProxy, chunkX, chunkZ, Blocks.IRON_ORE, 30, 1, 3, 0, 127);
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

    public void addGrassAndDirt(ChunkProxy chunkProxy, int chunkX, int chunkZ) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {

                for (int y = 127; y > 0; y--) {
                    Block blockAtPosition = chunkProxy.getRelative(x,y,z);
                    Block blockAbove = chunkProxy.getRelative(x,y + 1,z);
                    Block block2Above = chunkProxy.getRelative(x, y + 2, z);
                    Block block3Above = chunkProxy.getRelative(x, y + 3, z);
                    Block block4Above = chunkProxy.getRelative(x,y + 4,z);

                    if(block4Above == Blocks.GRASS) {
                        continue;
                    } else if(block3Above == Blocks.GRASS) {
                        chunkProxy.setRelative(x,y,z, Blocks.DIRT);
                    } else if(block2Above == Blocks.GRASS) {
                        chunkProxy.setRelative(x,y,z, Blocks.DIRT);
                    } else if(blockAbove == Blocks.GRASS) {
                        chunkProxy.setRelative(x,y,z, Blocks.DIRT);
                    } else if(blockAbove == null && y >= 62 && blockAtPosition == Blocks.STONE) {
                        chunkProxy.setRelative(x,y,z, Blocks.GRASS);
                    }

                    if(block3Above == Blocks.SAND) {
                        continue;
                    } else if(block2Above == Blocks.SAND) {
                        chunkProxy.setRelative(x,y,z, Blocks.SAND);
                    } else if(blockAbove == Blocks.SAND) {
                        chunkProxy.setRelative(x,y,z, Blocks.SAND);
                    } else if((blockAbove == Blocks.WATER || blockAbove == null) && y < 62 && blockAtPosition == Blocks.STONE) {
                        chunkProxy.setRelative(x,y,z, Blocks.SAND);
                    }
                }
            }
        }
    }

    public void addTrees(ChunkProxy chunkProxy, int chunkX, int chunkZ) {
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
                    Block blockAtPosition = chunkProxy.getRelative(x,y,z);
                    if(blockAtPosition == Blocks.GRASS) {
                        chunkProxy.setRelative(x, y + 1, z, Blocks.OAK_LOG);
                        chunkProxy.setRelative(x, y + 2, z, Blocks.OAK_LOG);
                        chunkProxy.setRelative(x, y + 3, z, Blocks.OAK_LOG);
                        chunkProxy.setRelative(x, y + 4, z, Blocks.OAK_LOG);

                        chunkProxy.setRelativeIfAbsent(x, y + 5, z, Blocks.OAK_LEAVES);
                        chunkProxy.setRelativeIfAbsent(x + 1, y + 5, z, Blocks.OAK_LEAVES);
                        chunkProxy.setRelativeIfAbsent(x - 1, y + 5, z, Blocks.OAK_LEAVES);
                        chunkProxy.setRelativeIfAbsent(x, y + 5, z + 1, Blocks.OAK_LEAVES);
                        chunkProxy.setRelativeIfAbsent(x, y + 5, z - 1, Blocks.OAK_LEAVES);

                        for(int leafX = x - 1; leafX <= x + 1; leafX++) {
                            for(int leafZ = z - 1; leafZ <= z + 1; leafZ++) {
                                chunkProxy.setRelativeIfAbsent(leafX, y + 4, leafZ, Blocks.OAK_LEAVES);
                            }
                        }

                        for(int leafX = x - 2; leafX <= x + 2; leafX++) {
                            for(int leafZ = z - 2; leafZ <= z + 2; leafZ++) {
                                chunkProxy.setRelativeIfAbsent(leafX, y + 3, leafZ, Blocks.OAK_LEAVES);
                                chunkProxy.setRelativeIfAbsent(leafX, y + 2, leafZ, Blocks.OAK_LEAVES);
                            }
                        }

                    } else if(blockAtPosition != null && blockAtPosition != Blocks.AIR && blockAtPosition != Blocks.OAK_LEAVES && blockAtPosition != Blocks.SHORT_GRASS) {
                        break;
                    }
                }
            }
        }
    }

    public void addOre(ChunkProxy chunkProxy, int chunkX, int chunkZ, Block oreBlock, int rarity, int minClusterSize, int maxClusterSize, int minY, int maxY) {
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

    public void addTulips(ChunkProxy chunkProxy, int chunkX, int chunkZ, Block tulip, int rarity, int minAmount, int maxAmount, int maxSize) {
        Random tulipRandom = new Random(tulip.getBlockId().hashCode() + (((long) chunkX) << 32) + ((long) chunkZ << 8) * 2);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if(tulipRandom.nextInt() % rarity == 0) {
                    int amount = (int) (minAmount + Math.floor(tulipRandom.nextFloat() * (maxAmount - minAmount)));

                    for (int i = 0; i < amount; i++) {
                        int positionX = (int) (x + Math.floor((tulipRandom.nextFloat() * 2 - 1) * maxSize));
                        int positionZ = (int) (z + Math.floor((tulipRandom.nextFloat() * 2 - 1) * maxSize));

                        for (int y = 127; y > 40; y--) {
                            if(chunkProxy.getRelative(positionX, y, positionZ) == Blocks.GRASS) {
                                chunkProxy.setRelative(positionX,y + 1,positionZ, tulip);
                            }
                        }
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
