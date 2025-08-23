package game.server.world;

import game.shared.multiplayer.packets.*;
import game.shared.util.PlayerProfile;
import game.shared.world.chunk.Chunk;
import game.shared.world.chunk.ChunkLoaderManager;
import game.server.GameServer;
import game.shared.world.creature.Player;
import io.netty.channel.ChannelHandlerContext;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerPlayer extends Player {
    public ChannelHandlerContext channelHandler;
    public GameServer server;
    public ChunkLoaderManager.Ticket playerLoadTicket;
    public ConcurrentLinkedQueue<Vector2i> chunksToSend = new ConcurrentLinkedQueue<>();
    public boolean waitingOnChunkReceiveConfirmation = false;

    public ServerPlayer(PlayerProfile playerProfile, GameServer server) {
        this.playerProfile = playerProfile;
        this.server = server;
        this.playerLoadTicket = new ChunkLoaderManager.Ticket(0,0, 8);
    }

    @Override
    public void tick() {
        super.tick();

        if(this.world instanceof ServerWorld serverWorld && (!this.position.equals(this.lastPosition) || this.yaw != this.lastYaw)) {
            CreatureMovePacket packet = new CreatureMovePacket(this);
            for (int i = 0; i < this.server.players.size(); i++) {
                ServerPlayer player = this.server.players.get(i);
                if(player != this) {
                    player.sendPacket(packet);
                }
            }
        }
    }

    public void sendPacket(Packet packet) {
        this.channelHandler.writeAndFlush(packet);
    }

    public void sendMessage(String message) {
        ChatMessagePacket packet = new ChatMessagePacket(message);
        this.sendPacket(packet);
    }

    @Override
    public void switchToHotbarSlot(int slot) {
        this.currentHotbarSlot = slot;
        this.sendPacket(new SetHotbarSlotPacket(slot));
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, z);
        PositionRotationPacket packet = new PositionRotationPacket(new Vector3f(x, y, z), this.yaw, this.pitch);
        this.sendPacket(packet);
    }

    public void sendNextChunk() {
        if(!this.waitingOnChunkReceiveConfirmation && !this.chunksToSend.isEmpty()) {
            Vector2i chunkPosition = this.chunksToSend.poll();

            Chunk chunk = this.world.getChunk(chunkPosition.x, chunkPosition.y);
            if(chunk == null) {
                this.chunksToSend.add(chunkPosition);
            } else {
                ChunkDataPacket chunkDataPacket = new ChunkDataPacket(chunk);
                this.sendPacket(chunkDataPacket);
            }
        }
    }
}