#version 330 core

in vec2 texCoord;
in vec4 texColor;

out vec4 FragColor;

uniform sampler2D textureSampler;

void main() {
    FragColor = texture(textureSampler, texCoord) * texColor;
}