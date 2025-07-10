package game.logic.world;

import game.logic.world.chunk.Chunk;
import game.logic.world.blocks.Block;
import game.logic.world.chunk.ServerChunk;
import game.logic.world.creature.Creature;
import game.logic.world.creature.Player;
import game.networking.GameServer;
import game.logic.world.creature.ServerPlayer;
import game.networking.packets.SetBlockPacket;
import game.networking.packets.SpawnCreaturePacket;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;

public class ServerWorld extends World {
    public GameServer server;
    private ArrayList<Vector2i> chunksToUnloadThisTick = new ArrayList<>();

    public ServerWorld(File worldFolder, GameServer server) {
        this.worldFolder = worldFolder;
        this.chunksFolder = new File(this.worldFolder, "chunks");
        this.chunksFolder.mkdirs();
        this.loadWorldInfo();

        this.worldGenerator = this.worldType.getGenerator(this.seed);

        this.server = server;
    }

    public ServerWorld(String name, int seed, WorldType worldType, File worldFolder, GameServer server) {
        this.name = name;
        this.seed = seed;
        this.worldType = worldType;
        this.worldFolder = worldFolder;
        this.chunksFolder = new File(this.worldFolder, "chunks");
        this.chunksFolder.mkdirs();

        this.worldGenerator = this.worldType.getGenerator(this.seed);

        this.server = server;
    }

    @Override
    public void spawnCreature(Creature creature, Vector3f position) {
        super.spawnCreature(creature, position);
        creature.networkId = this.server.getNextNetworkId();
        SpawnCreaturePacket spawnCreaturePacket = new SpawnCreaturePacket(creature);
        if(creature instanceof Player) {
            for (int i = 0; i < this.server.players.size(); i++) {
                ServerPlayer player = this.server.players.get(i);
                if(player != creature) {
                    player.sendPacket(spawnCreaturePacket);
                }
            }
        } else {
            this.server.sendPacketToAll(spawnCreaturePacket);
        }
    }

    @Override
    public Chunk createChunk(Vector2i chunkPosition) {
        return new ServerChunk(chunkPosition, this);
    }

    @Override
    public int getRenderDistance() {
        return 6;
    }

    @Override
    public boolean setBlockAt(int x, int y, int z, Block block) {
        if(super.setBlockAt(x,y,z, block)) {
            int chunkX = (int) Math.floor(x / 16F);
            int chunkZ = (int) Math.floor(z / 16F);

            SetBlockPacket setBlockPacket = new SetBlockPacket(x,y,z, block);

            for(int i = 0; i < this.server.players.size(); i++) {
                ServerPlayer player = this.server.players.get(i);
                if(player.getChunkPosition().x <= chunkX + this.getRenderDistance() + 5 && player.getChunkPosition().x >= chunkX - this.getRenderDistance() - 5
                && player.getChunkPosition().y <= chunkZ + this.getRenderDistance() + 5 && player.getChunkPosition().y >= chunkZ - this.getRenderDistance() - 5) {
                    player.sendPacket(setBlockPacket);
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public void tick() {
        super.tick();

        for(Vector2i chunkPosition : this.chunksToUnloadThisTick) {
            if(this.loadedChunks.get(chunkPosition).chunkUnloadingTimer < 1) {
                this.unloadChunk(this.loadedChunks.get(chunkPosition));
            }
        }
        this.chunksToUnloadThisTick.clear();
    }

    @Override
    public void generateChunksAround(int x, int z) {
        int radius = this.getRenderDistance() + 1;
        int loadedRadius = radius + 4;
        this.chunksToUnloadThisTick = new ArrayList<>(this.loadedChunks.keySet());

        for (int chunkX = x - loadedRadius; chunkX <= x + loadedRadius; chunkX++) {
            for (int chunkZ = z - loadedRadius; chunkZ <= z + loadedRadius ; chunkZ++) {
                if(Math.abs(chunkX - x) <= radius && Math.abs(chunkZ - z) <= radius && this.getChunkAtDespiteState(chunkX, chunkZ) == null) {
                    Chunk chunk = this.createChunk(new Vector2i(chunkX, chunkZ));
                    chunk.chunkUnloadingTimer = 10 * 20;
                    this.loadedChunks.put(new Vector2i(chunkX, chunkZ), chunk);
                    this.chunkLoaderManager.queue.add(chunk);
                } else {
                    this.chunksToUnloadThisTick.remove(new Vector2i(chunkX, chunkZ));
                    Chunk chunk = this.loadedChunks.get(new Vector2i(chunkX, chunkZ));
                    if(chunk != null) {
                        chunk.chunkUnloadingTimer = 10 * 20;
                    }

                }
            }
        }
    }
}
