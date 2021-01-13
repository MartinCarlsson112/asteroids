attribute vec3 position;
attribute vec2 texCoord;
attribute vec3 normals;

//uniform mat4 view;
//uniform mat4 model;

uniform mat4 modelViewProjection;
varying vec2 TexCoord;
varying vec3 Normal;
//out vec3 FragPos;

void main() {

    gl_Position =  modelViewProjection * vec4(position, 1.0);
    gl_PointSize = 8.0;
    TexCoord = texCoord;
    //FragPos = vec3(model * vec4(pos, 1.0f));
    Normal = normals;
}