attribute vec4 position;
uniform mat4 modelViewProjection;

void main() {
    gl_Position = modelViewProjection * position;
    gl_PointSize = 8.0;
}