package com.ip360323.asteroids;

public class Point2f {
    public float x = 0.0f;
    public float y = 0.0f;

    public Point2f(){}
    public Point2f(final float x, final float y){
        set(x, y);
    }
    public Point2f(final float[] p){
        set(p);
    }

    private void set(final float x, final float y){
        this.x = x;
        this.y = y;
    }

    private void set(final float[] p){
        Utils.require(p.length >= 2);
        x = p[0];
        y = p[1];
    }
}
