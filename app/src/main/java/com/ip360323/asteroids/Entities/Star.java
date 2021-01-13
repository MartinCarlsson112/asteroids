package com.ip360323.asteroids.Entities;

import android.content.res.Resources;

import com.ip360323.asteroids.MeshManager;
import com.ip360323.asteroids.R;
import com.ip360323.asteroids.Shader;
import com.ip360323.asteroids.TextureManager;
import com.ip360323.asteroids.Utils;

public class Star extends GLEntity {

    public Star(Shader shader, Resources res, float x, final float y){
        super();
        _x = x;
        _y = y;
        _depth = -11;
        _scale = Utils.between(0.02f, 0.05f);
        this.shader = shader;
        this.texture = TextureManager.getInstance().GetTextureHandle(res, R.drawable.star);
        _mesh = MeshManager.getInstance().LoadMesh(res, R.raw.sphere, shader);
    }
}