#version 330 core

in vec2 TexCoord;

out vec4 FragColor;

uniform sampler2D textureSampler;

void main() {
    vec4 sampledTexture = texture(textureSampler, TexCoord);
    if(sampledTexture.w == 0) discard;
    FragColor = sampledTexture;
}