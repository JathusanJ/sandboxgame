package game.logic.world.chunk;

import game.client.SandboxGame;
import org.joml.Vector2i;

public class ChunkLoader {
    public Chunk currentChunk;
    public boolean busy = false;
    public int id;
    public long start;
    public TaskType taskType;

    public ChunkLoader(int id) {
        this.id = id;
    }

    private void doWork() {
        if(this.taskType == TaskType.LOAD) {
            this.currentChunk.generateChunk();
        } else if(this.taskType == TaskType.FEATURES) {
            this.currentChunk.generateFeatures();
            this.currentChunk.setModified();
            this.currentChunk.calculateSkylight();
            this.currentChunk.enqueuedInChunkLoader = false;
        } else if(this.taskType == TaskType.UNLOAD) {
            this.currentChunk.unload();
            SandboxGame.getInstance().doOnTickingThread(() -> {
                SandboxGame.getInstance().getGameRenderer().world.loadedChunks.remove(new Vector2i(this.currentChunk.chunkPosition.x, this.currentChunk.chunkPosition.z));
            });
        }
        this.busy = false;
    }

    public void start(Chunk currentChunk, TaskType taskType) {
        this.taskType = taskType;
        this.currentChunk = currentChunk;
        if(this.currentChunk != null) {
            this.busy = true;
            Thread thread = new Thread(this::doWork, "ChunkLoader " + this.id);
            thread.setUncaughtExceptionHandler(((t, e) -> {
                e.printStackTrace();
            }));
            thread.start();
        }
    }

    public enum TaskType {
        LOAD,
        FEATURES,
        UNLOAD
    }
}
