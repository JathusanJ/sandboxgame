package game.client.world;

import game.client.SandboxGame;
import game.logic.world.chunk.Chunk;
import game.logic.world.chunk.ChunkLoaderManager;
import game.logic.world.creature.Player;
import org.joml.Vector2i;

import java.io.File;

public class SingleplayerWorld extends ClientWorld {
    public ChunkLoaderManager.Ticket playerTicket;
    public int chunkLoadingPercentage = 0;

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

    @Override
    public void tick() {
        if(!this.ready) {
            int radius = SandboxGame.getInstance().settings.renderDistance - 1;
            int chunksReady = 0;
            boolean allReady = true;

            for (int x = this.playerTicket.centerX - radius; x <= this.playerTicket.centerX + radius; x++) {
                for (int z = this.playerTicket.centerY - radius; z <= this.playerTicket.centerY + radius; z++) {
                    Chunk chunk = this.loadedChunks.get(new Vector2i(x,z));
                    if(chunk != null && chunk.featuresGenerated) {
                        chunksReady++;
                    } else {
                        allReady = false;
                    }
                }
            }

            this.chunkLoadingPercentage = (int) Math.floor((chunksReady / Math.pow(2 * radius + 1, 2) * 100));

            if(!allReady) {
                return;
            }

            this.ready = true;
        }

        Player player = SandboxGame.getInstance().getGameRenderer().player;
        playerTicket.centerX = player.getChunkPosition().x;
        playerTicket.centerY = player.getChunkPosition().y;
        playerTicket.radius = SandboxGame.getInstance().settings.renderDistance + 2;

        super.tick();
    }
}
