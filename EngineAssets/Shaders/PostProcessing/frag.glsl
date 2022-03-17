#version 330 core

in vec2 texCoords;
in vec2 blurTextureCoordsh[11];
in vec2 blurTextureCoordsv[11];

out vec4 outColor;

uniform sampler2D screenTexture;

uniform bool playing;
uniform float time;

// Effects
uniform bool invert;
uniform bool tint;
uniform bool grain;
uniform bool vignette;
uniform bool colorAdjust;
uniform bool guassianBlur;
uniform bool bloom;

// Effect Settings
uniform vec3 tintColor;

uniform vec3 vignetteColor;
uniform float innerVignetteRadius;
uniform float outerVignetteRadius;
uniform float vignetteIntensity;

uniform float colorContrast;

uniform float bloomThreshold;
uniform float bloomIntensity;

// Noise: https://gist.github.com/patriciogonzalezvivo/670c22f3966e662d2f83
// Random: https://stackoverflow.com/questions/4200224/random-noise-functions-for-glsl
uint hash( uint x ) {
    x += ( x << 10u );
    x ^= ( x >>  6u );
    x += ( x <<  3u );
    x ^= ( x >> 11u );
    x += ( x << 15u );
    return x;
}

// Compound versions of the hashing algorithm I whipped together.
uint hash( uvec2 v ) { return hash( v.x ^ hash(v.y)                         ); }
uint hash( uvec3 v ) { return hash( v.x ^ hash(v.y) ^ hash(v.z)             ); }
uint hash( uvec4 v ) { return hash( v.x ^ hash(v.y) ^ hash(v.z) ^ hash(v.w) ); }

// Construct a float with half-open range [0:1] using low 23 bits.
// All zeroes yields 0.0, all ones yields the next smallest representable value below 1.0.
float floatConstruct( uint m ) {
    const uint ieeeMantissa = 0x007FFFFFu; // binary32 mantissa bitmask
    const uint ieeeOne      = 0x3F800000u; // 1.0 in IEEE binary32

    m &= ieeeMantissa;                     // Keep only mantissa bits (fractional part)
    m |= ieeeOne;                          // Add fractional part to 1.0

    float  f = uintBitsToFloat( m );       // Range [1:2]
    return f - 1.0;                        // Range [0:1]
}

float random( float x ) { return floatConstruct(hash(floatBitsToUint(x))); }
float random( vec2  v ) { return floatConstruct(hash(floatBitsToUint(v))); }
float random( vec3  v ) { return floatConstruct(hash(floatBitsToUint(v))); }
float random( vec4  v ) { return floatConstruct(hash(floatBitsToUint(v))); }

float normalize(float val, float min, float max) {
    // norm = (x + 1) / 2;
    return (val + min) / (min + max);
}

vec3 normalize(vec3 val, float min, float max) {
    return vec3(normalize(val.x, min, max), normalize(val.y, min, max), normalize(val.z, min, max));
}

vec4 blur() {
    vec4 col = vec4(0);

    col += texture(screenTexture, blurTextureCoordsh[0]) * 0.0093;
    col += texture(screenTexture, blurTextureCoordsh[1]) * 0.028002;
    col += texture(screenTexture, blurTextureCoordsh[2]) * 0.065984;
    col += texture(screenTexture, blurTextureCoordsh[3]) * 0.121703;
    col += texture(screenTexture, blurTextureCoordsh[4]) * 0.175713;
    col += texture(screenTexture, blurTextureCoordsh[5]) * 0.198596;
    col += texture(screenTexture, blurTextureCoordsh[6]) * 0.175713;
    col += texture(screenTexture, blurTextureCoordsh[7]) * 0.121703;
    col += texture(screenTexture, blurTextureCoordsh[8]) * 0.065984;
    col += texture(screenTexture, blurTextureCoordsh[9]) * 0.028002;
    col += texture(screenTexture, blurTextureCoordsh[10]) * 0.0093;

    col += texture(screenTexture, blurTextureCoordsv[0]) * 0.0093;
    col += texture(screenTexture, blurTextureCoordsv[1]) * 0.028002;
    col += texture(screenTexture, blurTextureCoordsv[2]) * 0.065984;
    col += texture(screenTexture, blurTextureCoordsv[3]) * 0.121703;
    col += texture(screenTexture, blurTextureCoordsv[4]) * 0.175713;
    col += texture(screenTexture, blurTextureCoordsv[5]) * 0.198596;
    col += texture(screenTexture, blurTextureCoordsv[6]) * 0.175713;
    col += texture(screenTexture, blurTextureCoordsv[7]) * 0.121703;
    col += texture(screenTexture, blurTextureCoordsv[8]) * 0.065984;
    col += texture(screenTexture, blurTextureCoordsv[9]) * 0.028002;
    col += texture(screenTexture, blurTextureCoordsv[10]) * 0.0093;

    return col;
}

void main()
{
    if (!playing) {
        outColor = texture(screenTexture, texCoords);
        return;
    }

    outColor = texture(screenTexture, texCoords);
    if (guassianBlur) {
        outColor = vec4(0.0f);
        outColor = blur();
    }
    if (invert) {
        outColor.rgb = vec3(1.0f) - outColor.rgb;
    }
    if (tint) {
        outColor.rgb *= tintColor;
    }
    if (grain) {
        vec3 inputs = vec3(gl_FragCoord.xy, time);
        float rand = random(inputs);
        vec3 luma = vec3(rand);
        luma = normalize(luma, 0.3f, 1.0f);
        outColor.rgb *= luma;
    }
    if (vignette) {
        vec2 relativePosition = gl_FragCoord.xy / vec2(1920, 1080) - 0.5f;
        relativePosition.y *= 1920 / 1080;
        float len = length(relativePosition);
        float vignette = smoothstep(innerVignetteRadius, outerVignetteRadius, len) * vignetteIntensity;
        outColor.rgb = mix(outColor.rgb, vignetteColor, vignette);
    }
    if (colorAdjust) {
        outColor.rgb = (outColor.rgb - 0.5f) * (1.0f + colorContrast) + 0.5f;
    }
    if (bloom) {
        vec4 col = texture(screenTexture, texCoords);
        float brightness = (col.r * 0.2126) + (col.g * 0.7152) + (col.b * 0.0722);
        if (brightness >= bloomThreshold) {
            vec4 highlight = blur();
            outColor = col + highlight * bloomIntensity;
        }
    }
}