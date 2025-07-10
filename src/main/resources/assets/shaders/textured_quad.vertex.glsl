#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTexCoord;

uniform mat4 projection;
uniform mat4 model;
uniform mat4 view;

out vec2 texCoord;
out vec4 texColor;

void main() {
    texCoord = aTexCoord;
    texColor = aColor;
    gl_Position = projection * model * view * vec4(aPos, 1.0);
}