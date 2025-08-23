package game.client.ui.widget;

import engine.renderer.Camera;
import engine.renderer.Texture;
import game.client.SandboxGame;
import game.shared.world.creature.Player;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.*;

public class PlayerInUIWidget extends Widget {
    public Camera camera;
    public Player player;
    public static boolean framebufferInitialized = false;
    public static int fbo;
    public static int rbo;
    public static int texture;

    public PlayerInUIWidget(Player player, Camera camera) {
        this.player = player;
        this.camera = camera;

        if(!framebufferInitialized) {
            fbo = glGenFramebuffers();
            glBindFramebuffer(GL_FRAMEBUFFER, fbo);

            glViewport(0, 0, 300, 300);

            texture = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texture);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 300, 300, 0, GL_RGBA, GL_UNSIGNED_BYTE, MemoryUtil.NULL);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);

            rbo = glGenRenderbuffers();
            glBindRenderbuffer(GL_RENDERBUFFER, rbo);
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, (int) 300, (int) 300);
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);

            if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
                SandboxGame.getInstance().logger.error("Failed to render player in UI: {}", glCheckFramebufferStatus(GL_FRAMEBUFFER));
            }

            glBindFramebuffer(GL_FRAMEBUFFER, 0);

            glViewport(0, 0, SandboxGame.getInstance().getWindow().getWindowWidth(), SandboxGame.getInstance().getWindow().getWindowHeight());

            framebufferInitialized = true;
        }
    }

    @Override
    public void render(double deltaTime, int mouseX, int mouseY) {
        glViewport(0, 0, 300, 300);
        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo);
        glClearColor(0F, 0F, 0F, 0F);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        this.gameRenderer.creatureRenderer.shader.use();

        Matrix4f view = this.camera.getViewMatrix();
        Matrix4f projection = this.camera.getProjectionMatrix();

        this.gameRenderer.creatureRenderer.shader.uploadMatrix4f("view", view);
        this.gameRenderer.creatureRenderer.shader.uploadMatrix4f("projection", projection);

        glEnable(GL_DEPTH_TEST);

        this.gameRenderer.creatureRenderer.render(this.player, 0F, 1F);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glDisable(GL_DEPTH_TEST);

        glViewport(0, 0, SandboxGame.getInstance().getWindow().getWindowWidth(), SandboxGame.getInstance().getWindow().getWindowHeight());

        this.uiRenderer.renderTexture(new Texture(texture), this.position, new Vector2f(300, 300));
    }
}
