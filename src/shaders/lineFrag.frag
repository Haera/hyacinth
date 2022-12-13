#version 400 core

in vec3 pass_color

out vec4 out_Color;

uniform sampler2D modelTexture;
uniform vec3 lightColor;
uniform float shineDamper;
uniform float reflectivity;

void main(void){
	out_Color = vec4(pass_color, 1.0);
}