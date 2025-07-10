package game.networking;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

// Helpful: https://netty.io/4.1/xref/io/netty/example/factorial/BigIntegerDecoder.html#BigIntegerDecoder
public class ByteBufPacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> output) throws Exception {
        // The first 2 bytes indicate the size of the entire packet
        // Those need to be available before continuing
        if(byteBuf.readableBytes() < 2) return;

        byteBuf.markReaderIndex(); // To return to the start of the buffer in case not all the data has arrived

        int packetSize = byteBuf.readShort(); // 2 bytes should hopefully be enough, can expand to 3 if needed
        if(byteBuf.readableBytes() < packetSize) {
            byteBuf.resetReaderIndex();
            return;
        }

        output.add(byteBuf);
    }

    public static void writeString(ByteBuf buffer, String text) {
        if(text.getBytes(StandardCharsets.UTF_8).length > Short.MAX_VALUE) {
            throw new IllegalStateException("String too long");
        }

        buffer.writeShort(text.length());
        buffer.writeCharSequence(text, StandardCharsets.UTF_8);
    }

    public static String readString(ByteBuf buffer) {
        int length = buffer.readShort();
        return buffer.readCharSequence(length, StandardCharsets.UTF_8).toString();
    }
}
