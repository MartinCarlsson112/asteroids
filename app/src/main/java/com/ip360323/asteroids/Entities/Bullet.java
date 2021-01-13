package com.ip360323.asteroids.Entities;

import android.content.res.Resources;
import com.ip360323.asteroids.CollisionMesh;
import com.ip360323.asteroids.MeshManager;
import com.ip360323.asteroids.R;
import com.ip360323.asteroids.Shader;
import com.ip360323.asteroids.TextureManager;

public class Bullet extends GLEntity {
    private static final float TO_RADIANS = (float)Math.PI/180.0f;
    private static final float SPEED = 120f; //TODO: game play settings
    public static final float TIME_TO_LIVE = 1.0f; //seconds

    private float _ttl = TIME_TO_LIVE;
    public Bullet(Shader shader, Resources res) {
        this.shader = shader;
        _scale = 0.3f;
        this._mesh = MeshManager.getInstance().LoadMesh(res, R.raw.sphere, shader);
        this.texture = TextureManager.getInstance().GetTextureHandle(res, R.drawable.red);
        collisionMesh = new CollisionMesh(_mesh._radius * _scale);
    }

    public void fireFrom(GLEntity source){
        final float theta = source._rotationZ*TO_RADIANS;
        _x = source._x + (float)Math.sin(theta) * (source._width*0.5f);
        _y = source._y - (float)Math.cos(theta) * (source._height*0.5f);
        _velX = source._velX;
        _velY = source._velY;
        _velX += (float)Math.sin(theta) * SPEED;
        _velY -= (float)Math.cos(theta) * SPEED;
        _ttl = TIME_TO_LIVE;
    }
    @Override
    public boolean isDead(){
        return _ttl <= 0;
    }

    @Override
    public EntityEvent update(double dt) {
        if(_ttl > 0) {
            _ttl -= dt;
        }
        return super.update(dt);
    }
    @Override
    public void render(final float[] viewportMatrix){
        if(_ttl > 0) {
            super.render(viewportMatrix);
        }
    }
    @Override
    public void onCollision(final GLEntity that) {
        _ttl = 0;
    }
}