package com.ip360323.asteroids;

import android.opengl.GLES20;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class OBJMesh {

    final Shader shader;
    private IntBuffer bufferObjects;

    private final int posLoc;
    private final int normalLoc;
    private final int uvLoc;
    public int vertexCount = 0;

    private final Point3f _min = new Point3f();
    private final Point3f _max = new Point3f();
    private float _width = 0f;
    public float _height = 0f;
    private float _depth = 0f;
    public float _radius = 0f;

    private static final int POSVBO = 0;
    private static final int NORMVBO = 1;
    private static final int UVVBO = 2;
    private static final int FLOATSIZE = 4;

    OBJMesh(Shader shader) {
        this.shader = shader;
        posLoc = shader.GetAttribLocation("position");
        normalLoc = shader.GetAttribLocation("normals");
        uvLoc = shader.GetAttribLocation("texCoord");
    }



    void InitBuffers(FloatBuffer vertices, FloatBuffer uvs, FloatBuffer normals)
    {
        bufferObjects = IntBuffer.allocate(3);
        vertexCount = (int)(vertices.capacity() / 3.0f);
        //0 is position
        //1 is normal
        //2 is uv
        vertices.position(0);
        uvs.position(0);
        normals.position(0);

        GLES20.glGenBuffers(3, bufferObjects);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferObjects.get(POSVBO));
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices.capacity() * FLOATSIZE, vertices, GLES20.GL_STATIC_DRAW);
        GLES20.glEnableVertexAttribArray(posLoc);
        GLES20.glVertexAttribPointer(posLoc, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferObjects.get(NORMVBO));
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, normals.capacity() * FLOATSIZE, normals, GLES20.GL_STATIC_DRAW);
        GLES20.glEnableVertexAttribArray(normalLoc);
        GLES20.glVertexAttribPointer(normalLoc, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferObjects.get(UVVBO));
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, uvs.capacity() * FLOATSIZE, uvs, GLES20.GL_STATIC_DRAW);
        GLES20.glEnableVertexAttribArray(uvLoc);
        GLES20.glVertexAttribPointer(uvLoc, 2, GLES20.GL_FLOAT, false,0,0);

    }

    public void updateBounds(FloatBuffer vertices){
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        for(int i = 0; i < vertices.capacity(); i+=3) {
            final float x = vertices.get(i+0);
            final float y = vertices.get(i+1);
            final float z = vertices.get(i+2);
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

    public void normalize(FloatBuffer vertexBuffer) {
        final double inverseW = (_width  == 0.0) ? 0.0 : 1/_width;
        final double inverseH = (_height == 0.0) ? 0.0 : 1/_height;
        final double inverseD = (_depth  == 0.0) ? 0.0 : 1/_depth;
        for (int i = 0; i < vertexBuffer.capacity(); i += 3) {
            final double dx = vertexBuffer.get(i + 0) - _min.x; //"d" for "delta" or "difference"
            final double dy = vertexBuffer.get(i + 1) - _min.y;
            final double dz = vertexBuffer.get(i + 2) - _min.z;
            final double xNorm = 2.0 * (dx * inverseW) - 1.0; //(dx * inverseW) is equivalent to (dx / _width)
            final double yNorm = 2.0 * (dy * inverseH) - 1.0; //but avoids the risk of division-by-zero.
            final double zNorm = 2.0 * (dz * inverseD) - 1.0;
            vertexBuffer.put(i+0, (float)xNorm);
            vertexBuffer.put(i+1, (float)yNorm);
            vertexBuffer.put(i+2, (float)zNorm);
        }
        updateBounds(vertexBuffer);
        Utils.require(_width <= 2.0f, "x-axis is out of range!");
        Utils.require(_height <= 2.0f, "y-axis is out of range!");
        Utils.require(_depth <= 2.0f, "z-axis is out of range!");
        Utils.expect((_min.x >= -1.0f && _max.x <= 1.0f), "", "normalized x["+_min.x +", "+_max.x +"] expected x[-1.0, 1.0]");
        Utils.expect((_min.y >= -1.0f && _max.y <= 1.0f), "", "normalized y["+_min.y +", "+_max.y +"] expected y[-1.0, 1.0]");
        Utils.expect((_min.z >= -1.0f && _max.z <= 1.0f), "", "normalized z["+_min.z +", "+_max.z +"] expected z[-1.0, 1.0]");
    }


    void BindBuffers()
    {
        //position
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferObjects.get(POSVBO));
        GLES20.glEnableVertexAttribArray(posLoc);
        GLES20.glVertexAttribPointer(posLoc, 3, GLES20.GL_FLOAT, false, 0 , 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferObjects.get(NORMVBO));
        GLES20.glEnableVertexAttribArray(normalLoc);
        GLES20.glVertexAttribPointer(normalLoc, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferObjects.get(UVVBO));
        GLES20.glEnableVertexAttribArray(uvLoc);
        GLES20.glVertexAttribPointer(uvLoc, 2, GLES20.GL_FLOAT, false, 0, 0);
    }

    public void Delete()
    {
        GLES20.glDeleteBuffers(3, bufferObjects);
    }

}
