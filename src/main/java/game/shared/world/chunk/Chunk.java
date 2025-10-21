package game.shared.world.chunk;

import com.google.gson.stream.JsonReader;
import game.shared.DebugSettings;
import game.shared.Tickable;
import game.shared.util.GzipCompressionUtility;
import game.shared.util.json.WrappedJsonList;
import game.shared.util.json.WrappedJsonObject;
import game.shared.world.World;
import game.shared.world.blocks.*;
import game.shared.world.blocks.block_entity.BlockEntity;
import game.shared.world.blocks.block_entity.BlockEntityGenerator;
import game.shared.world.creature.Creature;
import game.shared.world.creature.Creatures;
import game.shared.world.creature.Player;
import org.joml.Vector2i;
import org.joml.Vector3i;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Chunk implements Tickable {
    public Block[] blocks;
    public Byte[] skylight = new Byte[16 * 16 * 128];
    public Byte[] light = new Byte[16 * 16 * 128];
    public Vector2i chunkPosition;
    private boolean isModified = false;
    public boolean isUnloaded = false;
    public World world;
    public ChunkState state = ChunkState.UNINITIALIZED;
    public boolean featuresGenerated = false;

    public static int positionToBlockArrayId(int x, int y, int z) {
        return y * 256 + z * 16 + x;
    }

    public static Vector3i blockArrayIdToPosition(int id) {
        int y = (int) Math.floor((double) id / (16 * 16));
        int z = (int) Math.floor((double) (id % (16 * 16)) / (16));
        int x = id % 16;

        return new Vector3i(x, y, z);
    }

    public void generateChunk() {
        if(this.blocks != null) {
            throw new IllegalStateException("Cannot generate an already generated chunk");
        }

        this.state = ChunkState.LOADING;

        File chunkFile = new File(this.world.chunksFolder, this.chunkPosition.x + "," + this.chunkPosition.y);
        if(chunkFile.exists()) {
            this.load();
        } else {
            this.blocks = new Block[16 * 16 * 128];
            ChunkProxy chunkProxy = new ChunkProxy(this);
            this.world.worldGenerator.generate(chunkProxy, this.chunkPosition.x, this.chunkPosition.y);
            this.setModified();
        }

        this.state = ChunkState.READY;
    }

    public void setBlockAtLocalizedPositionDirect(int x, int y, int z, Block block) {
        if(x > 15 || x < 0 || y > 127 || y < 0 || z > 15 || z < 0) return;
        this.blocks[Chunk.positionToBlockArrayId(x,y,z)] = block;
        if(block instanceof BlockEntityGenerator<?> generator) {
            BlockEntity blockEntity = generator.createBlockEntity(this.world, x + this.chunkPosition.x * 16, y, z + this.chunkPosition.y * 16);
            this.world.setBlockEntity(x + this.chunkPosition.x * 16, y, z + this.chunkPosition.y * 16, blockEntity);
        }
    }

    public boolean setBlockAtLocalizedPosition(int x, int y, int z, Block block) {
        if(x > 15 || x < 0 || y > 127 || y < 0 || z > 15 || z < 0) return false;
        Block existingBlock = this.blocks[Chunk.positionToBlockArrayId(x,y,z)];
        if(existingBlock == block) return false;

        if(existingBlock instanceof BlockEntityGenerator<?>) {
            this.world.removeBlockEntity(x + this.chunkPosition.x * 16, y, z + this.chunkPosition.y * 16);
        }

        this.blocks[Chunk.positionToBlockArrayId(x,y,z)] = block;
        this.setModified();

        this.calculateSkylight();


        if(block instanceof BlockEntityGenerator<?> generator) {
            BlockEntity blockEntity = generator.createBlockEntity(world, x + this.chunkPosition.x * 16, y, z + this.chunkPosition.y * 16);
            this.world.setBlockEntity(x + this.chunkPosition.x * 16, y, z + this.chunkPosition.y * 16, blockEntity);
        }

        return true;
    }

    public void setBlockDirect(int x, int y, int z, Block block) {
        if(x > 15 || x < 0 || y > 127 || y < 0 || z > 15 || z < 0) return;
        this.blocks[Chunk.positionToBlockArrayId(x,y,z)] = block;
        this.setModified();
    }

    public boolean isModified() {
        return this.isModified;
    }

    public void setModified() {
        this.isModified = true;
    }

    public void save() {
        if(!this.isModified || !this.isReady()) {
            return;
        }

        File chunkFile = new File(this.world.chunksFolder, this.chunkPosition.x + "," + this.chunkPosition.y);
        if(!chunkFile.exists()) {
            try {
                chunkFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to save chunk: ", e);
            }
        }

        // Get all blockIds in the chunk to assign a number to them
        WrappedJsonObject blockToChunkSavedIds = new WrappedJsonObject();
        blockToChunkSavedIds.put("air", 0);
        int nextNumber = 1;

        WrappedJsonObject blockEntities = new WrappedJsonObject();

        int[] savedData = new int[this.blocks.length];

        for(int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 128; y++) {
                    int blockArrayId = Chunk.positionToBlockArrayId(x,y,z);
                    Block block = this.blocks[blockArrayId];
                    if(block == null) block = Blocks.AIR;

                    if(block instanceof BlockEntityGenerator<?>) {
                        Vector3i position = new Vector3i(x + this.chunkPosition.x * 16, y, z + this.chunkPosition.y * 16);
                        WrappedJsonObject json = new WrappedJsonObject();
                        this.world.getBlockEntity(position.x, position.y, position.z).save(json);
                        blockEntities.put(String.valueOf(blockArrayId), json);
                    }

                    if(blockToChunkSavedIds.containsKey(block.getBlockId())) {
                        savedData[blockArrayId] = blockToChunkSavedIds.getInt(block.getBlockId());
                    } else {
                        blockToChunkSavedIds.put(block.getBlockId(), nextNumber);
                        savedData[blockArrayId] = nextNumber;
                        nextNumber++;
                    }
                }
            }
        }

        WrappedJsonList creatures = new WrappedJsonList();
        for(Creature creature : this.getCreaturesInChunk()) {
            if(creature instanceof Player) {
                continue;
            }
            
            WrappedJsonObject creatureJson = new WrappedJsonObject();
            creature.save(creatureJson);
            creatures.add(creatureJson);
        }

        WrappedJsonObject chunkData = new WrappedJsonObject();
        chunkData.put("version", World.CHUNK_VERSION);
        chunkData.put("blockIdToSaveId", blockToChunkSavedIds);
        chunkData.put("blockData", savedData);
        chunkData.put("blockEntities", blockEntities);
        chunkData.put("creatures", creatures);
        chunkData.put("featuresGenerated", this.featuresGenerated);

        try {
            if(DebugSettings.compressChunkData) {
                GzipCompressionUtility.compressToStream(chunkData.toString().getBytes(), new FileOutputStream(chunkFile));
            } else {
                FileOutputStream fileOutputStream = new FileOutputStream(chunkFile);
                fileOutputStream.write(chunkData.toString().getBytes());
                fileOutputStream.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.isModified = false;
    }

    public void load() {
        File chunkFile = new File(this.world.chunksFolder, this.chunkPosition.x + "," + this.chunkPosition.y);
        if(!chunkFile.exists()) return;

        WrappedJsonObject chunkData;

        try {
            byte[] data;
            if(DebugSettings.compressChunkData) {
                data = GzipCompressionUtility.decompressFromStream(new FileInputStream(chunkFile));
            } else {
                FileInputStream fileInputStream = new FileInputStream(chunkFile);
                data = fileInputStream.readAllBytes();
                fileInputStream.close();
            }
            chunkData = WrappedJsonObject.read(new JsonReader(new StringReader(new String(data))));

            if(chunkData == null) {
                throw new IllegalStateException("read operation returned null");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load chunk: ", e);
        }

        int chunkVersion = chunkData.getIntOrDefault("version", World.CHUNK_VERSION);

        HashMap<Integer, String> idToBlockId = new HashMap<>();

        for (String blockId : chunkData.getObject("blockIdToSaveId").children.keySet()) {
            idToBlockId.put(chunkData.getObject("blockIdToSaveId").getInt(blockId), blockId);
        }

        this.blocks = new Block[16 * 16 * 128];

        for(int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 128; y++) {
                    int blockArrayId = Chunk.positionToBlockArrayId(x,y,z);
                    int blockId = ((Long) chunkData.getList("blockData").get(blockArrayId)).intValue();

                    Block block = Blocks.idToBlock.get(idToBlockId.get(blockId));
                    if(block == null) {
                        throw new IllegalStateException("Attempted to load in invalid block id " + blockId);
                    }

                    if(!(block instanceof AirBlock)) {
                        this.blocks[blockArrayId] = block;

                        if(block instanceof BlockEntityGenerator<?> generator) {
                            BlockEntity blockEntity = generator.createBlockEntity(this.world, x + this.chunkPosition.x * 16, y, z + this.chunkPosition.y * 16);
                            WrappedJsonObject blockEntityData = chunkData.getObject("blockEntities").getObject(String.valueOf(blockArrayId));
                            if(blockEntityData != null) {
                                blockEntity.load(blockEntityData);
                            }
                            this.world.setBlockEntity(x + this.chunkPosition.x * 16, y, z + this.chunkPosition.y * 16, blockEntity);
                        }
                    }
                }
            }
        }

        WrappedJsonList creatures = chunkData.getJsonList("creatures");
        for (int i = 0; i < creatures.size(); i++) {
            WrappedJsonObject creatureJson = creatures.getObject(i);
            String id = creatureJson.getString("id");

            try {
                Creature creature = Creatures.getClassFor(id).getConstructor().newInstance();
                creature.load(creatureJson);
                this.world.spawnCreature(creature);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load creature with id " + id, e);
            }
        }

        this.featuresGenerated = chunkData.getBoolean("featuresGenerated");

        this.calculateSkylight();

        this.state = ChunkState.READY;
    }

    public boolean isPositionInsideChunk(float x, float y, float z) {
        return Math.floor(x / 16F) == this.chunkPosition.x && Math.floor(y / 128F) == this.chunkPosition.y && Math.floor(z / 16F) == this.chunkPosition.y;
    }

    public Vector3i toLocalizedBlockPosition(int x, int y, int z) {
        return new Vector3i((int) (this.chunkPosition.x - Math.floor(x / 16)), y, (int) (this.chunkPosition.y - Math.floor(z / 16)));
    }

    public Block getBlockAtLocalizedPosition(int x, int y, int z) {
        if(y < 0) return Blocks.BEDROCK; // Don't generate vertices at the bottom of the chunks
        if(x > 15 || x < 0 || y > 127 || z > 15 || z < 0) return Blocks.AIR;

        int arrayId = Chunk.positionToBlockArrayId(x,y,z);

        return this.blocks[arrayId] == null ? Blocks.AIR : this.blocks[arrayId];
    }

    public Block getBlockAtLocalizedPositionDirect(int x, int y, int z) {
        if(x > 15 || x < 0 || y > 127 || z > 15 || z < 0 || y < 0) return null;

        int arrayId = Chunk.positionToBlockArrayId(x,y,z);

        return this.blocks[arrayId];
    }

    public void calculateSkylight() {
        this.skylight = new Byte[16 * 16 * 128];

        for(int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                boolean underneathBlock = false;
                for (int y = 127; y >= 0; y--) {
                    Block block = this.getBlockAtLocalizedPosition(x, y, z);
                    // Scuffed but :shrug:
                    if(block.isEmpty() || block instanceof CrossBlock || block.isLiquid()) {
                        if(underneathBlock) {
                            skylight[Chunk.positionToBlockArrayId(x, y, z)] = 12;
                        } else {
                            skylight[Chunk.positionToBlockArrayId(x, y, z)] = 16;
                        }
                        if(block.isLiquid()) {
                            underneathBlock = true;
                        }
                    } else {
                        underneathBlock = true;
                    }
                }
            }
        }
    }

    public boolean areDirectNeighboursLoaded() {
        return     this.world.getChunk(this.chunkPosition.x + 1, this.chunkPosition.y) != null
                && this.world.getChunk(this.chunkPosition.x - 1, this.chunkPosition.y) != null
                && this.world.getChunk(this.chunkPosition.x, this.chunkPosition.y + 1) != null
                && this.world.getChunk(this.chunkPosition.x, this.chunkPosition.y - 1) != null;
    }

    public boolean areNeighboursLoaded() {
        return     this.world.getChunk(this.chunkPosition.x + 1, this.chunkPosition.y) != null
                && this.world.getChunk(this.chunkPosition.x - 1, this.chunkPosition.y) != null
                && this.world.getChunk(this.chunkPosition.x + 1, this.chunkPosition.y + 1) != null
                && this.world.getChunk(this.chunkPosition.x + 1, this.chunkPosition.y - 1) != null
                && this.world.getChunk(this.chunkPosition.x - 1, this.chunkPosition.y - 1) != null
                && this.world.getChunk(this.chunkPosition.x - 1, this.chunkPosition.y + 1) != null
                && this.world.getChunk(this.chunkPosition.x, this.chunkPosition.y + 1) != null
                && this.world.getChunk(this.chunkPosition.x, this.chunkPosition.y - 1) != null;
    }

    public boolean areNeighboursFullyGenerated() {
        if(!this.areNeighboursLoaded()) {
            return false;
        }
        return     this.world.getChunk(this.chunkPosition.x + 1, this.chunkPosition.y).featuresGenerated
                && this.world.getChunk(this.chunkPosition.x - 1, this.chunkPosition.y).featuresGenerated
                && this.world.getChunk(this.chunkPosition.x + 1, this.chunkPosition.y + 1).featuresGenerated
                && this.world.getChunk(this.chunkPosition.x + 1, this.chunkPosition.y - 1).featuresGenerated
                && this.world.getChunk(this.chunkPosition.x - 1, this.chunkPosition.y - 1).featuresGenerated
                && this.world.getChunk(this.chunkPosition.x - 1, this.chunkPosition.y + 1).featuresGenerated
                && this.world.getChunk(this.chunkPosition.x, this.chunkPosition.y + 1).featuresGenerated
                && this.world.getChunk(this.chunkPosition.x, this.chunkPosition.y - 1).featuresGenerated;
    }

    public void generateFeatures() {
        if(this.featuresGenerated && !this.areNeighboursLoaded()) {
            return;
        }

        ChunkProxy chunkProxy = new ChunkProxy(this);
        this.world.worldGenerator.generateFeatures(chunkProxy, this.chunkPosition.x, this.chunkPosition.y);
        this.featuresGenerated = true;
    }

    public void tick() {
        if(!this.areNeighboursFullyGenerated()) {
            return;
        }

        for(int ySection = 0; ySection <= 7; ySection++) {
            for(int i = 0; i < 3; i++) {
                int x = this.world.random.nextInt() % 16;
                int y = ySection * 16 + this.world.random.nextInt() % 16;
                int z = this.world.random.nextInt() % 16;

                if(this.getBlockAtLocalizedPosition(x,y,z) instanceof RandomTickable randomTickable) {
                    ChunkProxy chunkProxy = new ChunkProxy(this);
                    chunkProxy.direct = false;
                    randomTickable.randomTick(chunkProxy, x, y, z, this.chunkPosition.x * 16 + x, y, this.chunkPosition.y * 16 + z);
                }
            }
        }
    }

    public void unload() {
        if(this.isUnloaded) return;
        this.isUnloaded = true;
        this.save();
        this.removeCreatures();
    }

    public ArrayList<Creature> getCreaturesInChunk() {
        ArrayList<Creature> creatures = new ArrayList<>();

        for (int i = 0; i < this.world.creatures.size(); i++) {
            Creature creature = this.world.creatures.get(i);

            if(creature.getChunkPosition().x == this.chunkPosition.x && creature.getChunkPosition().y == this.chunkPosition.y) {
                creatures.add(creature);
            }
        }

        return creatures;
    }

    public void removeCreatures() {
        for(Creature creature : this.getCreaturesInChunk()) {
            creature.remove();
        }
    }

    public boolean isReady() {
        return this.blocks != null && this.state == ChunkState.READY;
    }

    public enum ChunkState {
        UNINITIALIZED,
        LOADING,
        READY
    }
}
