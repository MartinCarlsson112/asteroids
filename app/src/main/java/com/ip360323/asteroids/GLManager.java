package com.ip360323.asteroids;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;

public class GLManager {
    public final static String TAG = "GLManager";
    private static final int OFFSET = 0; //just to have a name for the parameter

    private static final String POSITION_UNIFORM = "position";
    private static final String COLOR_UNIFORM = "color";
    private static final String MVP_UNIFORM = "modelViewProjection";


    private static int posLoc = 0;
    private static final Shader shader = new Shader();


    public static void checkGLError(final String func) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(func, "glError " + error);
        }
    }

    public static void buildProgram(Context context) {
        shader.LoadShader(context.getResources(), R.raw.basicvertex, R.raw.basicfragment);
        posLoc = shader.GetAttribLocation(POSITION_UNIFORM);
        shader.GetUniformLocation(COLOR_UNIFORM);
        shader.GetUniformLocation(MVP_UNIFORM);
        shader.Use();
        GLES20.glLineWidth(5f); //draw lines 5px wide
    }

    private static void setModelViewProjection(final float[] modelViewMatrix) {
        final int COUNT = 1;
        final boolean TRANSPOSED = false;

        shader.SetUniform(MVP_UNIFORM, modelViewMatrix);
        checkGLError("setModelViewProjection");
    }

    public static void draw(final Mesh model, final float[] modelViewMatrix, final float[] color) {
        setShaderColor(color);
        uploadMesh(model._vertexBuffer);
        setModelViewProjection(modelViewMatrix);
        drawMesh(model._drawMode, model._vertexCount);
    }

    public static void draw(final OBJMesh model, final int texture, final float[] modelViewMatrix) {
        model.BindBuffers();
        model.shader.SetUniform(MVP_UNIFORM, modelViewMatrix);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        model.shader.SetUniformSampler("albedoMap", 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, OFFSET, model.vertexCount);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public static void draw(final Mesh model, final Shader shader, final float[] modelViewMatrix, float[] color) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        shader.SetUniform(MVP_UNIFORM, modelViewMatrix);
        shader.SetUniform(COLOR_UNIFORM, color[0], color[1], color[2], color[3]);
        uploadMesh(model._vertexBuffer);
        drawMesh(model._drawMode, model._vertexCount);
    }

    private static void uploadMesh(final FloatBuffer vertexBuffer) {
        final boolean NORMALIZED = false;
        // enable a handle to the vertice
        GLES20.glEnableVertexAttribArray(posLoc);
        // prepare the vertex coordinate data
        GLES20.glVertexAttribPointer(posLoc, Mesh.COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, NORMALIZED, Mesh.VERTEX_STRIDE,
                vertexBuffer);
        checkGLError("uploadMesh");
    }

    private static void setShaderColor(final float[] color) {
        final int COUNT = 1;
        // set color for drawing the pixels of our geometry
        int loc = shader.GetUniformLocation("color");
        GLES20.glUniform4fv(loc, COUNT, color, OFFSET);
        checkGLError("setShaderColor");
    }

    private static void drawMesh(final int drawMode, final int vertexCount) {
        Utils.require(drawMode == GLES20.GL_TRIANGLES
                || drawMode == GLES20.GL_LINES
                || drawMode == GLES20.GL_POINTS);
        // draw the previously uploaded vertices
        GLES20.glDrawArrays(drawMode, OFFSET, vertexCount);
        // disable vertex array
        GLES20.glDisableVertexAttribArray(posLoc);
        checkGLError("drawMesh");
    }
}