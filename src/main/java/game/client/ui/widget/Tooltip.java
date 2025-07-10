package game.client.ui.widget;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Tooltip {
    public ArrayList<String> content;
    public Vector2f position;

    public Tooltip(List<String> content, Vector2f position) {
        this.content = new ArrayList<>(content);
        this.position = position;
    }

    public Tooltip(String content, Vector2f position) {
        this(List.of(content), position);
    }

    public List<String> getContent() {
        return this.content;
    }
}
