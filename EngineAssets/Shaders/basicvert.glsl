#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec2 vertexTextureCoordinate;
layout(location = 2) in vec3 vertexNormal;

out vec3 position;
out vec2 uv;
out vec3 normal;

out vec4 worldPosition;

out mat4 modelMatrix;
out mat4 viewMatrix;
out mat4 projectionMatrix;

out vec3 eye;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

uniform vec3 cameraPosition;

void main() {
    worldPosition = model * vec4(vertexPosition, 1.0f);
    gl_Position = projection * view * worldPosition;
    
    modelMatrix = model;
    viewMatrix = view;
    projectionMatrix = projection;

    position = worldPosition.xyz;
    uv = vertexTextureCoordinate;
    normal = (model * vec4(vertexNormal, 0.0f)).xyz;

    eye = cameraPosition;
}