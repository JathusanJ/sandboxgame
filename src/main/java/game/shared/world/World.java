package game.shared.world;

import com.google.gson.stream.JsonReader;
import game.shared.Version;
import game.shared.util.json.WrappedJsonObject;
import game.shared.world.blocks.Blocks;
import game.shared.world.blocks.block_entity.BlockEntityGenerator;
import game.shared.world.chunk.Chunk;
import game.shared.Tickable;
import game.shared.world.blocks.Block;
import game.shared.world.blocks.block_entity.BlockEntity;
import game.shared.world.chunk.ChunkLoaderManager;
import game.shared.world.creature.Creature;
import game.shared.world.generators.*;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class World implements Tickable {
    public static int CHUNK_VERSION = 2;

    public Map<Vector2i, Chunk> loadedChunks = new ConcurrentHashMap<>();
    public List<Creature> creatures = Collections.synchronizedList(new ArrayList<>());
    public Map<Vector3i, BlockEntity> blockEntities = new ConcurrentHashMap<>();
    public File worldFolder;
    public File chunksFolder;
    public String worldFolderName;
    public Integer seed;
    public String name;
    public WorldGenerator worldGenerator;
    public WorldType worldType;
    public int worldTime = (int) (6.5 * 60 * 20);
    public boolean shouldTick = true;
    public int chunkVersion;

    public boolean ready = false;
    public ChunkLoaderManager chunkLoaderManager = new ChunkLoaderManager(this);
    public ChunkLoaderManager.Ticket spawnLoadTicket = new ChunkLoaderManager.Ticket(0,0,4);
    public Random random = new Random();
    public boolean commandsEnabled = false;

    public Logger logger = LoggerFactory.getLogger("World");
    public String lastSavedIn;
    public long lastSavedAt;

    public boolean loadWorldInfo() {
        File worldInfoFile = new File(this.worldFolder, "world.json");
        if(worldInfoFile.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(worldInfoFile);
                WrappedJsonObject json = WrappedJsonObject.read(new JsonReader(new StringReader(new String(fileInputStream.readAllBytes()))));
                fileInputStream.close();

                if(json == null) {
                    throw new IllegalStateException("read operation returned null");
                }

                this.name = json.getStringOrDefault("name", "N/A");
                this.seed = json.getIntOrDefault("seed", 0);
                this.worldType = WorldType.valueOf(json.getStringOrDefault("worldType", "DEFAULT"));
                this.worldTime = json.getIntOrDefault("worldTime", 0);
                this.commandsEnabled = json.getBooleanOrDefault("commandsEnabled", false);
                this.lastSavedIn = json.getStringOrDefault("lastSavedIn", "N/A");
                this.lastSavedAt = json.getLongOrDefault("lastSavedAt", 0);
                this.chunkVersion = json.getIntOrDefault("chunkVersion", 0);

                return true;
            } catch (Exception e) {
                throw new RuntimeException("Couldn't read world info of " + worldInfoFile.getPath(), e);
            }
        }

        return false;
    }

    public void writeWorldInfo() {
        File worldInfoFile = new File(this.worldFolder, "world.json");
        WrappedJsonObject json = new WrappedJsonObject();
        json.put("chunkVersion", CHUNK_VERSION);
        json.put("name", this.name);
        json.put("seed", this.seed);
        json.put("worldType", this.worldType);
        json.put("worldTime", this.worldTime);
        json.put("commandsEnabled", this.commandsEnabled);
        json.put("lastSavedIn", Version.GAME_VERSION.versionName());
        json.put("lastSavedAt", System.currentTimeMillis());

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

    public void save() {
        this.logger.info("Saving world");

        for(Chunk chunk : this.loadedChunks.values()) {
            this.logger.debug("Saving chunk {}", chunk.chunkPosition);
            chunk.save();
        }

        this.writeWorldInfo();

        this.logger.info("Saved world");
    }

    public void stop() {
        this.chunkLoaderManager.stop();
    }

    @Override
    public void tick() {
        if(!this.ready || !this.shouldTick) {
            return;
        }

        this.worldTime++;

        for (Chunk chunk : this.loadedChunks.values()) {
            chunk.tick();
        }

        for (BlockEntity blockEntity : this.blockEntities.values()) {
            blockEntity.tick();
        }

        for (int i = 0; i < this.creatures.size(); i++) {
            Creature creature = this.creatures.get(i);
            if(creature.markedForRemoval) {
                this.creatures.remove(i);
                this.onCreatureRemoved(creature);
            } else {
                this.creatures.get(i).tick();
            }
        }
    }

    public void onCreatureRemoved(Creature creature) {

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
            Block block = this.getBlock(blockPos.x, blockPos.y, blockPos.z);
            boolean isLiquid = ignoreLiquids && block.isLiquid();
            if(!block.isEmpty() && !isLiquid && block != Blocks.BARRIER) {
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
            block = this.getBlock(blockPos.x, blockPos.y, blockPos.z);
            isLiquid = ignoreLiquids && block.isLiquid();
            if(!block.isEmpty() && !isLiquid && block != Blocks.BARRIER) {
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
            block = this.getBlock(blockPos.x, blockPos.y, blockPos.z);
            isLiquid = ignoreLiquids && block.isLiquid();
            if(!block.isEmpty() && !isLiquid && block != Blocks.BARRIER) {
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

    public Vector2i getChunkPositionOfPosition(int x, int y, int z) {
        int chunkX = (int) Math.floor(x / 16F);
        int chunkZ = (int) Math.floor(z / 16F);

        return new Vector2i(chunkX, chunkZ);
    }

    public boolean setBlock(int x, int y, int z, Block block) {
        Vector2i chunkPosition = this.getChunkPositionOfPosition(x, y, z);

        Chunk chunk = this.loadedChunks.get(chunkPosition);

        if(chunk == null) {
            return false;
        }

        chunk.setBlockAtLocalizedPosition(x - chunkPosition.x * 16, y, z - chunkPosition.y * 16, block);

        if(block instanceof BlockEntityGenerator<?> generator) {
            BlockEntity blockEntity = generator.createBlockEntity(this, x, y, z);
            this.blockEntities.put(new Vector3i(x,y,z), blockEntity);
        }

        return true;
    }

    public void setBlockNoRemesh(int x, int y, int z, Block block) {
        Vector2i chunkPosition = this.getChunkPositionOfPosition(x, y, z);

        Chunk chunk = this.loadedChunks.get(chunkPosition);

        if(chunk == null) {
            return;
        }

        chunk.setBlockDirect(x - chunkPosition.x * 16, y, z - chunkPosition.y * 16, block);

        if(block instanceof BlockEntityGenerator<?> generator) {
            BlockEntity blockEntity = generator.createBlockEntity(this, x, y ,z);
            this.blockEntities.put(new Vector3i(x,y,z), blockEntity);
        }
    }

    public void setBlock(Vector3i blockPosition, Block block) {
        this.setBlock(blockPosition.x, blockPosition.y, blockPosition.z, block);
    }

    public Block getBlock(int x, int y, int z) {
        Vector2i chunkPosition = this.getChunkPositionOfPosition(x, y, z);

        Chunk chunk = this.loadedChunks.get(chunkPosition);

        if(chunk == null) {
            return Blocks.AIR;
        }

        return chunk.getBlockAtLocalizedPosition(x - chunkPosition.x * 16, y, z - chunkPosition.y * 16);
    }

    public Block getBlock(Vector3i blockPosition) {
        return this.getBlock(blockPosition.x, blockPosition.y, blockPosition.z);
    }

    public BlockEntity getBlockEntity(int x, int y, int z) {
        return this.blockEntities.get(new Vector3i(x,y,z));
    }

    public void setBlockEntity(int x, int y, int z, BlockEntity blockEntity) {
        this.blockEntities.put(new Vector3i(x,y,z), blockEntity);
    }

    public void removeBlockEntity(int x, int y, int z) {
        BlockEntity blockEntity = this.blockEntities.get(new Vector3i(x,y,z));
        if(blockEntity == null) {
            return;
        }
        blockEntity.onDestroy();
        this.blockEntities.remove(new Vector3i(x,y,z));
    }

    public int getSkylight(int x, int y, int z) {
        if(y < 0 || y > 127) {
            return 16;
        }

        Vector2i chunkPosition = this.getChunkPositionOfPosition(x, y, z);

        Chunk chunk = this.loadedChunks.get(chunkPosition);

        if(chunk == null) {
            return 16;
        }

        Byte skylight = chunk.skylight[Chunk.positionToBlockArrayId(x - chunkPosition.x * 16, y, z - chunkPosition.y * 16)];

        return skylight == null ? 0 : skylight;
    }

    public int getLight(int x, int y, int z) {
        if(y < 0 || y > 127) {
            return 0;
        }

        Vector2i chunkPosition = this.getChunkPositionOfPosition(x, y, z);

        Chunk chunk = this.loadedChunks.get(chunkPosition);

        if(chunk == null) {
            return 0;
        }

        return 0; //chunk.light[Chunk.positionToBlockArrayId(x - chunkPosition.x * 16, y, z - chunkPosition.y * 16)];
    }

    public abstract Chunk createChunk(int x, int y);

    public Chunk getChunk(int x, int y) {
        Chunk chunk = this.loadedChunks.get(new Vector2i(x,y));

        if(chunk == null || !chunk.isReady()) {
            return null;
        }

        return chunk;
    }

    public void unloadChunk(Chunk chunk) {
        chunk.unload();
        this.loadedChunks.remove(chunk.chunkPosition);
    }

    public void spawnCreature(Creature creature) {
        this.creatures.add(creature);
        creature.world = this;
    }

    public float getDayTime() {
        return this.worldTime % (24 * 60 * 20);
    }

    public Vector3f findPossibleSpawnLocation() {
        for (int i = 0; i < 20; i++) {
            int spawnRadius = 32;

            int x = this.random.nextInt() % (spawnRadius * 2) - spawnRadius;
            int z = this.random.nextInt() % (spawnRadius * 2) - spawnRadius;

            for (int y = 127; y > 0; y--) {
                Block blockAtPosition = this.getBlock(x,y,z);
                if(blockAtPosition == Blocks.WATER) {
                    break;
                }
                if(blockAtPosition.hasCollision()) {
                    return new Vector3f(x + 0.5F, y + 1F, z + 0.5F);
                }
            }
        }


        // If no locations are found then place the player at 0,0 at the first collidable block or water
        for (int y = 127; y > 0; y--) {
            Block blockAtPosition = this.getBlock(0,y,0);
            if(blockAtPosition == Blocks.WATER || blockAtPosition.hasCollision()) {
                return new Vector3f(0.5F, y + 1F, 0.5F);
            }
        }

        // Or if there's just void
        return new Vector3f(0F, 100F, 0F);
    }

    public record WorldRaycastResult(boolean success, Block block, Vector3i position, Vector3i normal) {}

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
