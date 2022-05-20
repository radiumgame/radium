#version 460 core

layout(local_size_x = 8, local_size_y = 8, local_size_z = 1) in;
layout(rgba32f, binding = 0) uniform image2D screen;

uniform vec2 resolution;
uniform float time;
uniform float deltaTime;

uniform mat4 CAMERA_VIEW;
uniform mat4 CAMERA_PROJECTION;
uniform mat4 CAMERA_WORLD;

const float PI = 3.14159265f;
vec3 SKY_COLOR = vec3(113.0f / 255.0f, 188.0f / 255.0f, 225.0f / 255.0f);
float INF = uintBitsToFloat(0x7F800000);

struct Ray
{
    vec3 origin;
    vec3 direction;
};

struct RayHit
{
    vec3 position;
    float distance;
    vec3 normal;
};

Ray CreateRay(vec3 origin, vec3 direction)
{
    Ray ray;
    ray.origin = origin;
    ray.direction = direction;

    return ray;
}

RayHit CreateRayHit()
{
    RayHit hit;
    hit.position = vec3(0.0f, 0.0f, 0.0f);
    hit.distance = INF;
    hit.normal = vec3(0.0f, 0.0f, 0.0f);

    return hit;
}

Ray CreateCameraRay(vec2 uv)
{
    vec3 origin = (CAMERA_WORLD * vec4(0.0f, 0.0f, 0.0f, 1.0f)).xyz;
    vec3 direction = (inverse(CAMERA_PROJECTION), vec4(uv, 0.0f, 1.0f)).xyz;
    
    direction = (CAMERA_WORLD * vec4(direction, 0.0f)).xyz;
    direction = normalize(direction);
    return CreateRay(origin, direction);
}

void IntersectGroundPlane(Ray ray, inout RayHit bestHit)
{
    float t = -ray.origin.y / ray.direction.y;
    if (t > 0 && t < bestHit.distance)
    {
        bestHit.distance = t;
        bestHit.position = ray.origin + t * ray.direction;
        bestHit.normal = vec3(0.0f, 1.0f, 0.0f);
    }
}

void IntersectSphere(Ray ray, inout RayHit bestHit, vec4 sphere)
{
    vec3 d = ray.origin - sphere.xyz;
    float p1 = -dot(ray.direction, d);
    float p2sqr = p1 * p1 - dot(d, d) + sphere.w * sphere.w;
    if (p2sqr < 0)
        return;
    float p2 = sqrt(p2sqr);
    float t = p1 - p2 > 0 ? p1 - p2 : p1 + p2;
    if (t > 0 && t < bestHit.distance)
    {
        bestHit.distance = t;
        bestHit.position = ray.origin + t * ray.direction;
        bestHit.normal = normalize(bestHit.position - sphere.xyz);
	}
}

RayHit Trace(Ray ray)
{
    RayHit bestHit = CreateRayHit();
    IntersectGroundPlane(ray, bestHit);
	IntersectSphere(ray, bestHit, vec4(0.0f, 0.0f, 0.0f, 1.0f));

    return bestHit;
}

vec3 Shade(inout Ray ray, RayHit hit)
{
    if (hit.distance < INF)
    {
        return hit.normal * 0.5f + 0.5f;
    }
    else
    {
        return SKY_COLOR;
    }
}

void main() {
	vec2 size = imageSize(screen);
	vec2 uv = vec2((gl_GlobalInvocationID.xy + vec2(0.5f, 0.5f)) / size * 2.0f - 1.0f);
	uv.y = -uv.y;

	Ray ray = CreateCameraRay(uv);
	RayHit hit = Trace(ray);
	vec3 result = Shade(ray, hit);

	imageStore(screen, ivec2(gl_GlobalInvocationID.xy), vec4(result, 1.0f));
}