package engine.renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform1iv;

public class Shader {
    private int shaderProgramId;

    public Shader(String vertexFilePath, String fragmentFilePath){
        String vertexSource;
        String fragmentSource;
        try {
            vertexSource = new String(Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/" + vertexFilePath).readAllBytes());
            fragmentSource = new String(Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/" + fragmentFilePath).readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read shader source files", e);
        }

        int vertexId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexId, vertexSource);
        glCompileShader(vertexId);

        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);

        if(success == GL_FALSE){
            int length = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);

            throw new IllegalStateException("Failed vertex shader compilation: \n" + glGetShaderInfoLog(vertexId, length));
        }

        int fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentId, fragmentSource);
        glCompileShader(fragmentId);

        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);

        if(success == GL_FALSE){
            int length = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);

            throw new IllegalStateException("Failed fragment shader compilation: \n" + glGetShaderInfoLog(fragmentId, length));
        }

        this.shaderProgramId = glCreateProgram();
        glAttachShader(this.shaderProgramId, vertexId);
        glAttachShader(this.shaderProgramId, fragmentId);
        glLinkProgram(this.shaderProgramId);

        success = glGetProgrami(this.shaderProgramId, GL_LINK_STATUS);

        if(success == GL_FALSE){
            int length = glGetProgrami(this.shaderProgramId, GL_INFO_LOG_LENGTH);

            throw new IllegalStateException("Failed shader program linking: \n" + glGetProgramInfoLog(this.shaderProgramId, length));
        }
    }

    public void use(){
        glUseProgram(this.shaderProgramId);
    }

    public void detach(){
        glUseProgram(0);
    }

    public void uploadMatrix4f(String variableName, Matrix4f matrix){
        int variableLocation = glGetUniformLocation(this.shaderProgramId, variableName);
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        matrix.get(matrixBuffer);
        use();
        glUniformMatrix4fv(variableLocation, false, matrixBuffer);
    }

    public void uploadMatrix3f(String variableName, Matrix3f matrix){
        int variableLocation = glGetUniformLocation(this.shaderProgramId, variableName);
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(9);
        matrix.get(matrixBuffer);
        use();
        glUniformMatrix3fv(variableLocation, false, matrixBuffer);
    }

    public void uploadVector4f(String variableName, Vector4f vector){
        int variableLocation = glGetUniformLocation(this.shaderProgramId, variableName);
        use();
        glUniform4f(variableLocation, vector.x, vector.y, vector.z, vector.w);
    }

    public void uploadVector3f(String variableName, Vector3f vector){
        int variableLocation = glGetUniformLocation(this.shaderProgramId, variableName);
        use();
        glUniform3f(variableLocation, vector.x, vector.y, vector.z);
    }

    public void uploadVector2f(String variableName, Vector2f vector){
        int variableLocation = glGetUniformLocation(this.shaderProgramId, variableName);
        use();
        glUniform2f(variableLocation, vector.x, vector.y);
    }

    public void uploadFloat(String variableName, float value){
        int variableLocation = glGetUniformLocation(this.shaderProgramId, variableName);
        use();
        glUniform1f(variableLocation, value);
    }

    public void uploadInt(String variableName, int value){
        int variableLocation = glGetUniformLocation(this.shaderProgramId, variableName);
        use();
        glUniform1i(variableLocation, value);
    }

    public void uploadIntArray(String variableName, int[] array){
        int variableLocation = glGetUniformLocation(this.shaderProgramId, variableName);
        use();
        glUniform1iv(variableLocation, array);
    }

    public void delete() {
        glDeleteShader(this.shaderProgramId);
    }
}