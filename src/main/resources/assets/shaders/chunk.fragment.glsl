#version 330 core

// Eingabe vom Vertex-Shader
in vec2 TexCoord;
in float faceLighting;

// Abgabe an OpenGL
out vec4 color;

// Die Textur, die wir ausgewählt haben
uniform sampler2D texture;

// Der Code, der schliesslich ausgeführt wird
void main() {
    vec4 sampledTexture = texture(texture, TexCoord); // Berechnet die Pixelfarbe von der Texture
    if(sampledTexture.w == 0) discard; // Verhindert, dass ganz transparente Pixeln gezeichnet werden
    color = vec4((sampledTexture * faceLighting).xyz, sampledTexture.w); // Berechnung der finalen Pixelfarbe
}


