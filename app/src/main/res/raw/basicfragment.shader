precision mediump float; //we don't need high precision floats for fragments

varying vec2 TexCoord;
varying vec3 Normal; //useful for lighting

uniform sampler2D albedoMap;

void main()
{
    float test = Normal.x * 0.0001; // If the attribute does not affect the output color,
                                //GLES optimizes it away,
                                //which returns -1 when trying to get the attrib location...
    gl_FragColor = texture2D(albedoMap, TexCoord) + test;
}