#version 330

in vec2 position;
in vec2 textureCoordinate;

out vec2 vertex_textureCoord;

uniform mat4 model;

void main() {
    gl_Position = model * vec4(position, 0, 1);

    vertex_textureCoord = textureCoordinate;
}