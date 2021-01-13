precision mediump float; //we don't need high precision floats for fragments

uniform vec4 color;

void main()
{
    gl_FragColor = color;
}