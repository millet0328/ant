#define NEW_LIGHTING
#include "common/inputs.sh"
$input v_texcoord0 OUTPUT_WORLDPOS OUTPUT_NORMAL OUTPUT_TANGENT OUTPUT_BITANGENT OUTPUT_LIGHTMAP_TEXCOORD OUTPUT_COLOR0

#include <bgfx_shader.sh>
#include <bgfx_compute.sh>
#include <shaderlib.sh>
#include "common/camera.sh"

#include "common/transform.sh"
#include "common/utils.sh"
#include "common/cluster_shading.sh"
#include "common/constants.sh"
#include "common/uvmotion.sh"
#include "pbr/lighting.sh"
#include "pbr/indirect_lighting.sh"
#include "pbr/pbr.sh"

#define v_distanceVS v_posWS.w
#ifdef ENABLE_SHADOW
#include "common/shadow.sh"
#endif //ENABLE_SHADOW

uniform vec4 u_construct_color;
#define _ConstructColor u_construct_color

uniform vec4 u_printer_factor;
#define _ConstructY u_printer_factor.x

#include "input_attributes.sh"

vec4 compute_lighting(input_attributes input_attribs, vec4 FragCoord, vec4 v_posWS, vec3 v_normal){

    material_info mi = init_material_info(input_attribs);
    vec3 color = calc_direct_light(mi, FragCoord, v_posWS.xyz);
#   ifdef ENABLE_SHADOW
    vec3 normalWS = normalize(v_normal);
    vec4 posWS = vec4(v_posWS.xyz + normalWS.xyz * u_normal_offset, 1.0);
	color = shadow_visibility(v_distanceVS, posWS, color);
#   endif //ENABLE_SHADOW

#   ifdef ENABLE_IBL
    color += calc_indirect_light(input_attribs, mi, v_distanceVS);
#   endif //ENABLE_IBL
    return vec4(color, input_attribs.basecolor.a) + input_attribs.emissive;

}

void main()
{ 
    #include "attributes_getter.sh"

    float building = 0;
    float t = _ConstructY;
    if(v_posWS.y > _ConstructY + 0.1){
        discard;
    }
    else{
        if(v_posWS.y < _ConstructY){
            //vec4 c = texture2D(s_basecolor, v_texcoord0) * u_basecolor_factor;
            //input_attribs.basecolor = c;
            building = 0;
        }
        else{
            input_attribs.basecolor = _ConstructColor;
            building = 1;
        }

        if(building){
            gl_FragColor = _ConstructColor;
        }
        else if(dot(input_attribs.N, input_attribs.V) < 0){
            gl_FragColor = _ConstructColor;
        }
        else{
            gl_FragColor = compute_lighting(input_attribs, gl_FragCoord, v_posWS, v_normal);
        }
    }
}


