attribute vec3 position;
attribute vec2 texCoord;

uniform mat4 modelViewProjection;
varying vec2 TexCoord;

void main() {
    TexCoord = texCoord;
    gl_Position = modelViewProjection * vec4(position, 1.0);
}