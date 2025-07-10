package game.logic.world.generators;

import game.logic.world.blocks.Block;
import org.joml.Vector3i;

import java.util.HashMap;

public abstract class WorldGenerator {
    int seed;

    public abstract HashMap<Vector3i, Block> generate(int chunkX, int chunkZ);
}
