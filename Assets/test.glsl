#version 330

in vec2 texCoords;

uniform sampler2D screenTexture;
uniform float time;

out vec4 outColor;

void main() {
outColor = vec4(1.0f, 0, 0, 1);
}

