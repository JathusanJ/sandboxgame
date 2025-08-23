package game.server;

import engine.StartupArguments;
import game.shared.TickManager;
import game.shared.Tickable;
import game.shared.Version;
import game.shared.multiplayer.ByteBufPacketDecoder;
import game.shared.multiplayer.ByteBufPacketEncoder;
import game.server.world.ServerWorld;
import game.shared.world.World;
import game.shared.world.creature.Creatures;
import game.server.world.ServerPlayer;
import game.shared.multiplayer.packets.ChatMessagePacket;
import game.shared.multiplayer.packets.Packet;
import game.shared.multiplayer.packets.PacketList;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

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
    public Logger logger = LoggerFactory.getLogger("Server");
    public int worldSize = 16;
    public long startTime;

    public GameServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        this.startTime = System.currentTimeMillis();

        Version.load();

        this.logger.info("Starting server {} on port {}", Version.GAME_VERSION.versionName(), this.port);

        if(this.worldSize < 4) {
            this.logger.warn("Current server world size of {} chunks is too small! Increasing it to 4 chunks", this.worldSize);
            this.worldSize = 4;
        } else if(this.worldSize > 32) {
            this.logger.warn("Current server world size of {} chunks is too large! Decreasing it to 32 chunks", this.worldSize);
            this.worldSize = 32;
        }

        Creatures.init();

        this.logger.info("Loading world");
        File worldFolder = new File("world");
        if(worldFolder.exists()) {
            this.world = new ServerWorld(worldFolder, this);
        } else {
            this.world = new ServerWorld("world", new Random().nextInt(), World.WorldType.DEFAULT, worldFolder, this);
        }

        this.world.chunkLoaderManager.addTicket(this.world.spawnLoadTicket);
        this.world.chunkLoaderManager.start();

        float prevPercentage = -1;
        while(this.world.loadedChunks.size() < Math.pow(this.world.spawnLoadTicket.radius * 2 + 1, 2)) {
            float percentage = (float) Math.floor(this.world.loadedChunks.size() / Math.pow(this.world.spawnLoadTicket.radius * 2 + 1, 2) * 100);

            if(percentage != prevPercentage) {
                prevPercentage = percentage;
                this.logger.info("Loading world ({}%)", percentage);
            }

            Thread.sleep(500);
        }
        this.logger.info("Loading world (100%)");
        this.world.save();
        this.world.ready = true;

        this.logger.info("Starting ticking");
        this.tickManager.tickables.add(this);
        this.tickManager.start();

        this.logger.info("Setting up networking");
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
                            socketChannel.pipeline().addLast(new ByteBufPacketEncoder(), new ByteBufPacketDecoder(), handler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = this.bootstrap.bind(this.port).sync();

            this.logger.info("Server started in {}s", (System.currentTimeMillis() - startTime) / 1000D);

            future.channel().closeFuture().sync();
        } finally {
            this.onStop();
        }
    }

    public void stop() {
        this.logger.info("Shutting down networking");
        this.bossGroup.shutdownGracefully();
        this.workersGroup.shutdownGracefully();
    }

    public void onStop() {
        this.logger.info("Stopping server");
        this.tickManager.isRunning = false;
        this.logger.info("Saving world");
        this.world.stop();
        this.world.save();
        this.logger.info("Server stopped");
    }

    public static void main(String[] arguments) throws InterruptedException {
        StartupArguments startupArguments = new StartupArguments(arguments);
        int port = 8080;
        if(startupArguments.map.containsKey("port")) {
            try {
                port = Integer.parseInt(startupArguments.map.get("port"));
            } catch(NumberFormatException ignored) {}
        }
        GameServer gameServer = new GameServer(port);
        if(startupArguments.map.containsKey("worldSize")) {
            try {
                gameServer.worldSize = Integer.parseInt(startupArguments.map.get("worldSize"));
            } catch(NumberFormatException ignored) {}
        }
        gameServer.start();
    }

    public void sendPacketToAll(Packet packet) {
        for(int i = 0; i < this.players.size(); i++) {
            this.players.get(i).sendPacket(packet);
        }
    }

    public void sendMessageToAll(String message) {
        this.logger.info("[CHAT] {}", message);
        this.sendPacketToAll(new ChatMessagePacket(message));
    }

    public void sendServerMessage(String message) {
        this.sendMessageToAll("[Server] " + message);
    }

    @Override
    public void tick() {
        this.world.tick();
    }

    public int getNextNetworkId() {
        this.nextNetworkId++;
        return this.nextNetworkId;
    }
}
