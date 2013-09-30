package com.example.reactr.reactr.models;

/**
 * Created by Kykmyrna on 30.09.13.
 */
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.example.reactr.MainActivity;
import com.example.reactr.fragments.CreatePhotoFragment;

public class CameraSurfaceView extends SurfaceView {
    Context context;
    public CameraSurfaceView(Context context) {
        super(context);
        this.context=context;
        // TODO Auto-generated constructor stub
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            float x = event.getX();
            float y = event.getY();
            float touchMajor = event.getTouchMajor();
            float touchMinor = event.getTouchMinor();

            Rect touchRect = new Rect(
                    (int)(x - touchMajor/2),
                    (int)(y - touchMinor/2),
                    (int)(x + touchMajor/2),
                    (int)(y + touchMinor/2));

       //     ((CreatePhotoFragment)getContext()).touchFocus(touchRect);
        }


        return true;
    }

}