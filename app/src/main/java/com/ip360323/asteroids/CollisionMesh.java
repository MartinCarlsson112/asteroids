package com.ip360323.asteroids;

import android.graphics.PointF;

import java.nio.FloatBuffer;

public class CollisionMesh {

    private final Point3f _min = new Point3f();
    private final Point3f _max = new Point3f();
    public float _width = 0f;
    public float _height = 0f;
    private float _depth = 0f;
    private float _radius = 0f;

    private static final float BOXMESH[] = {
            -0.5f,-0.5f,-0.5f,
            -0.5f,-0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            0.5f, 0.5f,-0.5f,
            -0.5f,-0.5f,-0.5f,
            -0.5f, 0.5f,-0.5f,
            0.5f,-0.5f, 0.5f,
            -0.5f,-0.5f,-0.5f,
            0.5f,-0.5f,-0.5f,
            0.5f, 0.5f,-0.5f,
            0.5f,-0.5f,-0.5f,
            -0.5f,-0.5f,-0.5f,
            -0.5f,-0.5f,-0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f,-0.5f,
            0.5f,-0.5f, 0.5f,
            -0.5f,-0.5f, 0.5f,
            -0.5f,-0.5f,-0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f,-0.5f, 0.5f,
            0.5f,-0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f,-0.5f,-0.5f,
            0.5f, 0.5f,-0.5f,
            0.5f,-0.5f,-0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f,-0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f,-0.5f,
            -0.5f, 0.5f,-0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f,-0.5f,
            -0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            0.5f,-0.5f, 0.5f
    };

    private FloatBuffer vertices;
    private FloatBuffer geometry;
    private PointF[] pointList;

    enum Type
    {
        Sphere, Box
    }

    public float left() {
        return _min.x;
    }
    public float right() {
        return _max.x;
    }
    public float top() {
        return _min.y;
    }
    public float bottom() {
        return _max.y;
    }
    public float centerX() {
        return _min.x + (_width * 0.5f);
    }
    public float centerY() {
        return _min.y + (_height * 0.5f);
    }

    public CollisionMesh(float[] vertices, float scale)
    {
        geometry = FloatBuffer.allocate(vertices.length);
        for (float vertice : vertices) {
            geometry.put(vertice * scale);
        }
        InitPointList(6);
        updateBounds();
    }

    public CollisionMesh(float width, float height, float depth)
    {
        GenerateBox(width * 0.5f, height * 0.5f, depth * 0.5f);
        InitPointList(72); //TODO: MAGIC  VALUE
        updateBounds();
    }

    public CollisionMesh(float radius)
    {
        GenerateSphere(radius);
        InitPointList(40); //TODO: MAGIC  VALUE
        updateBounds();
    }

    private void InitPointList(int size)
    {
        pointList = new PointF[size];
        for(int i = 0; i < pointList.length; i++)
        {
            pointList[i] = new PointF();
        }
    }

    private void GenerateSphere(float radius)
    {
        vertices = FloatBuffer.allocate(12 * 3);
        float t = (float)(1.0 + Math.sqrt(5.0f) / 2.0);
        final float i = 1;

        addVertex(-i, t , 0, radius);
        addVertex(i, t, 0, radius);
        addVertex(-i, -t, 0, radius);
        addVertex(i, -t, 0, radius);

        addVertex(0, -i, t, radius);
        addVertex(0, i, t, radius);
        addVertex(0, -i, -t, radius);
        addVertex(0, i, -t, radius);

        addVertex(t, 0, -i, radius);
        addVertex(t, 0, i, radius);
        addVertex(-t, 0, -i, radius);
        addVertex(-t, 0, i, radius);

        geometry = FloatBuffer.allocate(20 * 3);
        MakeTriangle(0, 11, 5);
        MakeTriangle(0, 5, 1);
        MakeTriangle(0, 1, 7);
        MakeTriangle(0, 7, 10);
        MakeTriangle(0, 10, 11);
        MakeTriangle(1, 5, 9);
        MakeTriangle(5, 11, 4);
        MakeTriangle(11, 10, 2);
        MakeTriangle(10, 7, 6);
        MakeTriangle(7, 1, 6);
        MakeTriangle(3,  9, 4 );
        MakeTriangle(3,  4, 2 );
        MakeTriangle(3,  2, 6 );
        MakeTriangle(3,  6, 8 );
        MakeTriangle(3,  8, 9 );
        MakeTriangle(4,  9, 5 );
        MakeTriangle(2,  4, 11);
        MakeTriangle(6,  2, 10);
        MakeTriangle(8,  6, 7 );
        MakeTriangle(9,  8, 1 );
    }

    private void MakeTriangle(int a, int b, int c)
    {
        geometry.put(vertices.get(a));
        geometry.put(vertices.get(b));
        geometry.put(vertices.get(c));
    }

    private void GenerateBox(float width, float height, float depth)
    {
        geometry = FloatBuffer.allocate(BOXMESH.length);
        int boundsIndex = 0;
        float bounds[] = {width, height, depth};

        for (float aBOXMESH : BOXMESH) {
            if (boundsIndex > 2) {
                boundsIndex = 0;
            }
            geometry.put(aBOXMESH * bounds[boundsIndex]);
            boundsIndex++;
        }
    }


    private void updateBounds(){
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        for(int i = 0; i < geometry.capacity(); i+=3) {
            final float x = geometry.get(i+0);
            final float y = geometry.get(i+1);
            final float z = geometry.get(i+2);
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }

        _min.set(minX, minY, minZ);
        _max.set(maxX, maxY, maxZ);
        _width = maxX - minX;
        _height = maxY - minY;
        _depth = maxZ - minZ;
        _radius = Math.max(Math.max(_width, _height), _depth) * 0.5f;
    }

    private void addVertex(float x, float y, float z, float radius)
    {
        double length = Math.sqrt(x * x + y * y + z * z);
        vertices.put((float)(x/length) * radius);
        vertices.put((float)(y/length) * radius);
        vertices.put((float)(z/length) * radius);
    }

    public PointF[] getPointList(final float offsetX, final float offsetY, final float facingAngleDegrees){
        float[] verts = new float[geometry.capacity()];
        final double sinTheta = Math.sin(facingAngleDegrees*Utils.TO_RAD);
        final double cosTheta = Math.cos(facingAngleDegrees*Utils.TO_RAD);

        geometry.position(0);
        geometry.get(verts); //bulk transfer all verts
        geometry.position(0);
        int index = 0;
        for (int i = 0; i < geometry.capacity(); i += 3) {
            final float x = verts[i + 0] + offsetX;
            final float y = verts[i + 1] + offsetY;

            final float rotatedX = (float) (x * cosTheta - y * sinTheta) + offsetX;
            final float rotatedY = (float) (y * cosTheta + x * sinTheta) + offsetY;
            //final float z = verts[i + Z];
            pointList[index++].x = x;
            pointList[index++].y = y;
        }
        return pointList;
    }
}
