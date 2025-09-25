package game.server;

import game.shared.Version;
import game.shared.multiplayer.ByteBufPacketDecoder;
import game.shared.multiplayer.packets.*;
import game.shared.multiplayer.skin.Skins;
import game.shared.util.PlayerProfile;
import game.shared.world.blocks.Block;
import game.shared.world.blocks.Blocks;
import game.shared.world.creature.ItemCreature;
import game.shared.world.creature.Player;
import game.server.world.ServerPlayer;
import game.shared.world.items.Item;
import game.shared.world.items.Items;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.UUID;

public class ServerPacketHandler extends PacketHandler {
    public void setup(GameServer server) {
        packetHandlers.put(LoginAttemptPacket.class, (player, buffer) -> {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            if(buffer.readInt() != 0) {
                DisconnectPacket packet = new DisconnectPacket("Server and client version mismatch! Try joining with a " + Version.GAME_VERSION.versionName() + " client!");
                serverPlayer.sendPacket(packet);
                serverPlayer.channelHandler.close();
                server.handler.connections.remove(serverPlayer.channelHandler);
                server.players.remove(player);
                return;
            }
            serverPlayer.setGamemode(Player.Gamemode.CREATIVE);
            String username = ByteBufPacketDecoder.readString(buffer);
            String skinId = ByteBufPacketDecoder.readString(buffer);
            serverPlayer.playerProfile = new PlayerProfile(username, UUID.randomUUID(), Skins.getSkin(skinId));
            server.players.add(serverPlayer);
            server.world.spawnCreature(serverPlayer);
            LoginResultPacket packet = new LoginResultPacket(server);
            serverPlayer.sendPacket(packet);

            Vector3f spawnLocation = server.world.findPossibleSpawnLocation();
            serverPlayer.setPosition(spawnLocation.x, spawnLocation.y, spawnLocation.z);

            for (int i = 0; i < server.players.size(); i++) {
                ServerPlayer otherPlayer = server.players.get(i);
                if(otherPlayer != serverPlayer) {
                    SpawnCreaturePacket spawnCreaturePacket = new SpawnCreaturePacket(otherPlayer);
                    serverPlayer.sendPacket(spawnCreaturePacket);
                }
            }

            for (int x = -serverPlayer.world.spawnLoadTicket.radius; x <= serverPlayer.world.spawnLoadTicket.radius; x++) {
                for (int y = -serverPlayer.world.spawnLoadTicket.radius; y <= serverPlayer.world.spawnLoadTicket.radius; y++) {
                    serverPlayer.chunksToSend.add(new Vector2i(x,y));
                }
            }

            serverPlayer.sendNextChunk();

            server.sendServerMessage(serverPlayer.playerProfile.getUsername() + " joined the server");
        });

        packetHandlers.put(ChatMessagePacket.class, (player, buffer) -> {
            ServerPlayer serverPlayer = ((ServerPlayer) player);
            String message = ByteBufPacketDecoder.readString(buffer);
            if(message.startsWith("/")) {
                if(message.startsWith("/respawn")) {
                    Vector3f position = serverPlayer.world.findPossibleSpawnLocation();
                    serverPlayer.setPosition(position.x, position.y, position.z);
                } else if(message.startsWith("/stop")) {
                    DisconnectPacket disconnectPacket = new DisconnectPacket("Server shutting down");
                    for(ServerPlayer player1 : server.players) {
                        player1.sendPacket(disconnectPacket);
                        player1.channelHandler.close();
                    }
                    server.stop();
                } else if(message.startsWith("/players") || message.startsWith("/playerlist") || message.startsWith("/list")) {
                    StringBuilder playerList = new StringBuilder("Players (" + server.players.size() + "): ");

                    for (int i = 0; i < server.players.size(); i++) {
                        ServerPlayer otherPlayer = server.players.get(i);
                        if(i != 0) {
                            playerList.append(", ");
                        }

                        playerList.append(otherPlayer.playerProfile.getUsername());
                    }

                    serverPlayer.sendMessage(playerList.toString());
                } else if(message.startsWith("/uptime") || message.startsWith("/status") || message.startsWith("/info")) {
                    int days = (int) Math.floor(server.startTime / (8.64 * Math.pow(10, 13)));
                    int hours = (int) Math.floor((server.startTime % (8.64 * Math.pow(10, 13))) / (3.6 * Math.pow(10, 12)));
                    int minutes = (int) Math.floor((server.startTime % (3.6 * Math.pow(10, 12))) / (6 * Math.pow(10, 10)));
                    int seconds = (int) Math.floor((server.startTime % (6 * Math.pow(10, 10))) / (1 * Math.pow(10, 9)));

                    StringBuilder uptime = new StringBuilder("Server has been online for");

                    if(days != 0) {
                        uptime.append(" ").append(days).append("days(s)");
                    }
                    if(hours != 0) {
                        uptime.append(" ").append(hours).append("hour(s)");
                    }
                    if(minutes != 0) {
                        uptime.append(" ").append(minutes).append("minute(s)");
                    }
                    if(seconds != 0) {
                        uptime.append(" ").append(seconds).append("second(s)");
                    }

                    serverPlayer.sendMessage(uptime.toString());
                }
            } else {
                server.sendMessageToAll(serverPlayer.playerProfile.getUsername() + ": " + message);
            }
        });

        packetHandlers.put(DisconnectPacket.class, (player, buffer) -> {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            player.remove();
            server.players.remove(player);
            serverPlayer.channelHandler.close();
            server.handler.connections.remove(serverPlayer.channelHandler);

            server.sendServerMessage(serverPlayer.playerProfile.getUsername() + " left the server");
        });

        packetHandlers.put(PositionRotationPacket.class, (player, buffer) -> {
           player.position.set(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
           player.yaw = buffer.readFloat();
           player.pitch = buffer.readFloat();
        });

        packetHandlers.put(ChunkReceivedPacket.class, (player, buffer) -> {
            ((ServerPlayer)player).waitingOnChunkReceiveConfirmation = false;
            ((ServerPlayer)player).sendNextChunk();
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

        packetHandlers.put(SetBlockPacket.class, (player, buffer) -> {
            int x = buffer.readInt();
            int y = buffer.readInt();
            int z = buffer.readInt();

            String blockId = ByteBufPacketDecoder.readString(buffer);
            Block block = Blocks.idToBlock.get(blockId);
            if(block == null) {
                return;
            }

            player.world.setBlock(x,y,z, block);
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
