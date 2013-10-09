package com.example.reactr;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reactr.fragments.AddMessageFragment;
import com.example.reactr.reactr.models.CameraSurfaceView;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class AndroidCamera extends SlidingFragmentActivity implements SurfaceHolder.Callback{

	Camera camera;
	CameraSurfaceView cameraSurfaceView;
	SurfaceHolder surfaceHolder;
	boolean previewing = false;
	LayoutInflater controlInflater = null;
	
	Button buttonTakePicture;
	TextView prompt;
	
	DrawingView drawingView;
	Face[] detectedFaces;
	
//	final int RESULT_SAVEIMAGE = 0;
	private ScheduledExecutorService myScheduledExecutorService;
	//********************************************************************
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setBehindContentView(R.layout.menu_frame);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        cameraSurfaceView = (CameraSurfaceView)findViewById(R.id.camerapreview);
        surfaceHolder = cameraSurfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    //    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        drawingView = new DrawingView(this);
        LayoutParams layoutParamsDrawing 
        	= new LayoutParams(LayoutParams.FILL_PARENT, 
        			LayoutParams.FILL_PARENT);
        this.addContentView(drawingView, layoutParamsDrawing);
        
        controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.control, null);
        LayoutParams layoutParamsControl 
        	= new LayoutParams(LayoutParams.FILL_PARENT, 
        			LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParamsControl);
        
        buttonTakePicture = (Button)findViewById(R.id.takepicture);
        buttonTakePicture.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
                Log.d("CAMERA", "BEFOREtakePicture");
				camera.takePicture(myShutterCallback, 
						myPictureCallback_RAW, myPictureCallback_JPG);
			}});
        
        prompt = (TextView)findViewById(R.id.prompt);
    }
    
    public void touchFocus(final Rect tfocusRect){
        Log.d("CAMERA", "TOUCHFOCUS");
    	//buttonTakePicture.setEnabled(false);
    	//camera.stopFaceDetection();
    	//Convert from View's width and height to +/- 1000
		final Rect targetFocusRect = new Rect(
				tfocusRect.left * 2000/drawingView.getWidth() - 1000,
				tfocusRect.top * 2000/drawingView.getHeight() - 1000,
				tfocusRect.right * 2000/drawingView.getWidth() - 1000,
				tfocusRect.bottom * 2000/drawingView.getHeight() - 1000);
		
		final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
		Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);
		focusList.add(focusArea);
		Parameters para = camera.getParameters();
		para.setFocusAreas(focusList);
		para.setMeteringAreas(focusList);
		camera.setParameters(para);
		camera.autoFocus(myAutoFocusCallback);
		drawingView.setHaveTouch(true, tfocusRect);
  		drawingView.invalidate();
        Log.d("CAMERA", "AFTERFOCUS");
    }


    AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){

		@Override
		public void onAutoFocus(boolean arg0, Camera arg1) {
            // TODO Auto-generated method stub
            if (arg0){
                //    buttonTakePicture.setEnabled(true);
                Log.d("CAMERA", "cancelAutoFocus");
                camera.cancelAutoFocus();

            }
            float focusDistances[] = new float[3];
            arg1.getParameters().getFocusDistances(focusDistances);
        }};
    
    ShutterCallback myShutterCallback = new ShutterCallback(){
		@Override
		public void onShutter() {
            Log.d("CAMERA", "onShutter");
		}};
		
	PictureCallback myPictureCallback_RAW = new PictureCallback(){
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {  }};



	PictureCallback myPictureCallback_JPG = new PictureCallback(){
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Toast.makeText(getBaseContext(), "data "+data.length, Toast.LENGTH_SHORT).show();

            Log.d("CAMERA", "onPictureTaken");
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream("/sdcard/test.jpg");
                outStream.write(data);
                outStream.close();
            }
            catch (FileNotFoundException e){
                Log.d("CAMERA", e.getMessage());
            }
            catch (IOException e){
                Log.d("CAMERA", e.getMessage());
            }
          //  ReactrBase.switchFraagment(AndroidCamera.this, new AddMessageFragment(data, 1));
           // finish();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new AddMessageFragment(data, 1))
                    .commit();

            Log.d("Camera","myPictureCallback_RAW");
        }

    };

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		if(previewing){
		//	camera.stopFaceDetection();
			camera.stopPreview();
			previewing = false;
		}
		
		if (camera != null){
			try {
				camera.setPreviewDisplay(surfaceHolder);
				camera.startPreview();
            //    camera.setDisplayOrientation(90);
				prompt.setText(String.valueOf(
						"Max Face: " + camera.getParameters().getMaxNumDetectedFaces()));
			//	camera.startFaceDetection();
				previewing = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera = Camera.open();
        camera.setDisplayOrientation(90);
	//	camera.setFaceDetectionListener(faceDetectionListener);
	}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
     //   camera.stopFaceDetection();
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }

    //******************************************************************
	private class DrawingView extends View{
		
		boolean haveFace;
		Paint drawingPaint;
		
		boolean haveTouch;
		Rect touchArea;

		public DrawingView(Context context) {
			super(context);
			haveFace = false;
			drawingPaint = new Paint();
			drawingPaint.setColor(Color.GREEN);
			drawingPaint.setStyle(Paint.Style.STROKE); 
			drawingPaint.setStrokeWidth(2);
			
			haveTouch = false;
		}
		
		public void setHaveFace(boolean h){
			haveFace = h;
		}
		
		public void setHaveTouch(boolean t, Rect tArea){
			haveTouch = t;
			touchArea = tArea;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
		/*	if(haveFace){

				// Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
				 // UI coordinates range from (0, 0) to (width, height).
				 
				 int vWidth = getWidth();
				 int vHeight = getHeight();
				
				for(int i=0; i<detectedFaces.length; i++){
					
					if(i == 0){
						drawingPaint.setColor(Color.GREEN);
					}else{
						drawingPaint.setColor(Color.RED);
					}
					
					int l = detectedFaces[i].rect.left;
					int t = detectedFaces[i].rect.top;
					int r = detectedFaces[i].rect.right;
					int b = detectedFaces[i].rect.bottom;
					int left	= (l+1000) * vWidth/2000;
					int top		= (t+1000) * vHeight/2000;
					int right	= (r+1000) * vWidth/2000;
					int bottom	= (b+1000) * vHeight/2000;
					canvas.drawRect(
							left, top, right, bottom,  
							drawingPaint);
				}
			}else*/{
				canvas.drawColor(Color.TRANSPARENT);
			}
			
			if(haveTouch){
                Log.d("CAMERA", "haveTouch");
				drawingPaint.setColor(Color.BLUE);
				canvas.drawRect(
						touchArea.left, touchArea.top, touchArea.right, touchArea.bottom,  
						drawingPaint);
			}
		}
		
	}
}