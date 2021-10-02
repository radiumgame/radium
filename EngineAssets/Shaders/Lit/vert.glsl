#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec2 vertexTextureCoordinate;
layout(location = 2) in vec3 vertexNormal;

out vec3 vertex_position;
out vec2 vertex_textureCoord;
out vec3 vertex_normal;

out vec4 worldPosition;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    worldPosition = model * vec4(vertexPosition, 1.0f);
    gl_Position = projection * view * worldPosition;

	vertex_position = vec4(vec4(vertexPosition, 1.f) * model).xyz;
	vertex_textureCoord = vertexTextureCoordinate;
	vertex_normal = (model * vec4(vertexNormal, 0.0f)).xyz;
}