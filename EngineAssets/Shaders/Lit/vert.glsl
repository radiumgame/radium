#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec2 vertexTextureCoordinate;
layout(location = 2) in vec3 vertexNormal;

out DATA {
    vec3 vpos;
    vec2 vtex;
    vec3 vnormal;

    mat4 model;
    mat4 vm;
    mat4 proj;
    vec4 lsv;

    vec3 cp;
} data;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 lightSpace;
uniform bool depthTestFrame;

uniform bool useNormalMap;
uniform vec3 cameraPosition;

void main() {
    gl_Position = model * vec4(vertexPosition, 1.0f);

    data.model = model;
    data.vm = view;
    data.proj = projection;

    data.vpos = vertexPosition;
    data.vtex = vertexTextureCoordinate;
    data.vnormal = vertexNormal;

    data.lsv = lightSpace * (model * vec4(vertexPosition, 1.0f));

    data.cp = cameraPosition;
}