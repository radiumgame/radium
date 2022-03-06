#version 330 core

in vec2 texCoords;

out vec4 outColor;

uniform sampler2D screenTexture;
uniform bool playing;

// Effects
uniform bool invert;

void main()
{
    if (!playing) {
        outColor = texture(screenTexture, texCoords);
        return;
    }

    outColor = texture(screenTexture, texCoords);
    if (invert) {
        outColor.rgb = vec3(1.0f) - outColor.rgb;
    }
}