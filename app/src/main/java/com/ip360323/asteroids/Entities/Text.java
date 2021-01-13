package com.ip360323.asteroids.Entities;

import android.opengl.Matrix;

import com.ip360323.asteroids.GLManager;
import com.ip360323.asteroids.GLPixelFont;
import com.ip360323.asteroids.Mesh;
import com.ip360323.asteroids.Shader;

public class Text extends GLEntity {
    private static final GLPixelFont FONT = new GLPixelFont();
    private static final float GLYPH_WIDTH = FONT.WIDTH;
    private static final float GLYPH_HEIGHT = FONT.HEIGHT;
    private static final float GLYPH_SPACING = 1f;

    private static final float TEXT_COLOR[] = {255/255f, 255/255f, 255/255f, 1f}; //RGBA

    private Mesh[] _meshes = null;
    private float _spacing = GLYPH_SPACING; //spacing between characters
    private float _glyphWidth = GLYPH_WIDTH;
    private float _glyphHeight = GLYPH_HEIGHT;

    public Text(final Shader shader,  final String s, final float x, final float y) {
        this.shader = shader;
        setString(s);
        _x = x;
        _y = y;
        setScale(0.75f); //TODO: magic value
    }

    @Override
    public void render(final float[] viewportMatrix){
        final int OFFSET = 0;
        for(int i = 0; i < _meshes.length; i++){
            if(_meshes[i] == null){ continue; }
            Matrix.setIdentityM(modelMatrix, OFFSET); //reset model matrix
            Matrix.translateM(modelMatrix, OFFSET, _x + (_glyphWidth+_spacing)*i, _y, _depth);
            Matrix.scaleM(modelMatrix, OFFSET, _scale, _scale, 1f);
            Matrix.multiplyMM(viewportModelMatrix, OFFSET, viewportMatrix, OFFSET, modelMatrix, OFFSET);
            GLManager.draw(_meshes[i], shader, viewportModelMatrix, TEXT_COLOR);
        }
    }
    private void setScale(float factor){
        _scale = factor;
        _spacing = GLYPH_SPACING*_scale;
        _glyphWidth = GLYPH_WIDTH*_scale;
        _glyphHeight = GLYPH_HEIGHT*_scale;
        _height = _glyphHeight;
        _width = (_glyphWidth+_spacing)*_meshes.length;
    }

    public void setString(final String s){
        _meshes = FONT.getString(s);
    }
}