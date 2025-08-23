package game.client.multiplayer;

import game.client.SandboxGame;
import game.client.ui.screen.DisconnectedScreen;
import game.client.ui.screen.ServerWorldLoadingScreen;
import game.client.world.ClientChunk;
import game.shared.multiplayer.packets.*;
import game.shared.world.chunk.Chunk;
import game.client.world.ClientWorld;
import game.shared.world.blocks.Block;
import game.shared.world.blocks.Blocks;
import game.client.world.creature.ClientPlayer;
import game.shared.world.creature.Creature;
import game.shared.world.creature.Creatures;
import game.shared.world.creature.Player;
import game.shared.world.items.Item;
import game.shared.world.items.Items;
import game.shared.multiplayer.ByteBufPacketDecoder;
import io.netty.buffer.ByteBufInputStream;
import org.joml.Vector3f;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

public class ClientPacketHandler extends PacketHandler {
    public void setup() {
        packetHandlers.put(LoginResultPacket.class, (server, buffer) -> {
            byte success = buffer.readByte();
            if(success == 0) {
                throw new IllegalStateException("Failed to login");
            } else {
                GameClient.state = GameClient.ClientState.INITIAL_WORLD_LOAD;

                ClientPlayer player = new ClientPlayer();
                player.setGamemode(Player.Gamemode.CREATIVE);
                SandboxGame.getInstance().getGameRenderer().player = player;

                GameClient.serverRenderDistance = buffer.readByte();

                ClientWorld world = new ClientWorld();
                SandboxGame.getInstance().getGameRenderer().loadWorldWithoutMarkingReadyAndTicking(world);
                SandboxGame.getInstance().getGameRenderer().setScreen(new ServerWorldLoadingScreen());

                if(GameClient.barrierChunk == null) {
                    GameClient.barrierChunk = new Block[16 * 16 * 128];
                    Arrays.fill(GameClient.barrierChunk, Blocks.BARRIER);
                }
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
                SandboxGame.getInstance().getGameRenderer().player.setPosition(position.x, position.y, position.z);
                SandboxGame.getInstance().getGameRenderer().player.pitch = pitch;
                SandboxGame.getInstance().getGameRenderer().camera.pitch = pitch;
                SandboxGame.getInstance().getGameRenderer().camera.yaw = yaw;
            }
        });

        packetHandlers.put(ChunkDataPacket.class, (player, buffer) -> {
            ClientWorld world = SandboxGame.getInstance().getGameRenderer().world;
            Chunk chunk = world.createChunk(buffer.readByte(), buffer.readByte());
            chunk.blocks = new Block[16 * 16 * 128];

            HashMap<Byte, Block> idToBlock = new HashMap<>();

            byte amountOfUniqueBlocks = buffer.readByte();
            for (int i = 0; i < amountOfUniqueBlocks; i++) {
                byte id = buffer.readByte();
                Block block = Blocks.idToBlock.get(ByteBufPacketDecoder.readString(buffer));
                idToBlock.put(id, block);
            }

            int amountOfBlocks = buffer.readInt();
            int compressedSize = buffer.readInt();

            try {
                byte[] compressedData = new byte[compressedSize];

                for (int i = 0; i < compressedSize; i++) {
                    compressedData[i] = buffer.readByte();
                }

                byte[] uncompressedData = new byte[amountOfBlocks];
                Inflater inflater = new Inflater();
                inflater.setInput(compressedData, 0, compressedSize);
                inflater.inflate(uncompressedData);
                inflater.end();

                for (int i = 0; i < amountOfBlocks; i++) {
                    chunk.blocks[i] = idToBlock.get(uncompressedData[i]);
                }
            } catch(Exception e) {
                throw new RuntimeException(e);
            }

            chunk.calculateSkylight();
            chunk.featuresGenerated = true;
            chunk.state = Chunk.ChunkState.READY;

            world.loadedChunks.put(chunk.chunkPosition, chunk);

            GameClientHandler.sendPacket(new ChunkReceivedPacket());

            if(world.loadedChunks.size() >= Math.pow(GameClient.serverRenderDistance * 2 + 1, 2)) {
                for (int x = -GameClient.serverRenderDistance - 2; x <= GameClient.serverRenderDistance + 2; x++) {
                    for (int y = -GameClient.serverRenderDistance - 2; y <= GameClient.serverRenderDistance + 2; y++) {
                        if(world.getChunk(x,y) != null) continue;
                        ClientChunk barrierChunk = new ClientChunk(x, y, world);
                        barrierChunk.blocks = GameClient.barrierChunk;
                        barrierChunk.featuresGenerated = true;
                        barrierChunk.state = Chunk.ChunkState.READY;

                        world.loadedChunks.put(barrierChunk.chunkPosition, barrierChunk);
                    }
                }

                world.ready = true;
                GameClient.state = GameClient.ClientState.PLAYING;
            }
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
                SandboxGame.getInstance().getGameRenderer().world.setBlock(x,y,z, block);
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
            SandboxGame.getInstance().getGameRenderer().world.spawnCreature(creature);
        });

        packetHandlers.put(RemoveCreaturePacket.class, (server, buffer) -> {
            int networkId = buffer.readInt();
            List<Creature> creatures = SandboxGame.getInstance().getGameRenderer().world.creatures;
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
