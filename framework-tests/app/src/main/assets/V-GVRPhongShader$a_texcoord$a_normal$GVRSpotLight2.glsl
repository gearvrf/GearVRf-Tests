#version 300 es
#define HAS_LIGHTSOURCES 1
#define HAS_VertexTemplate 1;
#define HAS_VertexShader 1;
#define HAS_VertexNormalShader 1;
#define HAS_VertexSkinShader 1;
#define HAS_a_texcoord 1
#define HAS_a_normal 1

#ifdef HAS_MULTIVIEW
#extension GL_OVR_multiview2 : enable
layout(num_views = 2) in;
uniform mat4 u_view_[2];
uniform mat4 u_mvp_[2];
uniform mat4 u_mv_[2];
uniform mat4 u_mv_it_[2];
flat out int view_id;
#else
uniform mat4 u_view;
uniform mat4 u_mvp;
uniform mat4 u_mv;
uniform mat4 u_mv_it;
#endif	

uniform mat4 u_model;
in vec3 a_position;
in vec2 a_texcoord;
in vec3 a_normal;

#ifdef HAS_VertexSkinShader
#ifdef HAS_SHADOWS
//
// shadow mapping uses more uniforms
// so we dont get as many bones
//
uniform mat4 u_bone_matrix[50];
#else
uniform mat4 u_bone_matrix[60];
#endif
in vec4 a_bone_weights;
in ivec4 a_bone_indices;
#endif

#ifdef HAS_VertexNormalShader
in vec3 a_tangent;
in vec3 a_bitangent;
#endif

out vec3 view_direction;
out vec3 viewspace_position;
out vec3 viewspace_normal;
out vec4 local_position;
out vec2 diffuse_coord;
out vec2 ambient_coord;
out vec2 specular_coord;
out vec2 emissive_coord;
out vec2 normal_coord;
out vec2 lightmap_coord;

struct Vertex
{
	vec4 local_position;
	vec4 local_normal;
	vec3 viewspace_position;
	vec3 viewspace_normal;
	vec3 view_direction;
};

#ifdef HAS_LIGHTSOURCES
	out vec4 vlight1_shadow_position;
out vec4 vlight0_shadow_position;
struct UniformGVRSpotLight {
    float enabled;
    vec3 world_position;
    vec3 world_direction;
    vec4 diffuse_intensity;
    vec4 ambient_intensity;
    vec4 specular_intensity;
    float attenuation_constant;
    float attenuation_linear;
    float attenuation_quadratic;
    float inner_cone_angle;
    float outer_cone_angle;
    float shadow_map_index;
    vec4 sm0;
    vec4 sm1;
    vec4 sm2;
    vec4 sm3;
};

uniform UniformGVRSpotLight light1;

uniform UniformGVRSpotLight light0;
void LightVertex(Vertex vertex) {
#ifdef HAS_SHADOWS
mat4 temp_vlight1 = mat4(light1.sm0, light1.sm1, light1.sm2, light1.sm3);
vlight1_shadow_position = temp_vlight1 * u_model * vertex.local_position;
#endif

#ifdef HAS_SHADOWS
mat4 temp_vlight0 = mat4(light0.sm0, light0.sm1, light0.sm2, light0.sm3);
vlight0_shadow_position = temp_vlight0 * u_model * vertex.local_position;
#endif

}

#endif
	
void main() {
	Vertex vertex;

	vertex.local_position = vec4(a_position.xyz, 1.0);
	vertex.local_normal = vec4(0.0, 0.0, 1.0, 0.0);
	
#ifdef HAS_MULTIVIEW
  vec4 pos = u_mv_[gl_ViewID_OVR] * vertex.local_position;
#else
  vec4 pos = u_mv * vertex.local_position;
#endif

vertex.viewspace_position = pos.xyz / pos.w;
#ifdef HAS_a_normal
   vertex.local_normal = vec4(normalize(a_normal), 0.0);
#endif

#ifdef HAS_MULTIVIEW
	vertex.viewspace_normal = normalize((u_mv_it_[gl_ViewID_OVR] * vertex.local_normal).xyz);
#else
	vertex.viewspace_normal = normalize((u_mv_it * vertex.local_normal).xyz);
#endif

vertex.view_direction = normalize(-vertex.viewspace_position);
#ifdef HAS_a_texcoord
   diffuse_coord = a_texcoord.xy;
   specular_coord = a_texcoord.xy;
   ambient_coord = a_texcoord.xy;
   normal_coord = a_texcoord.xy;
   lightmap_coord = a_texcoord.xy;
#endif
#ifdef HAS_VertexSkinShader
	#if defined(HAS_a_bone_indices) && defined(HAS_a_bone_weights)
	vec4 weights = a_bone_weights;
	ivec4 bone_idx = a_bone_indices;
	mat4 bone = u_bone_matrix[bone_idx[0]] * weights[0];
	bone += u_bone_matrix[bone_idx[1]] * weights[1];
	bone += u_bone_matrix[bone_idx[2]] * weights[2];
	bone += u_bone_matrix[bone_idx[3]] * weights[3];
	vertex.local_position = bone * vertex.local_position;
#endif
#endif
#ifdef HAS_VertexNormalShader
	#if defined(HAS_a_tangent) && defined(HAS_normalTexture) && defined(HAS_a_normal)
   mat3 tbnmtx = mat3(a_tangent, a_bitangent, vertex.local_normal.xyz);

#ifdef HAS_MULTIVIEW
   mat3 wtts = tbnmtx * mat3(u_mv_it_[gl_ViewID_OVR]);
#else
   mat3 wtts = tbnmtx * mat3(u_mv_it);
#endif
   vec3 d = wtts * -vertex.viewspace_position;
   vertex.view_direction = normalize(d);
#endif
#endif
#ifdef HAS_LIGHTSOURCES
	LightVertex(vertex);
#endif
#ifdef HAS_TEXCOORDS
	
#endif
	viewspace_position = vertex.viewspace_position;
	viewspace_normal = vertex.viewspace_normal;
	view_direction = vertex.view_direction;
#ifdef HAS_MULTIVIEW
	view_id = int(gl_ViewID_OVR);
	gl_Position = u_mvp_[gl_ViewID_OVR] * vertex.local_position;
#else
	gl_Position = u_mvp * vertex.local_position;	
#endif	
}