package game.logic.world.chunk;

public class ChunkLoader {
    public Chunk currentChunk;
    public boolean busy = false;
    public int id;
    public long start;

    public ChunkLoader(int id) {
        this.id = id;
    }

    private void doWork() {
        this.currentChunk.generateChunk();
        this.busy = false;
    }

    public void start(Chunk currentChunk) {
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
}
