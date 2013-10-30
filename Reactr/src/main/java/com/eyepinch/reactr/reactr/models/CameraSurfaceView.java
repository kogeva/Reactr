package com.eyepinch.reactr.reactr.models;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.eyepinch.reactr.fragments.CreatePhotoFragment;

public class CameraSurfaceView extends SurfaceView {

    private static final double ASPECT_RATIO = 3.0 / 4.0;


    private CreatePhotoFragment createPhotoFragment;
    private int camId=0;
	public CameraSurfaceView(Context context) {
		super(context);
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
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        /////////Ýòîò ó÷àñòîê êîäà ïîìîãàåò óáðàòü ïîëîñû
        //Äîáàâèëè ê øèðèíå è âûñîòå îäèíàêîâîå ÷èñëî ÷òîá íå ïîâëèÿëî íà ñîîòíîøåíèå

        width+=150;
        height+=150;


        ////////////////////////////////
        if (width > height * ASPECT_RATIO) {
            width = (int) (height * ASPECT_RATIO + .5);
        } else {
            height = (int) (width / ASPECT_RATIO + .5);
        }

        setMeasuredDimension(width, height);
    }


	@Override
	public boolean onTouchEvent(MotionEvent event) {
	//	if(camId==0)
        {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                float x = event.getX();
                float y = event.getY();
                int halfSide=75;
                Rect touchRect = new Rect(
                        (int)(x - halfSide),
                        (int)(y - halfSide),
                        (int)(x + halfSide),
                        (int)(y + halfSide));

               createPhotoFragment.touchFocus(touchRect);
            }
        }
		return true;
	}


    public void setContext(CreatePhotoFragment c){
        createPhotoFragment=c;
    }
    public void setCameraId(int i){
        camId=i;
    }
}
