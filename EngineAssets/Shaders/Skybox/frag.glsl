#version 330

in vec3 vertex_textureCoord;

out vec4 outColor;

uniform samplerCube tex;

void main() {
    outColor = texture(tex, vertex_textureCoord);
}