package game.client.rendering.renderer;

import game.client.SandboxGame;
import game.client.ui.widget.ChatMessage;
import org.joml.Vector2f;

import java.util.ArrayList;

public class ChatRenderer {
    private ArrayList<ChatMessage> messages = new ArrayList<>();

    public void render(double deltaTime) {
        for(int i = 0; i < messages.size(); i++) {
            Vector2f position = new Vector2f(25, 150 + i * 25);
            messages.get(i).render(deltaTime, position);
        }
    }

    public void add(ChatMessage message) {
        this.messages.addFirst(message);
        if(this.messages.size() > 255) {
            this.messages.removeLast();
        }
    }
}
