package game.client.rendering.chunk;

import game.client.SandboxGame;
import game.client.world.ClientChunk;

public class ChunkMeshBuilder {
    public ClientChunk currentChunk;
    public boolean busy = false;
    public int id;
    public long start;

    public ChunkMeshBuilder(int id) {
        this.id = id;
    }

    private void doWork() {
        if(this.currentChunk != null) {
            this.start = System.nanoTime();
            this.currentChunk.chunkMesh.generate();
            if(this.currentChunk.chunkMesh.state == ChunkMesh.State.FAILED) {
                SandboxGame.getInstance().getGameRenderer().chunkRenderer.chunkMeshGenerationManager.queue.add(this.currentChunk);
            } else if(this.currentChunk.chunkMesh.state == ChunkMesh.State.WAITING_TO_BE_ENQUEUED) {
                this.currentChunk.chunkMesh.state = ChunkMesh.State.WAITING_FOR_UPLOAD;
            } else {
                throw new IllegalStateException("Invalid chunk mesh state after generation: " + this.currentChunk.chunkMesh.state);
            }
        }
        this.busy = false;
    }

    public void start(ClientChunk currentChunk) {
        this.currentChunk = currentChunk;
        if(this.currentChunk != null) {
            this.busy = true;
            Thread thread = new Thread(this::doWork, "ChunkMeshBuilder " + this.id);
            thread.setUncaughtExceptionHandler(((t, e) -> {
                e.printStackTrace();
            }));
            thread.start();
        }
    }
}
