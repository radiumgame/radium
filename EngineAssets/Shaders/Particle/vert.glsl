#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec2 vertexTextureCoordinate;

out vec2 texCoord;
out vec3 particleColor;

uniform mat4 modelView;
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 color;

void main() {
    gl_Position = projection * modelView * vec4(vertexPosition.xz, 0, 1.0f);
    //gl_Position = projection * view * model * vec4(vertexPosition.xz, 0, 1.0f);

    particleColor = color;

    texCoord = vertexTextureCoordinate;
}