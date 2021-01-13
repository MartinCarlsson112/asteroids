package com.ip360323.asteroids;

class Viewport {
    private float lookAtX;
    private float lookAtY;
    private float lookAtZ;
    private int pixelsPerMeterX;
    private int pixelsPerMeterY;

    private final int screenDimensionX;
    private final int screenDimensionY;
    private final int screenCenterX;
    private final int screenCenterY;
    float metersToShowX;
    float metersToShowY;
    private float halfDistanceX;
    private float halfDistanceY;

    private final float x;
    private final float y;
    private final float z;

    private final float centerOffsetX;
    private final float centerOffsetY;

    private static final float OVERDRAW_BUFFER = 4F;


    private final float[] projectionMatrix;

    Viewport(final int screenDimensionX, final int screenDimensionY, final float metersToShowX, final float metersToShowY)
    {
        this.screenDimensionX = screenDimensionX;
        this.screenDimensionY = screenDimensionY;
        screenCenterX = screenDimensionX /2;
        screenCenterY = screenDimensionY /2;
        x = screenCenterX;
        y = screenCenterY;
        z = 0;

        SetMetersToShow(metersToShowX, metersToShowY);
        centerOffsetX = screenToWorldX(screenCenterX);
        centerOffsetY = screenToWorldY(screenCenterY);
        lookAtX = centerOffsetX;
        lookAtY = centerOffsetY;
        projectionMatrix = new float[16];
        InitMatrices();
    }

    private void InitMatrices()
    {
        final int offset = 0;
        final float left = lookAtX - centerOffsetX;
        final float right = metersToShowX + left;
        final float top = lookAtY - centerOffsetY;
        final float bottom = metersToShowY + top;

        final float near = -10f;
        final float far = 12f;
        android.opengl.Matrix.orthoM(projectionMatrix, offset, left, right, bottom, top, near, far);
    }

    private void SetMetersToShow(final float metersToShowX, final float metersToShowY)
    {
        if(metersToShowX <= 0f && metersToShowY <= 0) throw new IllegalArgumentException("viewport dimensions not allowed!");
        this.metersToShowX = metersToShowX;
        this.metersToShowY = metersToShowY;
        if(metersToShowX == 0 || metersToShowY== 0)
        {
            if(metersToShowY > 0)
            {
                this.metersToShowX = ((float)screenDimensionX / screenDimensionY) * metersToShowY;
            }
            else
            {
                this.metersToShowY = ((float)screenDimensionY / screenDimensionX) * metersToShowX;
            }
        }

        halfDistanceX = (this.metersToShowX + OVERDRAW_BUFFER ) * 0.5f;
        halfDistanceY = (this.metersToShowY + OVERDRAW_BUFFER ) * 0.5f;
        pixelsPerMeterX = (int)(screenDimensionX / this.metersToShowY);
        pixelsPerMeterY = (int)(screenDimensionY / this.metersToShowY);
    }

    //Position will be in the center of the screen
    public void LookAt(final float x, final float y, final float z)
    {
        lookAtX = x;
        lookAtY = y;
        lookAtZ = z;
        InitMatrices();
    }

    private float screenToWorldX(final int pixels)
    {
        return (float) pixels / pixelsPerMeterX;
    }
    private float screenToWorldY(final int pixels)
    {
        return (float) pixels / pixelsPerMeterY;
    }

    public float[] GetProjectionMatrix()
    {
        return projectionMatrix;
    }
}
