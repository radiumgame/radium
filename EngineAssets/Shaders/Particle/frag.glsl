#version 330 core

in vec3 vertex_position;
in vec2 vertex_textureCoord;
in vec3 particleColor;

out vec4 outColor;

uniform sampler2D tex;

void main() {
    outColor = texture(tex, vertex_textureCoord) * vec4(particleColor, 1.0f);
    outColor = vec4(1);

    if (outColor.a <= 0.0f) {
        discard;
    }
}