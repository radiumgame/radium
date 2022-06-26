#version 330 core

layout (location = 0) in vec3 vertex_position;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    vec4 worldPosition = model * vec4(vertex_position, 1);
    gl_Position = projection * view * worldPosition;
}