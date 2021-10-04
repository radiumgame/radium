#version 330 core

in vec3 vertex_position;
in vec2 vertex_textureCoord;
in vec3 vertex_normal;
in vec3 vertex_tangent;

out vec4 outColor;

uniform sampler2D tex;

void main() {
	outColor = texture(tex, vertex_textureCoord);
}