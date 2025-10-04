package game.client.rendering.renderer;

import game.client.ui.widget.ChatMessage;
import org.joml.Vector2f;

import java.util.ArrayList;

public class ChatRenderer {
    private ArrayList<ChatMessage> messages = new ArrayList<>();

    public void render(double deltaTime) {
        this.render(deltaTime, false);
    }

    public void render(double deltaTime, boolean renderAll) {
        for(int i = 0; i < messages.size(); i++) {
            Vector2f position = new Vector2f(25, 150 + i * 25);
            messages.get(i).render(deltaTime, position, renderAll);
        }
    }

    public void add(ChatMessage message) {
        this.messages.addFirst(message);
        if(this.messages.size() > 20) {
            this.messages.removeLast();
        }
    }

    public void clear() {
        this.messages.clear();
    }
}
