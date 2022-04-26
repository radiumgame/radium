in vec3 position;
in vec2 texture_coordinate;
in vec3 normal;
in vec3 tangent;
in vec3 bitangent;

in vec4 worldPosition;
in mat4 viewMatrix;
in mat3 TBN;

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

uniform float time;
uniform float deltaTime;
uniform vec3 color;

uniform vec3 viewDirection;