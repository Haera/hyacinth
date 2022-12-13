//combined projection and view matrix

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

//"in" attributes from our SpriteBatch

attribute vec2 Position;

attribute vec2 TexCoord;

attribute vec4 Color;



//"out" varyings to our fragment shader

varying vec4 vColor;

varying vec2 vTexCoord;

 

void main() {

	vColor = Color;

	vTexCoord = TexCoord;

	gl_Position = projectionMatrix * viewMatrix * vec4(Position, 0.0, 1.0);

}