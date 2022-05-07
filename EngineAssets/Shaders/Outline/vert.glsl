#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec2 vertexTextureCoordinate;
layout(location = 2) in vec3 vertexNormal;
layout(location = 3) in vec3 vertexTangent;
layout(location = 4) in vec3 vertexBitangent;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform float outline;

void main() {
    vec3 crntPos = vec3(model * vec4(vertexPosition + vertexNormal * outline, 1.0));
    gl_Position = projection * view * vec4(crntPos, 1.0);
}