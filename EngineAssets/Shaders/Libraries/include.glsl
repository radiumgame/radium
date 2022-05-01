in vec3 position;
in vec2 uv;
in vec3 normal;
in vec3 tangent;
in vec3 bitangent;

in vec4 worldPosition;
in mat3 TBN;

in mat4 modelMatrix;
in mat4 viewMatrix;
in mat4 projectionMatrix;

in vec3 eye;

struct GameObject {

    vec3 localPosition;
    vec3 localRotation;
    vec3 localScale;

    vec3 position;
    vec3 rotation;
    vec3 scale;

};

struct Color {

    float r;
    float g;
    float b;
    float a;

};

struct Light {

    vec3 position;
    vec3 color;
    float intensity;
    float attenuation;
    int lightType;

};

struct Material {

    float shineDamper;
    float reflectivity;

};

uniform GameObject gameObject;
uniform sampler2D screen;

uniform float time;
uniform float deltaTime;
uniform vec2 resolution;

uniform vec3 viewDirection;