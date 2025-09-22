package game.client.rendering.renderer;

import com.google.gson.stream.JsonReader;
import game.client.SandboxGame;
import engine.input.KeyboardAndMouseInput;
import engine.renderer.Camera;
import engine.renderer.Texture;
import game.client.rendering.chunk.ChunkMesh;
import game.client.rendering.chunk.ChunkRenderer;
import game.client.rendering.chunk.ChunkVertexBuilder;
import game.client.ui.item.ItemTextures;
import game.client.ui.screen.*;
import game.client.ui.text.Font;
import game.client.ui.widget.TextFieldWidget;
import game.client.world.SingleplayerWorld;
import game.shared.multiplayer.skin.Skins;
import game.shared.util.json.WrappedJsonObject;
import game.shared.world.World;
import game.shared.world.blocks.Blocks;
import game.client.world.creature.ClientPlayer;
import game.shared.world.chunk.ChunkLoaderManager;
import game.shared.world.creature.ItemCreature;
import game.shared.world.creature.Player;
import game.shared.TickManager;
import game.shared.world.blocks.Block;
import game.client.world.ClientChunk;
import game.client.world.ClientWorld;

import game.shared.world.items.BlockItem;
import game.shared.world.items.Item;
import game.shared.world.items.Items;
import game.client.multiplayer.GameClient;
import game.client.multiplayer.GameClientHandler;
import game.shared.multiplayer.packets.DropItemPacket;
import game.shared.multiplayer.packets.SetBlockPacket;
import org.joml.*;
import org.lwjgl.system.MemoryUtil;

import java.io.*;
import java.lang.Math;
import java.lang.Runtime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class GameRenderer {
    public Camera camera;
    public float cameraSpeed = 2.5F;

    public boolean testCulling = false;
    public boolean showDebugInfo = false;
    public boolean hideHUD = false;

    public UIRenderer uiRenderer = new UIRenderer();
    public ChunkRenderer chunkRenderer = new ChunkRenderer(this);
    public SkyRenderer skyRenderer = new SkyRenderer();
    public CreatureRenderer creatureRenderer = new CreatureRenderer(this);
    public BlockBreakingProgressRenderer blockBreakingProgressRenderer = new BlockBreakingProgressRenderer();
    public ChatRenderer chatRenderer = new ChatRenderer();
    public BlockSelectorRenderer blockSelectorRenderer = new BlockSelectorRenderer();
    public Vector3i blockCurrentlySelecting;
    public BlockItemTextureRenderer blockItemTextureRenderer = new BlockItemTextureRenderer(this);

    public ClientWorld world;
    public ClientPlayer player;

    public Texture crosshairTexture;
    public Texture hotbarUnselectedTexture;
    public Texture hotbarSelectedTexture;
    public Texture heartFullTexture;
    public Texture heartHalfTexture;
    public Texture heartEmptyTexture;

    public HashMap<Block, Texture> blockToItemIcon = new HashMap<>();

    private Screen currentScreen;
    private TextFieldWidget currentTextField;

    public TickManager tickManager = new TickManager();

    public Queue<ChunkMesh> chunkMeshesToDelete = new ConcurrentLinkedQueue<>();

    public void loadWorld(ClientWorld world) {
        SandboxGame.getInstance().logger.info("Loading world {}", world.name);

        this.player = new ClientPlayer();
        this.player.position.set(0, 100, 0);
        this.player.lastPosition.set(this.player.position);

        File playerJson = new File(world.worldFolder, "player.json");
        if(playerJson.exists()) {
            WrappedJsonObject json;
            try {
                FileInputStream inputStream = new FileInputStream(playerJson);
                json = WrappedJsonObject.read(new JsonReader(new StringReader(new String(inputStream.readAllBytes()))));
                inputStream.close();
            } catch (IOException e) {
                throw new IllegalStateException("Couldn't read player.json", e);
            }

            this.player.load(json);
        }

        world.chunkLoaderManager.addTicket(world.spawnLoadTicket);
        if(world instanceof SingleplayerWorld singleplayerWorld) {
            singleplayerWorld.playerTicket = new ChunkLoaderManager.Ticket(player.getChunkPosition().x, player.getChunkPosition().y, SandboxGame.getInstance().settings.renderDistance + 1);
            singleplayerWorld.chunkLoaderManager.addTicket(singleplayerWorld.playerTicket);
        }

        this.loadWorldWithoutMarkingReadyAndTicking(world);
    }

    public void markWorldReady() {
        this.world.ready = true;
    }

    public void loadWorldWithoutMarkingReadyAndTicking(ClientWorld world) {
        this.world = world;
        this.world.spawnCreature(this.player);
        this.world.chunkLoaderManager.start();
    }

    public void unloadCurrentWorld() {
        if(this.world == null) return;
        this.world.stop();
        this.world.deleteChunkMeshes();

        if(!GameClient.isConnectedToServer) {
            SandboxGame.getInstance().logger.info("Saving world");

            this.world.save();
            WrappedJsonObject json = new WrappedJsonObject();
            this.player.save(json);

            File playerJson = new File(world.worldFolder, "player.json");
            try {
                SandboxGame.getInstance().logger.info("Saving player data");

                if (!playerJson.exists()) {
                    if (!playerJson.createNewFile()) {
                        throw new IllegalStateException("player.json already present");
                    }
                }

                FileOutputStream outputStream = new FileOutputStream(playerJson);
                outputStream.write(json.toElement().toString().getBytes());
                outputStream.close();

                SandboxGame.getInstance().logger.info("Saved player data");
            } catch (IOException e) {
                throw new IllegalStateException("Couldn't write to player.json", e);
            }
        } else {
            SandboxGame.getInstance().logger.info("Disconnecting from server");
            GameClient.disconnect();
        }

        SandboxGame.getInstance().doOnMainThread(() -> {
            this.world = null;
            this.player = null;
            this.chatRenderer.clear();
        });
    }

    public void setup() {
        this.camera = new Camera();
        this.camera.perspectiveProjection(SandboxGame.getInstance().getWindow().getWindowWidth(), SandboxGame.getInstance().getWindow().getWindowHeight(), this.camera.fov);

        this.chunkRenderer.setup();
        this.uiRenderer.setup();
        this.skyRenderer.setup();
        this.creatureRenderer.setup();
        this.blockBreakingProgressRenderer.setup();
        this.blockSelectorRenderer.setup();
        this.blockItemTextureRenderer.setup();
        ItemTextures.initialize();
        Skins.loadTextures();

        this.crosshairTexture = new Texture("textures/ui/crosshair.png");
        this.hotbarUnselectedTexture = new Texture("textures/ui/hotbar_unselected.png");
        this.hotbarSelectedTexture = new Texture("textures/ui/hotbar_selected.png");
        this.heartFullTexture = new Texture("textures/ui/heart_full.png");
        this.heartHalfTexture = new Texture("textures/ui/heart_half.png");
        this.heartEmptyTexture = new Texture("textures/ui/heart_empty.png");

        this.tickManager.tickables.add(SandboxGame.getInstance());
        this.tickManager.start((thread, e) -> {
            this.setScreen(new StaticWorldSavingScreen());
            if(this.world != null) {
                this.world.shouldTick = false;
                this.world.save();
                this.world.stop();
            }
            SandboxGame.getInstance().logger.error("Error while ticking ", e);
            System.exit(0);
        });
    }

    public void render(double deltaTime, int mouseX, int mouseY) {
        if(this.world != null) {
            while(this.chunkMeshesToDelete.peek() != null) {
                this.chunkMeshesToDelete.poll().delete();
            }

            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
            this.renderWorld(deltaTime);
            glDisable(GL_CULL_FACE);
            this.renderCreatures(deltaTime);
            glDisable(GL_DEPTH_TEST);

            Block blockInCamera = this.world.getBlock((int) Math.floor(this.camera.position.x), (int) Math.floor(this.camera.position.y), (int) Math.floor(this.camera.position.z));
            if(blockInCamera != null && blockInCamera.isLiquid()) {
                Vector4f color = new Vector4f(0.1F);
                if(blockInCamera == Blocks.WATER) {
                    color = new Vector4f(6F / 255F, 2F / 255F, 112F / 255F, 50F / 255F);
                } else if(blockInCamera == Blocks.LAVA) {
                    color = new Vector4f(220F / 255F, 100F / 255F, 0F / 255F, 50F / 255F);
                }
                this.uiRenderer.renderColoredQuad(new Vector2f(0, 0), new Vector2f(SandboxGame.getInstance().getWindow().getWindowWidth(), SandboxGame.getInstance().getWindow().getWindowHeight()), color);
            }
        }

        this.renderUI(deltaTime);

        if(this.currentScreen != null) {
            this.currentScreen.render(deltaTime, mouseX, mouseY);
        }
        this.uiRenderer.renderAllTooltips();
    }

    public void renderCreatures(double deltaTime) {
        this.creatureRenderer.shader.use();

        Matrix4f view = this.camera.getViewMatrix();
        Matrix4f projection = this.camera.getProjectionMatrix();

        this.creatureRenderer.shader.uploadMatrix4f("view", view);
        this.creatureRenderer.shader.uploadMatrix4f("projection", projection);

        double deltaTickTime = Math.clamp((glfwGetTime() - this.tickManager.lastTickTime) / 0.05F, 0, 1);

        for(int i = 0; i < this.world.creatures.size(); i++) {
            if(this.world.creatures.get(i) == this.player) {
                continue;
            }
            this.creatureRenderer.render(this.world.creatures.get(i), deltaTickTime, 0.25F + this.skyRenderer.skyColorSpline.calculateLinear(this.world.getDayTime()) * 0.75F);
        }
    }

    public void renderWorld(double deltaTime) {
        if(this.player != null) {
            World.WorldRaycastResult raycastResult = this.world.blockRaycast(this.camera.position, this.camera.getDirection(), 5);
            if(raycastResult.success()) {
                this.blockCurrentlySelecting = raycastResult.position();
            } else {
                this.blockCurrentlySelecting = null;
            }

            this.player.resetMovement();

            if(this.currentScreen == null) {
                if (KeyboardAndMouseInput.pressingKey(GLFW_KEY_LEFT_CONTROL) && this.player.gamemode == Player.Gamemode.CREATIVE) {
                    this.cameraSpeed = 50F;
                } else {
                    this.cameraSpeed = 4F;
                }

                this.player.pitch = this.camera.pitch;

                if (this.player.world != null) {
                    this.player.handleMovement((float) deltaTime, this.camera.getFront(), this.camera.getRight());
                    if (KeyboardAndMouseInput.pressedKey(GLFW_KEY_M)) {
                        if (this.world.commandsEnabled) {
                            if (this.player.gamemode == Player.Gamemode.CREATIVE) {
                                this.player.setGamemode(Player.Gamemode.SURVIVAL);
                            } else {
                                this.player.setGamemode(Player.Gamemode.CREATIVE);
                            }
                            this.player.sendChatMessage("Changed gamemode to " + this.player.gamemode.name());
                        } else if (GameClient.isConnectedToServer) {
                            this.player.sendChatMessage("Cannot change gamemode: Survival isn't available in multiplayer");
                        } else {
                            this.player.sendChatMessage("Cannot change gamemode: Commands aren't enabled in this world");
                        }
                    }
                }

                if(KeyboardAndMouseInput.pressingMouseButton(GLFW_MOUSE_BUTTON_1)) {
                    if(raycastResult.success()) {
                        if(this.player.blockBreakingProgress == null) {
                            if(KeyboardAndMouseInput.pressedMouseButton(GLFW_MOUSE_BUTTON_1) || this.player.breakingCooldown == 0) {
                                this.player.blockBreakingProgress = new Player.BlockBreakingProgress(raycastResult.position(), this.player);
                                if (GameClient.isConnectedToServer) {
                                    GameClientHandler.sendPacket(new SetBlockPacket(raycastResult.position().x, raycastResult.position().y, raycastResult.position().z, Blocks.AIR));
                                    this.player.breakingCooldown = 5;
                                }
                            }
                        } else if(!this.player.blockBreakingProgress.blockPosition.equals(raycastResult.position()) || this.player.currentHotbarSlot != this.player.blockBreakingProgress.hotbarSlot){
                        /*if(GameClient.isConnectedToServer) {
                            GameClientHandler.sendPacket(new BlockBreakingPacket(BlockBreakingPacket.State.CLIENT_STOP));
                        }*/
                            this.player.blockBreakingProgress = null;
                        }
                    } else if(this.player.blockBreakingProgress != null) {
                    /*if(GameClient.isConnectedToServer) {
                        GameClientHandler.sendPacket(new BlockBreakingPacket(BlockBreakingPacket.State.CLIENT_STOP));
                    }*/
                        this.player.blockBreakingProgress = null;
                    }
                } else if(this.player.blockBreakingProgress != null) {
                /*if(GameClient.isConnectedToServer) {
                    GameClientHandler.sendPacket(new BlockBreakingPacket(BlockBreakingPacket.State.CLIENT_STOP));
                }*/
                    this.player.blockBreakingProgress = null;
                }
                if(KeyboardAndMouseInput.pressedMouseButton(GLFW_MOUSE_BUTTON_2)) {
                    World.WorldRaycastResult result = this.world.blockRaycast(this.camera.position, this.camera.getDirection(), 5);
                    if(result.success() && this.world.getBlock(result.position().x, result.position().y, result.position().z).onRightClick(this.world, result.position())) {
                        Item item = this.player.inventory[this.player.currentHotbarSlot].getItem();
                        if (item != null) {
                            item.onUse(new Item.ItemUsageContext(this.world, this.player, this.player.inventory[this.player.currentHotbarSlot], result.position(), result.normal()));
                        }
                    }
                }

                if(KeyboardAndMouseInput.pressedKey(GLFW_KEY_Q)) {
                    if(GameClient.isConnectedToServer) {
                        DropItemPacket packet;

                        if(KeyboardAndMouseInput.pressedKey(GLFW_KEY_LEFT_SHIFT)) {
                            packet = new DropItemPacket(this.player.currentHotbarSlot, this.player.inventory[this.player.currentHotbarSlot].amount);
                        } else {
                            packet = new DropItemPacket(this.player.currentHotbarSlot, 1);
                        }

                        GameClientHandler.sendPacket(packet);
                    } else {
                        if (this.player.inventory[this.player.currentHotbarSlot].getItem() != Items.AIR) {
                            ItemCreature itemCreature = new ItemCreature();
                            itemCreature.representingItemStack.setItem(this.player.inventory[this.player.currentHotbarSlot].getItem());

                            itemCreature.position.set(this.player.position);
                            itemCreature.position.add(0, 1.75F, 0);
                            itemCreature.velocity.add(this.camera.getFront().x * 4F, 2F, this.camera.getFront().z * 4F);

                            if (KeyboardAndMouseInput.pressedKey(GLFW_KEY_LEFT_SHIFT)) {
                                itemCreature.representingItemStack.setAmount(this.player.inventory[this.player.currentHotbarSlot].amount);
                                this.player.inventory[this.player.currentHotbarSlot].decreaseBy(this.player.inventory[this.player.currentHotbarSlot].amount);
                            } else {
                                itemCreature.representingItemStack.setAmount(1);
                                this.player.inventory[this.player.currentHotbarSlot].decreaseBy(1);
                            }

                            this.world.spawnCreature(itemCreature);
                        }
                    }
                }

                if(KeyboardAndMouseInput.pressingKey(GLFW_KEY_1)) {
                    this.player.switchToHotbarSlot(0);
                }
                if(KeyboardAndMouseInput.pressingKey(GLFW_KEY_2)) {
                    this.player.switchToHotbarSlot(1);
                }
                if(KeyboardAndMouseInput.pressingKey(GLFW_KEY_3)) {
                    this.player.switchToHotbarSlot(2);
                }
                if(KeyboardAndMouseInput.pressingKey(GLFW_KEY_4)) {
                    this.player.switchToHotbarSlot(3);
                }
                if(KeyboardAndMouseInput.pressingKey(GLFW_KEY_5)) {
                    this.player.switchToHotbarSlot(4);
                }
                if(KeyboardAndMouseInput.pressingKey(GLFW_KEY_6)) {
                    this.player.switchToHotbarSlot(5);
                }
                if(KeyboardAndMouseInput.pressingKey(GLFW_KEY_7)) {
                    this.player.switchToHotbarSlot(6);
                }
                if(KeyboardAndMouseInput.pressingKey(GLFW_KEY_8)) {
                    this.player.switchToHotbarSlot(7);
                }
                if(KeyboardAndMouseInput.pressingKey(GLFW_KEY_9)) {
                    this.player.switchToHotbarSlot(8);
                }
                if(KeyboardAndMouseInput.pressedKey(GLFW_KEY_ESCAPE)) {
                    this.setScreen(new PauseMenuScreen());
                    this.world.shouldTick = false;
                }
                if(KeyboardAndMouseInput.pressedKey(GLFW_KEY_F3)) {
                    this.showDebugInfo = !this.showDebugInfo;
                }
                if(KeyboardAndMouseInput.pressedKey(GLFW_KEY_E)) {
                    if(this.player.gamemode == Player.Gamemode.SURVIVAL) {
                        this.setScreen(new InventoryScreen());
                    } else {
                        this.setScreen(new BlockAndItemSelectorScreen());
                    }
                }
                if(KeyboardAndMouseInput.pressedKey(GLFW_KEY_T)) {
                    this.setScreen(new ChatHudScreen());
                }
                if(KeyboardAndMouseInput.pressedKey(GLFW_KEY_F2)) {
                    this.hideHUD = !this.hideHUD;
                }
                if(KeyboardAndMouseInput.pressingKey(GLFW_KEY_P) && KeyboardAndMouseInput.pressedKey(GLFW_KEY_1)) {
                    this.camera.pitch = 0F;
                    this.camera.yaw = 0F;
                }
                if(KeyboardAndMouseInput.pressingKey(GLFW_KEY_P) && KeyboardAndMouseInput.pressedKey(GLFW_KEY_2)) {
                    this.camera.pitch = 0F;
                    this.camera.yaw = 90F;
                }
                if(KeyboardAndMouseInput.pressingKey(GLFW_KEY_P) && KeyboardAndMouseInput.pressedKey(GLFW_KEY_3)) {
                    this.camera.pitch = 0F;
                    this.camera.yaw = 180F;
                }
                if(KeyboardAndMouseInput.pressingKey(GLFW_KEY_P) && KeyboardAndMouseInput.pressedKey(GLFW_KEY_4)) {
                    this.camera.pitch = 0F;
                    this.camera.yaw = 270F;
                }
            }

            if(this.world != null && this.world.shouldTick && this.world.ready) {
                this.player.applyPhysics((float) deltaTime);
            }
        }

        if(this.player != null && !(this.currentScreen instanceof WorldSavingScreen)) {
            this.camera.position.set(this.player.position).add(0, 1.75F, 0);

            ArrayList<ClientChunk> chunksToRender = new ArrayList<>();

            for (int x = this.player.getChunkPosition().x - SandboxGame.getInstance().settings.renderDistance; x < this.player.getChunkPosition().x + SandboxGame.getInstance().settings.renderDistance; x++) {
                for (int y = this.player.getChunkPosition().y - SandboxGame.getInstance().settings.renderDistance; y < this.player.getChunkPosition().y + SandboxGame.getInstance().settings.renderDistance; y++) {
                    Vector2i chunkPosition = new Vector2i(x, y);
                    if (this.world.loadedChunks.containsKey(chunkPosition)) {
                        chunksToRender.add((ClientChunk) this.world.loadedChunks.get(chunkPosition));
                    }
                }
            }

            this.skyRenderer.render();

            this.chunkRenderer.renderChunks(chunksToRender, 0.25F + this.skyRenderer.skyColorSpline.calculateLinear(this.world.getDayTime()) * 0.75F);

            if (this.player.blockBreakingProgress != null) {
                this.blockBreakingProgressRenderer.render(this.player.blockBreakingProgress);
            }
            if (this.blockCurrentlySelecting != null) {
                this.blockSelectorRenderer.render(this.blockCurrentlySelecting);
            }
        }
    }

    public void renderUI(double deltaTime) {
        if(this.hideHUD) return;

        if(this.world != null && !(this.currentScreen instanceof WorldSavingScreen)) {
            this.renderHUD(deltaTime);

            if(this.showDebugInfo) {
                this.uiRenderer.renderTextWithShadow("Sandbox Game " + SandboxGame.getInstance().getVersion().versionName() + " - " + SandboxGame.getInstance().getWindow().getFps() + " FPS", new Vector2f(0, SandboxGame.getInstance().getWindow().getWindowHeight() - 24), 24);
                if(this.world != null && this.player != null) {
                    this.uiRenderer.renderTextWithShadow(this.world.loadedChunks.size() + " chunks loaded", new Vector2f(0, SandboxGame.getInstance().getWindow().getWindowHeight() - 24 * 2), 24);
                    this.uiRenderer.renderTextWithShadow(this.chunkRenderer.chunksRendered + " chunks rendering (" + this.chunkRenderer.verticesRendered + " vertices, "+ (this.chunkRenderer.verticesRendered / 4) +" faces)", new Vector2f(0, SandboxGame.getInstance().getWindow().getWindowHeight() - 24 * 3), 24);
                    this.uiRenderer.renderTextWithShadow("position x: " + Math.floor(this.player.position.x * 100) / 100 + " y: " + Math.floor(this.player.position.y * 100) / 100 + " (camera: " + Math.floor(this.camera.position.y * 100) / 100 + ") z: " + Math.floor(this.player.position.z * 100) / 100, new Vector2f(0, SandboxGame.getInstance().getWindow().getWindowHeight() - 24 * 4), 24);
                    this.uiRenderer.renderTextWithShadow("chunk position x: " + Math.floor(this.camera.position.x / 16D) + " z: " + Math.floor(this.camera.position.z / 16D), new Vector2f(0, SandboxGame.getInstance().getWindow().getWindowHeight() - 24 * 5), 24);

                    this.uiRenderer.renderTextWithShadow("seed: " + this.world.seed, new Vector2f(0, SandboxGame.getInstance().getWindow().getWindowHeight() - 24 * 7), 24);
                    this.uiRenderer.renderTextWithShadow("world time: " + this.world.worldTime, new Vector2f(0, SandboxGame.getInstance().getWindow().getWindowHeight() - 24 * 8), 24);
                    this.uiRenderer.renderTextWithShadow("day time: " + this.world.getDayTime(), new Vector2f(0, SandboxGame.getInstance().getWindow().getWindowHeight() - 24 * 9), 24);

                    Vector3i playerBlockPosition = new Vector3i((int) Math.floor(this.player.position.x), (int) Math.floor(this.player.position.y), (int) Math.floor(this.player.position.z));

                    float skylight = this.world.getSkylight(playerBlockPosition.x, playerBlockPosition.y, playerBlockPosition.z) / 16F;
                    float light = this.world.getLight(playerBlockPosition.x, playerBlockPosition.y, playerBlockPosition.z) / 16F;

                    this.uiRenderer.renderTextWithShadow("skylight: " + skylight + " light: " + light, new Vector2f(0, SandboxGame.getInstance().getWindow().getWindowHeight() - 24 * 11), 24);

                    if(this.player.blockBreakingProgress != null) {
                        this.uiRenderer.renderTextWithShadow("Block breaking progress: " + this.player.blockBreakingProgress.breakingTicks, new Vector2f(0, SandboxGame.getInstance().getWindow().getWindowHeight() - 24 * 14), 24);
                    }

                    this.uiRenderer.renderTextWithShadowRightSided("Java " + Runtime.version().feature() + " (" + Runtime.version().toString() + ")", new Vector2f(SandboxGame.getInstance().getWindow().getWindowWidth(), SandboxGame.getInstance().getWindow().getWindowHeight() - 24), 24);
                    this.uiRenderer.renderTextWithShadowRightSided("Memory: " + Math.round((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024F * 1024F)) + " Mb / " + (Runtime.getRuntime().totalMemory() / (1024F * 1024F)) + " Mb", new Vector2f(SandboxGame.getInstance().getWindow().getWindowWidth(), SandboxGame.getInstance().getWindow().getWindowHeight() - 24 * 2), 24);

                    this.uiRenderer.renderTextWithShadowRightSided("Window size: " + SandboxGame.getInstance().getWindow().getWindowWidth() + "x" + SandboxGame.getInstance().getWindow().getWindowHeight(), new Vector2f(SandboxGame.getInstance().getWindow().getWindowWidth(), SandboxGame.getInstance().getWindow().getWindowHeight() - 24 * 4), 24);
                }
            }
        }
    }

    public void renderHUD(double deltaTime) {
        if(this.currentScreen instanceof ChatHudScreen) {
            this.chatRenderer.render(deltaTime, true);
        } else {
            this.chatRenderer.render(deltaTime);
        }

        this.uiRenderer.renderTexture(this.crosshairTexture, new Vector2f(SandboxGame.getInstance().getWindow().getWindowWidth() / 2F - 10, SandboxGame.getInstance().getWindow().getWindowHeight() / 2F - 10), new Vector2f(20, 20));
        for(int i = 0; i < 9; i++) {
            this.uiRenderer.renderTexture(i == this.player.currentHotbarSlot ? this.hotbarSelectedTexture : this.hotbarUnselectedTexture, new Vector2f(SandboxGame.getInstance().getWindow().getWindowWidth() / 2F - 4.5F * 75 + i * 75, 0), new Vector2f(75, 75));
            if(this.player.inventory[i] != null && this.player.inventory[i].getItem() != Items.AIR) {
                if(this.player.inventory[i].getItem() instanceof BlockItem blockItem) {
                    this.uiRenderer.renderTexture(this.getBlockItemTexture(blockItem), new Vector2f(SandboxGame.getInstance().getWindow().getWindowWidth() / 2F - 4.5F * 75 + i * 75 + 7.5F, 7.5F), new Vector2f(60, 60));
                } else {
                    this.uiRenderer.renderTexture(ItemTextures.getTexture(this.player.inventory[i].getItem().id), new Vector2f(SandboxGame.getInstance().getWindow().getWindowWidth() / 2F - 4.5F * 75 + i * 75 + 7.5F, 7.5F), new Vector2f(60, 60));
                }
                if(this.player.inventory[i].amount != 1) {
                    this.uiRenderer.renderText(String.valueOf(this.player.inventory[i].amount), new Vector2f(SandboxGame.getInstance().getWindow().getWindowWidth() / 2F - 4.5F * 75 + i * 75 + 7.5F + 60 - Font.getTextWidth(String.valueOf(this.player.inventory[i].amount), 24), 7.5F), 24);
                }
            }
        }

        if(this.player.inventory[this.player.currentHotbarSlot] != null && this.player.inventory[this.player.currentHotbarSlot].getItem() != Items.AIR) {
            this.uiRenderer.renderTextWithShadow(this.player.inventory[this.player.currentHotbarSlot].getItem().getName(), new Vector2f(SandboxGame.getInstance().getWindow().getWindowWidth() / 2F, 120), 24, true);
        }

        if(this.player.gamemode == Player.Gamemode.SURVIVAL) {
            for (int i = 0; i < 10 ; i++) {
                double fullHearts = Math.floor(this.player.health) / 2;
                boolean full = i < fullHearts;
                boolean half = i * 2 < Math.ceil(this.player.health) ;

                Texture textureToUse;

                if(full) {
                    textureToUse = this.heartFullTexture;
                } else if(half) {
                    textureToUse = this.heartHalfTexture;
                } else {
                    textureToUse = this.heartEmptyTexture;
                }

                float offset = 0;
                if(fullHearts <= 2) {
                    offset = (float) Math.sin(SandboxGame.getInstance().getWindow().getTimeSinceWindowInitialization() * (3 - fullHearts) * 5F - i * 1.5F) * 4;
                }

                this.uiRenderer.renderTexture(textureToUse, new Vector2f(SandboxGame.getInstance().getWindow().getWindowWidth() / 2F - 4.5F * 75 + i * 32, 80 + offset), new Vector2f(32,32));
            }
        }
    }

    public Texture getBlockItemTexture(BlockItem block) {
        if(block.getOverriddenItemTexture() == null) {
            return this.getBlockItemTexture(block.getBlock());
        }

        return ItemTextures.getTexture(block.getOverriddenItemTexture());
    }

    public Texture getBlockItemTexture(Block block) {
        if(!this.blockToItemIcon.containsKey(block)) {
            this.renderBlock(block);
        }

        return this.blockToItemIcon.get(block);
    }

    public void renderBlock(Block block) {
        int fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 800, 600, 0, GL_RGBA, GL_UNSIGNED_BYTE, MemoryUtil.NULL);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);

        if(!(glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE)) {
            // Replace with better logging later on
            new RuntimeException("Couldn't render block in ui: Framebuffer returned status " + glCheckFramebufferStatus(GL_FRAMEBUFFER)).printStackTrace();

            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glDeleteFramebuffers(fbo);
            return;
        }

        Camera renderCamera = new Camera();
        renderCamera.orthographicProjection(1,1);
        renderCamera.position.add(7F, 7F, 12.75F);
        renderCamera.pitch = -35F;
        renderCamera.yaw = -135F;

        ChunkVertexBuilder vertexBuilder = new ChunkVertexBuilder();

        int x = 0;
        int y = 0;
        int z = 0;

        // Top side
        vertexBuilder.vertex(x + 1, y + 1, z + 1, 1, 0, ChunkVertexBuilder.Normal.TOP, block);
        vertexBuilder.vertex(x + 1, y + 1, z, 1, 1, ChunkVertexBuilder.Normal.TOP, block);
        vertexBuilder.vertex(x, y + 1, z, 0, 1, ChunkVertexBuilder.Normal.TOP, block);

        vertexBuilder.vertex(x, y + 1, z, 0, 1, ChunkVertexBuilder.Normal.TOP, block);
        vertexBuilder.vertex(x, y + 1, z + 1, 0, 0, ChunkVertexBuilder.Normal.TOP, block);
        vertexBuilder.vertex(x + 1, y + 1, z + 1, 1, 0, ChunkVertexBuilder.Normal.TOP, block);

        // Right side
        vertexBuilder.vertex(x + 1, y, z, 1, 0, ChunkVertexBuilder.Normal.RIGHT, block);
        vertexBuilder.vertex(x + 1, y + 1, z, 1, 1, ChunkVertexBuilder.Normal.RIGHT, block);
        vertexBuilder.vertex(x + 1, y + 1, z + 1, 0, 1, ChunkVertexBuilder.Normal.RIGHT, block);

        vertexBuilder.vertex(x + 1, y + 1, z + 1, 0, 1, ChunkVertexBuilder.Normal.RIGHT, block);
        vertexBuilder.vertex(x + 1, y, z + 1, 0, 0, ChunkVertexBuilder.Normal.RIGHT, block);
        vertexBuilder.vertex(x + 1, y, z, 1, 0, ChunkVertexBuilder.Normal.RIGHT, block);

        // Left side
        vertexBuilder.vertex(x, y + 1, z + 1, 1, 1, ChunkVertexBuilder.Normal.LEFT, block);
        vertexBuilder.vertex(x, y + 1, z, 0, 1, ChunkVertexBuilder.Normal.LEFT, block);
        vertexBuilder.vertex(x, y, z, 0, 0, ChunkVertexBuilder.Normal.LEFT, block);

        vertexBuilder.vertex(x, y, z, 0, 0, ChunkVertexBuilder.Normal.LEFT, block);
        vertexBuilder.vertex(x, y, z + 1, 1, 0, ChunkVertexBuilder.Normal.LEFT, block);
        vertexBuilder.vertex(x, y + 1, z + 1, 1, 1, ChunkVertexBuilder.Normal.LEFT, block);

        // Front side
        vertexBuilder.vertex(x, y, z + 1, 0, 0, ChunkVertexBuilder.Normal.FRONT, block);
        vertexBuilder.vertex(x + 1, y, z + 1, 1, 0, ChunkVertexBuilder.Normal.FRONT, block);
        vertexBuilder.vertex(x + 1, y + 1, z + 1, 1, 1, ChunkVertexBuilder.Normal.FRONT, block);

        vertexBuilder.vertex(x + 1, y + 1, z + 1, 1, 1, ChunkVertexBuilder.Normal.FRONT, block);
        vertexBuilder.vertex(x, y + 1, z + 1, 0, 1, ChunkVertexBuilder.Normal.FRONT, block);
        vertexBuilder.vertex(x, y, z + 1, 0, 0, ChunkVertexBuilder.Normal.FRONT, block);

        float[] vertices = vertexBuilder.compile(0, 0, 5);

        this.chunkRenderer.shader.use();
        this.chunkRenderer.shader.uploadMatrix4f("view", renderCamera.getViewMatrix());
        this.chunkRenderer.shader.uploadMatrix4f("projection", renderCamera.getProjectionMatrix());
        this.chunkRenderer.shader.uploadFloat("sunLight", 1F);

        this.chunkRenderer.texture.bind();

        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        this.chunkRenderer.createVertexAttributes();

        glViewport(0, 0, 800, 600);

        glBindVertexArray(vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        glEnable(GL_CULL_FACE);
        glDrawArrays(GL_TRIANGLES, 0, vertices.length / 8);
        glDisable(GL_CULL_FACE);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glDeleteFramebuffers(fbo);
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);

        glViewport(0, 0, SandboxGame.getInstance().getWindow().getWindowWidth(), SandboxGame.getInstance().getWindow().getWindowHeight());

        this.blockToItemIcon.put(block, new Texture(texture));
    }

    public void setScreen(Screen screen) {
        this.currentScreen = screen;
        if(screen == null) {
            if(this.world != null) {
                SandboxGame.getInstance().getWindow().captureCursor();
            }
        } else {
            SandboxGame.getInstance().getWindow().freeCursor();
            this.currentScreen.positionContent();
        }
        KeyboardAndMouseInput.updateLastFramePressed();
    }

    public void mouseMovement(double movementX, double movementY) {
        if(this.currentScreen == null && this.world != null) {
            this.camera.mouseMovement(movementX, movementY);
        }
    }

    public void mouseScroll(double scrollY) {
        if(this.player != null && this.world != null && this.currentScreen == null) {
            this.player.currentHotbarSlot += scrollY < 0 ? 1 : -1;
            if(this.player.currentHotbarSlot > 8) this.player.currentHotbarSlot = 0;
            if(this.player.currentHotbarSlot < 0) this.player.currentHotbarSlot = 8;
            this.player.switchToHotbarSlot(this.player.currentHotbarSlot);
        } else if (this.currentScreen != null) {
            this.currentScreen.onScroll(scrollY, KeyboardAndMouseInput.getMousePosition().x, KeyboardAndMouseInput.getMousePosition().y);
        }
    }

    public void onWindowResize(int width, int height) {
        if(this.camera != null) {
            this.camera.perspectiveProjection(width, height, this.camera.fov);
            this.uiRenderer.camera.orthographicProjection(width, height);
        }
        if(this.currentScreen != null) {
            this.currentScreen.positionContent();
        }
    }

    public void onCharacterInput(String characters) {
        if(this.currentScreen != null && this.currentTextField != null) {
            this.currentTextField.onCharacterInput(characters);
        }
    }

    public void setCurrentTextField(TextFieldWidget textFieldWidget) {
        this.currentTextField = textFieldWidget;
    }

    public TextFieldWidget getCurrentTextField() {
        return this.currentTextField;
    }
}
