#version 330 core

in vec4 FragPos;

out vec4 color;

uniform vec3 lightPos;
uniform float farPlane;

void main() {
    gl_FragDepth = length(FragPos.xyz - lightPos) / farPlane;
    color = vec4(1);
}