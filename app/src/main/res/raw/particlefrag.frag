precision mediump float; //we don't need high precision floats for fragments
varying vec2 TexCoord;
uniform sampler2D albedoMap;

void main()
{
    gl_FragColor = texture2D(albedoMap, TexCoord);
}