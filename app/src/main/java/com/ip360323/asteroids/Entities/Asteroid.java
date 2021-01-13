package com.ip360323.asteroids.Entities;

import android.content.res.Resources;

import com.ip360323.asteroids.CollisionMeshManager;
import com.ip360323.asteroids.MeshManager;
import com.ip360323.asteroids.Point2f;
import com.ip360323.asteroids.R;
import com.ip360323.asteroids.Shader;
import com.ip360323.asteroids.TextureManager;
import com.ip360323.asteroids.Utils;

public class Asteroid extends GLEntity {

    private static final float LARGE_SPEED = 4.0f;
    private static final float MEDIUM_SPEED = 8.0f;
    private static final float SMALL_SPEED = 16.0f;

    private static final float LARGE_SCALE = 5.0f;
    private static final float MEDIUM_SCALE = 2.5f;
    private static final float SMALL_SCALE = 1.0f;

    private static final CollisionMeshManager collisionMeshManager = CollisionMeshManager.getInstance();

    public enum Type{
        SMALL,
        MEDIUM,
        LARGE
    }
    public Type asteroidType = Type.LARGE;

    public Asteroid(Shader shader, Resources res, final float x, final float y, Type t){
        asteroidType = t;
        _x = x;
        _y = y;
        _depth = -2; //Just used to make sure the player ship is drawn on top
        float speed;
        if(t == Type.LARGE)
        {
            speed = LARGE_SPEED;
            _scale = LARGE_SCALE;
        }
        else if(t == Type.MEDIUM)
        {
            speed = MEDIUM_SPEED;
            _scale = MEDIUM_SCALE;
        }
        else
        {
            speed = SMALL_SPEED;
            _scale = SMALL_SCALE;
        }


        Point2f dir = Utils.RandomDirection();
        _velX = dir.x * speed;
        _velY = dir.y * speed;

        this.shader = shader;

        int meshChooser = Utils.randomPick(0, 1);
        if(meshChooser == 0)
        {
            this.texture = TextureManager.getInstance().GetTextureHandle(res, R.drawable.astroid_texture2);
            this._mesh = MeshManager.getInstance().LoadMesh(res, R.raw.astroid3, shader);
        }
        else
        {
            this.texture = TextureManager.getInstance().GetTextureHandle(res, R.drawable.astroid_texture);
            this._mesh = MeshManager.getInstance().LoadMesh(res, R.raw.astroid4, shader);
        }

        _rotationX = Utils.between(0f, 360f);
        _rotationY = Utils.between(0f, 360f);
        _rotationZ = Utils.between(0f, 360f);

        collisionMesh = collisionMeshManager.GetMesh(_mesh._radius * _scale);
    }

    @Override
    public EntityEvent update(final double dt) {

        _rotationZ += dt * 5.0f;
        _rotationY += dt * 5.0f;
        _rotationX += dt * 30.0f;
        return super.update(dt);
    }

}