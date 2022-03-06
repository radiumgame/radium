#version 330 core

in vec2 texCoords;

out vec4 outColor;

uniform sampler2D screenTexture;
uniform bool playing;

// Effects
uniform bool invert;
uniform bool tint;
uniform bool celShading;

// Effect Settings
uniform vec3 tintColor;
uniform int celLevels;

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
    if (tint) {
        outColor.rgb *= tintColor;
    }
    if (celShading) {
        vec3 originalCol = vec3(outColor.rgb);
        float brightness = (outColor.r + outColor.g + outColor.b) / 3.0f;
        float level = floor(brightness * celLevels);
        outColor.rgb = originalCol * vec3(level);
    }
}