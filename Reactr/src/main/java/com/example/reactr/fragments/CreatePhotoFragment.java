package com.example.reactr.fragments;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.reactr.R;
import com.example.reactr.ReactrBase;
import com.example.reactr.reactr.models.CameraSurfaceView;
import com.example.reactr.reactr.models.MessageEntity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;






import java.io.FileNotFoundException;
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
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class CreatePhotoFragment extends SherlockFragment implements SurfaceHolder.Callback {

    private Camera camera;
 //   private SurfaceView sfView;
 //   private SurfaceHolder sfHolder;
    private ImageButton shootButton;
    private ImageButton switchCamera;
    private ToggleButton toggleFlash;
    private MessageEntity messageEntity;
    private View actionBarView;
    public int currentCamera;

    private ScheduledExecutorService myScheduledExecutorService;
    CameraSurfaceView cameraSurfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;

  //  Button buttonTakePicture;
 //   TextView prompt;

    DrawingView drawingView;
    Face[] detectedFaces;

    final int RESULT_SAVEIMAGE = 0;

    public CreatePhotoFragment() {
    }

    public CreatePhotoFragment(MessageEntity messageEntity) {
        this.messageEntity = messageEntity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
     //   View v = inflater.inflate(R.layout.main, container ,false);
        View v = inflater.inflate(R.layout.camera_layout, container ,false);

    //    sfView = (SurfaceView) v.findViewById(R.id.bbyby);
          shootButton = (ImageButton) v.findViewById(R.id.shootButton);
        switchCamera = (ImageButton) v.findViewById(R.id.toggle_button);
        toggleFlash = (ToggleButton) v.findViewById(R.id.switchCamera);
     /*   sfHolder = sfView.getHolder();
        sfHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        sfHolder.addCallback(this);*/
        shootButton.setOnClickListener(shootClick);
        switchCamera.setOnClickListener(switchCameraClick);
        toggleFlash.setOnClickListener(toogleFlashLightClick);
        FrameLayout preview = (FrameLayout) v.findViewById(R.id.photoFrame);

        if(currentCamera == Camera.CameraInfo.CAMERA_FACING_FRONT)
            toggleFlash.setBackgroundColor(000);

        actionBarView = getSherlockActivity().getSupportActionBar().getCustomView();
        ((TextView) actionBarView.findViewById(R.id.barTitle)).setText("TAKE PICTURE");
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setVisibility(View.INVISIBLE);
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setImageResource(R.drawable.act_bar_make_photo);

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        getActivity().getWindow().setFormat(PixelFormat.UNKNOWN);
        cameraSurfaceView = (CameraSurfaceView)v.findViewById(R.id.bbyby);
        surfaceHolder = cameraSurfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        drawingView = new DrawingView(getActivity().getApplicationContext());
        LayoutParams layoutParamsDrawing
                = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);

        ((FrameLayout) v.findViewById(R.id.photoFrame)).addView(drawingView, layoutParamsDrawing);
      //  getActivity().addContentView(drawingView, layoutParamsDrawing);


      /*  View viewControl = inflater.inflate(R.layout.control, container ,false);
        LayoutParams layoutParamsControl
                = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        getActivity().addContentView(viewControl, layoutParamsControl);*/

     /*    buttonTakePicture = (Button)v.findViewById(R.id.takepicture);
        buttonTakePicture.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                camera.takePicture(myShutterCallback,
                        myPictureCallback_RAW, myPictureCallback_JPG);
            }});*/

        LinearLayout layoutBackground = (LinearLayout)v.findViewById(R.id.background);

     layoutBackground.setOnClickListener(new LinearLayout.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			//	buttonTakePicture.setEnabled(false);
				camera.autoFocus(myAutoFocusCallback);
			}});


        //prompt = (TextView)v.findViewById(R.id.prompt);
        return v;


      //  return preview;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(camera == null)
        {
            try {


                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                camera = camera.open(currentCamera);
                Camera.Parameters parameters = camera.getParameters();

                Camera.Size previewSize = camera.getParameters().getPreviewSize();
                float aspect = (float) previewSize.width / previewSize.height;
                /*
                int previewSurfaceWidth = sfView.getWidth();
                int previewSurfaceHeight = sfView.getHeight();
                ViewGroup.LayoutParams lp = sfView.getLayoutParams();
                */
                int previewSurfaceWidth = cameraSurfaceView.getWidth();
                int previewSurfaceHeight = cameraSurfaceView.getHeight();
                ViewGroup.LayoutParams lp = cameraSurfaceView.getLayoutParams();

                if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
                {
                    // портретный вид
                    camera.setDisplayOrientation(90);
                    lp.height = previewSurfaceHeight;
                    lp.width = (int) (previewSurfaceHeight / aspect);
                    ;
                }
                else
                {
                    // ландшафтный
                    camera.setDisplayOrientation(0);
                    lp.width = previewSurfaceWidth;
                    lp.height = (int) (previewSurfaceWidth / aspect);
                }
                //camera.setR;

                List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
                camera.setParameters(parameters);
                camera.setPreviewDisplay(holder);

                cameraSurfaceView.setLayoutParams(lp);


             //   sfView.setLayoutParams(lp);
                camera.startPreview();
            }
            catch (IOException e){
                Log.d("CAMERA", e.getMessage());
            }
        }
    }
/*
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
    */
/*
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
    }*/

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
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
            if (messageEntity == null)
                ReactrBase.switchFraagment(getSherlockActivity(), new AddMessageFragment(data, currentCamera));
            else
                ReactrBase.switchFraagment(getSherlockActivity(), new AddMessageFragment(data, messageEntity, currentCamera));

        }
    };

    private View.OnClickListener shootClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            camera.takePicture(shutterCallback, rawCallback, jpegCallback);
        }
    };

    private View.OnClickListener switchCameraClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CreatePhotoFragment frag = new CreatePhotoFragment();
            if(currentCamera == 1)
                frag.currentCamera = 0;
            else
                frag.currentCamera = 1;

            ReactrBase.switchFraagment(getSherlockActivity(), frag);
        }
    };

    private View.OnClickListener toogleFlashLightClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(currentCamera != Camera.CameraInfo.CAMERA_FACING_FRONT)
            {
                Camera.Parameters p = camera.getParameters();
                if(p.getFlashMode().equals("off")){
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    camera.setParameters(p);
                }
                else
                {
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(p);
                }
            }
        }
    };

    /////////////////////////////////////////////////////////////////////////////////////
    public void touchFocus(final Rect tfocusRect){

        //buttonTakePicture.setEnabled(false);

        camera.stopFaceDetection();

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
    }

    FaceDetectionListener faceDetectionListener
            = new FaceDetectionListener(){

        @Override
        public void onFaceDetection(Face[] faces, Camera tcamera) {

            if (faces.length == 0){
                //prompt.setText(" No Face Detected! ");
                drawingView.setHaveFace(false);
            }else{
                //prompt.setText(String.valueOf(faces.length) + " Face Detected :) ");
                drawingView.setHaveFace(true);
                detectedFaces = faces;

                //Set the FocusAreas using the first detected face
                List<Camera.Area> focusList = new ArrayList<Camera.Area>();
                Camera.Area firstFace = new Camera.Area(faces[0].rect, 1000);
                focusList.add(firstFace);

                Parameters para = camera.getParameters();

                if(para.getMaxNumFocusAreas()>0){
                    para.setFocusAreas(focusList);
                }

                if(para.getMaxNumMeteringAreas()>0){
                    para.setMeteringAreas(focusList);
                }

                camera.setParameters(para);

              //  buttonTakePicture.setEnabled(false);

                //Stop further Face Detection
                camera.stopFaceDetection();

            //    buttonTakePicture.setEnabled(false);

				/*
				 * Allways throw java.lang.RuntimeException: autoFocus failed
				 * if I call autoFocus(myAutoFocusCallback) here!
				 *
					camera.autoFocus(myAutoFocusCallback);
				*/

                //Delay call autoFocus(myAutoFocusCallback)
                myScheduledExecutorService = Executors.newScheduledThreadPool(1);
                myScheduledExecutorService.schedule(new Runnable(){
                    public void run() {
                        camera.autoFocus(myAutoFocusCallback);
                    }
                }, 500, TimeUnit.MILLISECONDS);

            }

            drawingView.invalidate();

        }};

    AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){

        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            // TODO Auto-generated method stub
            if (arg0){
            //    buttonTakePicture.setEnabled(true);
                camera.cancelAutoFocus();
            }

            float focusDistances[] = new float[3];
            arg1.getParameters().getFocusDistances(focusDistances);
         //   prompt.setText("Optimal Focus Distance(meters): "
          //          + focusDistances[Camera.Parameters.FOCUS_DISTANCE_OPTIMAL_INDEX]);

        }};

    ShutterCallback myShutterCallback = new ShutterCallback(){

        @Override
        public void onShutter() {
            // TODO Auto-generated method stub

        }};

    PictureCallback myPictureCallback_RAW = new PictureCallback(){

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // TODO Auto-generated method stub

        }};

    PictureCallback myPictureCallback_JPG = new PictureCallback(){

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // TODO Auto-generated method stub
			/*Bitmap bitmapPicture
				= BitmapFactory.decodeByteArray(arg0, 0, arg0.length);	*/

            Uri uriTarget = getActivity().getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, new ContentValues());

            OutputStream imageFileOS;
            try {
                imageFileOS = getActivity().getContentResolver().openOutputStream(uriTarget);
                imageFileOS.write(arg0);
                imageFileOS.flush();
                imageFileOS.close();

                //prompt.setText("Image saved: " + uriTarget.toString());

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            camera.startPreview();
            camera.startFaceDetection();
        }};

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        if(previewing){
            camera.stopFaceDetection();
            camera.stopPreview();
            previewing = false;
        }

        if (camera != null){
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();

            //    prompt.setText(String.valueOf(
             //           "Max Face: " + camera.getParameters().getMaxNumDetectedFaces()));
                camera.startFaceDetection();
                previewing = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
/*
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera = Camera.open();
        camera.setFaceDetectionListener(faceDetectionListener);
    }*/

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera.stopFaceDetection();
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }

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
            if(haveFace){

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
            }else{
                canvas.drawColor(Color.TRANSPARENT);
            }

            if(haveTouch){
                drawingPaint.setColor(Color.BLUE);
                canvas.drawRect(
                        touchArea.left, touchArea.top, touchArea.right, touchArea.bottom,
                        drawingPaint);
            }
        }

    }































}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*
public class AndroidCamera extends Activity implements SurfaceHolder.Callback{




    @Override
    public void onCreate(Bundle savedInstanceState) {

    }


}*/






























