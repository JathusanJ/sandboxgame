package engine;

public abstract class Game {
    public abstract void initialize(StartupArguments arguments);
    public abstract void postWindowInitialization();
    public abstract void postWindowLoop();

    public abstract void render(double delta);

    public abstract String getGameName();
    public abstract String getGameVersion();

    public abstract void onWindowResize(int width, int height);

    public abstract void onMouseMovement(double offsetX, double offsetY);
    public abstract void onMouseScroll(double xScroll, double yScroll);

    public abstract void onCharacterInput(String character);
}
