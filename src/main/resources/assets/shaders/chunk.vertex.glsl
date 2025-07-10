#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;
layout (location = 2) in float normalLighting;
layout (location = 3) in float skylight;
layout (location = 4) in float light;

out vec2 TexCoord;
out float faceLighting;

uniform mat4 view;
uniform mat4 projection;
uniform float sunLight;

void main() {
    gl_Position = projection * view * vec4(aPos.x, aPos.y, aPos.z, 1.0);
    TexCoord = aTexCoord;
    faceLighting = normalLighting * max(light, skylight * sunLight);
}