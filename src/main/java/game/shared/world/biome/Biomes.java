package game.shared.world.biome;

import java.util.HashMap;

public class Biomes {
    public static HashMap<String, Biome> idToBiome = new HashMap<>();

    public static Biome PLAINS = register(new PlainsBiome(), "plains");
    public static Biome DESERT = register(new DesertBiome(), "desert");

    public static Biome register(Biome biome, String id)  {
        idToBiome.put(id, biome);
        biome.setId(id);

        return biome;
    }
}
