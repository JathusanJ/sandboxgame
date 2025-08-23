package game.client.multiplayer;

import game.client.SandboxGame;
import game.client.ui.screen.DisconnectedScreen;
import game.client.ui.screen.TitleScreen;
import game.shared.world.blocks.Block;
import game.shared.multiplayer.ByteBufPacketDecoder;
import game.shared.multiplayer.ByteBufPacketEncoder;
import game.shared.multiplayer.packets.DisconnectPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class GameClient {
    public static boolean isConnectedToServer = false;
    public static GameClientHandler handler;
    public static ClientState state = ClientState.UNINITIALIZED;
    public static int serverRenderDistance = 0;
    public static Block[] barrierChunk;

    public static void connect(String host, int port) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        handler = new GameClientHandler();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    channel.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(Short.MAX_VALUE * 100));
                    channel.pipeline().addLast(new ByteBufPacketEncoder(), new ByteBufPacketDecoder(), handler);
                }
            });

            ChannelFuture future = bootstrap.connect(host, port).sync();
            GameClient.state = ClientState.CONNECTING;

            future.channel().closeFuture().sync();
        } catch(Exception e) {
            SandboxGame.getInstance().doOnMainThread(() -> {
                SandboxGame.getInstance().getGameRenderer().setScreen(new DisconnectedScreen(e.toString()));
            });
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void disconnect() {
        SandboxGame.getInstance().doOnMainThread(() -> {
            if(SandboxGame.getInstance().getGameRenderer().world != null) {
                SandboxGame.getInstance().getGameRenderer().world.deleteChunkMeshes();
                SandboxGame.getInstance().getGameRenderer().chunkMeshesToDelete.clear();
                SandboxGame.getInstance().getGameRenderer().world = null;
                SandboxGame.getInstance().getGameRenderer().player = null;
                SandboxGame.getInstance().getGameRenderer().setScreen(new TitleScreen());
            }
        });
        DisconnectPacket packet = new DisconnectPacket();
        GameClientHandler.sendPacket(packet);
        disconnectWithoutPacket();
    }

    public static void disconnectWithoutPacket() {
        if(!isConnectedToServer) return;
        if(GameClientHandler.serverHandler != null) {
            GameClientHandler.serverHandler.close();
            GameClientHandler.serverHandler = null;
        }
        isConnectedToServer = false;
        handler = null;
        state = ClientState.UNINITIALIZED;
    }

    public enum ClientState {
        UNINITIALIZED,
        CONNECTING,
        INITIAL_WORLD_LOAD,
        PLAYING
    }
}
