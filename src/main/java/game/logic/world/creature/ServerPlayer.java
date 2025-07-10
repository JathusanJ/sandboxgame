package game.logic.world.creature;

import game.logic.util.PlayerProfile;
import game.logic.world.ServerWorld;
import game.logic.world.blocks.Blocks;
import game.logic.world.items.ItemStack;
import game.logic.world.items.Items;
import game.networking.GameServer;
import game.networking.packets.Packet;
import game.networking.packets.PacketList;
import game.networking.packets.BlockBreakingPacket;
import game.networking.packets.ChatMessagePacket;
import game.networking.packets.CreatureMovePacket;
import game.networking.packets.SetHotbarSlotPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.joml.Vector3f;

public class ServerPlayer extends Player {
    public PlayerProfile playerProfile;
    public ChannelHandlerContext channelHandler;
    public GameServer server;

    public ServerPlayer(PlayerProfile playerProfile, GameServer server) {
        this.playerProfile = playerProfile;
        this.server = server;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.blockBreakingProgress != null) {
            if(this.blockBreakingProgress.isDone()) {
                ItemStack itemStack = this.world.getBlockAt(this.blockBreakingProgress.blockPosition.x,this.blockBreakingProgress.blockPosition.y,this.blockBreakingProgress.blockPosition.z).getAsDroppedItem(blockBreakingProgress.player, blockBreakingProgress.player.inventory[blockBreakingProgress.hotbarSlot]);
                if(itemStack != null && itemStack.item != Items.AIR) {
                    ItemCreature droppedBlock = new ItemCreature();
                    droppedBlock.representingItemStack = itemStack;
                    this.world.spawnCreature(droppedBlock, new Vector3f(this.blockBreakingProgress.blockPosition.x + 0.5F, this.blockBreakingProgress.blockPosition.y + 0.5F, this.blockBreakingProgress.blockPosition.z + 0.5F));
                }
                this.world.setBlockAt(this.blockBreakingProgress.blockPosition.x, this.blockBreakingProgress.blockPosition.y, this.blockBreakingProgress.blockPosition.z, Blocks.AIR);
                this.blockBreakingProgress = null;
                this.sendPacket(new BlockBreakingPacket(BlockBreakingPacket.State.SERVER_STOP));
            } else {
                this.blockBreakingProgress.breakingTicks = this.blockBreakingProgress.breakingTicks + 1;
            }
        }
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

    public ByteBuf allocatePacketBuffer() {
        ByteBuf buffer = this.channelHandler.alloc().buffer();
        buffer.writeShort(0); // Placeholder for size
        return buffer;
    }

    public void sendPacket(Packet packet) {
        ByteBuf buffer = allocatePacketBuffer();
        buffer.writeByte(PacketList.getIdOf(packet.getClass()));
        packet.write(buffer);
        buffer.setShort(0, buffer.readableBytes() - 2);
        this.channelHandler.writeAndFlush(buffer);
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
}
