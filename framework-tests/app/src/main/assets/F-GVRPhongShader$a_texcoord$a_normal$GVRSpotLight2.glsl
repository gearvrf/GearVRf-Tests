#version 300 es
#define HAS_LIGHTSOURCES 1
#define HAS_FragmentAddLight 1;
#define HAS_FragmentTemplate 1;
#define HAS_FragmentSurface 1;
#define HAS_a_texcoord 1
#define HAS_a_normal 1
precision highp float;
precision highp sampler2DArray;

#ifdef HAS_MULTIVIEW
	flat in int view_id;
	uniform mat4 u_view_[2];
#else
    uniform mat4 u_view; 
#endif

out vec4 fragColor;

uniform mat4 u_model;

in vec3 viewspace_position;
in vec3 viewspace_normal;
in vec4 local_position;
in vec4 proj_position;
in vec3 view_direction;
in vec2 diffuse_coord;

#ifdef HAS_ambientTexture
out vec2 ambient_coord;
#endif

#ifdef HAS_specularTexture
out vec2 specular_coord;
#endif

#ifdef HAS_emissiveTexture
out vec2 emissive_coord;
#endif

#ifdef HAS_normalTexture
out vec2 normal_coord;
#endif

#ifdef HAS_SHADOWS
uniform sampler2DArray u_shadow_maps;

float unpackFloatFromVec4i(const vec4 value)
{
    const vec4 bitSh = vec4(1.0 / (256.0 * 256.0 * 256.0), 1.0 / (256.0 * 256.0), 1.0 / 256.0, 1.0);
    const vec4 unpackFactors = vec4( 1.0 / (256.0 * 256.0 * 256.0), 1.0 / (256.0 * 256.0), 1.0 / 256.0, 1.0 );
    return dot(value,unpackFactors);
}

#endif

struct Radiance
{
   vec3 ambient_intensity;
   vec3 diffuse_intensity;
   vec3 specular_intensity;
   vec3 direction; // view space direction from light to surface
   float attenuation;
};


uniform sampler2D ambientTexture;
uniform sampler2D diffuseTexture;
uniform sampler2D specularTexture;
uniform sampler2D opacityTexture;
uniform sampler2D lightmapTexture;
uniform sampler2D emissiveTexture;
uniform sampler2D normalTexture;

uniform vec4 ambient_color;
uniform vec4 diffuse_color;
uniform vec4 specular_color;
uniform vec4 emissive_color;
uniform float specular_exponent;
uniform vec2 u_lightmap_offset;
uniform vec2 u_lightmap_scale;

struct Surface
{
   vec3 viewspaceNormal;
   vec4 ambient;
   vec4 diffuse;
   vec4 specular;
   vec4 emission;
};

Surface GVRPhongShader()
{
	vec4 diffuse = diffuse_color;
	vec4 emission = emissive_color;
	vec4 specular = specular_color;
	vec4 ambient = ambient_color;
	vec3 viewspaceNormal;
	
#ifdef HAS_ambientTexture
	ambient *= texture(ambientTexture, ambient_coord.xy);
#endif
#ifdef HAS_diffuseTexture
	diffuse *= texture(diffuseTexture, diffuse_coord.xy);
#endif
#ifdef HAS_opacityTexture
	diffuse.w *= texture(opacityTexture, diffuse_coord.xy).a;
#endif
diffuse.xyz *= diffuse.w;
#ifdef HAS_specularTexture
	specular *= texture(specularTexture, specular_coord.xy);
#endif
#ifdef HAS_emissiveTexture
	emission = texture(emissiveTexture, emissive_coord.xy);
#else
	emission = vec4(0.0, 0.0, 0.0, 0.0);
#endif
#ifdef HAS_normalTexture
	viewspaceNormal = texture(normalTexture, normal_coord.xy).xyz * 2.0 - 1.0;
#else
	viewspaceNormal = viewspace_normal;
#endif

#ifdef HAS_lightMapTexture
	vec2 lmap_coord = (lightmap_coord * u_lightMap_scale) + u_lightMap_offset;
	diffuse *= texture(lightMapTexture, vec2(lmap_coord.x, 1 - lmap_coord.y);
	return Surface(viewspaceNormal, ambient, vec4(0.0, 0.0, 0.0, 0.0), specular, diffuse);
#else
	return Surface(viewspaceNormal, ambient, diffuse, specular, emission);
#endif
}


vec4 AddLight(Surface s, Radiance r)
{
	vec3 L = r.direction.xyz;	// From surface to light, unit length, view-space
	float nDotL = max(dot(s.viewspaceNormal, L), 0.0);
	
	vec3 kE = s.emission.xyz;
	float alpha = s.diffuse.a;
	vec3 kA = s.ambient.xyz * clamp(r.ambient_intensity.xyz, 0.0, 1.0);
	vec3 kS = vec3(0, 0, 0);
	vec3 kD = nDotL * s.diffuse.xyz * clamp(r.diffuse_intensity.xyz, 0.0, 1.0);
	if (nDotL > 0.0)
	{
		vec3 reflect = normalize(reflect(-L, s.viewspaceNormal));
		float cosAngle = dot(view_direction, reflect);
		if (cosAngle > 0.0)
		{
			kS = s.specular.xyz * clamp(r.specular_intensity, 0.0, 1.0);
			kS *= pow(cosAngle, specular_exponent);
		}
	}
	return vec4(kE + r.attenuation * (kA + kD + kS), alpha);
}


in vec4 vlight1_shadow_position;
in vec4 vlight0_shadow_position;

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

struct VertexGVRSpotLight {
    vec4 shadow_position;
};
Radiance GVRSpotLight(Surface s, in UniformGVRSpotLight data, VertexGVRSpotLight vertex)
{
#ifdef HAS_MULTIVIEW
	vec4 lightpos = u_view_[view_id] * vec4(data.world_position.xyz, 1.0);
#else
    vec4 lightpos = u_view * vec4(data.world_position.xyz, 1.0);
#endif
     vec3 lightdir = normalize(lightpos.xyz - viewspace_position.xyz);
          
     // Attenuation
     float distance    = length(lightpos.xyz - viewspace_position);
     float attenuation = 1.0 / (data.attenuation_constant + data.attenuation_linear * distance + 
    					data.attenuation_quadratic * (distance * distance));
     
#ifdef HAS_MULTIVIEW 
	vec4 spotDir =  normalize(u_view_[view_id] * vec4(data.world_direction.xyz, 0.0));
#else
	vec4 spotDir =  normalize(u_view * vec4(data.world_direction.xyz, 0.0));  
#endif        
     float cosSpotAngle = dot(spotDir.xyz, -lightdir);
     float outer = data.outer_cone_angle;
     float inner = data.inner_cone_angle;
     float inner_minus_outer = inner - outer;  
     float spot = clamp((cosSpotAngle - outer) / inner_minus_outer , 0.0, 1.0);

#ifdef HAS_SHADOWS
    if (data.shadow_map_index >= 0.0)
	{
        vec4 ShadowCoord = vertex.shadow_position;
        float bias = 0.0015;
        vec3 shadowMapPosition = ShadowCoord.xyz / ShadowCoord.w;
        vec3 texcoord = vec3(shadowMapPosition.x, shadowMapPosition.y, data.shadow_map_index);
        vec4 depth = texture(u_shadow_maps, texcoord);
        float distanceFromLight = unpackFloatFromVec4i(depth);

        if (distanceFromLight + bias < shadowMapPosition.z)
            attenuation = 0.5;
	}
#endif
    return Radiance(data.ambient_intensity.xyz,
                    data.diffuse_intensity.xyz,
                    data.specular_intensity.xyz,
                    lightdir.xyz,
                    spot * attenuation);  
                   
}
VertexGVRSpotLight vlight1;

uniform UniformGVRSpotLight light1;
VertexGVRSpotLight vlight0;

uniform UniformGVRSpotLight light0;
vec4 LightPixel(Surface s) {
   vec4 color = vec4(0.0, 0.0, 0.0, 0.0);
   vec4 c;
   float enable;
   Radiance r;
   vlight1.shadow_position = vlight1_shadow_position;
   r = GVRSpotLight(s, light1, vlight1);
   enable = light1.enabled;
   c = vec4(enable, enable, enable, 1) * AddLight(s, r);
   color.xyz += c.xyz;
   color.w = c.w;
   vlight0.shadow_position = vlight0_shadow_position;
   r = GVRSpotLight(s, light0, vlight0);
   enable = light0.enabled;
   c = vec4(enable, enable, enable, 1) * AddLight(s, r);
   color.xyz += c.xyz;
   color.w = c.w;
   return color; }


void main()
{
	Surface s = GVRPhongShader();
#if defined(HAS_LIGHTSOURCES)
	vec4 color = LightPixel(s);
	color = clamp(color, vec4(0), vec4(1));
	fragColor = color;
#else
	fragColor = s.diffuse;
#endif
}
