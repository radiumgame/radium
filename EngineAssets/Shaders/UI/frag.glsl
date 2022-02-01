#version 330

in vec2 vertex_textureCoord;

out vec4 outColor;

uniform sampler2D tex;

uniform vec3 color;
uniform float alpha;

void main() {
    if (alpha <= 0.4f) {
        discard;
    }

    outColor = vec4(texture(tex, vertex_textureCoord).rgb * color, alpha);
}