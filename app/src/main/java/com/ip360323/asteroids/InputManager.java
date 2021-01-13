package com.ip360323.asteroids;

import android.graphics.PointF;

public class InputManager {
    public final PointF axis = new PointF();

    public static final int GOFORWARD = 0;
    public static final int SHOOT = 1;
    private static final int BUTTON_COUNT = 2;

    public static class Button
    {
        public boolean state;
        public boolean justPressed;
        public boolean justReleased;

        Button()
        {
            state = false;
            justPressed = false;
            justReleased = false;
        }
    }

    final Button[] buttons = new Button[BUTTON_COUNT];

    InputManager()
    {
        for(int i = 0; i < BUTTON_COUNT; i++)
        {
            buttons[i] = new Button();
        }
    }

    void SetInput(int index, boolean state)
    {
        buttons[index].state = state;
        if(state)
        {
            buttons[index].justPressed = true;
        }
        else
        {
            buttons[index].justReleased = true;
        }
    }

    public void Update()
    {
        for(int i = 0; i < BUTTON_COUNT; i++)
        {
            buttons[i].justPressed = false;
            buttons[i].justReleased = false;
        }
    }

    public void onStart() {}

    public void onStop() {}
    public void onPause() {}
    public void onResume() {}
}