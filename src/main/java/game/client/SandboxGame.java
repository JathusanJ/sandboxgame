package game.client;

import com.google.gson.GsonBuilder;
import engine.Game;
import engine.GameEngine;
import engine.StartupArguments;
import engine.input.KeyboardAndMouseInput;
import game.client.rendering.renderer.GameRenderer;
import engine.sound.Sounds;
import game.client.ui.screen.WorldLoadingScreen;
import game.client.ui.text.Language;
import engine.renderer.Window;
import game.client.ui.screen.TitleScreen;
import game.logic.Tickable;
import game.logic.recipes.CraftingRecipes;
import game.logic.recipes.FurnaceRecipes;
import game.logic.util.PlayerProfile;
import game.logic.world.blocks.Blocks;
import game.logic.world.blocks.block_entity.FurnaceBlockEntity;
import game.logic.world.creature.Creatures;
import game.logic.world.items.Items;
import game.client.networking.GameClient;
import game.client.networking.GameClientHandler;
import game.networking.packets.PacketList;
import game.networking.packets.PositionRotationPacket;
import game.networking.packets.RequestChunkPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

public class SandboxGame extends Game implements Tickable {
    public Logger logger = LoggerFactory.getLogger("Sandbox Game");

    private GameRenderer gameRenderer;
    private Version version;
    public GameSettings settings;

    private File gameFolder;
    private File worldsFolder;

    private PlayerProfile playerProfile;

    public static SandboxGame getInstance(){
        return (SandboxGame) GameEngine.getGame();
    }

    public Queue<Runnable> stuffToDoOnMainThread = new LinkedList<>();
    public Queue<Runnable> stuffToDoOnTickingThread = new LinkedList<>();

    @Override
    public void initialize(StartupArguments arguments) {
        try {
            this.version = new GsonBuilder().create().fromJson(new String(Thread.currentThread().getContextClassLoader().getResourceAsStream("version.json").readAllBytes()) , Version.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load game version data", e);
        }

        this.createDirectoriesIfNeeded();

        this.settings = new GameSettings(new File(gameFolder, "settings.json"));
        this.settings.load();

        Blocks.init();
        Items.init();
        Creatures.init();
        CraftingRecipes.initialize();
        FurnaceRecipes.initialize();
        FurnaceBlockEntity.loadFuelTimes();
        Language.load("en");
        PacketList.setup();

        this.playerProfile = new PlayerProfile(arguments.map.getOrDefault("username", "Player"), UUID.randomUUID());
    }

    @Override
    public void postWindowInitialization() {
        glfwSwapInterval(SandboxGame.getInstance().settings.vsync ? 1 : 0);

        Sounds.initialize();
        this.gameRenderer = new GameRenderer();
        this.gameRenderer.setup();
        this.gameRenderer.setScreen(new TitleScreen());
        WorldLoadingScreen.splashes[WorldLoadingScreen.splashes.length - 1] = WorldLoadingScreen.splashes.length + " total world loading splashes!";
    }

    @Override
    public void postWindowLoop() {
        SandboxGame.getInstance().getGameRenderer().tickManager.isRunning = false;

        if(this.gameRenderer.world != null) {
            this.gameRenderer.unloadCurrentWorld();
        }
        this.gameRenderer.blockBreakingProgressRenderer.delete();
        this.gameRenderer.creatureRenderer.delete();
        this.gameRenderer.skyRenderer.delete();
        this.gameRenderer.uiRenderer.delete();
    }

    @Override
    public void render(double delta) {
        if(!stuffToDoOnMainThread.isEmpty()) {
            stuffToDoOnMainThread.poll().run();
        }
        this.gameRenderer.render(delta, KeyboardAndMouseInput.getMousePosition().x, KeyboardAndMouseInput.getMousePosition().y);
    }

    @Override
    public String getGameName() {
        return "Sandbox Game";
    }

    @Override
    public String getGameVersion() {
        return this.version.versionName;
    }

    @Override
    public void onWindowResize(int width, int height) {
        SandboxGame.getInstance().getWindow().width = width;
        SandboxGame.getInstance().getWindow().height = height;
        if(SandboxGame.getInstance().getGameRenderer() != null) {
            SandboxGame.getInstance().getGameRenderer().onWindowResize(width, height);
        }
    }

    @Override
    public void onMouseMovement(double offsetX, double offsetY) {
        this.gameRenderer.mouseMovement(offsetX, offsetY);
    }

    @Override
    public void onMouseScroll(double xScroll, double yScroll) {
        this.gameRenderer.mouseScroll(yScroll);
    }

    @Override
    public void onCharacterInput(String character) {
        this.gameRenderer.onCharacterInput(character);
    }

    public Window getWindow(){
        return GameEngine.getWindow();
    }

    public GameRenderer getGameRenderer(){
        return this.gameRenderer;
    }

    public void createDirectoriesIfNeeded() {
        this.gameFolder = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\sandboxgame");
        if(!this.gameFolder.exists()) this.gameFolder.mkdir();

        this.worldsFolder = new File(this.gameFolder, "worlds");
        if(!this.worldsFolder.exists()) this.worldsFolder.mkdir();
    }

    public File getWorldsFolder() {
        return this.worldsFolder;
    }

    public Version getVersion() {
        return this.version;
    }

    public PlayerProfile getPlayerProfile() {
        return this.playerProfile;
    }

    public void doOnMainThread(Runnable runnable) {
        this.stuffToDoOnMainThread.add(runnable);
    }

    public void doOnTickingThread(Runnable runnable) {
        this.stuffToDoOnTickingThread.add(runnable);
    }

    @Override
    public void tick() {
        while(!this.stuffToDoOnTickingThread.isEmpty()) {
            Runnable runnable = this.stuffToDoOnTickingThread.poll();
            if(runnable != null) {
                runnable.run();
            }
        }
        if(this.gameRenderer.world != null) {
            if(GameClient.isConnectedToServer) {
                if(GameClient.state == GameClient.ClientState.PLAYING && this.gameRenderer.player != null) {
                    if (this.gameRenderer.player.position != this.gameRenderer.player.lastPosition) {
                        PositionRotationPacket positionRotationPacket = new PositionRotationPacket(this.gameRenderer.player.position, this.gameRenderer.camera.yaw, this.gameRenderer.camera.pitch);
                        GameClientHandler.sendPacket(positionRotationPacket);
                    }
                }
                GameClient.chunkDataRequestDelay -= 1;
                if(GameClient.chunkDataReceived && !GameClient.chunksToRequest.isEmpty() && GameClient.chunkDataRequestDelay < 1) {
                    RequestChunkPacket requestChunkPacket = new RequestChunkPacket(GameClient.chunksToRequest.removeFirst());
                    GameClientHandler.sendPacket(requestChunkPacket);
                    GameClient.chunkDataReceived = false;
                }
            }
            this.gameRenderer.world.tick();
        }
    }

    public record Version(String versionId, String versionName){}
}
