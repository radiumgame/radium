float calculateDepth(float maxDepth) {
    float depth = gl_FragCoord.z / gl_FragCoord.w;
    return depth / maxDepth;
}

float cameraDepth() {
    return distance(vec4(eye, 1.0f), worldPosition);
}