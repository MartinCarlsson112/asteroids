package com.ip360323.asteroids;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.IntBuffer;

class Texture2D {
    int textureHandle = 0;
    private IntBuffer tHB;
    public int GetTexture()
    {
        return textureHandle;
    }

    public void LoadTexture(Resources res, int resID)
    {
        tHB = IntBuffer.allocate(1);

        GLES20.glGenTextures(1, tHB);

        if(tHB.get(0) != 0)
        {
            textureHandle = tHB.get(0);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false; //dont want to scale our textures on import
            final Bitmap bitmap = BitmapFactory.decodeResource(res, resID, options);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
            GLES20.glTexParameteri( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,  GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,  GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,  GLES20.GL_LINEAR);
            GLES20.glTexParameteri( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,  GLES20.GL_LINEAR);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0); //create texture object in ogl memory
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D); //generate mip maps
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            bitmap.recycle();
        }
    }

    public void Free()
    {
        GLES20.glDeleteTextures(1, tHB);
    }

}
