$input v_texcoord0

//v_texcoord0: xy for uv, z for line counter

#include <bgfx_shader.sh>
#include "polyline/polyline.sh"
#include "common/uvmotion.sh"
#ifdef ENABLE_POLYLINE_TEXTURE
SAMPLER2D(s_tex, 0);
#endif //ENABLE_POLYLINE_TEXTURE
#ifdef ENABLE_POLYLINE_EMISSIVE_TEXTURE
SAMPLER2D(s_emissive, 1);
uniform vec4 u_emissive_factor;
#endif //ENABLE_POLYLINE_EMISSIVE_TEXTURE

void main() {
    vec4 c  = u_color;

    vec2 uv = uv_motion(v_uv);
#ifdef ENABLE_POLYLINE_TEXTURE
    c *= texture2D(s_tex, uv);
#endif //POLYLINE_TEXTURE

#ifdef ENABLE_POLYLINE_EMISSIVE_TEXTURE
    c += texture2D(s_emissive, uv) * u_emissive_factor;
#endif //ENABLE_POLYLINE_EMISSIVE_TEXTURE

#ifdef ENABLE_POLYLINE_DASH
    //c.a *= ceil(mod(v_counters + u_dash_offset, u_dash_array) - (u_dash_array * u_dash_ratio));
    float dash = mod(v_counters, u_dash_round);
    c.a *= step(dash, u_dash_ratio);
#endif //ENABLE_POLYLINE_DASH

    gl_FragColor = c;
}