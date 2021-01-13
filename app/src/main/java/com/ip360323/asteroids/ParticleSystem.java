package com.ip360323.asteroids;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

//Particles all use the same quad which is sent as a VBO
public class ParticleSystem {

    private static final int POSVBO =0;
    private static final int UVVBO =1;

    private static final float[] modelMatrix = new float[4*4];
    private static final float[] viewportModelMatrix = new float[4*4];
    private static final float[] rotationViewportModelMatrix = new float[4*4];
    private final Shader particleShader;
    private static final int FLOAT_SIZE = 4;
    private static final int OFFSET = 0;
    private int lastUsedParticle = 0;
    private int posLoc;
    private int uvLoc;
    static class Particle
    {
        float x = 0;
        float y = 0;
        float z = 0;
        int textureHandle = 0;
        float ttl = 0;
        private float startttl = 0;
        float angle = 0;
        float size = 0;
        private float startsize = 0;
        float speed = 0;

        boolean isAlive()
        {
            return ttl > 0;
        }

        boolean isDead()
        {
            return ttl <= 0;
        }
    }

    private static final int MAX_PARTICLES = 200;
    private final Particle[] particleContainer = new Particle[MAX_PARTICLES];

    private IntBuffer bufferObjects;

    private static final float[] vertices = {
            -0.5f,  0.5f, 0.0f,			// Top left
            0.5f, -0.5f, 0.0f,			// Bottom right
            -0.5f, -0.5f, 0.0f,			// Bottom left


            -0.5f,  0.5f, 0.0f,			// Top left
            0.5f,  0.5f, 0.0f,			// Top right
            0.5f, -0.5f, 0.0f,			// Bottom right

    };

    private static final float[] uvs = {
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,

            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

    };

    ParticleSystem(Resources res)
    {
        particleShader = new Shader();
        particleShader.LoadShader(res, R.raw.particlevert, R.raw.particlefrag);
        for(int i = 0; i < particleContainer.length; i++)
        {
            particleContainer[i] = new Particle();
        }
        InitBuffers();

    }

    public void Clear()
    {
        for (Particle aParticleContainer : particleContainer) {
            aParticleContainer.ttl = 0;
        }
    }

    public void Delete()
    {
        GLES20.glDeleteBuffers(2, bufferObjects);
        particleShader.DeleteProgram();
    }

    private void InitBuffers()
    {
        posLoc = particleShader.GetAttribLocation("position");
        uvLoc = particleShader.GetAttribLocation("texCoord");
        FloatBuffer vertexBuffer = FloatBuffer.allocate(vertices.length);
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        FloatBuffer uvBuffer = FloatBuffer.allocate(uvs.length);
        uvBuffer.put(uvs);
        uvBuffer.position(0);

        bufferObjects = IntBuffer.allocate(2);
        GLES20.glGenBuffers(2, bufferObjects);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferObjects.get(POSVBO));
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * FLOAT_SIZE, vertexBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glEnableVertexAttribArray(posLoc);
        GLES20.glVertexAttribPointer(posLoc, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferObjects.get(UVVBO));
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, uvs.length * FLOAT_SIZE, uvBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glEnableVertexAttribArray(uvLoc);
        GLES20.glVertexAttribPointer(uvLoc, 2, GLES20.GL_FLOAT, false, 0, 0);
        GLManager.checkGLError("ParticleSystem Init");
    }

    //from http://www.opengl-tutorial.org/intermediate-tutorials/billboards-particles/particles-instancing/
    private int FindUnusedParticle()
    {
        for(int i = lastUsedParticle; i <MAX_PARTICLES; i++)
        {
            if(particleContainer[i].isDead())
            {
                lastUsedParticle = i;
                return i;
            }
        }

        for(int i = 0; i < lastUsedParticle; i++)
        {
            if(particleContainer[i].isDead())
            {
                lastUsedParticle = i;
                return i;
            }
        }
        return 0;
    }

    public void SpawnParticle(float x, float y, float z, float angle, float speed, float size, float ttl, int texHandle)
    {
        int index = FindUnusedParticle();
        particleContainer[index].ttl = ttl;
        particleContainer[index].startttl = ttl;
        particleContainer[index].x = x;
        particleContainer[index].y = y;
        particleContainer[index].z = z;
        particleContainer[index].textureHandle = texHandle;
        particleContainer[index].size = size;
        particleContainer[index].startsize = size;
        particleContainer[index].speed = speed;
        particleContainer[index].angle = angle;
    }


    public void Update(float deltaTime) {
        for (Particle aParticleContainer : particleContainer) {
            if (aParticleContainer.isAlive()) {
                aParticleContainer.ttl -= deltaTime;
                //apply physics
                double theta = aParticleContainer.angle;
                double cos = Math.cos(theta);
                double sin = Math.sin(theta);
                aParticleContainer.x += cos * deltaTime * aParticleContainer.speed;
                aParticleContainer.y += sin * deltaTime * aParticleContainer.speed;

                //Size over life time
                aParticleContainer.size =
                        aParticleContainer.startsize *
                                (aParticleContainer.ttl / aParticleContainer.startttl);

            }
        }
    }

    private void BindBuffers()
    {
        //position
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferObjects.get(POSVBO));
        GLES20.glEnableVertexAttribArray(posLoc);
        GLES20.glVertexAttribPointer(posLoc, 3, GLES20.GL_FLOAT, false, 0 , 0);

        //UV
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferObjects.get(UVVBO));
        GLES20.glEnableVertexAttribArray(uvLoc);
        GLES20.glVertexAttribPointer(uvLoc, 2, GLES20.GL_FLOAT, false, 0, 0);
        GLManager.checkGLError("ParticleSystem BindBuffers");
    }

    public void Render(float[] projectionMatrix)
    {
        BindBuffers();
        for (Particle aParticleContainer : particleContainer) {
            if (aParticleContainer.isAlive()) {
                Matrix.setIdentityM(modelMatrix, OFFSET); //reset model matrix
                Matrix.translateM(modelMatrix, OFFSET, aParticleContainer.x, aParticleContainer.y, aParticleContainer.z);

                Matrix.multiplyMM(viewportModelMatrix, OFFSET, projectionMatrix, OFFSET, modelMatrix, OFFSET);

                Matrix.setRotateM(modelMatrix, OFFSET, 0, 1.0f, 0.0f, 0.0f);
                Matrix.setRotateM(modelMatrix, OFFSET, 0, 0.0f, 1.0f, 0.0f);
                Matrix.setRotateM(modelMatrix, OFFSET, 0, 0.0f, 0.0f, 1.0f);
                Matrix.rotateM(modelMatrix, OFFSET, 0, 0.0f, 0.0f, 1.0f);
                Matrix.rotateM(modelMatrix, OFFSET, 0, 0.0f, 1.0f, 0.0f);
                Matrix.rotateM(modelMatrix, OFFSET, 0, 1.0f, 0.0f, 0.0f);

                Matrix.scaleM(modelMatrix, OFFSET, aParticleContainer.size, aParticleContainer.size, aParticleContainer.size);
                Matrix.multiplyMM(rotationViewportModelMatrix, OFFSET, viewportModelMatrix, OFFSET, modelMatrix, OFFSET);
                particleShader.SetUniform("modelViewProjection", rotationViewportModelMatrix);

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, aParticleContainer.textureHandle);
                particleShader.SetUniformSampler("albedoMap", 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length / 3); //if we had instancing we would only need one draw call
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            }
        }
        GLManager.checkGLError("ParticleSystem Render");
    }
}
