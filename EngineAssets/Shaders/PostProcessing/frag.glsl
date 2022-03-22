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
uniform bool posterize;
uniform bool pixelize;
uniform bool sharpen;

// Effect Settings
uniform float blurIntensity;

uniform vec3 tintColor;

uniform vec3 vignetteColor;
uniform float innerVignetteRadius;
uniform float outerVignetteRadius;
uniform float vignetteIntensity;

uniform float colorContrast;

uniform float bloomThreshold;
uniform float bloomIntensity;

uniform int posterizeLevels;

uniform int pixelSize;

uniform float sharpenIntensity;

vec2 texSize = vec2(1920, 1080);

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

void main()
{
    if (!playing) {
        outColor = texture(screenTexture, texCoords);
        return;
    }

    outColor = texture(screenTexture, texCoords);
    if (guassianBlur) {
        int size = 1;
        float separation = 5;
        float amount = 1;

        vec2 texSize = textureSize(screenTexture, 0).xy;
        float value = 0.0;
        float count = 0.0;
        vec4 result = vec4(0);
        vec4 color  = vec4(0);
        for (int i = -size; i <= size; ++i) {
            for (int j = -size; j <= size; ++j) {
                color = texture(screenTexture, (gl_FragCoord.xy + (vec2(i, j) * blurIntensity)) / texSize);

                result += color;
                count += 1.0;
            }
        }
        result /= count;
        outColor = mix(vec4(0), result, amount);
    }
    if (pixelize) {
        float x = int(gl_FragCoord.x) % pixelSize;
        float y = int(gl_FragCoord.y) % pixelSize;
        x = floor(pixelSize / 2.0) - x;
        y = floor(pixelSize / 2.0) - y;
        x = gl_FragCoord.x + x;
        y = gl_FragCoord.y + y;

        outColor = texture(screenTexture, vec2(x, y) / texSize);
    }
    if (invert) {
        outColor.rgb = vec3(1.0f) - outColor.rgb;
    }
    if (tint) {
        outColor.rgb *= tintColor;
    }
    if (grain) {
        /* Old film algorithm
        vec3 inputs = vec3(gl_FragCoord.xy, time);
        float rand = random(inputs);
        vec3 luma = vec3(rand);
        luma = normalize(luma, 0.3f, 1.0f);
        outColor.rgb *= luma;
        */

        float amount = 0.1f;
        float toRadians = 3.14 / 180;

        float randomIntensity = fract(10000 * sin((gl_FragCoord.x + gl_FragCoord.y * time) * toRadians));
        amount *= randomIntensity;
        outColor.rgb += amount;
    }
    if (vignette) {
        vec2 relativePosition = gl_FragCoord.xy / texSize - 0.5f;
        relativePosition.y *= 1920 / 1080;
        float len = length(relativePosition);
        float vignette = smoothstep(innerVignetteRadius, outerVignetteRadius, len) * vignetteIntensity;
        outColor.rgb = mix(outColor.rgb, vignetteColor, vignette);
    }
    if (colorAdjust) {
        outColor.rgb = (outColor.rgb - 0.5f) * (1.0f + colorContrast) + 0.5f;
    }
    if (bloom) {
        /* Old bloom algorithm
        vec4 col = texture(screenTexture, texCoords);
        float brightness = (col.r * 0.2126) + (col.g * 0.7152) + (col.b * 0.0722);
        if (brightness >= bloomThreshold) {
            vec4 highlight = blur();
            outColor = col + highlight * bloomIntensity;
        }
        */

        int size = 5;
        float separation = 3;
        float amount = 1;

        vec2 texSize = textureSize(screenTexture, 0).xy;
        float value = 0.0;
        float count = 0.0;
        vec4 result = vec4(0);
        vec4 color  = vec4(0);
        for (int i = -size; i <= size; ++i) {
            for (int j = -size; j <= size; ++j) {
                color = texture(screenTexture, (gl_FragCoord.xy + (vec2(i, j) * separation)) / texSize);
                value = max(color.r, max(color.g, color.b));
                if (value < bloomThreshold) { color = vec4(0, 0, 0, 1); }

                result += color;
                count += 1.0;
            }
        }
        result /= count;
        vec4 final = mix(vec4(0), result, amount) * bloomIntensity;
        outColor += final;
    }
    if (posterize) {
        float greyscale = max(outColor.r, max(outColor.g, outColor.b));
        float lower = floor(greyscale * posterizeLevels) / posterizeLevels;
        float lowerDiff = abs(greyscale - lower);
        float upper = ceil(greyscale * posterizeLevels) / posterizeLevels;
        float upperDiff = abs(upper - greyscale);
        float level = lowerDiff <= upperDiff ? lower : upper;
        float adjustment = level / greyscale;
        outColor.rgb *= adjustment;
    }
    if (sharpen) {
        float neighbor = sharpenIntensity * -1;
        float center = sharpenIntensity * 4 + 1;
        vec3 color =
        texture(screenTexture, vec2(gl_FragCoord.x + 0, gl_FragCoord.y + 1) / texSize).rgb
        * neighbor
        + texture(screenTexture, vec2(gl_FragCoord.x - 1, gl_FragCoord.y + 0) / texSize).rgb
        * neighbor
        + texture(screenTexture, vec2(gl_FragCoord.x + 0, gl_FragCoord.y + 0) / texSize).rgb
        * center
        + texture(screenTexture, vec2(gl_FragCoord.x + 1, gl_FragCoord.y + 0) / texSize).rgb
        * neighbor

        + texture(screenTexture, vec2(gl_FragCoord.x + 0, gl_FragCoord.y - 1) / texSize).rgb
        * neighbor;
        outColor = vec4(color, texture(screenTexture, texCoords).a);
    }
}