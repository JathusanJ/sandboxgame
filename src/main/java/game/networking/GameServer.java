package game.networking;

import game.logic.TickManager;
import game.logic.Tickable;
import game.logic.world.ServerWorld;
import game.logic.world.World;
import game.logic.world.creature.Creatures;
import game.logic.world.creature.ServerPlayer;
import game.networking.packets.ChatMessagePacket;
import game.networking.packets.Packet;
import game.networking.packets.PacketList;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.File;
import java.util.ArrayList;

public class GameServer implements Tickable {
    private int port;
    public ArrayList<ServerPlayer> players = new ArrayList<>();
    public GameServerHandler handler;
    public World world;
    public TickManager tickManager = new TickManager();
    public ServerBootstrap bootstrap;
    public EventLoopGroup bossGroup;
    public EventLoopGroup workersGroup;
    private int nextNetworkId = 0;

    public GameServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        System.out.println("Starting server");
        Creatures.init();
        System.out.println("Loading world");
        File worldFolder = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\sandboxgame\\server\\worlds\\world");
        if(worldFolder.exists()) {
            this.world = new ServerWorld(worldFolder, this);
        } else {
            this.world = new ServerWorld("world", 0, World.WorldType.DEFAULT, worldFolder, this);
        }
        this.tickManager.tickables.add(this);
        this.tickManager.start();
        System.out.println("Started ticking");
        System.out.println("Setting up networking");

        PacketList.setup();

        this.bossGroup = new NioEventLoopGroup();
        this.workersGroup = new NioEventLoopGroup();

        this.handler = new GameServerHandler(this);

        try {
            this.bootstrap = new ServerBootstrap();
            this.bootstrap.group(this.bossGroup, this.workersGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new ByteBufPacketDecoder(), handler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = this.bootstrap.bind(this.port).sync();

            System.out.println("Server started");

            future.channel().closeFuture().sync();
        } finally {
            this.onStop();
        }
    }

    public void stop() {
        System.out.println("Shutting down networking");
        this.bossGroup.shutdownGracefully();
        this.workersGroup.shutdownGracefully();
    }

    public void onStop() {
        System.out.println("Stopping server");
        this.tickManager.isRunning = false;
        System.out.println("Saving world");
        this.world.stop();
        this.world.save();
        System.out.println("Finished shutdown");
    }

    public static void main(String[] arguments) throws InterruptedException {
        new GameServer(8080).start();
    }

    public void sendPacketToAll(Packet packet) {
        for(int i = 0; i < this.players.size(); i++) {
            this.players.get(i).sendPacket(packet);
        }
    }

    public void sendMessageToAll(String message) {
        this.sendPacketToAll(new ChatMessagePacket(message));
    }

    @Override
    public void tick() {
        this.world.tick();
    }

    public int getNextNetworkId() {
        nextNetworkId++;
        return nextNetworkId;
    }
}
