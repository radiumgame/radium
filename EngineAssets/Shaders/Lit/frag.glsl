#version 330 core

struct Light {

    vec3 position;
    vec3 color;
    float intensity;
    float attenuation;
    int lightType;

};

struct Material {

    float reflectivity;
    float shineDamper;

    float metallic;
    float alpha;
    float baseReflectivity;

};

in vec3 vertex_position;
in vec2 vertex_textureCoord;
in vec3 vertex_normal;
in vec3 vertex_tangent;
in vec3 vertex_bitangent;

in vec4 worldPosition;
in mat4 viewMatrix;
in mat3 TBN;
in vec4 lightSpaceVector;

out vec4 outColor;

uniform sampler2D tex;
uniform sampler2D normalMap;
uniform sampler2D specularMap;
uniform sampler2D lightDepth;

uniform Light lights[512];
uniform int lightCount;
uniform float ambient;
uniform float gamma;
uniform float exposure;

uniform bool useBlinn;
uniform bool useGammaCorrection;
uniform bool HDR;
uniform bool specularLighting;
uniform bool useNormalMap;
uniform bool useSpecularMap;

uniform int lightCalcMode;

uniform Material material;
uniform vec3 color;

uniform vec3 cameraPosition;

uniform bool depthTestFrame;

const float PI = 3.14159265359;

// PBR
// GGX/Trowbridge-Reitz Distribution
float D(float alpha, vec3 N, vec3 H) {
    float numerator = pow(alpha, 2.0);
    float NdotH = max(dot(N, H), 0);
    float denominator = PI * pow(pow(NdotH, 2.0) * (pow(alpha, 2.0) - 1.0) + 1.0, 2.0);
    denominator = max(denominator, 0.000001);
    return numerator / denominator;
}

// Schlick-Beckmann Geometry Shadowing
float G1(float alpha, vec3 N, vec3 X) {
    float numerator = max(dot(N, X), 0.0);

    float k = alpha / 2.0;
    float denominator = max(dot(N, X), 0.0) * (1.0 - k) + k;
    denominator = max(denominator, 0.000001);

    return numerator / denominator;
}

// Smith Model
float G(float alpha, vec3 N, vec3 V, vec3 L) {
    return G1(alpha, N, V) * G1(alpha, N, L);
}

// Fresnel-Schlick Approximation
vec3 F(vec3 F0, vec3 V, vec3 H) {
    return F0 + (vec3(1.0) - F0) * pow(1.0 - dot(V, H), 5.0);
}

// Regular Lighting
float CalculateShadow(int lightIndex) {
    vec3 projectionCoords = lightSpaceVector.xyz / lightSpaceVector.w;
    projectionCoords = projectionCoords * 0.5f + 0.5f;
    float closestDepth = texture(lightDepth, projectionCoords.xy).r;
    float currentDepth = projectionCoords.z;

    vec3 toLightVector = lights[lightIndex].position - worldPosition.xyz;
    vec3 lightDirection = -normalize(toLightVector);
    float bias = max(0.05f * (1.0f - dot(vertex_normal, lightDirection)), 0.005f);

    float shadow = currentDepth - bias > closestDepth ? 1.0f : 0.0f;
    vec2 texelSize = 1.0 / textureSize(lightDepth, 0);
    for(int x = -1; x <= 1; x++)
    {
        for(int y = -1; y <= 1; y++)
        {
            float pcfDepth = texture(lightDepth, projectionCoords.xy + vec2(x, y) * texelSize).r;
            shadow += currentDepth - bias > pcfDepth ? 1.0 : 0.0;
        }
    }
    shadow /= 9.0f;

    if (projectionCoords.z > 1.0f) {
        shadow = 0.0f;
    }

    return shadow;
}

vec3 CalculateNormal() {
    if (!useNormalMap) return vertex_normal;

    vec3 newNormal = texture(normalMap, vertex_textureCoord).rgb;
    newNormal = newNormal * 2.0 - 1.0;
    newNormal = normalize(TBN * newNormal);

    return newNormal;
}

vec4 CalculateLight() {
    vec3 useNormal = CalculateNormal();
    vec3 finalLight = vec3(0.0f);
    for (int i = 0; i < lightCount; i++) {
        vec3 toLightVector = lights[i].position - worldPosition.xyz;
        vec3 toCameraVector = (inverse(viewMatrix) * vec4(0, 0, 0, 1)).xyz - worldPosition.xyz;
        vec3 unitNormal = normalize(useNormal);

        vec3 unitLightVector;
        if (useNormalMap) {
            unitLightVector = TBN * normalize(toLightVector);
        } else {
            unitLightVector = normalize(toLightVector);
        }

        vec3 unitCameraVector;
        if (useNormalMap) {
            unitCameraVector = TBN * normalize(toCameraVector);
        } else {
            unitCameraVector = normalize(toCameraVector);
        }

        vec3 lightDirection = -unitLightVector;
        vec3 halfwayDirection = normalize(unitLightVector + unitCameraVector);

        vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
        float specularFactor = dot(useBlinn ? halfwayDirection : reflectedLightDirection, unitCameraVector);
        specularFactor = max(specularFactor, 0.0f);
        float dampedFactor = pow(specularFactor, material.shineDamper);
        vec3 specular = dampedFactor * material.reflectivity * lights[i].color;

        float nDotl = dot(unitNormal, unitLightVector);
        float brightness = max(nDotl, 0.0);
        vec3 diffuse = brightness * lights[i].color;

        if (useSpecularMap) {
            vec4 specularMapInfo = texture(specularMap, vertex_textureCoord);
            specular *= specularMapInfo.rgb;
        }

        if (lights[i].lightType == 1) {
            float distanceFromLight = length(lights[i].position - vertex_position);
            float attenuation = 1.f / (1.f + lights[i].attenuation * distanceFromLight * 0.0075f * (distanceFromLight * distanceFromLight));
            diffuse *= attenuation;
            specular *= attenuation;

            finalLight += (ambient + (1.0f - CalculateShadow(0)) * ((diffuse + (specularLighting ? specular : vec3(0))))) * lights[i].color * lights[i].intensity * attenuation;
        } else {
            finalLight += (ambient + (1.0f - CalculateShadow(0)) * ((diffuse + (specularLighting ? specular : vec3(0))))) * lights[i].color * lights[i].intensity;
        }
    }

    return vec4(max(finalLight, ambient), 1.0f);
}

vec4 PBR(vec4 col) {
    vec3 nor = CalculateNormal();
    vec3 finalLight = vec3(0.0f);
    for (int i = 0; i < lightCount; i++) {
        vec3 N = normalize(nor);
        vec3 V = normalize(cameraPosition - worldPosition.xyz);

        vec3 L = normalize(lights[i].position - worldPosition.xyz);

        vec3 H = normalize(V + L);

        vec3 Ks = F(vec3(material.baseReflectivity), V, H);
        vec3 Kd = (vec3(1.0f) - Ks) * (1.0 - material.metallic);

        vec3 lambert = col.rgb / vec3(PI);

        vec3 cookTorranceNumerator = D(material.alpha, N, H) * G(material.alpha, N, V, L) * F(vec3(material.baseReflectivity), V, H);
        float cookTorranceDenominator = 4.0 * max(dot(V, N), 0.0) * max(dot(L, N), 0.0);
        cookTorranceDenominator = max(cookTorranceDenominator, 0.000001);
        vec3 cookTorrance = cookTorranceNumerator / cookTorranceDenominator;

        vec3 BRDF = Kd * lambert + cookTorrance;
        vec3 outgoingLight = lights[i].intensity * BRDF * lights[i].color * max(dot(L, N), 0.0);

        finalLight.xyz += outgoingLight * (1.0f - CalculateShadow(0));
    }

    return vec4(max(finalLight, ambient), 1.0f);
}

void main() {
    if (depthTestFrame) {
        outColor = vec4(1, 1, 1, 1);
        return;
    }

    if (lightCalcMode == 0) {
        outColor = texture(tex, vertex_textureCoord) * CalculateLight();
    } else {
        outColor = texture(tex, vertex_textureCoord);
        outColor *= PBR(outColor);
    }

    if (useGammaCorrection) {
        vec3 toneMapped = outColor.rgb;
        outColor.rgb = pow(toneMapped, vec3(1.0f / gamma));
    }
    if (HDR) {
        outColor.rgb = vec3(1.0) - exp(-outColor.rgb * exposure);
    }

    outColor.rgb *= color;
}