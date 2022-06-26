#version 330 core

out vec4 fragColor;
uniform vec3 id;

void main() {
    fragColor = vec4(id, 1);
}