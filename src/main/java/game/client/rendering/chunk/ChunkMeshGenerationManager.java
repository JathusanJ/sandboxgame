package game.client.rendering.chunk;

import game.client.world.ClientChunk;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChunkMeshGenerationManager {
    public ConcurrentLinkedQueue<ClientChunk> queue = new ConcurrentLinkedQueue<>();

    public ArrayList<ChunkMeshBuilder> chunkMeshBuilders = new ArrayList<>();

    public int buildersUsedThisTick = 0;

    public ChunkMeshGenerationManager() {
        int amount = 4;
        for (int i = 0; i < amount; i++) {
            this.chunkMeshBuilders.add(new ChunkMeshBuilder(i));
        }
    }

    public void tick() {
        this.buildersUsedThisTick = 0;
        this.work();
    }

    public void work() {
        if(this.queue.isEmpty()) {
            return;
        }

        ChunkMeshBuilder availableBuilder = null;
        int availableBuilders = 0;
        for(ChunkMeshBuilder builder : this.chunkMeshBuilders) {
            if(!builder.busy) {
                if(availableBuilder == null) {
                    availableBuilder = builder;
                } else {
                    availableBuilders++;
                }
            }
        }
        if(availableBuilder == null) {
            return;
        }

        availableBuilder.start(this.findChunkToBuild());

        this.buildersUsedThisTick++;

        // I have no idea why I need to limit the amount of builders used in a single tick for it to not cause a StackOverflow
        // I might have copied some logic from the chunk loading one equivalent wrong
        if(availableBuilders > 0 && !this.queue.isEmpty() && this.buildersUsedThisTick < this.chunkMeshBuilders.size()) {
            this.work();
        }
    }

    private ClientChunk findChunkToBuild() {
        if(this.queue.isEmpty()) {
            return null;
        }

        ClientChunk currentChunk = this.queue.poll();
        ClientChunk chunkStartedAt = currentChunk;

        while(!currentChunk.areNeighboursLoaded()) {
            this.queue.add(currentChunk);
            currentChunk = this.queue.poll();
            if(currentChunk == chunkStartedAt) {
                this.queue.add(currentChunk);
                return null;
            }
        }

        return currentChunk;
    }
}
