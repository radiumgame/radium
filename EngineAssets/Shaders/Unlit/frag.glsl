#version 330 core

in vec3 vertex_position;
in vec2 vertex_textureCoord;
in vec3 vertex_normal;

in vec4 worldPosition;
in mat4 viewMatrix;

out vec4 outColor;

uniform sampler2D tex;
uniform vec3 color;

uniform float outlineWidth;
uniform vec3 outlineColor;
uniform bool outline;

void main() {
	outColor = texture(tex, vertex_textureCoord) * vec4(color, 1.0f);

	if (outline) {
		if (dot(normalize((inverse(viewMatrix) * vec4(0, 0, 0, 1)).xyz - worldPosition.xyz), vertex_normal) < outlineWidth) {
			outColor = vec4(outlineColor, 1);
		}
	}
}