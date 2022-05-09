#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec2 vertexTextureCoordinate;
layout(location = 2) in vec3 vertexNormal;
layout(location = 3) in vec3 vertexTangent;
layout(location = 4) in vec3 vertexBitangent;

out vec3 vertex_position;
out vec2 vertex_textureCoord;
out vec3 vertex_normal;
out vec3 vertex_tangent;
out vec3 vertex_bitangent;

out vec4 worldPosition;
out mat4 viewMatrix;
out mat3 TBN;
out vec4 lightSpaceVector;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 lightSpace;
uniform bool depthTestFrame;

void main() {
    worldPosition = model * vec4(vertexPosition, 1.0f);

    if (depthTestFrame) {
        gl_Position = lightSpace * worldPosition;
    } else {
        gl_Position = projection * view * worldPosition;
    }

    viewMatrix = view;

    vertex_position = worldPosition.xyz;
    vertex_textureCoord = vertexTextureCoordinate;
    vertex_normal = (model * vec4(vertexNormal, 0.0f)).xyz;
    vertex_tangent = (model * vec4(vertexTangent, 0.0f)).xyz;
    vertex_bitangent = (model * vec4(vertexTangent, 0.0f)).xyz;
    lightSpaceVector = lightSpace * worldPosition;

    vec3 T = normalize(vec3(model * vec4(vertexTangent, 0.0)));
    vec3 B = normalize(vec3(model * vec4(vertexBitangent, 0.0)));
    vec3 N = normalize(vec3(model * vec4(vertexNormal, 0.0)));
    TBN = transpose(mat3(T, B, N));
}