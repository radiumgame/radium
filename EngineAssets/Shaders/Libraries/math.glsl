// https://gist.github.com/patriciogonzalezvivo/986341af1560138dde52
const float PI = 3.1415926535897932384626433832795;
const float PHI = (1.0 + sqrt(5.0)) / 2.0;
const float PI180 = float(PI / 180.0);
float sind(float a) { return sin(a * PI180); }
float cosd(float a) { return cos(a * PI180); }
float tand(float a) { return tan(a * PI180); }
float asind(float a) { return asin(a) * 180.0 / PI; }
float acosd(float a) { return acos(a) * 180.0 / PI; }
float atand(float a) { return atan(a) * 180.0 / PI; }
float lerp(float a, float b, float t) { return a + (b - a) * t; }
float clamp(float a, float min, float max) { return max(min(a, max), min); }
float smoothstep(float edge0, float edge1, float x) {
    float t = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
    return t * t * (3.0 - 2.0 * t);
}
float step(float edge, float x) { return x < edge ? 0.0 : 1.0; }
float random(float seed) {
    return fract(sin(seed) * 43758.5453123);
}
float random() {
    return random(time);
}
float random2D(vec2 seed){
    return fract(sin(dot(seed.xy, vec2(12.9898, 78.233))) * 43758.5453);
}
float random3D(vec3 seed){
    return fract(sin(dot(seed.xyz, vec3(12.9898, 78.233, 144.7272))) * 43758.5453);
}

vec3 toObjectSpace(vec4 vec) {
    return (vec * inverse(modelMatrix)).xyz;
}

vec3 toObjectSpace(vec3 vec) {
    return toObjectSpace(vec4(vec, 1.0)).xyz;
}

vec4 toWorldSpace(vec3 vec) {
    return modelMatrix * vec4(vec, 1.0f);
}

vec4 toWorldSpace(vec4 vec) {
    return modelMatrix * vec;
}

float fresnel(float bias, float scale, float power) {
    vec3 i = normalize(worldPosition.xyz - eye);
    float r = bias + scale * pow(1.0 + dot(i, normal), power);
    return r;
}

float fresnel() {
    return fresnel(0.25, 0.5, 1.0);
}

float fresnel(float power) {
    return fresnel(0.25, 0.5, power);
}