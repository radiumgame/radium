vec3 calculateNormal(sampler2D normalMap, vec2 uv) {
    vec3 newNormal = texture(normalMap, uv).rgb;
    newNormal = newNormal * 2.0 - 1.0;
    newNormal = normalize(TBN * newNormal);

    return newNormal;
}

vec3 calculateSpecular(sampler2D specularMap, vec2 uv) {
    return texture(specularMap, uv).rgb;
}

vec3 calculateSpecular(sampler2D specularMap) {
    return calculateSpecular(specularMap, uv);
}

vec3 calculateNormal(sampler2D normalMap) {
    return calculateNormal(normalMap, uv);
}

vec4 calculateLight(Light light, vec3 n, vec3 spec, Material material) {
    vec3 toLightVector = light.position - worldPosition.xyz;
    vec3 toCameraVector = (inverse(viewMatrix) * vec4(0, 0, 0, 1)).xyz - worldPosition.xyz;
    vec3 unitNormal = normalize(n);
    vec3 unitLightVector = normalize(toLightVector);
    vec3 unitCameraVector = normalize(toCameraVector);

    vec3 lightDirection = -unitLightVector;
    vec3 halfwayDirection = normalize(unitLightVector + unitCameraVector);

    vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
    float specularFactor = dot(reflectedLightDirection, unitCameraVector);
    specularFactor = max(specularFactor, 0.0f);
    float dampedFactor = pow(specularFactor, material.shineDamper);
    vec3 specular = dampedFactor * material.reflectivity * light.color;
    specular *= spec;

    float nDotl = dot(unitNormal, unitLightVector);
    float brightness = max(nDotl, 0.0);
    vec3 diffuse = brightness * light.color;

    float ambient = 0.3f;
    if (light.lightType == 1) {
        float distanceFromLight = length(light.position - position);
        float attenuation = 1.f / (1.f + light.attenuation * distanceFromLight * 0.0075f * (distanceFromLight * distanceFromLight));
        diffuse *= attenuation;
        specular *= attenuation;

        vec3 light = (diffuse + specular) * light.color * light.intensity * attenuation;
        light = max(light, ambient);
        return vec4(light, 1.0f);
    } else {
        vec3 light = (diffuse + specular) * light.color * light.intensity;
        light = max(light, ambient);
        return vec4(light, 1.0f);
    }
}

vec4 calculateLight(Light light, vec3 n, vec3 spec) {
    return calculateLight(light, n, spec, Material(10, 1));
}

vec4 calculateLight(Light light) {
    return calculateLight(light, normal, vec3(1), Material(10.0f, 1.0f));
}

vec4 calculateLight(Light light, Material mat) {
    return calculateLight(light, normal, vec3(1), mat);
}

vec4 calculateLight(Light light, vec3 n) {
    return calculateLight(light, n, vec3(1), Material(10.0f, 1.0f));
}

vec4 calculateLight(Light light, vec3 n, Material material) {
    return calculateLight(light, n, vec3(1), material);
}

vec4 correctGamma(vec4 col, float gamma) {
    vec3 toneMapped = col.rgb;
    return vec4(pow(toneMapped, vec3(1.0f / gamma)), 1.0f);
}

vec4 applyHDR(vec4 col, float exposure) {
    return vec4(vec3(1.0) - exp(-col.rgb * exposure), 1.0f);
}

Light createDirectionalLight() {
    return Light(vec3(0), vec3(1), 1.0f, 0.045f, 0);
}

Light createPointLight() {
    return Light(vec3(0), vec3(1), 1.0f, 0.045f, 1);
}