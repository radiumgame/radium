#version 330 core

in vec2 texCoord;
in vec3 particleColor;

out vec4 fragColor;

uniform sampler2D tex;

void main() {
    fragColor = texture(tex, texCoord) * vec4(particleColor, 1.0f);
}