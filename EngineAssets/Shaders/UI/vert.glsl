#version 330

in vec2 position;
in vec2 textureCoordinate;

out vec2 vertex_textureCoord;

uniform mat4 model;
uniform mat4 projection;

void main() {
    gl_Position = projection * model * vec4(position, 0, 1);

    vertex_textureCoord = textureCoordinate;
}