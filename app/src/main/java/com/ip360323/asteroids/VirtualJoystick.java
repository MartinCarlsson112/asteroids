package com.ip360323.asteroids;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

class VirtualJoystick extends InputManager{

    private final int mMaxDistance;

    private class ActionButtonTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event){
            int action = event.getActionMasked();
            if(v.getId() == R.id.button_region_attack)
            {
                if(action == MotionEvent.ACTION_DOWN){
                    SetInput(GOFORWARD, true);
                }else if(action == MotionEvent.ACTION_UP){
                    SetInput(GOFORWARD, false);
                }
            }
            else if(v.getId() == R.id.button_region_jump)
            {
                if(action == MotionEvent.ACTION_DOWN){
                    SetInput(SHOOT, true);
                }else if(action == MotionEvent.ACTION_UP){
                    SetInput(SHOOT, false);
                }
            }
            return true;
        }
    }

    private class JoystickTouchListener implements View.OnTouchListener{
        private float startingPositionX = 0;
        private float startingPositionY = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event){
            int action = event.getActionMasked();
            if(action == MotionEvent.ACTION_DOWN){
                startingPositionX = event.getX(0);
                startingPositionY = event.getY(0);
            }else if(action == MotionEvent.ACTION_UP){
                axis.x = 0.0f;
                axis.y = 0.0f;
            }else if(action == MotionEvent.ACTION_MOVE){
                //get the proportion to the maxDistance
                axis.x  = (event.getX(0) - startingPositionX)/mMaxDistance;
                axis.x  = Utils.Clamp(axis.x, -1.0f, 1.0f);

                axis.y = (event.getY(0) - startingPositionY)/mMaxDistance;
                axis.y = Utils.Clamp(axis.y, -1.0f, 1.0f);
            }
            return true;
        }
    }


    private final String TAG  = "VIRTUAL_JOYSTICK";

    public VirtualJoystick(View view) {
        view.findViewById(R.id.joystick_region)
                .setOnTouchListener(new JoystickTouchListener());
        view.findViewById(R.id.button_region_attack)
                .setOnTouchListener(new ActionButtonTouchListener());
        view.findViewById(R.id.button_region_jump)
                .setOnTouchListener(new ActionButtonTouchListener());

        mMaxDistance = Utils.dpToPx(48*2); //48dp = minimum hit target. maxDistance is in pixels.
        Log.d(TAG, "MaxDistance (pixels): " + mMaxDistance);
    }

}
