#version 330 core

out vec4 fragColor;

in vec3 nearPoint;
in vec3 farPoint;

in mat4 vw;
in mat4 proj;

uniform float gridScale;
uniform vec3 gridColor;
uniform vec3 xAxisColor;
uniform vec3 zAxisColor;

uniform vec3 cameraPosition;

float near = 0.1f;
float far = 150.0f;

vec4 grid(vec3 fragPos3D, float scale) {
    vec2 coord = fragPos3D.xz * scale;
    vec2 derivative = fwidth(coord);
    vec2 grid = abs(fract(coord - 0.5) - 0.5) / derivative;
    float line = min(grid.x, grid.y);
    float minimumz = min(derivative.y, 1);
    float minimumx = min(derivative.x, 1);
    vec4 color = vec4(gridColor, 1.0 - min(line, 1.0));

    if(fragPos3D.x > -0.1 * minimumx && fragPos3D.x < 0.1 * minimumx)
        color.rgb = xAxisColor;
    if(fragPos3D.z > -0.1 * minimumz && fragPos3D.z < 0.1 * minimumz)
        color.rgb = zAxisColor;
    
    return color;
}

float computeDepth(vec3 pos) {
    vec4 clip_space_pos = proj * vw * vec4(pos.xyz, 1.0);
    return (clip_space_pos.z / clip_space_pos.w);
}

float computeLinearDepth(vec3 pos) {
    vec4 clip_space_pos = proj * vw * vec4(pos.xyz, 1.0);
    float clip_space_depth = (clip_space_pos.z / clip_space_pos.w) * 2.0 - 1.0;
    float linearDepth = (2.0 * near * far) / (far + near - clip_space_depth * (far - near));
    return linearDepth / far;
}

void main() {
    float t = -nearPoint.y / (farPoint.y - nearPoint.y);
    vec3 fragPos3D = nearPoint + t * (farPoint - nearPoint);

    gl_FragDepth = computeDepth(fragPos3D);
    float linearDepth = computeLinearDepth(fragPos3D);
    float fading = max(0, (0.5 - linearDepth));

    vec4 smallerGrid = grid(fragPos3D, 10.0f);
    fragColor = grid(fragPos3D, gridScale) * float(t > 0);
    fragColor.a *= fading;

    if (smallerGrid.xyz != gridColor) {
        fragColor.rgb = smallerGrid.rgb;
    }
}