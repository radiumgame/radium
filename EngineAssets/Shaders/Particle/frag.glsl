#version 330 core

in vec2 texCoord;
in vec3 particleColor;

out vec4 fragColor;

uniform sampler2D tex;
uniform bool alphaIsTransparency;

void main() {
    fragColor = texture(tex, texCoord) * vec4(particleColor, 1.0f);

    if (!alphaIsTransparency) {
        float a = (fragColor.r + fragColor.g + fragColor.b) / 3.0f;
        fragColor.a = a;
    }
}