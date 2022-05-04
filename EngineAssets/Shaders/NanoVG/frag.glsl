#version 330 core

in vec2 texCoords;
out vec4 fragColor;

uniform sampler2D screen;
uniform sampler2D ui;

void main() {
    fragColor = texture(ui, texCoords);
}