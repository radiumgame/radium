#version 330

struct Light {

    vec3 position;
    vec3 color;
    float intensity;
    float attenuation;
    float farPlane;
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

in vec3 worldPosition;
in mat4 viewMatrix;
in vec4 lightSpaceVector;

in vec3 camPos;
in vec3 reflectedVector;

out vec4 outColor;

uniform sampler2D tex;
uniform sampler2D normalMap;
uniform sampler2D specularMap;
uniform samplerCube env;
uniform sampler2D lightDepth;
uniform samplerCube lightDepthCube;

uniform Light lights[512];
uniform int lightCount;
uniform float ambient;
uniform float gamma;
uniform float exposure;

uniform bool useBlinn;
uniform bool useGammaCorrection;
uniform bool HDR;
uniform int shadowSamples;
uniform float directionalShadowBias;
uniform float pointShadowBias;
uniform bool specularLighting;
uniform bool useNormalMap;
uniform bool useSpecularMap;
uniform bool reflective;
uniform float reflectionAmount;

uniform int lightCalcMode;

uniform Material material;
uniform vec3 color;

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
float CalculateDirectionalShadow(int lightIndex) {
    vec3 projectionCoords = lightSpaceVector.xyz / lightSpaceVector.w;
    projectionCoords = projectionCoords * 0.5f + 0.5f;
    float closestDepth = texture(lightDepth, projectionCoords.xy).r;
    float currentDepth = projectionCoords.z;

    vec3 toLightVector = lights[lightIndex].position - worldPosition;
    vec3 lightDirection = -normalize(toLightVector);
    float bias = max(0.05f * (1.0f - dot(vertex_normal, lightDirection)), directionalShadowBias);

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

const vec3 sampleOffsetDirections[20] = vec3[]
(
   vec3( 1,  1,  1), vec3( 1, -1,  1), vec3(-1, -1,  1), vec3(-1,  1,  1), 
   vec3( 1,  1, -1), vec3( 1, -1, -1), vec3(-1, -1, -1), vec3(-1,  1, -1),
   vec3( 1,  1,  0), vec3( 1, -1,  0), vec3(-1, -1,  0), vec3(-1,  1,  0),
   vec3( 1,  0,  1), vec3(-1,  0,  1), vec3( 1,  0, -1), vec3(-1,  0, -1),
   vec3( 0,  1,  1), vec3( 0, -1,  1), vec3( 0, -1, -1), vec3( 0,  1, -1)
);
float CalculatePointShadow(int lightIndex) {
    float farPlane = lights[lightIndex].farPlane;
    vec3 fragToLight = worldPosition - lights[lightIndex].position;
    float currentDepth = length(fragToLight);
    float shadow = 0.0;
    float viewDistance = length(camPos - worldPosition);
    float diskRadius = (1.0 + (viewDistance / farPlane)) / 25.0;
    for(int i = 0; i < shadowSamples; ++i)
    {
        float closestDepth = texture(lightDepthCube, fragToLight + sampleOffsetDirections[i] * diskRadius).r;
        closestDepth *= farPlane;
        if(currentDepth - pointShadowBias > closestDepth)
            shadow += 1.0;
    }
    shadow /= float(shadowSamples); 

    return shadow;
}

float CalculateShadow(int lightIndex) {
    int lightType = lights[lightIndex].lightType;
    if (lightType == 0) {
        return CalculateDirectionalShadow(lightIndex);
    } else {
        return CalculatePointShadow(lightIndex);
    }
}

vec3 CalculateNormal() {
    if (!useNormalMap) return vertex_normal;

    vec3 Normal = normalize(vertex_normal);
    vec3 Tangent = normalize(vertex_tangent);
    Tangent = normalize(Tangent - dot(Tangent, Normal) * Normal);
    vec3 Bitangent = cross(Tangent, Normal);
    vec3 BumpMapNormal = texture(normalMap, vertex_textureCoord).xyz;
    BumpMapNormal = 2.0 * BumpMapNormal - vec3(1.0, 1.0, 1.0);
    vec3 NewNormal;
    mat3 tbnMat = mat3(Tangent, Bitangent, Normal);
    NewNormal = tbnMat * BumpMapNormal;
    return NewNormal;
}

vec4 CalculateLight() {
    vec3 useNormal = CalculateNormal();
    vec3 finalLight = vec3(0.0f);

    float shadow = 0;
    for (int i = 0; i < lightCount; i++) {
        float newShadow = 1.0f - CalculateShadow(i);
        if (newShadow > shadow) {
            shadow = newShadow;
        }
    }

    for (int i = 0; i < lightCount; i++) {
        vec3 fragPos = worldPosition;
        vec3 lp = lights[i].position;
        vec3 toLightVector = lp - fragPos;
        vec3 toCameraVector = camPos - fragPos;
        vec3 unitNormal = normalize(useNormal);

        vec3 unitLightVector = normalize(toLightVector);
        vec3 unitCameraVector = normalize(toCameraVector);

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

            finalLight += (ambient + shadow * ((diffuse + (specularLighting ? specular : vec3(0))))) * lights[i].color * lights[i].intensity * attenuation;
        } else {
            finalLight += (ambient + shadow * ((diffuse + (specularLighting ? specular : vec3(0))))) * lights[i].color * lights[i].intensity;
        }
    }

    return vec4(max(finalLight, ambient), 1.0f);
}

vec4 PBR(vec4 col) {
    vec3 nor = CalculateNormal();
    vec3 finalLight = vec3(0.0f);
    for (int i = 0; i < lightCount; i++) {
        vec3 fragPos = worldPosition;

        vec3 N = normalize(nor);

        vec3 cp = camPos;
        vec3 V = normalize(cp - fragPos);

        vec3 lp = lights[i].position;
        vec3 L = normalize(lp - fragPos);

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
    
    if (reflective) {
        vec4 envCol = texture(env, reflectedVector);
        outColor = mix(outColor, envCol, reflectionAmount);
    }
}