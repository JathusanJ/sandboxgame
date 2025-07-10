package engine.renderer;

import engine.GameEngine;
import engine.input.KeyboardAndMouseInput;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Window {
    public int width;
    public int height;
    private String title;
    private long glfwWindow;
    private int fps = 0;

    private double windowInitTime;

    private ImGuiImplGlfw imGuiImplGlfw;
    private ImGuiImplGl3 imGuiImplGl3;

    public Window(int width, int height, String title){
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public float getFps(){
        return this.fps;
    }

    public int getWindowHeight() {
        return this.height;
    }

    public int getWindowWidth() {
        return this.width;
    }

    public long getGlfwWindow(){
        return this.glfwWindow;
    }

    public void init(){
        GameEngine.logger.info("Using LWJGL {}", Version.getVersion());
        GameEngine.logger.info("Using ImGui {}", ImGui.getVersion());

        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit()){
            throw new IllegalStateException("Couldn't initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        this.glfwWindow = glfwCreateWindow(this.width, this.height, this.title, MemoryUtil.NULL, MemoryUtil.NULL);

        glfwMakeContextCurrent(this.glfwWindow);

        // Enable V-Sync
        glfwSwapInterval(1);

        this.setWindowIcon();

        GL.createCapabilities();

        // Enable blending capabilities (transparency)
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        glfwSetFramebufferSizeCallback(this.glfwWindow, Window::frameBufferSizeCallback);
        glfwSetKeyCallback(this.glfwWindow, KeyboardAndMouseInput::onKeyCallback);
        glfwSetCursorPosCallback(this.glfwWindow, KeyboardAndMouseInput::onCursorPosCallback);
        glfwSetScrollCallback(this.glfwWindow, KeyboardAndMouseInput::onCursorScrollCallback);
        glfwSetMouseButtonCallback(this.glfwWindow, KeyboardAndMouseInput::onMouseButtonCallback);
        glfwSetCharCallback(this.glfwWindow, KeyboardAndMouseInput::onCharacterInput);

        // ImGui setup
        ImGui.createContext();
        ImGuiIO imGuiIO = ImGui.getIO();

        imGuiIO.getFonts().setFreeTypeRenderer(true);
        imGuiIO.getFonts().addFontDefault();
        imGuiIO.getFonts().build();

        imGuiIO.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);

        ImGui.styleColorsDark();

        this.imGuiImplGl3 = new ImGuiImplGl3();
        this.imGuiImplGl3.init("#version 330 core");

        this.imGuiImplGlfw = new ImGuiImplGlfw();
        this.imGuiImplGlfw.init(this.glfwWindow, true);

        this.windowInitTime = glfwGetTime();
    }

    public static void frameBufferSizeCallback(long glfwWindow, int width, int height) {
        glViewport(0,0, width, height);
        GameEngine.getGame().onWindowResize(width, height);
    }

    public void loop(){
        double frameStartTime = glfwGetTime();
        double frameEndTime;
        double deltaTime = 0;
        int fpsCounter = 0;
        double cumulator = 0;

        while(!glfwWindowShouldClose(this.glfwWindow)){
            glfwPollEvents();

            glClearColor(85F / 255F, 136F / 255F, 1F, 1.0F);
            glClear(GL_COLOR_BUFFER_BIT);
            glClear(GL_DEPTH_BUFFER_BIT);

            imGuiImplGl3.newFrame();
            imGuiImplGlfw.newFrame();
            ImGui.newFrame();

            GameEngine.getGame().render(deltaTime);

            ImGui.render();
            imGuiImplGl3.renderDrawData(ImGui.getDrawData());

            glfwSwapBuffers(this.glfwWindow);

            frameEndTime = glfwGetTime();

            deltaTime = frameEndTime - frameStartTime;
            frameStartTime = frameEndTime;
            cumulator += deltaTime;
            fpsCounter++;

            if(cumulator > 1){
                cumulator -= 1;
                this.fps = fpsCounter;
                fpsCounter = 0;
            }

            KeyboardAndMouseInput.updateLastFramePressed();
        }

        GameEngine.getGame().postWindowLoop();

        glfwDestroyWindow(this.glfwWindow);
        glfwTerminate();
    }

    private void setWindowIcon(){
        ByteBuffer image = null;
        try {
            IntBuffer width = BufferUtils.createIntBuffer(1);
            IntBuffer height = BufferUtils.createIntBuffer(1);
            IntBuffer channels = BufferUtils.createIntBuffer(1);

            byte[] data = Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/textures/icon.png").readAllBytes();

            ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
            buffer.put(data);
            buffer.flip();

            image = stbi_load_from_memory(buffer, width, height, channels, 0);

            String failure = stbi_failure_reason();
            if(failure != null) {
                System.err.println("Couldn't load window icon: " + failure);
                stbi_image_free(image);
                return;
            }

            GLFWImage glfwImage = GLFWImage.malloc();
            GLFWImage.Buffer imageBuffer = GLFWImage.malloc(1);
            glfwImage.set(width.get(), height.get(), image);
            imageBuffer.put(0, glfwImage);

            glfwSetWindowIcon(this.glfwWindow, imageBuffer);

            stbi_image_free(image);
        } catch (IOException e) {
            System.err.println("Couldn't load window icon");
            e.printStackTrace();
            stbi_image_free(image);
        }
    }

    public void setWindowTitle(String title){
        this.title = title;
        glfwSetWindowTitle(this.glfwWindow, title);
    }

    public double getTimeSinceWindowInitialization(){
        return glfwGetTime() - this.windowInitTime;
    }

    public void show(){
        glfwShowWindow(this.glfwWindow);
    }

    public void hide(){
        glfwHideWindow(this.glfwWindow);
    }

    public void captureCursor() {
        glfwSetInputMode(this.glfwWindow, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public void freeCursor() {
        glfwSetInputMode(this.glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }
}
