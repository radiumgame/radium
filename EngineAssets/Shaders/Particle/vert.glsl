#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec2 vertexTextureCoordinate;
layout (location = 2) in mat4 modelView;
layout (location = 6) in vec4 texOffsets;
layout (location = 7) in vec3 color;
layout (location = 8) in float blendFactor;

out vec2 texCoord0;
out vec2 texCoord1;
out vec2 texCoord2;
out float blend;

out vec3 particleColor;

uniform mat4 projection;
uniform float numberOfRows;

void main() {
    gl_Position = projection * modelView * vec4(vertexPosition.xz, 0, 1.0f);

    particleColor = color;

    vec2 texCoord = vertexTextureCoordinate;
    texCoord.y = 1.0f - texCoord.y;
    texCoord /= numberOfRows;
    texCoord1 = texCoord + texOffsets.xy;
    texCoord2 = texCoord + texOffsets.zw;
    blend = blendFactor;

    texCoord0 = vertexTextureCoordinate;
}