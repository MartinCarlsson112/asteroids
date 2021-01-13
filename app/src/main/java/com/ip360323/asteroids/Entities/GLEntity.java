package com.ip360323.asteroids.Entities;

import android.graphics.PointF;
import android.opengl.Matrix;

import com.ip360323.asteroids.CollisionMesh;
import com.ip360323.asteroids.GLManager;
import com.ip360323.asteroids.Game;
import com.ip360323.asteroids.OBJMesh;
import com.ip360323.asteroids.Shader;

public class GLEntity {

    public static class EntityEvent
    {
        public boolean wantsToShoot = false;
    }
    CollisionMesh collisionMesh;

    OBJMesh _mesh;
    int texture;
    Shader shader;
    final EntityEvent entityEvent = new EntityEvent();
    float _x = 0.0f;
    float _y = 0.0f;
    float _depth = 0.0f; //we'll use _depth for z-axis

    float _scale = 1f;
    float _rotationX = 0f;
    float _rotationY = 0f;
    float _rotationZ = 0f;


    float _width = 0.0f;
    float _height = 0.0f;
    float _velX = 0f;
    float _velY = 0f;

    static final float[] modelMatrix = new float[4*4];
    static final float[] viewportModelMatrix = new float[4*4];
    static final float[] rotationViewportModelMatrix = new float[4*4];

    boolean _isAlive = true;
    public boolean isDead(){
        return !_isAlive;
    }

    GLEntity(){}

    EntityEvent update(final double dt) {
        _x += _velX * dt;
        _y += _velY * dt;
        if (left() > Game.WORLD_WIDTH) {
            setRight(0);
        } else if (right() < 0) {
            setLeft(Game.WORLD_WIDTH);
        }
        if (top() > Game.WORLD_HEIGHT) {
            setBottom(0);
        } else if (bottom() < 0) {
            setTop(Game.WORLD_HEIGHT);
        }
        return entityEvent;
    }

    public float centerX() {
        return _x; //assumes our mesh has been centered on [0,0] (normalized)
    }

    public float centerY() {
        return _y; //assumes our mesh has been centered on [0,0] (normalized)
    }

    private float left() {
        return _x + collisionMesh.left();
    }
    private float right() {
        return _x + collisionMesh.right();
    }
    private void setLeft(final float leftEdgePosition) {
        _x = leftEdgePosition - collisionMesh.left();
    }

    private void setRight(final float rightEdgePosition) {
        _x = rightEdgePosition - collisionMesh.right();
    }

    private float top() {
        return _y+collisionMesh.top();
    }
    private float bottom() {
        return _y + collisionMesh.bottom();
    }
    private void setTop(final float topEdgePosition) {
        _y = topEdgePosition - collisionMesh.top();
    }
    private void setBottom(final float bottomEdgePosition) {
        _y = bottomEdgePosition - collisionMesh.bottom();
    }

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

    @SuppressWarnings("UnusedReturnValue")
    static boolean getOverlap(final GLEntity a, final GLEntity b, final PointF overlap) {
        overlap.x = 0.0f;
        overlap.y = 0.0f;
        final float centerDeltaX = a.centerX() - b.centerX();
        final float halfWidths = (a.collisionMesh._width + b.collisionMesh._width) * 0.5f;
        float dx = Math.abs(centerDeltaX); //cache the abs, we need it twice

        if (dx > halfWidths) return false ; //no overlap on x == no collision

        final float centerDeltaY = a.centerY() - b.centerY();
        final float halfHeights = (a.collisionMesh._height + b.collisionMesh._height) * 0.5f;
        float dy = Math.abs(centerDeltaY);

        if (dy > halfHeights) return false ; //no overlap on y == no collision

        dx = halfWidths - dx; //overlap on x
        dy = halfHeights - dy; //overlap on y
        if (dy < dx) {
            overlap.y = (centerDeltaY < 0 ) ? -dy : dy;
        } else if (dy > dx) {
            overlap.x = (centerDeltaX < 0 ) ? -dx : dx;
        } else {
            overlap.x = (centerDeltaX < 0 ) ? -dx : dx;
            overlap.y = (centerDeltaY < 0 ) ? -dy : dy;
        }
        return true ;
    }

    private float radius() {
        //use the longest side to calculate radius
        return (collisionMesh._width > collisionMesh._height) ? collisionMesh._width * 0.5f : collisionMesh._height * 0.5f;
    }

    //Some good reading on bounding-box intersection tests:
//https://gamedev.stackexchange.com/questions/586/what-is-the-fastest-way-to-work-out-2d-bounding-box-intersection
    private static boolean isAABBOverlapping(final GLEntity a, final GLEntity b) {
        return !(a.right() <= b.left()
                || b.right() <= a.left()
                || a.bottom() <= b.top()
                || b.bottom() <= a.top());
    }

    static boolean areBoundingSpheresOverlapping(final GLEntity a, final GLEntity b) {
        final float dx = a.centerX()-b.centerX(); //delta x
        final float dy = a.centerY()-b.centerY();
        final float distanceSq = (dx*dx + dy*dy);
        final float thisRad = a.radius();
        final float thatRad = b.radius();
        final float minDistance = thisRad + thatRad;
        final float minDistanceSq = minDistance*minDistance;
        return distanceSq < minDistanceSq;
    }

    PointF[] getPointList(){
        return collisionMesh.getPointList(_x, _y, _rotationZ);
    }

    public boolean isColliding(final GLEntity that) {
        if (this == that) {
            throw new AssertionError("isColliding: You shouldn't test Entities against themselves!");
        }
        return GLEntity.isAABBOverlapping(this, that);
    }

    public void onCollision(final GLEntity that) {
        _isAlive = false;
    }
}