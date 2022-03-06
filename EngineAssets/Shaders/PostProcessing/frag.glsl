#version 330 core

in vec2 texCoords;

out vec4 outColor;

uniform sampler2D screenTexture;
uniform bool playing;

void main()
{
    if (!playing) {
        outColor = texture(screenTexture, texCoords);
        return;
    }

    vec4 col = texture(screenTexture, texCoords);
    col = col.rbga;

    outColor = col;
}