#version 400 core

in vec3 position;
in vec3 color;
in vec3 normal;

out vec3 pass_color;

uniform mat4 transformationMatrix;

0uniform vec3 lightPosition;

void main(void){
	vec4 actualPosition = transformationMatrix * vec4(position,1.0);
	gl_Position = projectionMatrix * viewMatrix * actualPosition;
	pass_color = color;
}