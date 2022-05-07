float calculateDepth(float maxDepth) {
    float depth = gl_FragCoord.z / gl_FragCoord.w;
    return depth / maxDepth;
}

float cameraDepth() {
    return distance(vec4(eye, 1.0f), worldPosition);
}

vec4 screenspace() {
    vec4 temp = projectionMatrix * viewMatrix * worldPosition;
    temp.xyz /= temp.w;
    temp.xy = (0.5) + (temp.xy) * 0.5;
    return temp;
}