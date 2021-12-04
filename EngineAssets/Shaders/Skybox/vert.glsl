#version 330

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec2 vertexTextureCoordinate;
layout(location = 2) in vec3 vertexNormal;

out vec3 vertex_textureCoord;

uniform mat4 view;
uniform mat4 projection;

void main() {
    gl_Position = projection * view * vec4(vertexPosition, 1.0f);

    vertex_textureCoord = vertexPosition;
}