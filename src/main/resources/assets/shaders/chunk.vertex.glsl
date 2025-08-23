#version 330 core

// Die Daten des Eckpunktes
layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoordinates;
layout (location = 2) in float blockFaceLighting;
layout (location = 3) in float skylight;
layout (location = 4) in float light;

// Abgabe an den Fragment-Shader
out vec2 TexCoord;
out float faceLighting;

// Shader Parametern
uniform mat4 view;
uniform mat4 projection;
uniform float sunLight;

// Der Code, der schliesslich ausgeführt wird
void main() {
    gl_Position = projection * view * vec4(position.x, position.y, position.z, 1.0); // Transformiert die Position des Eckpunktes
    TexCoord = textureCoordinates; // Gibt die Texturkoordinaten weiter
    faceLighting = blockFaceLighting * max(light, skylight * sunLight); // Berechnet die finale Seitenbeleuchtungswert
}

