package com.ip360323.asteroids;

import android.graphics.PointF;

public class Input {
    private InputManager inputManager = new InputManager();
    private static class StaticHolder {
        static final Input INSTANCE = new Input();
    }

    public static Input getInstance() {
        return StaticHolder.INSTANCE;
    }

    private Input()
    {

    }

    public void SetInputs(InputManager inputs)
    {
        if(inputManager != null)
        {
            inputManager.onPause();
            inputManager.onStop();
        }
        inputManager = inputs;
        inputManager.onStart();
    }

    public PointF GetAxis()
    {
        if(inputManager == null)
        {
            return new PointF();
        }
        return inputManager.axis;
    }

    public boolean GetButton(int button)
    {
        if(inputManager == null) {
            return false;
        }
        if(button == InputManager.GOFORWARD)
        {
            return inputManager.buttons[InputManager.GOFORWARD].state;
        }
        if(button == InputManager.SHOOT)
        {
            return inputManager.buttons[InputManager.SHOOT].state;
        }
        return false;
    }

    public boolean GetButtonDown(int button)
    {
        if(inputManager == null) {
            return false;
        }
        if(button == InputManager.GOFORWARD)
        {
            return inputManager.buttons[InputManager.GOFORWARD].justPressed;
        }
        if(button == InputManager.SHOOT)
        {
            return inputManager.buttons[InputManager.SHOOT].justPressed;
        }
        return false;
    }

    public boolean GetButtonUp(int button)
    {
        if(inputManager == null) {
            return false;
        }
        if(button == InputManager.GOFORWARD)
        {
            return inputManager.buttons[InputManager.GOFORWARD].justReleased;
        }
        if(button == InputManager.SHOOT)
        {
            return inputManager.buttons[InputManager.SHOOT].justReleased;
        }
        return false;
    }

    public void Update()
    {
        if(inputManager != null)
        {
            inputManager.Update();
        }
    }

    public void Free()
    {
        inputManager = null;
    }

    public void Pause()
    {
        inputManager.onPause();
        inputManager.onStop();
    }

    public void Resume()
    {
        inputManager.onResume();
    }
}