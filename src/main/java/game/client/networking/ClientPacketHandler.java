package game.client.networking;

import game.client.SandboxGame;
import game.client.ui.screen.DisconnectedScreen;
import game.client.ui.screen.ServerWorldLoadingScreen;
import game.client.ui.widget.ChatMessage;
import game.logic.world.chunk.Chunk;
import game.client.world.ClientWorld;
import game.logic.world.blocks.Block;
import game.logic.world.blocks.Blocks;
import game.client.world.creature.ClientPlayer;
import game.logic.world.creature.Creature;
import game.logic.world.creature.Creatures;
import game.logic.world.items.Item;
import game.logic.world.items.Items;
import game.networking.ByteBufPacketDecoder;
import game.networking.packets.PacketHandler;
import game.networking.packets.*;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientPacketHandler extends PacketHandler {
    public void setup() {
        packetHandlers.put(LoginResultPacket.class, (server, buffer) -> {
            byte success = buffer.readByte();
            if(success == 0) {
                throw new IllegalStateException("Failed to login");
            } else {
                GameClient.state = GameClient.ClientState.INITIAL_WORLD_LOAD;

                SandboxGame.getInstance().getGameRenderer().player = new ClientPlayer();
                SandboxGame.getInstance().getGameRenderer().player.position.set(0, 100, 0);
                SandboxGame.getInstance().getGameRenderer().player.lastPosition.set(0, 100, 0);

                int serverRenderDistance = buffer.readByte();

                SandboxGame.getInstance().doOnMainThread(() -> {
                    ClientWorld world = new ClientWorld();
                    world.renderDistanceOverride = serverRenderDistance;
                    Thread worldLoadingThread = new Thread(() -> {
                        SandboxGame.getInstance().getGameRenderer().loadWorldWithoutMarkingReadyAndTicking(world);
                    });

                    GameClient.chunksExpectedToLoad = 0;

                    for (int x = -4; x <= 4; x++) {
                        for (int y = -4; y <= 4; y++) {
                            GameClient.chunksToRequest.add(new Vector2i(x,y));
                            GameClient.chunksExpectedToLoad++;
                        }
                    }

                    RequestChunkPacket requestChunkPacket = new RequestChunkPacket(GameClient.chunksToRequest.removeFirst());
                    GameClientHandler.sendPacket(requestChunkPacket);

                    SandboxGame.getInstance().getGameRenderer().setScreen(new ServerWorldLoadingScreen());
                    worldLoadingThread.start();
                });
            }
        });

        packetHandlers.put(ChatMessagePacket.class, (server, buffer) -> {
            String message = ByteBufPacketDecoder.readString(buffer);
            SandboxGame.getInstance().getGameRenderer().player.sendChatMessage(message);
        });

        packetHandlers.put(DisconnectPacket.class, (server, buffer) -> {
            String reason = ByteBufPacketDecoder.readString(buffer);
            SandboxGame.getInstance().doOnMainThread(() -> {
                if(SandboxGame.getInstance().getGameRenderer().world != null) {
                    SandboxGame.getInstance().getGameRenderer().chunkMeshesToDelete.clear();
                    SandboxGame.getInstance().getGameRenderer().world.deleteChunkMeshes();
                    SandboxGame.getInstance().getGameRenderer().world = null;
                    SandboxGame.getInstance().getGameRenderer().player = null;
                    SandboxGame.getInstance().getGameRenderer().setScreen(new DisconnectedScreen("Kicked from server: " + reason));
                }
            });
            GameClient.disconnectWithoutPacket();
        });

        packetHandlers.put(PositionRotationPacket.class, (server, buffer) -> {
            Vector3f position = new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
            float yaw = buffer.readFloat();
            float pitch = buffer.readFloat();
            if(SandboxGame.getInstance().getGameRenderer().player != null) {
                SandboxGame.getInstance().getGameRenderer().player.position.set(position);
                SandboxGame.getInstance().getGameRenderer().player.pitch = pitch;
                SandboxGame.getInstance().getGameRenderer().camera.pitch = pitch;
                SandboxGame.getInstance().getGameRenderer().camera.yaw = yaw;
            }
        });

        packetHandlers.put(ChunkDataPacket.class, (player, buffer) -> {
            Chunk chunk = SandboxGame.getInstance().getGameRenderer().world.createChunk(new Vector2i(buffer.readInt(), buffer.readInt()));
            chunk.blocks = new Block[16 * 16 * 128];

            GameClient.chunkDataRequestAttempts = 0;

            HashMap<Short, Block> saveIdToBlock = new HashMap<>();
            int amountOfBlockEntries = buffer.readShort();
            for(int i = 0; i < amountOfBlockEntries; i++) {
                String blockId = ByteBufPacketDecoder.readString(buffer);
                saveIdToBlock.put(buffer.readShort(), Blocks.idToBlock.get(blockId));
            }

            for(int x = 0; x < 16; x++) {
                for(int z = 0; z < 16; z++) {
                    for(int y = 0; y < 128; y++) {
                        int arrayId = Chunk.positionToBlockArrayId(x,y,z);
                        chunk.blocks[arrayId] = saveIdToBlock.get(buffer.readShort());
                    }
                }
            }

            chunk.calculateSkylight();

            synchronized (SandboxGame.getInstance().getGameRenderer().world.loadedChunks) {
                SandboxGame.getInstance().getGameRenderer().world.loadedChunks.put(new Vector2i(chunk.chunkPosition.x, chunk.chunkPosition.z), chunk);
            }

            if(GameClient.state == GameClient.ClientState.INITIAL_WORLD_LOAD && GameClient.chunksExpectedToLoad >= SandboxGame.getInstance().getGameRenderer().world.loadedChunks.size()) {
                GameClient.state = GameClient.ClientState.PLAYING;
                SandboxGame.getInstance().getGameRenderer().markWorldReady();
            }

            if(!GameClient.chunksToRequest.isEmpty()) {
                RequestChunkPacket requestChunkPacket = new RequestChunkPacket(GameClient.chunksToRequest.removeFirst());
                GameClientHandler.sendPacket(requestChunkPacket);
                GameClient.chunkDataReceived = false;
            } else {
                GameClient.chunkDataReceived = true;
            }
        });

        packetHandlers.put(ChunkRequestFailurePacket.class, (server, buffer) -> {
            ChunkRequestFailurePacket.FailureType failureType = ChunkRequestFailurePacket.FailureType.values()[buffer.readByte()];
            int chunkX = buffer.readInt();
            int chunkY = buffer.readInt();
            GameClient.chunkDataRequestAttempts++;
            if(GameClient.chunkDataRequestAttempts > 4) {
                GameClient.chunkDataRequestAttempts = 0;
            } else {
                GameClient.chunksToRequest.addFirst(new Vector2i(chunkX, chunkY));
                GameClient.chunkDataRequestDelay = 1;
            }
            GameClient.chunkDataReceived = true;
        });

        packetHandlers.put(SetBlockPacket.class, (server, buffer) -> {
            int x = buffer.readInt();
            int y = buffer.readInt();
            int z = buffer.readInt();
            String blockId = ByteBufPacketDecoder.readString(buffer);
            Block block = Blocks.idToBlock.get(blockId);

            if(block == null) {
                throw new IllegalStateException("Attempted to set invalid block id " + blockId + " at position " + x + ", " + y + ", " + z);
            }

            SandboxGame.getInstance().doOnMainThread(() -> {
                SandboxGame.getInstance().getGameRenderer().world.setBlockAt(x,y,z, block);
            });
        });

        packetHandlers.put(SetHotbarSlotPacket.class, (server, buffer) -> {
            int slot = buffer.readByte();
            if(slot < 0 || slot > 8) return;

            SandboxGame.getInstance().getGameRenderer().player.currentHotbarSlot = slot;
        });

        packetHandlers.put(BlockBreakingPacket.class, (server, buffer) -> {
            int type = buffer.readByte();
            if(type < 0 || type >= BlockBreakingPacket.State.values().length) return;
            BlockBreakingPacket.State state = BlockBreakingPacket.State.values()[type];

            if(state == BlockBreakingPacket.State.SERVER_STOP) {
                SandboxGame.getInstance().getGameRenderer().player.blockBreakingProgress = null;
            }
        });

        packetHandlers.put(SpawnCreaturePacket.class, (server, buffer) -> {
            String creatureId = ByteBufPacketDecoder.readString(buffer);
            Class<? extends Creature> creatureClass = Creatures.getClassFor(creatureId);
            Creature creature;
            try {
                creature = creatureClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            creature.networkId = buffer.readInt();
            creature.readSpawnPacket(buffer);
            SandboxGame.getInstance().getGameRenderer().world.spawnCreature(creature, creature.position);
        });

        packetHandlers.put(RemoveCreaturePacket.class, (server, buffer) -> {
            int networkId = buffer.readInt();
            ArrayList<Creature> creatures = SandboxGame.getInstance().getGameRenderer().world.creatures;
            for(int i = 0; i < creatures.size(); i++) {
                Creature creature = creatures.get(i);
                if(creature.networkId == networkId) {
                    creature.remove();
                    break;
                }
            }
        });

        packetHandlers.put(CreatureMovePacket.class, (server, buffer) -> {
            int networkId = buffer.readInt();
            Vector3f position = new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
            float yaw = buffer.readFloat();

            for(int i = 0; i < SandboxGame.getInstance().getGameRenderer().world.creatures.size(); i++) {
                Creature creature = SandboxGame.getInstance().getGameRenderer().world.creatures.get(i);
                if(creature.networkId == networkId) {
                    creature.lastPosition.set(creature.position);
                    creature.position.set(position);
                    creature.lastYaw = yaw;
                    creature.yaw = yaw;
                    break;
                }
            }
        });

        packetHandlers.put(SetInventorySlotContentPacket.class, (server, buffer) -> {
            int slotId = buffer.readByte();
            Item item = Items.idToItem.get(ByteBufPacketDecoder.readString(buffer));
            int amount = buffer.readByte();

            if(slotId < 0 || slotId >= SandboxGame.getInstance().getGameRenderer().player.inventory.length) return;

            SandboxGame.getInstance().getGameRenderer().player.inventory[slotId].setItem(item);
            SandboxGame.getInstance().getGameRenderer().player.inventory[slotId].setAmount(amount);
        });
    }
}
