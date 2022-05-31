#version 330 core

in vec2 texCoord0;
in vec2 texCoord1;
in vec2 texCoord2;
in float blend;

in vec3 particleColor;

out vec4 fragColor;

uniform sampler2D tex;
uniform bool alphaIsTransparency;

void main() {
    vec4 col1 = texture(tex, texCoord1);
    vec4 col2 = texture(tex, texCoord2);
    fragColor = mix(col1, col2, blend) * vec4(particleColor, 1.0f);

    if (fragColor.a <= 0.1f) discard;
    
    if (!alphaIsTransparency) {
        float a = (fragColor.r + fragColor.g + fragColor.b) / 3.0f;
        fragColor.a = a;
    }
}