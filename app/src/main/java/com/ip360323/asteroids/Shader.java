package com.ip360323.asteroids;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.renderscript.Matrix4f;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Shader {

    int handle;

    enum ShaderType
    {
        VERTEX,FRAGMENT, PROGRAM
    }

    private final HashMap<String, Integer> uniformLocs = new HashMap<>();

    void Use()
    {
        if(handle > 0)
        {
            GLES20.glUseProgram(handle);
        }
    }

    void DeleteProgram()
    {
        GLES20.glDeleteProgram(handle);
    }

    void LoadShader(Resources res, final int vertShaderID, final int fragShaderID)
    {
        try {
            Load(res, vertShaderID, fragShaderID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Load(Resources res, final int vertShaderID, final int fragShaderID) throws IOException {
       // resID = Resources.getSystem().getIdentifier(vertShaderFileName, "raw", "com.ip360323.asteroids");
        InputStream vertShaderSource = res.openRawResource(vertShaderID);
        int vertSize = vertShaderSource.available();
        byte[] vertSource = new byte[vertSize];
        vertShaderSource.read(vertSource);
        vertShaderSource.close();
        InputStream fragShaderSource = res.openRawResource(fragShaderID);
        int fragSize = fragShaderSource.available();
        byte[] fragSource = new byte[fragSize];
        fragShaderSource.read(fragSource);
        fragShaderSource.close();

        int fragShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        int vertShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);


        final String vert = new String(vertSource);
        GLES20.glShaderSource(vertShader, vert);
        final String frag = new String(fragSource);
        GLES20.glShaderSource(fragShader, frag);

        GLES20.glCompileShader(vertShader);
        CheckCompilerErrors(vertShader, ShaderType.VERTEX);
        GLES20.glCompileShader(fragShader);
        CheckCompilerErrors(fragShader, ShaderType.FRAGMENT);

        handle = GLES20.glCreateProgram();

        GLES20.glAttachShader(handle, vertShader);
        GLES20.glAttachShader(handle, fragShader);
        GLES20.glLinkProgram(handle);
        CheckCompilerErrors(handle, ShaderType.PROGRAM);

        GLES20.glDeleteShader(vertShader);
        GLES20.glDeleteShader(fragShader);
    }

    private void CheckCompilerErrors(final int handle, final ShaderType type)
    {
        int status[] = new int[1];
        if (type == ShaderType.PROGRAM)
        {
            GLES20.glGetProgramiv(handle, GLES20.GL_LINK_STATUS, status, 0);
            if (status[0] == GLES20.GL_FALSE)
            {
                int length[] = new int[1];
                GLES20.glGetProgramiv(handle,GLES20.GL_INFO_LOG_LENGTH, length, 0);
                String errorLog = GLES20.glGetProgramInfoLog(handle);
                System.out.println("Error: Program failed to link:\n" + errorLog );
            }
        }
        else
        {
            GLES20.glGetShaderiv(handle, GLES20.GL_COMPILE_STATUS, status, 0);
            if (status[0] == GLES20.GL_FALSE)
            {
                int length[] = new int[1];
                GLES20.glGetShaderiv(handle,GLES20.GL_INFO_LOG_LENGTH, length, 0);
                String errorLog = GLES20.glGetShaderInfoLog(handle);
                System.out.println("Error: Shader failed to Compile:\n" + errorLog );
            }
        }
    }

    public int GetUniformLocation(final String name)
    {
        if(!uniformLocs.containsKey(name))
        {
            uniformLocs.put(name, GLES20.glGetUniformLocation(handle, name));
        }
        return uniformLocs.get(name);
    }

    public int GetAttribLocation(final String name)
    {
        return GLES20.glGetAttribLocation(handle, name);
    }

    public void SetUniform(final String name, final float x, final float y)
    {
        Use();
        int loc = GetUniformLocation(name);
        GLES20.glUniform2f(loc, x, y);
    }

    public void SetUniform(final String name, final float x)
    {
        Use();
        int loc = GetUniformLocation(name);
        GLES20.glUniform1f(loc, x);
    }

    public void SetUniform(final String name, final int x, final int y)
    {
        Use();
        int loc = GetUniformLocation(name);
        GLES20.glUniform2i(loc, x, y);
    }

    public void SetUniform(final String name, final float x, final float y, final float z)
    {
        Use();
        int loc = GetUniformLocation(name);
        GLES20.glUniform3f(loc, x, y, z);
    }

    public void SetUniform(final String name, final float x, final float y, final float z, final float w)
    {
        Use();
        int loc = GetUniformLocation(name);
        GLES20.glUniform4f(loc, x, y, z, w);
    }

    public void SetUniform(final String name, final Matrix4f data)
    {
        Use();
        int loc = GetUniformLocation(name);
        GLES20.glUniformMatrix4fv(loc, 1, false, data.getArray(), 0);
    }

    public void SetUniform(final String name, final float[] data)
    {
        Use();
        int loc = GetUniformLocation(name);
        GLES20.glUniformMatrix4fv(loc, 1, false, data, 0);
    }

    public void SetUniformSampler(final String name, final int slot)
    {
        Use();
        GLES20.glActiveTexture( GLES20.GL_TEXTURE0 + slot);
        int loc = GetUniformLocation(name);
        GLES20.glUniform1i(loc, slot);
    }
}
