package com.ip360323.asteroids.Entities;

import android.content.res.Resources;
import android.graphics.PointF;

import com.ip360323.asteroids.CollisionDetection;
import com.ip360323.asteroids.CollisionMesh;
import com.ip360323.asteroids.Input;
import com.ip360323.asteroids.InputManager;
import com.ip360323.asteroids.MeshManager;
import com.ip360323.asteroids.ParticleSystem;
import com.ip360323.asteroids.R;
import com.ip360323.asteroids.Shader;
import com.ip360323.asteroids.TextureManager;
import com.ip360323.asteroids.Utils;

public class Player extends GLEntity {

    public static final float TIME_BETWEEN_SHOTS = 0.25f; //seconds. TODO: game play setting!
    private float _bulletCooldown = 0;
    private static final String TAG = "Player";
    private static final float ROTATION_VELOCITY = 360f; //TODO: game play values!
    private static final float THRUST = 3f;
    private static final float DRAG = 0.99f;
    private final float respawnTimer = 1.0f;
    private float respawnAccu = 0;
    public boolean respawning = false;
    public int lifes = 3;

    private final ParticleSystem particleSystem;
    private final int particleTexture;

    private final float spawnX;
    private final float spawnY;

    private final Input input = Input.getInstance();
    private final float[] vertices = { // in counterclockwise order:
            0.0f,  0.5f, 0.0f, 		// top
            -0.5f, -0.5f, 0.0f,		// bottom left
            0.5f, -0.5f, 0.0f  		// bottom right
    };

    public Player(Shader shader, Resources res, ParticleSystem particles, final float x, final float y){
        super();
        spawnX = x;
        spawnY = y;
        _x = x;
        _y = y;
        _scale = 5.0f;
        _rotationX = 90;
        this.shader = shader;
        particleTexture = TextureManager.getInstance().GetTextureHandle(res, R.drawable.particleblue);
        this.texture = TextureManager.getInstance().GetTextureHandle(res, R.drawable.spaceship2);
        _mesh = MeshManager.getInstance().LoadMesh(res, R.raw.spaceship2, shader);
        collisionMesh = new CollisionMesh(vertices, _scale);
        this.particleSystem = particles;
    }

    private void Spawn()
    {
        respawning = false;
        _x = spawnX;
        _y = spawnY;
        _rotationX = 90;
        _rotationZ = 0;
        _velX = 0;
        _velY = 0;
    }


    private void AccelerationParticle(float x, float y, float angle)
    {
        particleSystem.SpawnParticle(x, y, 0, angle, 0.0f, 3.0f, 0.3f, particleTexture);
    }

    @Override
    public EntityEvent update(double dt){
        if(respawning) {
            respawnAccu += dt;
            if(respawnAccu > respawnTimer)
            {
                Spawn();
                respawnAccu = 0;
            }
        }
        else
        {
            _rotationZ += (dt*ROTATION_VELOCITY) * input.GetAxis().x;
            if(input.GetButton(InputManager.GOFORWARD)){
                final float theta = _rotationZ* (float) Utils.TO_RAD;
                final float height = _mesh._height * _scale * 0.5f;
                float x = -((float)Math.sin(theta) * height);
                float y = ((float)Math.cos(theta) * height);


                AccelerationParticle(centerX() + x, centerY() + y, -theta);

                _velX += (float)Math.sin(theta) * THRUST;
                _velY -= (float)Math.cos(theta) * THRUST;
            }
            _velX *= DRAG;
            _velY *= DRAG;

            _bulletCooldown -= dt;
            if(Input.getInstance().GetButtonDown(InputManager.SHOOT) && _bulletCooldown <= 0){
                entityEvent.wantsToShoot = true;
            }
        }
        return super.update(dt);
    }

    @Override
    public void render(final float[] viewportMatrix)
    {
        if(!respawning)
        {
            super.render(viewportMatrix);
        }
    }

    @Override
    public void onCollision(final GLEntity that)
    {
        if(that instanceof Asteroid)
        {
            if(!respawning)
            {
                lifes--;
                if(lifes > 0)
                {
                    respawning = true;
                }
                else
                {
                    _isAlive = false;
                }
            }
        }
    }

    @Override
    public boolean isColliding(final GLEntity that){
        if(!areBoundingSpheresOverlapping(this, that)){
            return false;
        }
        final PointF[] shipHull = getPointList();
        final PointF[] asteroidHull = that.getPointList();
        if(CollisionDetection.polygonVsPolygon(shipHull, asteroidHull)){
            return true;
        }
        return CollisionDetection.polygonVsPoint(asteroidHull, _x, _y); //finally, check if we're inside the asteroid
    }

    public void Shoot()
    {
        _bulletCooldown = TIME_BETWEEN_SHOTS;
    }

}