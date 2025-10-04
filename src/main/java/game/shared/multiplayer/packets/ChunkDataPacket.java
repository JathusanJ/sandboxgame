package game.shared.multiplayer.packets;

import game.shared.world.blocks.Blocks;
import game.shared.world.chunk.Chunk;
import game.shared.world.blocks.Block;
import game.shared.multiplayer.ByteBufPacketDecoder;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.zip.Deflater;

public class ChunkDataPacket extends Packet {
    public Chunk chunk;

    public ChunkDataPacket(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeByte(this.chunk.chunkPosition.x);
        buffer.writeByte(this.chunk.chunkPosition.y);

        ArrayList<Block> idToBlock = new ArrayList<>();
        idToBlock.add(Blocks.AIR);

        byte[] blocks = new byte[16 * 16 * 128];

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 128; y++) {
                    int arrayId = Chunk.positionToBlockArrayId(x,y,z);

                    Block block = this.chunk.blocks[arrayId];
                    if(block == null) {
                        block = Blocks.AIR;
                    }

                    if(!idToBlock.contains(block)) {
                        idToBlock.add(block);
                    }

                    blocks[arrayId] = (byte) idToBlock.indexOf(block);
                }
            }
        }

        buffer.writeByte(idToBlock.size());

        for (int i = 0; i < idToBlock.size(); i++) {
            buffer.writeByte(i);
            ByteBufPacketDecoder.writeString(buffer, idToBlock.get(i).getBlockId());
        }

        int amountOfBlocksToSend = blocks.length;

        for (int i = blocks.length - 1; i >= 0; i--) {
            if(blocks[i] != 0) {
                amountOfBlocksToSend = i + 1;
                break;
            }
        }

        buffer.writeInt(amountOfBlocksToSend);

        try {
            Deflater deflater = new Deflater();
            deflater.setInput(blocks, 0, amountOfBlocksToSend);
            deflater.finish();
            byte[] finalData = new byte[amountOfBlocksToSend];
            buffer.writeInt(deflater.deflate(finalData));

            for (int i = 0; i < deflater.getBytesWritten(); i++) {
                buffer.writeByte(finalData[i]);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
