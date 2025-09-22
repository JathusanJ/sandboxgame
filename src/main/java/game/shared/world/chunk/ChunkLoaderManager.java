package game.shared.world.chunk;

import game.client.SandboxGame;
import game.client.multiplayer.GameClient;
import game.client.ui.screen.StaticWorldSavingScreen;
import game.client.world.SingleplayerWorld;
import game.shared.TickManager;
import game.shared.Tickable;
import game.shared.world.World;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChunkLoaderManager implements Tickable {
    public ArrayList<Ticket> tickets = new ArrayList<>();
    public World world;
    public TickManager tickManager = new TickManager();
    public Map<Vector2i, Chunk> chunksToRemove = new HashMap<>();

    public ChunkLoaderManager(World world) {
        this.world = world;
        this.tickManager.tickables.add(this);
    }

    @Override
    public void tick() {
        this.chunksToRemove.putAll(this.world.loadedChunks);

        for(Ticket ticket : tickets) {
            ticket.work(this.world, this.chunksToRemove);
        }

        for(Chunk chunk : this.chunksToRemove.values()) {
            this.world.unloadChunk(chunk);
        }

        this.chunksToRemove.clear();
    }

    public void start() {
        if(GameClient.isConnectedToServer) return;
        this.tickManager.start((thread, e) -> {
            if(this.world instanceof SingleplayerWorld) {
                SandboxGame.getInstance().getGameRenderer().setScreen(new StaticWorldSavingScreen());
            }
            this.world.shouldTick = false;
            this.world.save();
            this.world.stop();
            this.world.logger.error("Error in ChunkLoaderManager ", e);
            System.exit(0);
        });
    }

    public void stop() {
        this.tickManager.stop();
    }

    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
    }

    public void removeTicket(Ticket ticket) {
        this.tickets.remove(ticket);
    }

    public static class Ticket {
        public int centerX;
        public int centerY;
        public int radius;

        public Ticket(int centerX, int centerY, int radius) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.radius = radius;
        }

        public void work(World world, Map<Vector2i, Chunk> chunksToRemove) {
            for(int x = centerX - radius; x <=  centerX + radius; x++) {
                for(int y = centerY - radius; y <= centerY + radius; y++) {
                    Chunk chunk = world.loadedChunks.get(new Vector2i(x,y));
                    if(chunk == null) {
                        chunk = world.createChunk(x, y);
                        chunk.generateChunk();
                        world.loadedChunks.put(new Vector2i(x, y), chunk);
                    } else {
                        chunksToRemove.remove(chunk.chunkPosition);
                        if(!chunk.featuresGenerated && chunk.areNeighboursLoaded()) {
                            chunk.generateFeatures();
                            chunk.calculateSkylight();
                        }
                    }
                }
            }
        }
    }
}
