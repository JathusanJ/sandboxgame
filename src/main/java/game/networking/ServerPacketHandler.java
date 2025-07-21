package game.networking;

import game.logic.world.chunk.Chunk;
import game.logic.util.PlayerProfile;
import game.logic.world.creature.Creature;
import game.logic.world.creature.ItemCreature;
import game.logic.world.creature.Player;
import game.logic.world.creature.ServerPlayer;
import game.logic.world.items.Item;
import game.logic.world.items.ItemStack;
import game.logic.world.items.Items;
import game.networking.packets.*;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.UUID;

public class ServerPacketHandler extends PacketHandler {
    public void setup(GameServer server) {
        packetHandlers.put(LoginAttemptPacket.class, (client, buffer) -> {
            ServerPlayer serverPlayer = (ServerPlayer) client;
            String username = ByteBufPacketDecoder.readString(buffer);
            serverPlayer.playerProfile = new PlayerProfile(username, UUID.randomUUID());
            server.players.add(serverPlayer);
            server.world.spawnCreature(serverPlayer);
            LoginResultPacket packet = new LoginResultPacket(server);
            serverPlayer.sendPacket(packet);

            serverPlayer.inventory[0] = new ItemStack(Items.BEDROCK);
            serverPlayer.inventory[0].setAmount(64);
            serverPlayer.inventory[1] = new ItemStack(Items.STICK);
            serverPlayer.inventory[1].setAmount(64);
            serverPlayer.inventory[2] = new ItemStack(Items.COAL);
            serverPlayer.inventory[2].setAmount(64);

            for(int i = 0; i < serverPlayer.inventory.length; i++) {
                serverPlayer.sendPacket(new SetInventorySlotContentPacket(serverPlayer, i));
            }

            server.sendMessageToAll(serverPlayer.playerProfile.getUsername() + " joined the server");
        });

        packetHandlers.put(ChatMessagePacket.class, (player, buffer) -> {
            ServerPlayer serverPlayer = ((ServerPlayer) player);
            String message = ByteBufPacketDecoder.readString(buffer);
            if(message.startsWith("/")) {
                if(message.startsWith("/respawn")) {
                    PositionRotationPacket packet = new PositionRotationPacket(new Vector3f(0, 100, 0), 0, 0);
                    serverPlayer.sendPacket(packet);
                } else if(message.startsWith("/stop")) {
                    for(ServerPlayer player1 : server.players) {
                        DisconnectPacket disconnectPacket = new DisconnectPacket("Server shutting down");
                        player1.sendPacket(disconnectPacket);
                        player1.channelHandler.close();
                    }
                    server.stop();
                }
            } else {
                server.sendMessageToAll(serverPlayer.playerProfile.getUsername() + ": " + message);
            }
        });

        packetHandlers.put(DisconnectPacket.class, (player, buffer) -> {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            server.players.remove(player);
            server.world.creatures.remove(player);
            serverPlayer.channelHandler.close();
            server.handler.connections.remove(serverPlayer.channelHandler);

            server.sendMessageToAll(serverPlayer.playerProfile.getUsername() + " left the server");
        });

        packetHandlers.put(PositionRotationPacket.class, (player, buffer) -> {
           player.position.set(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
           player.yaw = buffer.readFloat();
           player.pitch = buffer.readFloat();
        });

        packetHandlers.put(RequestChunkPacket.class, (player, buffer) -> {
            int chunkX = buffer.readInt();
            int chunkY = buffer.readInt();
            Chunk chunk = server.world.getChunk(chunkX, chunkY);
            if(chunk == null) {
                ChunkRequestFailurePacket chunkRequestFailurePacket = new ChunkRequestFailurePacket(ChunkRequestFailurePacket.FailureType.CHUNK_NOT_LOADED_YET, chunkX, chunkY);
                ((ServerPlayer)player).sendPacket(chunkRequestFailurePacket);

                chunk = server.world.createChunk(chunkX, chunkY);
                chunk.generateChunk();
                server.world.loadedChunks.put(new Vector2i(chunkX, chunkY), chunk);
                return;
            }
            ChunkDataPacket chunkDataPacket = new ChunkDataPacket(chunk);
            ((ServerPlayer) player).sendPacket(chunkDataPacket);

            for(int i = 0; i < server.world.creatures.size(); i++) {
                Creature creature = server.world.creatures.get(i);
                if(creature != player && creature.getChunkPosition().equals(new Vector2i(chunkX, chunkY))) {
                    ((ServerPlayer) player).sendPacket(new SpawnCreaturePacket(creature));
                }
            }
        });

        packetHandlers.put(UseItemPacket.class, (player, buffer) -> {
            int targetX = buffer.readInt();
            int targetY = buffer.readInt();
            int targetZ = buffer.readInt();
            int normalX = buffer.readInt();
            int normalY = buffer.readInt();
            int normalZ = buffer.readInt();
            if(player.inventory[player.currentHotbarSlot] == null) return;

            player.inventory[player.currentHotbarSlot].getItem().onUse(new Item.ItemUsageContext(server.world, player, player.inventory[player.currentHotbarSlot], new Vector3i(targetX, targetY, targetZ), new Vector3i(normalX, normalY, normalZ)));
        });

        packetHandlers.put(SetHotbarSlotPacket.class, (player, buffer) -> {
            int slot = buffer.readByte();
            if(slot < 0 || slot > 8) return;

            player.currentHotbarSlot = slot;
        });

        packetHandlers.put(BlockBreakingPacket.class, (player, buffer) -> {
            int type = buffer.readByte();
            if(type < 0 || type >= BlockBreakingPacket.State.values().length) return;
            BlockBreakingPacket.State state = BlockBreakingPacket.State.values()[type];

            if(state == BlockBreakingPacket.State.CLIENT_STOP) {
                player.blockBreakingProgress = null;
                return;
            }

            if(state == BlockBreakingPacket.State.CLIENT_START) {
                int x = buffer.readInt();
                int y = buffer.readInt();
                int z = buffer.readInt();

                player.blockBreakingProgress = new Player.BlockBreakingProgress(new Vector3i(x,y,z), player);
            }
        });

        packetHandlers.put(DropItemPacket.class, (player, buffer) -> {
            int slot = buffer.readByte();
            int amount = buffer.readByte();
            if(slot < 0 || slot >= player.inventory.length || amount < 1 || amount > 64) return;

            if(player.inventory[slot].getItem() != Items.AIR) {
                ItemCreature itemCreature = new ItemCreature();
                itemCreature.representingItemStack.setItem(player.inventory[slot].getItem());

                itemCreature.position.set(player.position);
                itemCreature.position.add(0, 1.75F, 0);

                Vector3f direction = new Vector3f(
                        (float) Math.cos(Math.toRadians(player.yaw)),
                        0,
                        (float) Math.sin(Math.toRadians(player.yaw))
                ).normalize();

                itemCreature.velocity.add(direction.x * 4F, 2F, direction.z * 4F);

                amount = Math.min(amount, player.inventory[slot].amount - 1);

                itemCreature.representingItemStack.setAmount(amount);
                player.inventory[slot].decreaseBy(amount);

                player.world.spawnCreature(itemCreature);

                SetInventorySlotContentPacket packet = new SetInventorySlotContentPacket(player, slot);
                ((ServerPlayer) player).sendPacket(packet);
            }
        });
    }
}
