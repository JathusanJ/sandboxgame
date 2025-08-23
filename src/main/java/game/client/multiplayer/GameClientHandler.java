package game.client.multiplayer;

import game.client.SandboxGame;
import game.client.ui.screen.DisconnectedScreen;
import game.shared.multiplayer.packets.Packet;
import game.shared.multiplayer.packets.LoginAttemptPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class GameClientHandler extends ChannelInboundHandlerAdapter {
    public static ChannelHandlerContext serverHandler;
    public ClientPacketHandler packetHandler = new ClientPacketHandler();

    public GameClientHandler() {
        packetHandler.setup();
    }

    public static ByteBuf allocatePacketBuffer() {
        ByteBuf buffer = serverHandler.alloc().buffer();
        buffer.writeShort(0); // Placeholder for size
        return buffer;
    }

    public static void sendPacket(Packet packet) {
        serverHandler.writeAndFlush(packet);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;

        packetHandler.readAndHandle(null, buffer);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SandboxGame.getInstance().logger.error("Exception while playing on multiplayer", cause);

        ctx.close();

        SandboxGame.getInstance().doOnMainThread(() -> {
            if(SandboxGame.getInstance().getGameRenderer().world != null) {
                SandboxGame.getInstance().getGameRenderer().world.deleteChunkMeshes();
                SandboxGame.getInstance().getGameRenderer().chunkMeshesToDelete.clear();
                SandboxGame.getInstance().getGameRenderer().world = null;
                SandboxGame.getInstance().getGameRenderer().player = null;
                SandboxGame.getInstance().getGameRenderer().setScreen(new DisconnectedScreen(cause.getMessage()));
            }
        });
        GameClient.isConnectedToServer = false;
        GameClient.disconnectWithoutPacket();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        serverHandler = ctx;
        GameClient.isConnectedToServer = true;

        LoginAttemptPacket packet = new LoginAttemptPacket(SandboxGame.getInstance().getPlayerProfile().getUsername(), SandboxGame.getInstance().getPlayerProfile().getSkin());
        sendPacket(packet);
    }
}
