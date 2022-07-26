#version 330 core

layout (location = 0) in vec3 vpos;

uniform mat4 model;

void main() {
    gl_Position = model * vec4(vpos, 1);
}