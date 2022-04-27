#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec2 vertexTextureCoordinate;
layout(location = 2) in vec3 vertexNormal;
layout(location = 3) in vec3 vertexTangent;
layout(location = 4) in vec3 vertexBitangent;

out vec3 position;
out vec2 texture_coordinate;
out vec3 normal;
out vec3 tangent;
out vec3 bitangent;

out vec4 worldPosition;
out mat4 viewMatrix;
out mat3 TBN;

out vec3 eye;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

uniform vec3 cameraPosition;

void main() {
    worldPosition = model * vec4(vertexPosition, 1.0f);
    gl_Position = projection * view * worldPosition;
    viewMatrix = view;

    position = worldPosition.xyz;
    texture_coordinate = vertexTextureCoordinate;
    normal = (model * vec4(vertexNormal, 0.0f)).xyz;
    tangent = (model * vec4(vertexTangent, 0.0f)).xyz;
    bitangent = (model * vec4(vertexTangent, 0.0f)).xyz;

    vec3 T = normalize(vec3(model * vec4(vertexTangent, 0.0)));
    vec3 B = normalize(vec3(model * vec4(vertexBitangent, 0.0)));
    vec3 N = normalize(vec3(model * vec4(vertexNormal, 0.0)));
    TBN = transpose(mat3(T, B, N));

    eye = cameraPosition;
}