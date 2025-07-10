package game.networking;

import game.logic.world.creature.ServerPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

import java.util.ArrayList;

@ChannelHandler.Sharable
public class GameServerHandler extends ChannelInboundHandlerAdapter {
    public ArrayList<ChannelHandlerContext> connections = new ArrayList<>();
    public static AttributeKey<ServerPlayer> playerAttributeKey = AttributeKey.valueOf("player");
    public GameServer server;
    public ServerPacketHandler packetHandler = new ServerPacketHandler();

    public GameServerHandler(GameServer server) {
        this.server = server;
        packetHandler.setup(server);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if(ctx.channel().attr(playerAttributeKey).get() == null) {
            ServerPlayer uninitializedPlayer = new ServerPlayer(null, server);
            uninitializedPlayer.channelHandler = ctx;
            ctx.channel().attr(playerAttributeKey).set(uninitializedPlayer);
        }

        packetHandler.readAndHandle(ctx.channel().attr(playerAttributeKey).get(), (ByteBuf) msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();

        ServerPlayer serverPlayer = ctx.channel().attr(playerAttributeKey).get();
        server.players.remove(serverPlayer);
        server.world.creatures.remove(serverPlayer);
        server.handler.connections.remove(serverPlayer.channelHandler);
        server.sendMessageToAll(serverPlayer.playerProfile.getUsername() + " left the server");

        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        connections.add(ctx);
    }
}
