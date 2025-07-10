package game.logic.world.chunk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import game.logic.DebugSettings;
import game.logic.Tickable;
import game.logic.util.GzipCompressionUtility;
import game.client.world.ClientChunk;
import game.logic.util.json.WrappedJsonObject;
import game.logic.world.World;
import game.logic.world.blocks.AirBlock;
import game.logic.world.blocks.Block;
import game.logic.world.blocks.Blocks;
import game.logic.world.blocks.block_entity.BlockEntity;
import game.logic.world.blocks.block_entity.BlockEntityGenerator;
import org.joml.Vector3i;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Chunk implements Tickable {
    public Block[] blocks;
    public Byte[] skylight = new Byte[16 * 16 * 128];
    public Byte[] light = new Byte[16 * 16 * 128];
    public Vector3i chunkPosition;
    private boolean isModified = false;
    public boolean isUnloaded = false;
    public World world;
    public int chunkUnloadingTimer = 0;
    public ChunkState state = ChunkState.UNINITIALIZED;

    public static Gson gson = new GsonBuilder().create();

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

        File chunkFile = new File(this.world.chunksFolder, this.chunkPosition.x + "," + this.chunkPosition.z);
        if(chunkFile.exists()) {
            this.load();
        } else {
            HashMap<Vector3i, Block> blocksMap = this.world.worldGenerator.generate(this.chunkPosition.x, this.chunkPosition.z);
            // TODO: insert into the array directly
            this.blocks = new Block[16 * 16 * 128];
            for(int y = 0; y < 128; y++) {
                for(int z = 0; z < 16; z++) {
                    for(int x = 0; x < 16; x++) {
                        this.blocks[Chunk.positionToBlockArrayId(x,y,z)] = blocksMap.get(new Vector3i(x,y,z));
                        if(this.blocks[Chunk.positionToBlockArrayId(x,y,z)] instanceof BlockEntityGenerator<?>) {
                            this.world.createBlockEntityFor(new Vector3i(x + this.chunkPosition.x * 16, y, z + this.chunkPosition.z * 16), this.blocks[ClientChunk.positionToBlockArrayId(x,y,z)]);
                        }
                    }
                }
            }
            this.setModified();
        }

        this.calculateSkylight();

        this.state = ChunkState.READY;
    }

    public boolean setBlockAtLocalizedPosition(int x, int y, int z, Block block) {
        if(x > 15 || x < 0 || y > 127 || y < 0 || z > 15 || z < 0) return false;
        Block existingBlock = this.blocks[Chunk.positionToBlockArrayId(x,y,z)];
        if(existingBlock == block) return false;

        if(existingBlock instanceof BlockEntityGenerator<?>) {
            this.world.removeBlockEntityFor(new Vector3i(x + this.chunkPosition.x * 16, y, z + this.chunkPosition.z * 16));
        }

        this.blocks[Chunk.positionToBlockArrayId(x,y,z)] = block;
        this.setModified();

        this.calculateSkylight();


        if(block instanceof BlockEntityGenerator<?>) {
            this.world.createBlockEntityFor(new Vector3i(x + this.chunkPosition.x * 16, y, z + this.chunkPosition.z * 16), block);
        }

        return true;
    }

    public boolean isModified() {
        return this.isModified;
    }

    public void setModified() {
        this.isModified = true;
    }

    public void save() {
        if(!this.isModified || this.blocks == null || this.state != ChunkState.READY) return;

        File chunkFile = new File(this.world.chunksFolder, this.chunkPosition.x + "," + this.chunkPosition.z);
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
                        Vector3i position = new Vector3i(x + this.chunkPosition.x * 16, y, z + this.chunkPosition.z * 16);
                        WrappedJsonObject json = new WrappedJsonObject();
                        this.world.getBlockEntity(position).save(json);
                        blockEntities.put(String.valueOf(blockArrayId), json);

                        this.world.removeBlockEntityFor(position);
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

        WrappedJsonObject chunkData = new WrappedJsonObject();
        chunkData.put("blockIdToSaveId", blockToChunkSavedIds);
        chunkData.put("blockData", savedData);
        chunkData.put("blockEntities", blockEntities);

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
        File chunkFile = new File(this.world.chunksFolder, this.chunkPosition.x + "," + this.chunkPosition.z);
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
        } catch (IOException e) {
            throw new RuntimeException("Failed to load chunk: ", e);
        }

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

                        if(block instanceof BlockEntityGenerator<?>) {
                            Vector3i position = new Vector3i(x + this.chunkPosition.x * 16, y, z + this.chunkPosition.z * 16);
                            BlockEntity blockEntity = this.world.createBlockEntityFor(position, block);
                            WrappedJsonObject blockEntityData = chunkData.getObject("blockEntities").getObject(String.valueOf(blockArrayId));
                            if(blockEntity != null && blockEntityData != null) {
                                blockEntity.load(blockEntityData);
                            }
                        }
                    }
                }
            }
        }
        this.state = ChunkState.READY;
    }

    public boolean isPositionInsideChunk(float x, float y, float z) {
        return Math.floor(x / 16F) == this.chunkPosition.x && Math.floor(y / 128F) == this.chunkPosition.y && Math.floor(z / 16F) == this.chunkPosition.z;
    }

    public Vector3i toLocalizedBlockPosition(int x, int y, int z) {
        return new Vector3i((int) (this.chunkPosition.x - Math.floor(x / 16)), y, (int) (this.chunkPosition.z - Math.floor(z / 16)));
    }

    public Block getBlockAtLocalizedPosition(int x, int y, int z) {
        if(y < 0) return Blocks.BEDROCK; // Don't generate vertices at the bottom of the chunks
        if(x > 16 || x < 0 || y > 127 || z > 16 || z < 0) return Blocks.AIR;

        int arrayId = Chunk.positionToBlockArrayId(x,y,z);

        return this.blocks[arrayId] == null ? Blocks.AIR : this.blocks[arrayId];
    }

    public void calculateSkylight() {
        this.skylight = new Byte[16 * 16 * 128];

        for(int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 127; y >= 0; y--) {
                    if (this.getBlockAtLocalizedPosition(x, y, z).isEmpty()) {
                        skylight[ClientChunk.positionToBlockArrayId(x, y, z)] = 16;
                    } else {
                        break;
                    }
                }
            }
        }
    }

    public boolean areNeighboursLoaded() {
        return     this.world.getChunkAt(this.chunkPosition.x + 1, this.chunkPosition.z) != null
                && this.world.getChunkAt(this.chunkPosition.x - 1, this.chunkPosition.z) != null
                && this.world.getChunkAt(this.chunkPosition.x, this.chunkPosition.z + 1) != null
                && this.world.getChunkAt(this.chunkPosition.x, this.chunkPosition.z - 1) != null;
    }

    public void tick() {
        this.chunkUnloadingTimer = this.chunkUnloadingTimer - 1;
    }

    public static class FileContents {
        public HashMap<String, Integer> blockIdToSaveId;
        public ArrayList<Integer> data;
        public HashMap<Integer, String> blockEntities;
    }

    public enum ChunkState {
        UNINITIALIZED,
        LOADING,
        READY
    }
}
