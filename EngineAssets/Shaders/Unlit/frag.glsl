#version 330 core

in vec3 vertex_position;
in vec2 vertex_textureCoord;
in vec3 vertex_normal;

in vec4 worldPosition;
in mat4 viewMatrix;

out vec4 outColor;

uniform sampler2D tex;
uniform vec3 color;

void main() {
	outColor = texture(tex, vertex_textureCoord) * vec4(color, 1.0f);
}