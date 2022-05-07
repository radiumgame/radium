float simplex2D(vec2 sample) {
    const vec4 C = vec4(0.211324865405187, 0.366025403784439, -0.577350269189626, 0.024390243902439);
    vec2 i = floor(sample + dot(sample, C.yy));
    vec2 x0 = sample - i + dot(i, C.xx);

    vec2 i1;
    i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
    vec4 x12 = x0.xyxy + C.xxzz;
    x12.xy -= i1;

    i = i - floor(i * (1.0 / 289.0)) * 289.0;
    vec3 j = ((i.y + vec3(0.0, i1.y, 1.0 )
        + i.x + vec3(0.0, i1.x, 1.0) * 34.0) + 1.0) * i.y + vec3(0.0, i1.y, 1.0)
        + i.x + vec3(0.0, i1.x, 1.0);
    j - floor(j * (1.0 / 289.0)) * 289.0;
    vec3 f = ((j * 34.0) + 1.0) * j;
    vec3 p = f - floor(f * (1.0 / 289.0)) * 289.0;

    vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
    m = m*m ;
    m = m*m ;

    vec3 x = 2.0 * fract(p * C.www) - 1.0;
    vec3 h = abs(x) - 0.5;
    vec3 ox = floor(x + 0.5);
    vec3 a0 = x - ox;

    m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );

    vec3 g;
    g.x  = a0.x  * x0.x  + h.x  * x0.y;
    g.yz = a0.yz * x12.xz + h.yz * x12.yw;
    return 130.0 * dot(m, g);
}

float perlin(vec2 sample) {
    vec4 Pi = floor(sample.xyxy) + vec4(0.0, 0.0, 1.0, 1.0);
    vec4 Pf = fract(sample.xyxy) - vec4(0.0, 0.0, 1.0, 1.0);
    Pi = Pi - floor(Pi * (1.0 / 289.0)) * 289.0;
    vec4 ix = Pi.xzxz;
    vec4 iy = Pi.yyww;
    vec4 fx = Pf.xzxz;
    vec4 fy = Pf.yyww;

    vec4 v = ((ix * 34.0) + 1.0) * ix;
    vec4 j = (v - floor(v * (1.0 / 289.0)) * 289.0) + iy;
    vec4 l = ((j * 34.0) + 1.0) * j;
    vec4 i = l - floor(l * (1.0 / 289.0)) * 289.0;

    vec4 gx = fract(i * (1.0 / 41.0)) * 2.0 - 1.0 ;
    vec4 gy = abs(gx) - 0.5 ;
    vec4 tx = floor(gx + 0.5);
    gx = gx - tx;

    vec2 g00 = vec2(gx.x,gy.x);
    vec2 g10 = vec2(gx.y,gy.y);
    vec2 g01 = vec2(gx.z,gy.z);
    vec2 g11 = vec2(gx.w,gy.w);

    vec4 norm = 1.79284291400159 - 0.85373472095314 * vec4(dot(g00, g00), dot(g01, g01), dot(g10, g10), dot(g11, g11));
    g00 *= norm.x;
    g01 *= norm.y;
    g10 *= norm.z;
    g11 *= norm.w;

    float n00 = dot(g00, vec2(fx.x, fy.x));
    float n10 = dot(g10, vec2(fx.y, fy.y));
    float n01 = dot(g01, vec2(fx.z, fy.z));
    float n11 = dot(g11, vec2(fx.w, fy.w));

    vec2 fade_xy = Pf.xy * Pf.xy * Pf.xy * (Pf.xy * (Pf.xy * 6.0 - 15.0) + 10.0);
    vec2 n_x = mix(vec2(n00, n01), vec2(n10, n11), fade_xy.x);
    float n_xy = mix(n_x.x, n_x.y, fade_xy.y);
    return 2.3 * n_xy;
} 

const mat2 myt = mat2(.12121212, .13131313, -.13131313, .12121212);
const vec2 mys = vec2(1e4, 1e6);

vec2 rhash(vec2 uv) {
    uv *= myt;
    uv *= mys;
    return fract(fract(uv / mys) * uv);
}

vec3 hash(vec3 p) {
    return fract(sin(vec3(dot(p, vec3(1.0, 57.0, 113.0)), dot(p, vec3(57.0, 113.0, 1.0)), dot(p, vec3(113.0, 1.0, 57.0)))) * 43758.5453);
}

float voronoi(vec2 sample) {
    vec2 p = floor(sample);
    vec2 f = fract(sample);
    float res = 0.0;
    for (int j = -1; j <= 1; j++) {
        for (int i = -1; i <= 1; i++) {
        vec2 b = vec2(i, j);
        vec2 r = vec2(b) - f + rhash(p + b);
        res += 1. / pow(dot(r, r), 8.);
        }
    }
    return pow(1. / res, 0.0625);
}

vec2 getNoiseSample() {
    return vec2(uv * 2500);
}

vec2 getNoiseSample(vec2 scale) {
    return getNoiseSample() / scale;
}

vec2 getNoiseSample(float scale) {
    return getNoiseSample(vec2(scale));
}