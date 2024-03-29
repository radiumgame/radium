float getDepth() {
    return gl_FragCoord.z;
}

float getLinearDepth() {
    float depth = getDepth();
    return (2.0 * nearPlane * farPlane) / (farPlane + nearPlane - (depth * 2.0f - 1.0f) * (farPlane - nearPlane));
}

float getLogisticDepth(float steepness, float offset) {
    float depth = getLinearDepth();
    return (1 / (1 + exp(-steepness * (depth - offset))));
}

float getLogisticDepth() {
    return getLogisticDepth(0.5f, 5.0f);
}

vec4 screenspace() {
    vec4 temp = projectionMatrix * viewMatrix * vec4(worldPosition, 1);
    temp.xyz /= temp.w;
    temp.xy = (0.5) + (temp.xy) * 0.5;
    return temp;
}