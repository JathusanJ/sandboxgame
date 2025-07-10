package engine.renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private int textureId;
    private int height;
    private int width;

    public Texture(String filePath){
        this.textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Make the image repeat when the UV coordinates are larger than the image
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Use nearest neighbour for scaling images
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        stbi_set_flip_vertically_on_load(true);

        ByteBuffer image;

        try {
            byte[] data = Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/" + filePath).readAllBytes();

            ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
            buffer.put(data);
            buffer.flip();

            image = stbi_load_from_memory(buffer, width, height, channels, 0);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't load texture \"assets/"+ filePath +"\":", e);
        }

        if(image != null){
            this.width = width.get();
            this.height = height.get();

            if(channels.get(0) == 4){
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            } else if(channels.get(0) == 3){
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.width, this.height, 0, GL_RGB, GL_UNSIGNED_BYTE, image);
            } else {
                throw new IllegalStateException("Couldn't load image: The image doesn't have exactly 4 or 3 channels");
            }
            glGenerateMipmap(GL_TEXTURE_2D);
        } else {
            throw new IllegalStateException("Couldn't load image: " + stbi_failure_reason());
        }

        stbi_image_free(image);
    }

    public Texture(int textureId) {
        this.textureId = textureId;
    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D, this.textureId);
    }

    public void bindAtSlot(int slot){
        glActiveTexture(GL_TEXTURE + slot);
        this.bind();
    }

    public void unbind(){
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth(){
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void delete() {
        glDeleteTextures(this.textureId);
    }
}
