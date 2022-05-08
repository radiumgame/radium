float calculateDepth(float maxDepth) {
    float depth = gl_FragCoord.z / gl_FragCoord.w;
    return depth / maxDepth;
}

float getDepth() {
    return gl_FragCoord.z;
}

float getLinearDepth() {
    float depth = getDepth();
    return (2.0 * nearPlane * farPlane) / (farPlane + nearPlane - (depth * 2.0f - 1.0f) * (farPlane - nearPlane));
}

float getLogisticDepth(float steepness = 0.5f, float offset = 5.0f) {
    float depth = getLinearDepth();
    return (1 / (1 + exp(-steepness * (depth - offset))));
}

vec4 screenspace() {
    vec4 temp = projectionMatrix * viewMatrix * worldPosition;
    temp.xyz /= temp.w;
    temp.xy = (0.5) + (temp.xy) * 0.5;
    return temp;
}