package game.networking.packets;

import game.logic.world.chunk.Chunk;
import game.logic.world.blocks.Block;
import game.networking.ByteBufPacketDecoder;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChunkDataPacket extends Packet {
    public Chunk chunk;

    public ChunkDataPacket(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeInt(this.chunk.chunkPosition.x);
        buffer.writeInt(this.chunk.chunkPosition.y);

        HashMap<Block, Short> blockToSaveId = new HashMap<>();
        ArrayList<Short> data = new ArrayList<>();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 128; y++) {
                    Block block = this.chunk.getBlockAtLocalizedPosition(x,y,z);

                    if(!blockToSaveId.containsKey(block)) {
                        blockToSaveId.put(block, (short) blockToSaveId.size());
                    }

                    data.add(blockToSaveId.get(block));
                }
            }
        }

        buffer.writeShort(blockToSaveId.size());
        for(Map.Entry<Block, Short> entry : blockToSaveId.entrySet()) {
            ByteBufPacketDecoder.writeString(buffer, entry.getKey().getBlockId());
            buffer.writeShort(entry.getValue());
        }

        for(Short id : data) {
            buffer.writeShort(id);
        }
    }
}
