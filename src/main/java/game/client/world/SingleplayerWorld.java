package game.client.world;

import java.io.File;

public class SingleplayerWorld extends ClientWorld {
    public SingleplayerWorld(String name, int seed, WorldType worldType, String worldFolderName, File worldsFolder) {
        this.worldFolder = new File(worldsFolder, worldFolderName);
        if(!this.worldFolder.exists()) this.worldFolder.mkdir();

        this.chunksFolder = new File(this.worldFolder, "chunks");
        if(!this.chunksFolder.exists()) this.chunksFolder.mkdir();

        this.worldFolderName = worldFolderName;

        this.name = name;
        this.seed = seed;
        this.worldType = worldType;
        this.worldGenerator = worldType.getGenerator(this.seed);

        this.loadWorldInfo();
    }

    public SingleplayerWorld(String worldFolderName, File worldsFolder) {
        this.worldFolder = new File(worldsFolder, worldFolderName);
        if(!this.worldFolder.exists()) this.worldFolder.mkdir();

        this.chunksFolder = new File(this.worldFolder, "chunks");
        if(!this.chunksFolder.exists()) this.chunksFolder.mkdir();

        this.worldFolderName = worldFolderName;

        this.loadWorldInfo();

        this.worldGenerator = this.worldType.getGenerator(this.seed);
    }
}
