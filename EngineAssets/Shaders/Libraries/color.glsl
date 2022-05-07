vec3 rgb(Color col) {
    return vec3(col.r, col.g, col.a);
}

vec4 rgba(Color col) {
    return vec4(col.r, col.g, col.b, col.a);
}

vec4 lerp(Color a, Color b, float t) {
    return mix(rgba(a), rgba(b), t);
}