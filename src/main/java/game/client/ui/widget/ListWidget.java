package game.client.ui.widget;

import engine.input.KeyboardAndMouseInput;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.opengl.GL11.*;

public class ListWidget extends Widget {
    public ArrayList<Widget> widgets = new ArrayList<>();
    public float scroll = 0F;
    public boolean isMovingScrollbar = false;
    public int lastMouseY = 0;

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

        float scrollBarXStart = this.position.x + this.size.x - 25;
        float scrollBarXEnd = this.position.x + this.size.x;
        float scrollBarYStart = this.position.y + this.size.y - scrollBarHeight + scrollPercent * (this.size.y - scrollBarHeight);
        float scrollBarYEnd = this.position.y + this.size.y + scrollPercent * (this.size.y - scrollBarHeight);

        boolean mouseOverScrollBar = mouseX >= scrollBarXStart && mouseX <= scrollBarXEnd && mouseY >= scrollBarYStart && mouseY <= scrollBarYEnd;

        if(mouseOverScrollBar && KeyboardAndMouseInput.pressingMouseButton(GLFW_MOUSE_BUTTON_1)) {
            if (!this.isMovingScrollbar) {
                this.lastMouseY = mouseY;
            }
            this.isMovingScrollbar = true;
        }

        if(scrollBarHeight != this.size.y) {
            if(mouseOverScrollBar || this.isMovingScrollbar) {
                this.uiRenderer.renderTexture(SliderWidget.SLIDER_BAR_SELECTED_TEXTURE, new Vector2f(scrollBarXStart, scrollBarYStart), new Vector2f(25, scrollBarHeight));
            } else {
                this.uiRenderer.renderTexture(SliderWidget.SLIDER_BAR_UNSELECTED_TEXTURE, new Vector2f(scrollBarXStart, scrollBarYStart), new Vector2f(25, scrollBarHeight));
            }
        }

        if(this.isMovingScrollbar) {
            if(!KeyboardAndMouseInput.pressingMouseButton(GLFW_MOUSE_BUTTON_1)) {
                this.isMovingScrollbar = false;
                return;
            }

            // Don't know what number I have to multiply by to get it working properly, but this does the job well enough
            this.onScroll((mouseY - this.lastMouseY) * ((this.getContentHeight() - this.size.y) / this.size.y), mouseX, mouseY);
            this.lastMouseY = mouseY;
        }
    }

    @Override
    public void onScroll(double scroll, float mouseX, float mouseY) {
        this.scroll = Math.clamp((float) (this.scroll + scroll), -Math.max(this.getContentHeight() - this.size.y, 0), 0);
    }

    public float getContentHeight() {
        float height = 0F;

        for(Widget widget : this.widgets) {
            height = height + widget.size.y;
        }

        return height;
    }
}
