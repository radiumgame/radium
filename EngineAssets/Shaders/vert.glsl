#version 460 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec2 vertexTextureCoordinate;

out vec3 vertex_position;
out vec2 vertex_textureCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    vec4 worldPosition = model * vec4(vertexPosition, 1.0f);
    gl_Position = projection * view * worldPosition;

	vertex_position = vec4(vec4(vertexPosition, 1.f) * model).xyz;
	vertex_textureCoord = vertexTextureCoordinate;
}