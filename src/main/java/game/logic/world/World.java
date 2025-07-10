package game.logic.world;

import com.google.gson.stream.JsonReader;
import game.client.world.ClientChunk;
import game.logic.util.json.WrappedJsonObject;
import game.logic.world.chunk.Chunk;
import game.logic.Tickable;
import game.logic.world.blocks.Block;
import game.logic.world.blocks.Blocks;
import game.logic.world.blocks.block_entity.BlockEntity;
import game.logic.world.blocks.block_entity.BlockEntityGenerator;
import game.logic.world.chunk.ChunkLoaderManager;
import game.logic.world.creature.Creature;
import game.logic.world.creature.Player;
import game.logic.world.generators.*;
import game.networking.packets.RemoveCreaturePacket;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public abstract class World implements Tickable {
    public HashMap<Vector2i, Chunk> loadedChunks = new HashMap<>();
    public File worldFolder;
    public File chunksFolder;
    public String worldFolderName;
    public Integer seed;
    public String name;
    public WorldGenerator worldGenerator;
    public WorldType worldType;
    public int worldTime = (int) (6.5 * 60 * 20);
    public boolean shouldTick = true;
    public ArrayList<Creature> creatures = new ArrayList<>();
    public Map<Vector3i, BlockEntity> blockEntities = new HashMap<>();
    public boolean ready = false;
    public ChunkLoaderManager chunkLoaderManager = new ChunkLoaderManager();
    public Random random = new Random();
    public boolean commandsEnabled = false;

    public void loadWorldInfo() {
        File worldInfoFile = new File(this.worldFolder, "world.json");
        if(worldInfoFile.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(worldInfoFile);
                WrappedJsonObject json = WrappedJsonObject.read(new JsonReader(new StringReader(new String(fileInputStream.readAllBytes()))));
                fileInputStream.close();

                this.name = json.getString("name");
                this.seed = json.getInt("seed");
                this.worldType = WorldType.DEFAULT; // Temporary TODO
                this.worldTime = json.getInt("worldTime");
                this.commandsEnabled = json.getBoolean("commandsEnabled");
            } catch (IOException e) {
                throw new RuntimeException("Couldn't read world info of \"" + worldFolderName + "\"", e);
            }
        } else {
            // TODO add proper measures to deal with worlds without world.json
        }
    }

    public void writeWorldInfo() {
        File worldInfoFile = new File(this.worldFolder, "world.json");
        WrappedJsonObject json = new WrappedJsonObject();
        json.put("name", this.name);
        json.put("seed", this.seed);
        json.put("worldType", this.worldType);
        json.put("worldTime", this.worldTime);
        json.put("commandsEnabled", this.commandsEnabled);

        if(!worldInfoFile.exists()) {
            try {
                if(!worldInfoFile.createNewFile()) {
                    throw new IllegalStateException("world.json already present");
                }
            } catch (Exception e) {
                throw new IllegalStateException("Couldn't create world.json", e);
            }
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(worldInfoFile);
            outputStream.write(json.toElement().toString().getBytes());
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't write to world.json", e);
        }
    }

    public Block getBlockAt(int x, int y, int z) {
        // This differs from (x / 16) (Integer division) for some reason
        int chunkX = (int) Math.floor(x / 16F);
        int chunkZ = (int) Math.floor(z / 16F);

        Chunk chunk = this.getChunkAt(chunkX, chunkZ);
        if(chunk != null) {
            return chunk.getBlockAtLocalizedPosition(x - chunkX * 16, y, z - chunkZ * 16);
        }

        return Blocks.AIR;
    }

    public boolean setBlockAt(int x, int y, int z, Block block) {
        int chunkX = (int) Math.floor(x / 16F);
        int chunkZ = (int) Math.floor(z / 16F);

        Chunk chunk = this.getChunkAt(chunkX, chunkZ);
        if(chunk != null) {
            return chunk.setBlockAtLocalizedPosition(x - chunkX * 16, y, z - chunkZ * 16, block);
        }

        return false;
    }

    public WorldRaycastResult blockRaycast(Vector3f start, Vector3f direction, float range) {
        return this.blockRaycast(start, direction, range, true);
    }

    public WorldRaycastResult blockRaycast(Vector3f start, Vector3f direction, float range, boolean ignoreLiquids) {
        float travelledDistance = 0F;
        Vector3f currentRayPosition = new Vector3f(start);
        Vector3f lastRayPosition = new Vector3f(currentRayPosition);
        while(true) {
            // The idea to move and check in x, y, z individually doesn't make it more accurate. TODO: fix this
            // Initial check/z movement check
            Vector3i blockPos = new Vector3i((int) Math.floor(currentRayPosition.x), (int) Math.floor(currentRayPosition.y), (int) Math.floor(currentRayPosition.z));
            Block block = this.getBlockAt(blockPos.x, blockPos.y, blockPos.z);
            boolean isLiquid = ignoreLiquids && block.isLiquid();
            if(!block.isEmpty() && !isLiquid) {
                Vector3i normal = new Vector3i(
                        (int) (Math.floor(lastRayPosition.x) - Math.floor(currentRayPosition.x)),
                        (int) (Math.floor(lastRayPosition.y) - Math.floor(currentRayPosition.y)),
                        (int) (Math.floor(lastRayPosition.z) - Math.floor(currentRayPosition.z))
                );
                return new WorldRaycastResult(true, block, blockPos, normal);
            }

            if(travelledDistance >= range) break;

            float distanceToTravel = Math.min(range - travelledDistance, 0.01F);

            lastRayPosition.set(currentRayPosition);
            // x movement
            currentRayPosition.add(direction.x * distanceToTravel, 0, 0);
            blockPos = new Vector3i((int) Math.floor(currentRayPosition.x), (int) Math.floor(currentRayPosition.y), (int) Math.floor(currentRayPosition.z));
            block = this.getBlockAt(blockPos.x, blockPos.y, blockPos.z);
            isLiquid = ignoreLiquids && block.isLiquid();
            if(!block.isEmpty() && !isLiquid) {
                Vector3i normal = new Vector3i(
                        (int) (Math.floor(lastRayPosition.x) - Math.floor(currentRayPosition.x)),
                        (int) (Math.floor(lastRayPosition.y) - Math.floor(currentRayPosition.y)),
                        (int) (Math.floor(lastRayPosition.z) - Math.floor(currentRayPosition.z))
                );
                return new WorldRaycastResult(true, block, blockPos, normal);
            }

            lastRayPosition.set(currentRayPosition);
            // y movement
            currentRayPosition.add(0, direction.y * distanceToTravel, 0);
            blockPos = new Vector3i((int) Math.floor(currentRayPosition.x), (int) Math.floor(currentRayPosition.y), (int) Math.floor(currentRayPosition.z));
            block = this.getBlockAt(blockPos.x, blockPos.y, blockPos.z);
            isLiquid = ignoreLiquids && block.isLiquid();
            if(!block.isEmpty() && !isLiquid) {
                Vector3i normal = new Vector3i(
                        (int) (Math.floor(lastRayPosition.x) - Math.floor(currentRayPosition.x)),
                        (int) (Math.floor(lastRayPosition.y) - Math.floor(currentRayPosition.y)),
                        (int) (Math.floor(lastRayPosition.z) - Math.floor(currentRayPosition.z))
                );
                return new WorldRaycastResult(true, block, blockPos, normal);
            }

            lastRayPosition.set(currentRayPosition);
            // z movement, check for this one is at the next loop iteration
            currentRayPosition.add(0, 0, direction.z * distanceToTravel);

            travelledDistance += distanceToTravel;
        }

        return new WorldRaycastResult(false, null, null, null);
    }

    public void loadWorld() {

    }

    public void saveWorld() {
        synchronized (this.loadedChunks) {
            for (Chunk chunk : this.loadedChunks.values()) {
                chunk.save();
            }
        }
        this.writeWorldInfo();
    }

    public Chunk getChunkAt(int x, int z) {
        Chunk chunk = this.loadedChunks.get(new Vector2i(x, z));
        if(chunk != null && chunk.state == Chunk.ChunkState.READY) {
            return chunk;
        }
        return null;
    }

    public Chunk getChunkAtDespiteState(int x, int z) {
        return this.loadedChunks.get(new Vector2i(x, z));
    }

    public void generateChunksAround(int x, int z) {
        int radius = this.getRenderDistance() + 1;
        int loadedRadius = radius + 4;
        ArrayList<Vector2i> chunksToUnload = new ArrayList<>(this.loadedChunks.keySet());

        for (int chunkX = x - loadedRadius; chunkX <= x + loadedRadius; chunkX++) {
            for (int chunkZ = z - loadedRadius; chunkZ <= z + loadedRadius ; chunkZ++) {
                if(Math.abs(chunkX - x) <= radius && Math.abs(chunkZ - z) <= radius && this.getChunkAtDespiteState(chunkX, chunkZ) == null) {
                    Chunk chunk = this.createChunk(new Vector2i(chunkX, chunkZ));
                    this.loadedChunks.put(new Vector2i(chunkX, chunkZ), chunk);
                    this.chunkLoaderManager.queue.add(chunk);

                } else {
                    chunksToUnload.remove(new Vector2i(chunkX, chunkZ));
                }
            }
        }

        for(Vector2i chunkToUnload : chunksToUnload) {
            Chunk chunk = this.loadedChunks.get(chunkToUnload);
            this.unloadChunk(chunk);
        }
    }

    public void unloadChunk(Chunk chunk) {
        this.loadedChunks.remove(new Vector2i(chunk.chunkPosition.x, chunk.chunkPosition.z));
        chunk.isUnloaded = true;
        chunk.save();
    }

    @Override
    public void tick() {
        this.chunkLoaderManager.tick();

        if(!this.ready && this.chunkLoaderManager.queue.isEmpty() && !this.chunkLoaderManager.areTasksRunning()) {
            this.ready = true;
        }

        if(!this.shouldTick || !this.ready) return;
        this.worldTime++;



        synchronized (this.loadedChunks) {
            for (Chunk chunk : this.loadedChunks.values()) {
                chunk.tick();
            }
        }

        for(int i = 0; i < this.creatures.size(); i++) {
            Creature creature = this.creatures.get(i);
            if(creature.world != this) creature.world = this;
            if(creature.markedForRemoval) {
                this.creatures.remove(i);
                if(this instanceof ServerWorld serverWorld) {
                    RemoveCreaturePacket packet = new RemoveCreaturePacket(creature.networkId);
                    serverWorld.server.sendPacketToAll(packet);
                }
                i = i - 1;
                continue;
            }

            creature.tick();

            if(creature instanceof Player) {
                this.generateChunksAround(creature.getChunkPosition().x, creature.getChunkPosition().y);
            }
        }

        for(BlockEntity blockEntity : this.blockEntities.values()) {
            blockEntity.tick();
        }
    }

    public int getDayTime() {
        return this.worldTime % (24 * 60 * 20);
    }

    public boolean isDay() {
        return this.getDayTime() > (6F * 60 * 20) && this.getDayTime() < (19F * 60 * 20);
    }

    public boolean isNight() {
        return !this.isDay();
    }

    public BlockEntity createBlockEntityFor(Vector3i blockPosition, Block block) {
        if(this.blockEntities.containsKey(blockPosition)) return null;

        if(block instanceof BlockEntityGenerator<?> generator) {
            BlockEntity blockEntity = generator.createBlockEntity();
            blockEntity.world = this;
            blockEntity.position = blockPosition;

            this.blockEntities.put(blockPosition, blockEntity);

            return blockEntity;
        } else {
            System.out.println(blockPosition.x + ", " + blockPosition.y + ", " + blockPosition.z + ": Not block entity generator (It is a " + block.getBlockId() + ")");
            return null;
        }
    }

    public void createBlockEntityFor(Vector3i blockPosition) {
        this.createBlockEntityFor(blockPosition, this.getBlockAt(blockPosition.x, blockPosition.y, blockPosition.z));
    }

    public void removeBlockEntityFor(Vector3i blockPosition) {
        this.blockEntities.remove(blockPosition);
    }

    public BlockEntity getBlockEntity(Vector3i blockPosition) {
        return this.blockEntities.get(blockPosition);
    }

    public void spawnCreature(Creature creature, Vector3f position) {
        creature.position.set(position);
        creature.lastPosition.set(position);
        this.creatures.add(creature);
    }

    public Chunk getChunkAtBlockPosition(Vector3i blockPosition) {
        int chunkX = (int) Math.floor(blockPosition.x / 16F);
        int chunkZ = (int) Math.floor(blockPosition.z / 16F);

        Vector2i chunkPosition = new Vector2i(chunkX, chunkZ);
        if(this.loadedChunks.containsKey(chunkPosition)) {
            return this.loadedChunks.get(chunkPosition);
        }
        return null;
    }

    public float getSkylightAt(int x, int y, int z) {
        int chunkX = (int) Math.floor(x / 16D);
        int chunkZ = (int) Math.floor(z / 16D);

        Vector2i chunkPosition = new Vector2i(chunkX, chunkZ);
        if(this.loadedChunks.containsKey(chunkPosition) && y >= 0 && y <= 127) {
            Byte skylight = this.loadedChunks.get(chunkPosition).skylight[Chunk.positionToBlockArrayId(x - chunkX * 16, y, z - chunkZ * 16)];

            return skylight == null ? 12 : skylight;
        }

        return 12;
    }

    public float getLightAt(int x, int y, int z) {
        int chunkX = (int) Math.floor(x / 16D);
        int chunkZ = (int) Math.floor(z / 16D);

        Vector2i chunkPosition = new Vector2i(chunkX, chunkZ);
        if(this.loadedChunks.containsKey(chunkPosition) && y >= 0 && y <= 127) {
            Byte light = this.loadedChunks.get(chunkPosition).light[Chunk.positionToBlockArrayId(x - chunkX * 16, y, z - chunkZ * 16)];

            return light == null ? 0 : light;
        }

        return 0;
    }

    public abstract Chunk createChunk(Vector2i chunkPosition);

    public abstract int getRenderDistance();

    public record WorldRaycastResult(boolean success, Block block, Vector3i position, Vector3i normal) {}

    public record SavedWorldInfo(String name, int seed, WorldType worldType){}

    public enum WorldType {
        DEFAULT(DefaultWorldGenerator::new),
        FLAT((seed) -> new FlatWorldGenerator()),
        MOON(MoonWorldGenerator::new);

        WorldType(Function<Integer, WorldGenerator> worldGeneratorFactory) {
            this.worldGeneratorFactory = worldGeneratorFactory;
        }

        Function<Integer, WorldGenerator> worldGeneratorFactory;

        public WorldGenerator getGenerator(Integer seed) {
            return this.worldGeneratorFactory.apply(seed);
        }
    }
}
