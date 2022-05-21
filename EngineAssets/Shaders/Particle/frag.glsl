#version 330 core

in vec3 vertex_position;
in vec2 vertex_textureCoord;
in vec3 particleColor;

out vec4 fragColor;

uniform sampler2D tex;

void main() {
    fragColor = texture(tex, vertex_textureCoord) * vec4(particleColor, 1.0f);
}