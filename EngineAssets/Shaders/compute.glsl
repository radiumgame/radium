#version 460 core
layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;
layout(rgba32f, binding = 0) uniform image2D screen;

void main() {
	vec2 size = imageSize(screen);
	vec2 uv = vec2(gl_GlobalInvocationID.xy / size);

	imageStore(screen, ivec2(gl_GlobalInvocationID.xy), vec4(uv, 0.0f, 1.0));
}