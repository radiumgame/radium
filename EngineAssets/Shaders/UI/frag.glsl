#version 330

in vec2 vertex_textureCoord;

out vec4 outColor;

uniform sampler2D tex;

uniform vec3 color;
uniform float alpha;

void main() {
    outColor = texture(tex, vertex_textureCoord) * vec4(color, 1.0f);

    if (outColor.a <= 0.1f) {
        discard;
    }
}