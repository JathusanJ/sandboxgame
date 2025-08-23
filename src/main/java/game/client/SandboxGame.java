package game.client;

import engine.Game;
import engine.GameEngine;
import engine.StartupArguments;
import engine.input.KeyboardAndMouseInput;
import game.client.rendering.renderer.GameRenderer;
import engine.sound.Sounds;
import game.client.ui.screen.SkinSetupScreen;
import game.client.ui.screen.UsernameSetupScreen;
import game.client.ui.screen.WorldLoadingScreen;
import game.client.ui.text.Language;
import engine.renderer.Window;
import game.client.ui.screen.TitleScreen;
import game.shared.Tickable;
import game.shared.Version;
import game.shared.multiplayer.skin.Skins;
import game.shared.recipes.CraftingRecipes;
import game.shared.recipes.FurnaceRecipes;
import game.shared.util.PlayerProfile;
import game.shared.world.blocks.Blocks;
import game.shared.world.blocks.block_entity.FurnaceBlockEntity;
import game.shared.world.creature.Creatures;
import game.shared.world.items.Items;
import game.client.multiplayer.GameClient;
import game.client.multiplayer.GameClientHandler;
import game.shared.multiplayer.packets.PacketList;
import game.shared.multiplayer.packets.PositionRotationPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

public class SandboxGame extends Game implements Tickable {
    public Logger logger = LoggerFactory.getLogger("Sandbox Game");

    private GameRenderer gameRenderer;
    public GameSettings settings;

    private File gameFolder;
    private File worldsFolder;

    private PlayerProfile playerProfile;

    public static SandboxGame getInstance(){
        return (SandboxGame) GameEngine.getGame();
    }

    public Queue<Runnable> stuffToDoOnMainThread = new ConcurrentLinkedQueue<>();
    public Queue<Runnable> stuffToDoOnTickingThread = new ConcurrentLinkedQueue <>();

    public boolean firstTimeLaunch = false;

    @Override
    public void initialize(StartupArguments arguments) {
        Version.load();

        this.createDirectoriesIfNeeded();

        File settingsFile = new File(gameFolder, "settings.json");

        this.firstTimeLaunch = !settingsFile.exists();

        this.settings = new GameSettings(settingsFile);
        this.settings.load();

        Blocks.init();
        Items.init();
        Creatures.init();
        CraftingRecipes.initialize();
        FurnaceRecipes.initialize();
        FurnaceBlockEntity.loadFuelTimes();
        Language.load(this.settings.language);
        PacketList.setup();

        this.playerProfile = new PlayerProfile(arguments.map.getOrDefault("username", this.settings.username), UUID.randomUUID(), Skins.getSkin(this.settings.skin));
    }

    @Override
    public void postWindowInitialization() {
        glfwSwapInterval(SandboxGame.getInstance().settings.vsync ? 1 : 0);

        Sounds.initialize();
        this.gameRenderer = new GameRenderer();
        this.gameRenderer.setup();
        WorldLoadingScreen.splashes[WorldLoadingScreen.splashes.length - 1] = WorldLoadingScreen.splashes.length + " total world loading splashes!";

        if(this.firstTimeLaunch) {
            this.gameRenderer.setScreen(new UsernameSetupScreen(new SkinSetupScreen(new TitleScreen())));
        } else {
            this.gameRenderer.setScreen(new TitleScreen());
        }

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
        return Version.GAME_VERSION.versionName();
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
        return Version.GAME_VERSION;
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

            }
            this.gameRenderer.world.tick();
        }
    }
}
