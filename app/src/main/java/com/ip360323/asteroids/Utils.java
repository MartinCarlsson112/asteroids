package com.ip360323.asteroids;

import android.content.res.Resources;
import android.util.Log;

import java.util.Random;

public class Utils {

    public static final double TO_DEG = 180.0/Math.PI;
    public static final double TO_RAD = Math.PI/180.0;
    private static final Random rng = new Random();
    public static float between(float a, float b)
    {
        return  a + (rng.nextFloat() * b);
    }

    public static int randomPick(int a, int b)
    {
        int value = rng.nextInt(100);
        if(value > 49)
        {
            return b;
        }
        return a;
    }

    private static final Point2f randomDir = new Point2f();

    public static float RandomAngle()
    {
        return (float)((rng.nextFloat() * 360f) * TO_RAD);
    }

    public static Point2f RandomDirection()
    {
        double theta = (rng.nextFloat() * 360f) * TO_RAD;

        double cos = Math.cos(theta);
        double sin = Math.sin(theta);

        randomDir.x = (float)(1 * cos);
        randomDir.y = (float)(1 * sin);

        return randomDir;
    }

    public static void expect(final boolean condition, final String tag) {
        Utils.expect(condition, tag, "Expectation was broken.");
    }
    public static void expect(final boolean condition, final String tag, final String message) {
        if(!condition) {
            Log.e(tag, message);
        }
    }
    public static void require(final boolean condition) {
        Utils.require(condition, "Assertion failed!");
    }
    public static void require(final boolean condition, final String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }


    public static float Clamp(final float val, final float min, final float max)
    {
        if(val < min)
        {
            return min;
        }
        else if(val > max)
        {
            return max;
        }
        else
        {
            return val;
        }
    }

    public static int Clamp(final int val, final int min, final int max)
    {
        if(val < min)
        {
            return min;
        }
        else if(val > max)
        {
            return max;
        }
        else
        {
            return val;
        }
    }

    public static float Wrap(final float val, final float min, final float max)
    {
        if(val < min)
        {
            return max - (val + min);
        }
        if(val > max)
        {
            return min + (val-max);
        }
        return val;
    }

    public static int pxToDp(final int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(final int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}
