#version 110
// Uniform
uniform mat4 ProjectionModelviewMatrix;
uniform vec4 TexMatrix0_a;
uniform vec4 TexMatrix0_b;
uniform vec4 LightPosition0;
uniform mat4 ModelviewMatrix;
// Varying
varying vec2 VTexCoord0;
varying vec3 VHalfVector0;
varying vec3 VEyeNormal;
varying vec3 VEyeVertex;

void main() {
    vec3 eyeVertex;
    vec3 lightVector, eyeVector;
    gl_Position = ProjectionModelviewMatrix * gl_Vertex;
    VTexCoord0.x = dot(TexMatrix0_a, gl_MultiTexCoord0);
    VTexCoord0.y = dot(TexMatrix0_b, gl_MultiTexCoord0);
    eyeVertex = vec3(ModelviewMatrix * gl_Vertex);
    VEyeVertex = eyeVertex;
    eyeVector = normalize(-eyeVertex);
    lightVector = LightPosition0.xyz;
    VHalfVector0 = lightVector + eyeVector;
    VEyeNormal = vec3(ModelviewMatrix * vec4(gl_Normal, 0.0));
}