package com.ip360323.asteroids.Entities;

import android.content.res.Resources;
import android.opengl.Matrix;

import com.ip360323.asteroids.GLManager;
import com.ip360323.asteroids.MeshManager;
import com.ip360323.asteroids.R;
import com.ip360323.asteroids.Shader;
import com.ip360323.asteroids.TextureManager;

public class PlayerLifeEntity  extends  GLEntity{


    public PlayerLifeEntity(Shader shader, Resources res, final float x, final float y){
        super();
        _x = x;
        _y = y;
        _depth = -10;
        _scale = 2.0f;
        _rotationX = 90;
        this.shader = shader;
        this.texture = TextureManager.getInstance().GetTextureHandle(res, R.drawable.spaceship2);
        _mesh = MeshManager.getInstance().LoadMesh(res, R.raw.spaceship2, shader);
    }


    @Override
    public EntityEvent update(double dt){
        _rotationY += dt * 5.0f;
        return super.update(dt);
    }


    @Override
    public void render(final float[] viewportMatrix){
        final int OFFSET = 0;
        //reset the model matrix and then translate (move) it into world space
        Matrix.setIdentityM(modelMatrix, OFFSET); //reset model matrix
        Matrix.translateM(modelMatrix, OFFSET, _x, _y, _depth);
        //viewportMatrix * modelMatrix combines into the viewportModelMatrix
        //NOTE: projection matrix on the left side and the model matrix on the right side.
        Matrix.multiplyMM(viewportModelMatrix, OFFSET, viewportMatrix, OFFSET, modelMatrix, OFFSET);
        //apply a rotation around the Z-axis to our modelMatrix. Rotation is in degrees.
        Matrix.setRotateM(modelMatrix, OFFSET, 0, 1.0f, 0.0f, 0.0f);
        Matrix.setRotateM(modelMatrix, OFFSET, 0, 0.0f, 1.0f, 0.0f);
        Matrix.setRotateM(modelMatrix, OFFSET, 0, 0.0f, 0.0f, 1.0f);
        Matrix.rotateM(modelMatrix, OFFSET, _rotationZ, 0.0f, 0.0f, 1.0f);
        Matrix.rotateM(modelMatrix, OFFSET, _rotationY, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMatrix, OFFSET, _rotationX, 1.0f, 0.0f, 0.0f);

        //apply scaling to our modelMatrix, on the x and y axis only.
        Matrix.scaleM(modelMatrix, OFFSET, _scale, _scale, _scale);
        //finally, multiply the rotated & scaled model matrix into the model-viewport matrix
        //creating the final rotationViewportModelMatrix that we pass on to OpenGL
        Matrix.multiplyMM(rotationViewportModelMatrix, OFFSET, viewportModelMatrix, OFFSET, modelMatrix, OFFSET);

        GLManager.draw(_mesh, texture, rotationViewportModelMatrix);
    }
}
