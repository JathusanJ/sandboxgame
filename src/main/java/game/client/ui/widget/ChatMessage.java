package game.client.ui.widget;

import game.client.SandboxGame;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ChatMessage {
    public String message;
    public double age = 0;

    public ChatMessage(String message) {
        this.message = message;
    }

    public void render(double deltaTime, Vector2f position, boolean fullyVisible) {
        this.age = this.age + deltaTime;
        if(age >= 10 && !fullyVisible) return;
        float transparency = (float) (1 - Math.max(age - 9, 0));
        if(fullyVisible) {
            transparency = 1;
        }
        SandboxGame.getInstance().getGameRenderer().uiRenderer.renderColoredQuad(position, new Vector2f(700, 25), new Vector4f(0,0,0, transparency * 0.75F));
        SandboxGame.getInstance().getGameRenderer().textRenderer.renderText(this.message, position.x, position.y + 12.5F, new Vector4f(transparency,transparency,transparency, transparency), false, true);
    }
}
