package game.logic.world.chunk;

import game.logic.TickManager;
import game.logic.Tickable;
import game.logic.world.World;
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
        tickManager.tickables.add(this);
    }

    @Override
    public void tick() {
        this.chunksToRemove.putAll(this.world.loadedChunks);

        for(Ticket ticket : tickets) {
            ticket.work(this.world, this.chunksToRemove);
        }

        for(Chunk chunk : this.chunksToRemove.values()) {
            this.world.loadedChunks.remove(chunk.chunkPosition);
        }

        this.chunksToRemove.clear();
    }

    public void start() {
        this.tickManager.start();
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
