#version 330 core

out vec4 fragColor;

uniform vec3 color;

void main() {
    fragColor = vec4(color.rgb, 1.0f);
}