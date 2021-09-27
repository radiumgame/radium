#version 460 core

in vec3 vertex_position;
in vec2 vertex_textureCoord;

out vec4 outColor;

uniform sampler2D tex;

void main() {
	outColor = texture(tex, vertex_textureCoord);
}