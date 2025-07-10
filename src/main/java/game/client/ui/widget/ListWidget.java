package game.client.ui.widget;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class ListWidget extends Widget {
    public ArrayList<Widget> widgets = new ArrayList<>();
    public float scroll = 0F;

    @Override
    public void render(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderColoredQuad(this.position, this.size, new Vector4f(0,0,0,0.5F));
        float posY = this.position.y + this.size.y - scroll;

        glEnable(GL_SCISSOR_TEST);
        glScissor((int) this.position.x, (int) this.position.y, (int) this.size.x, (int) this.size.y);

        for(Widget widget : this.widgets) {
            posY = posY - widget.size.y;
            widget.position.y = posY;
            widget.position.x = this.position.x;
            if(widget.position.y > this.position.y - widget.size.y && widget.position.y < this.position.y + this.size.y) {
                widget.render(deltaTime, mouseX, mouseY);
            }
        }

        glDisable(GL_SCISSOR_TEST);

        float scrollBarHeight = Math.min(this.size.y * this.size.y / this.getContentHeight(), this.size.y);
        float scrollPercent = this.scroll / (this.getContentHeight() - this.size.y);
        if(scrollBarHeight != this.size.y) {
            this.uiRenderer.renderTexture(SliderWidget.SLIDER_BAR_UNSELECTED_TEXTURE, new Vector2f(this.position.x + this.size.x - 25, this.position.y + this.size.y - scrollBarHeight + scrollPercent * (this.size.y - scrollBarHeight)), new Vector2f(25, scrollBarHeight));
        }
    }

    @Override
    public void onScroll(double scroll, float mouseX, float mouseY) {
        this.scroll = Math.clamp((float) (this.scroll + scroll * 10), -Math.max(this.getContentHeight() - this.size.y, 0), 0);
    }

    public float getContentHeight() {
        float height = 0F;

        for(Widget widget : this.widgets) {
            height = height + widget.size.y;
        }

        return height;
    }
}
