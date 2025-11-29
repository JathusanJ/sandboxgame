package game.client.ui.screen;

import com.google.gson.GsonBuilder;
import engine.input.KeyboardAndMouseInput;
import game.client.ui.text.Language;
import game.client.ui.widget.ButtonWidget;
import game.client.ui.widget.ListWidget;
import game.client.ui.widget.Widget;
import org.joml.Vector2f;

import java.io.IOException;
import java.util.Map;

import static game.client.ui.widget.WorldWidget.BORDER_SELECTED_TEXTURE;

public class LibrariesScreen extends Screen {
    public Screen prev;
    public ListWidget listWidget = new ListWidget();
    public ButtonWidget closeButton = new ButtonWidget(Language.translate("ui.close"), this::close);

    public LibrariesScreen(Screen prev) {
        this.prev = prev;

        Map<String, String> licensesMap;

        try {
            licensesMap = new GsonBuilder().create().fromJson(new String(Thread.currentThread().getContextClassLoader().getResourceAsStream("licenses/licenses.json").readAllBytes()), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(Map.Entry<String, String> entry : licensesMap.entrySet()) {
            this.listWidget.widgets.add(new ListEntry(this, entry.getKey(), entry.getValue()));
        }

        this.renderableWidgets.add(listWidget);
        this.renderableWidgets.add(closeButton);
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.gameRenderer.textRenderer.renderTextWithShadow(Language.translate("ui.libraries"), 32, 50, this.getScreenHeight() - 32 - 50);
    }

    @Override
    public void close() {
        this.gameRenderer.setScreen(this.prev);
    }

    @Override
    public void positionContent() {
        this.closeButton.position = new Vector2f(this.getScreenWidth() - 200F - 50F, 50);
        this.closeButton.size = new Vector2f(200F, 50F);
        this.listWidget.position = new Vector2f(50F, 150F);
        this.listWidget.size = new Vector2f(this.getScreenWidth() - 100F, this.getScreenHeight() - 100F - 50F - 32F - 50F - 50F);
    }

    public static class ListEntry extends Widget {
        public String name;
        public String path;
        public Screen parent;

        public ListEntry(Screen parent, String name, String path) {
            this.name = name;
            this.path = path;
            this.size = new Vector2f(400F, 50F);
            this.parent = parent;
        }

        @Override
        public void render(double deltaTime, int mouseX, int mouseY) {
            this.gameRenderer.textRenderer.renderTextWithShadow(this.name, this.position.x + 10F, this.position.y + this.size.y / 2F, false, true);

            boolean mouseHoveringOver = mouseX > this.position.x && mouseX < this.position.x + this.size.x && mouseY > this.position.y && mouseY < this.position.y + this.size.y;
            if(mouseHoveringOver && KeyboardAndMouseInput.hasLeftClicked()) {
                this.gameRenderer.setScreen(new LicenseViewerScreen(this.parent, this.name, this.path));
            }
        }
    }
}
