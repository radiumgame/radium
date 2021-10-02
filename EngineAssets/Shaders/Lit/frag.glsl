#version 330 core

struct Light {

    vec3 position;
    vec3 color;
    float intensity;
    float attenuation;

};

in vec3 vertex_position;
in vec2 vertex_textureCoord;
in vec3 vertex_normal;

in vec4 worldPosition;

out vec4 outColor;

uniform sampler2D tex;

uniform Light lights[256];
uniform float ambient;

vec4 CalculateLight() {
    vec4 finalLight = vec4(0.0f);
    for (int i = 0; i < lights.length(); i++) {
        if (lights[i].intensity == 0) break;

        vec3 toLightVector = lights[i].position - worldPosition.xyz;
        vec3 unitNormal = normalize(vertex_normal);
        vec3 unitLightVector = normalize(toLightVector);
        float nDotl = dot(unitNormal, unitLightVector);
        float brightness = max(nDotl, 0.0);
        vec3 diffuse = brightness * lights[i].color;

        float distanceFromLight = length(lights[i].position - vertex_position);
        float attenuation = 1.f / (1.f + lights[i].attenuation * distanceFromLight * 0.0075f * (distanceFromLight * distanceFromLight));

        diffuse *= attenuation;

        finalLight += (((vec4(diffuse, 1.f) * lights[i].intensity)) * attenuation * vec4(lights[i].color, 1.f));
    }

    return max(finalLight, ambient);
}

void main() {
	outColor = texture(tex, vertex_textureCoord) * CalculateLight();
}