#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec2 vertexTextureCoordinate;

out vec2 texCoord0;
out vec2 texCoord1;
out vec2 texCoord2;
out float blend;

out vec3 particleColor;

uniform mat4 modelView;
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 color;

uniform vec2 texOffset1;
uniform vec2 texOffset2;
uniform vec2 texCoordData;

void main() {
    gl_Position = projection * modelView * vec4(vertexPosition.xz, 0, 1.0f);
    //gl_Position = projection * view * model * vec4(vertexPosition.xz, 0, 1.0f);

    particleColor = color;

    vec2 texCoord = vertexTextureCoordinate;
    texCoord.y = 1.0f - texCoord.y;
    texCoord /= texCoordData.x;
    texCoord1 = texCoord + texOffset1;
    texCoord2 = texCoord + texOffset2;
    blend = texCoordData.y;

    texCoord0 = vertexTextureCoordinate;
}