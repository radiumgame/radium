#version 330 core

in vec3 VertexPosition;

uniform mat4 view;
uniform mat4 projection;

out mat4 vw;
out mat4 proj;

out vec3 nearPoint;
out vec3 farPoint;

vec3 UnprojectPoint(float x, float y, float z) {
    mat4 viewInv = inverse(view);
    mat4 projInv = inverse(projection);
    vec4 unprojectedPoint = viewInv * projInv * vec4(x, y, z, 1.0);
    return unprojectedPoint.xyz / unprojectedPoint.w;
}

void main() {
    vec3 p = VertexPosition;
    nearPoint = UnprojectPoint(p.x, p.y, 0.0).xyz;
    farPoint = UnprojectPoint(p.x, p.y, 1.0).xyz;

    vw = view;
    proj = projection;

    gl_Position = vec4(p, 1.0);
}