#version 330 core

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 tex_coords;

out vec2 texCoords;

out vec2 blurTextureCoordsh[11];
out vec2 blurTextureCoordsv[11];

uniform vec2 targetSize;

void main()
{
    gl_Position = vec4(position.x, position.y, 0.0, 1.0);
    vec2 centerCoords = position * 0.5f + 0.5f;
    texCoords = tex_coords;

    vec2 pixelSize = 1.0f / targetSize;
    for (int i = 0; i <= 5; i++) {
        blurTextureCoordsh[i + 5] = centerCoords + vec2(pixelSize.x * i, 0.0f);
        blurTextureCoordsv[i + 5] = centerCoords + vec2(0.0f, pixelSize.y * i);
    }
}