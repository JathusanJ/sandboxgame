package game.client.networking;

import game.client.SandboxGame;
import game.client.ui.screen.DisconnectedScreen;
import game.client.ui.screen.TitleScreen;
import game.networking.ByteBufPacketDecoder;
import game.networking.packets.DisconnectPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.joml.Vector2i;

import java.util.ArrayList;

public class GameClient {
    public static boolean isConnectedToServer = false;
    public static GameClientHandler handler;
    public static boolean chunkDataReceived = false;
    public static ArrayList<Vector2i> chunksToRequest = new ArrayList<>();
    public static ClientState state = ClientState.UNINITIALIZED;
    public static int chunksExpectedToLoad = 0;
    public static int chunkDataRequestDelay = 0;
    public static int chunkDataRequestAttempts = 0;

    public static void connect(String host, int port) throws InterruptedException {
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
                    channel.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(4049600));
                    channel.pipeline().addLast(new ByteBufPacketDecoder(), handler);
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
