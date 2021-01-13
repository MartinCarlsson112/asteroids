package com.ip360323.asteroids;

public class Point3f {
    public float x = 0.0f;
    public float y = 0.0f;
    public float z = 0.0f;

    public Point3f(){}
    public Point3f(final float x, final float y, final float z){
        set(x, y, z);
    }
    public Point3f(final float[] p){
        set(p);
    }

    public void set(final float x, final float y, final float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private void set(final float[] p){
        Utils.require(p.length == 3);
        x = p[0];
        y = p[1];
        z = p[2];
    }

    private float Magnitude()
    {
        return (float)Math.sqrt(Math.pow(x, 2.0f) + Math.pow(y, 2.0f) + Math.pow(z, 2.0f));
    }

    public void Normalize()
    {
        float magnitude = Magnitude();
        float normalizationFactor = 1.0f / magnitude;
        x *= normalizationFactor;
        y *= normalizationFactor;
        z *= normalizationFactor;
    }

    public final  float distanceSquared(Point3f that){
        final float dx = this.x -that.x;
        final float dy = this.y -that.y;
        final float dz = this.z -that.z;
        return dx*dx+dy*dy+dz*dz;
    }
    public final float distance(Point3f that){
        final float dx = this.x -that.x;
        final float dy = this.y -that.y;
        final float dz = this.z -that.z;
        return (float) Math.sqrt(dx*dx+dy*dy+dz*dz);
    }
    public final  float distanceL1(Point3f that){
        return(Math.abs(this.x -that.x) + Math.abs(this.y -that.y) + Math.abs(this.z -that.z));
    }
}