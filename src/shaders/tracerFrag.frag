#version 110
// Uniform
uniform sampler2D Texture0;
uniform vec4 LightPosition0;
uniform vec4 AllLightAmbient_MaterialAmbient;
uniform vec4 LightMaterialDiffuse0;
uniform vec4 LightMaterialSpecular0;
uniform float MaterialShininess;
// Varying
varying vec2 TexCoord0;
varying vec3 HalfVector0;
varying vec3 EyeNormal;

// eyeNormal must be normalized already
// lightVector must be normalized already. xyz is lightvector and w is light distance from vertex
// halfVector must be normalized already
//
// output diffuse color and output specular color
// Then do diffuse * texture_color + specular
// diffuse.a = material_diffuse.a
void ComputeDirectionalLight(out vec4 diffuseColor, out vec4 specularColor, in vec3 eyeNormal, in vec3 lightVector, in vec3 halfVector, in vec4 lightMaterialDiffuse, in vec4 lightMaterialSpecular) {
  float dotProduct;
  dotProduct = clamp(dot(eyeNormal, lightVector), 0.0, 1.0);
  diffuseColor = dotProduct * lightMaterialDiffuse;
  specularColor = vec4(0.0);
  dotProduct = clamp(dot(eyeNormal, halfVector), 0.0, 1.0);
  if(dotProduct>0.0)
      specularColor = pow(dotProduct, MaterialShininess) * lightMaterialSpecular;
}
// --------------------
void main() {
    vec4 texel, diffuseColor, specularColor;
    vec4 ColorSum;
    vec3 eyeNormal, halfVector;
    texel = texture2D(Texture0, TexCoord0);
    eyeNormal = normalize(EyeNormal);
    halfVector = normalize(HalfVector0);
    ComputeDirectionalLight(diffuseColor, specularColor, eyeNormal, LightPosition0.xyz, halfVector, LightMaterialDiffuse0, LightMaterialSpecular0);
    ColorSum = (AllLightAmbient_MaterialAmbient + diffuseColor) * texel + specularColor;
    ColorSum.a = texel.a * LightMaterialDiffuse0.a;
    gl_FragColor = clamp(ColorSum, 0.0, 1.0);
  }