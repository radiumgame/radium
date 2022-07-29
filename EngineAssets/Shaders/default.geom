#version 330

layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

in DATA {
    vec3 vpos;
    vec2 vtex;
    vec3 vnormal;

    mat4 model;
    mat4 vm;
    mat4 proj;
    vec4 lsv;

    vec3 cp;
} data[];

out vec3 vertex_position;
out vec3 tangent_vertex_position;
out vec2 vertex_textureCoord;
out vec3 vertex_normal;

out vec3 worldPosition;
out vec3 tangentPosition;
out mat4 viewMatrix;
out vec4 lightSpaceVector;

out vec3 camPos;
out vec3 tangentCamPos;

out mat3 TBN;

void main() {
    vec3 edge0 = gl_in[1].gl_Position.xyz - gl_in[0].gl_Position.xyz;
    vec3 edge1 = gl_in[2].gl_Position.xyz - gl_in[0].gl_Position.xyz;
    vec2 deltaUV0 = data[1].vtex - data[0].vtex;
    vec2 deltaUV1 = data[2].vtex - data[0].vtex;
    float invDet = 1.0f / (deltaUV0.x * deltaUV1.y - deltaUV1.x * deltaUV0.y);
    vec3 tangent = vec3(invDet * (deltaUV1.y * edge0 - deltaUV0.y * edge1));
    vec3 bitangent = vec3(invDet * (-deltaUV1.x * edge0 + deltaUV0.x * edge1));
    vec3 T = normalize(vec3(data[0].model * vec4(tangent, 0.0f)));
    vec3 B = normalize(vec3(data[0].model * vec4(bitangent, 0.0f)));
    vec3 N = normalize(vec3(data[0].model * vec4(cross(edge1, edge0), 0.0f)));
    mat3 newTBN = mat3(T, B, N);
    newTBN = transpose(newTBN);

    gl_Position = data[0].proj * data[0].vm * gl_in[0].gl_Position;
    vertex_position = (data[0].model * vec4(data[0].vpos, 1)).xyz;
    tangent_vertex_position = newTBN * data[0].vpos;
    vertex_textureCoord = data[0].vtex;
    vertex_normal = (data[0].model * vec4(data[0].vnormal, 1)).xyz;
    worldPosition = gl_in[0].gl_Position.xyz;
    tangentPosition = newTBN * gl_in[0].gl_Position.xyz;
    viewMatrix = data[0].vm;
    lightSpaceVector = data[0].lsv;
    camPos = data[0].cp;
    tangentCamPos = newTBN * data[0].cp;
    TBN = newTBN;
    EmitVertex();

    gl_Position = data[1].proj * data[1].vm * gl_in[1].gl_Position;
    vertex_position = (data[1].model * vec4(data[1].vpos, 1)).xyz;
    tangent_vertex_position = newTBN * data[1].vpos;
    vertex_textureCoord = data[1].vtex;
    vertex_normal = (data[1].model * vec4(data[1].vnormal, 1)).xyz;
    worldPosition = gl_in[1].gl_Position.xyz;
    tangentPosition = newTBN * gl_in[1].gl_Position.xyz;
    viewMatrix = data[1].vm;
    lightSpaceVector = data[1].lsv;
    camPos = data[1].cp;
    tangentCamPos = newTBN * data[1].cp;
    TBN = newTBN;
    EmitVertex();

    gl_Position = data[2].proj * data[2].vm * gl_in[2].gl_Position;
    vertex_position = (data[2].model * vec4(data[2].vpos, 1)).xyz;
    tangent_vertex_position = newTBN * data[2].vpos;
    vertex_textureCoord = data[2].vtex;
    vertex_normal = (data[2].model * vec4(data[2].vnormal, 1)).xyz;
    worldPosition = gl_in[2].gl_Position.xyz;
    tangentPosition = newTBN * gl_in[2].gl_Position.xyz;
    viewMatrix = data[2].vm;
    lightSpaceVector = data[2].lsv;
    camPos = data[2].cp;
    tangentCamPos = newTBN * data[2].cp;
    TBN = newTBN;
    EmitVertex();

    EndPrimitive();
}