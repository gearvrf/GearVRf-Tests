precision mediump float;

layout (std140) uniform Transform_ubo
{
 #ifdef HAS_MULTIVIEW
     mat4 u_view_[2];
     mat4 u_mvp_[2];
     mat4 u_mv_[2];
     mat4 u_mv_it_[2];
 #else
     mat4 u_view;
     mat4 u_mvp;
     mat4 u_mv;
     mat4 u_mv_it;
 #endif
     mat4 u_model;
     mat4 u_view_i;
     vec4 u_right;
};

in vec3 a_position;
in vec2 a_texcoord;
in vec4 a_color;
out vec4 v_color;
out vec2 v_texcoord;

void main() {
  gl_Position = u_mvp * vec4(a_position, 1.0);
  v_color = a_color;
  v_texcoord = a_texcoord;
}
