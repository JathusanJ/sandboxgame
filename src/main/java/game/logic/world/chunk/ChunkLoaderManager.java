package game.logic.world.chunk;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChunkLoaderManager {
    public ConcurrentLinkedQueue<Chunk> queue = new ConcurrentLinkedQueue<>();
    public ConcurrentLinkedQueue<Chunk> features = new ConcurrentLinkedQueue<>();
    public ConcurrentLinkedQueue<Chunk> unload = new ConcurrentLinkedQueue<>();

    public ArrayList<ChunkLoader> chunkLoaders = new ArrayList<>();

    public int loadersUsedThisTick = 0;

    public ChunkLoaderManager() {
        int amount = 10;
        for (int i = 0; i < amount; i++) {
            this.chunkLoaders.add(new ChunkLoader(i));
        }
    }

    public void tick() {
        if(this.queue.isEmpty() && this.features.isEmpty() && this.unload.isEmpty()) {
            return;
        }

        this.loadersUsedThisTick = 0;

        ChunkLoader availableLoader = null;
        int availableLoaders = 0;
        for(ChunkLoader loader : this.chunkLoaders) {
            if(!loader.busy) {
                if(availableLoader == null) {
                    availableLoader = loader;
                } else {
                    availableLoaders++;
                }
            }
        }
        if(availableLoader == null) {
            return;
        }

        if(!this.unload.isEmpty() && this.unload.size() > 50) {
            availableLoader.start(this.unload.poll(), ChunkLoader.TaskType.UNLOAD);
        } if(!this.features.isEmpty()) {
            availableLoader.start(this.features.poll(), ChunkLoader.TaskType.FEATURES);
        } else if(!this.queue.isEmpty()) {
            availableLoader.start(this.queue.poll(), ChunkLoader.TaskType.LOAD);
        } else {
            availableLoader.start(this.unload.poll(), ChunkLoader.TaskType.UNLOAD);
        }


        this.loadersUsedThisTick++;

        if(availableLoaders > 0 && !(this.queue.isEmpty() && this.features.isEmpty() && this.unload.isEmpty()) && loadersUsedThisTick < this.chunkLoaders.size()) {
            this.tick();
        }
    }

    public boolean areTasksRunning() {
        for (ChunkLoader loader : this.chunkLoaders) {
            if(loader.busy) {
                return true;
            }
        }

        return false;
    }
}
