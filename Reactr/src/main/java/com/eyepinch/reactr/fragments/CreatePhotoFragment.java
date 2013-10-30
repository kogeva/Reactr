package com.eyepinch.reactr.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.eyepinch.reactr.MainActivity;
import com.eyepinch.reactr.R;
import com.eyepinch.reactr.ReactrBase;
import com.eyepinch.reactr.reactr.models.CameraSurfaceView;
import com.eyepinch.reactr.reactr.models.MessageEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreatePhotoFragment extends SherlockFragment implements SurfaceHolder.Callback {

    private static final int PICTURE_SIZE_MAX_WIDTH = 1280;
    private static final int PREVIEW_SIZE_MAX_WIDTH = 640;

     ImageButton shootButton;
    private ImageButton switchCamera;
    private ToggleButton toggleFlash;
    private MessageEntity messageEntity;
    private View actionBarView;
    boolean backCameraIsActive = true;
    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

    Camera camera;
    CameraSurfaceView cameraSurfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;
    ImageButton buttonTakePicture;
    DrawingView drawingView;


    public CreatePhotoFragment() {
    }

    public CreatePhotoFragment(MessageEntity messageEntity) {
        this.messageEntity = messageEntity;
    }
    /** Called when the activity is first created. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //*************************************
        View v = inflater.inflate(R.layout.camera_layout, container ,false);

        shootButton = (ImageButton) v.findViewById(R.id.shootButton);
        switchCamera = (ImageButton) v.findViewById(R.id.toggle_button);
        toggleFlash = (ToggleButton) v.findViewById(R.id.switchCamera);

        shootButton.setOnClickListener(shootClick);
        switchCamera.setOnClickListener(switchCameraClick);
        toggleFlash.setOnClickListener(toogleFlashLightClick);

        actionBarView = getSherlockActivity().getSupportActionBar().getCustomView();
        ((TextView) actionBarView.findViewById(R.id.barTitle)).setText("TAKE PICTURE");

        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setVisibility(View.VISIBLE);
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setImageResource(R.drawable.dots_menu);
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setOnClickListener(goToGalleryClick);
        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setImageResource(R.drawable.to_menu);
        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setOnClickListener(((MainActivity) getSherlockActivity()).toogleMenu);

        //*************************************
        getActivity().getWindow().setFormat(PixelFormat.UNKNOWN);
        cameraSurfaceView = (CameraSurfaceView)v.findViewById(R.id.cameraSurface);
        surfaceHolder = cameraSurfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        drawingView = new DrawingView(this.getActivity().getBaseContext());

        if(backCameraIsActive)
            drawingView.setVisibility(View.VISIBLE);
        else
            drawingView.setVisibility(View.GONE);
        LayoutParams layoutParamsDrawing
                = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        getActivity().addContentView(drawingView, layoutParamsDrawing);

        controlInflater = LayoutInflater.from(getActivity().getBaseContext());

        buttonTakePicture = (ImageButton)v.findViewById(R.id.shootButton);
        buttonTakePicture.setOnClickListener(shootClick);

        cameraSurfaceView.setContext(CreatePhotoFragment.this);
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras < 2) {
            switchCamera.setVisibility(View.INVISIBLE);
        }
        return v;

    }

    private View.OnClickListener shootClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shootButton.setEnabled(false);
            camera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);
        }
    };

    private View.OnClickListener switchCameraClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            camera.stopPreview();
            camera.release();
            if (backCameraIsActive) {
                cameraInfo.facing = Camera.CameraInfo.CAMERA_FACING_FRONT;
                backCameraIsActive = false;
                toggleFlash.setVisibility(View.INVISIBLE);
                drawingView.setVisibility(View.INVISIBLE);
            } else {
                cameraInfo.facing = Camera.CameraInfo.CAMERA_FACING_BACK;
                backCameraIsActive = true;
                toggleFlash.setVisibility(View.VISIBLE);
                drawingView.setVisibility(View.GONE);
            }
            camera = Camera.open(cameraInfo.facing);
            setupCamera();
            determineDisplayOrientation();
            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
        }
    };


    View.OnClickListener goToGalleryClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
            builder.setMessage("Upload photo from library?");
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.IsInGallery=true;
                    Intent intent = new Intent(
                            Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    if (messageEntity != null)
                        ((MainActivity)getActivity()).setMessageEntity(messageEntity);
                    startActivityForResult(intent, ((MainActivity) getActivity()).getResultLoadImage());
                }
            });

            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    };


    private View.OnClickListener toogleFlashLightClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(cameraInfo.facing != Camera.CameraInfo.CAMERA_FACING_FRONT)
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
    //*********************************************************************************************
    public void touchFocus(final Rect tfocusRect){
        Log.d("CAMERA", "TOUCHFOCUS");
        if(backCameraIsActive)
        {
            drawingView.setVisibility(View.VISIBLE);
            Log.d("CAMERA", "VISIBLE");

            final Rect targetFocusRect = new Rect(
                    tfocusRect.left * 2000/drawingView.getWidth() - 1000,
                    tfocusRect.top * 2000/drawingView.getHeight() - 1000,
                    tfocusRect.right * 2000/drawingView.getWidth() - 1000,
                    tfocusRect.bottom * 2000/drawingView.getHeight() - 1000);

            final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
            try {
                Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);
                focusList.add(focusArea);
                Parameters para = camera.getParameters();
                para.setFocusAreas(focusList);
                para.setMeteringAreas(focusList);
                camera.setParameters(para);
            }
            catch (Exception ex)
            {
                drawingView.setVisibility(View.INVISIBLE);
            }
            camera.autoFocus(myAutoFocusCallback);
            drawingView.setHaveTouch(true, tfocusRect);
            drawingView.invalidate();
            Log.d("CAMERA", "AFTERFOCUS");
        }
    }


    AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){

        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            if (arg0){
                Log.d("CAMERA", "cancelAutoFocus");
               // camera.cancelAutoFocus();
                Log.d("CAMERA", "GONE");
            }
            drawingView.setVisibility(View.GONE);
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
            Log.d("CAMERA", "onPictureTaken");
        //    FileOutputStream outStream = null;
        //    try {
        //        outStream = new FileOutputStream("/sdcard/test.jpg");
         //       outStream.write(data);
         //       outStream.close();
        //    }
        //    catch (FileNotFoundException e){
        //       Log.d("CAMERA", e.getMessage());
         //   }
        //    catch (IOException e){
        //        Log.d("CAMERA", e.getMessage());
        //    }
            //     drawingView.setVisibility(View.GONE);
            shootButton.setEnabled(true);
            if (messageEntity == null)
                ReactrBase.switchFraagment(getSherlockActivity(), new AddMessageFragment(data, cameraInfo.facing));
            else
                ReactrBase.switchFraagment(getSherlockActivity(), new AddMessageFragment(data, messageEntity, cameraInfo.facing));
        }

    };

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

        // TODO Auto-generated method stub
        if(previewing){
            camera.stopPreview();
            previewing = false;
        }

        if (camera != null){
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                previewing = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        this.surfaceHolder = holder;
        determineDisplayOrientation();
        startCameraPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }
    //******************************
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        drawingView.setVisibility(View.GONE);
    }
    @Override
    public void onResume() {
        super.onResume();

        try {
            camera = Camera.open(cameraInfo.facing);
        } catch (Exception exception) {
            Log.e("false", "Can't open camera with id " + cameraInfo.facing, exception);

            return;
        }
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
            drawingPaint.setStrokeWidth(5);
            haveTouch = false;
        }

        public void setHaveTouch(boolean t, Rect tArea){
            haveTouch = t;
            touchArea = tArea;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.TRANSPARENT);
            if(haveTouch){
                Log.d("CAMERA", "haveTouch");
                drawingPaint.setColor(Color.parseColor("#00dcee"));

                canvas.drawRect(touchArea.left, touchArea.top, touchArea.right, touchArea.bottom, drawingPaint);
            }
        }
    }

    /**
     * Start the camera preview.
     */
    private synchronized void startCameraPreview() {

        setupCamera();
        determineDisplayOrientation();
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException exception) {
            Log.e("Error", "Can't start camera preview due to IOException", exception);
        }
    }

    /**
     * Determine the current display orientation and rotate the camera preview
     * accordingly.
     */
    public void determineDisplayOrientation() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraInfo.facing, cameraInfo);

        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees  = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int displayOrientation;

        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayOrientation = (cameraInfo.orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else {
            displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(displayOrientation);
    }

    /**
     * Setup the camera parameters.
     */
    public void setupCamera() {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size bestPreviewSize = determineBestPreviewSize(parameters);
        Camera.Size bestPictureSize = determineBestPictureSize(parameters);
        parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
        parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);
        camera.setParameters(parameters);
    }


    private Camera.Size determineBestPreviewSize(Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();

        return determineBestSize(sizes, PREVIEW_SIZE_MAX_WIDTH);
    }

    private Camera.Size determineBestPictureSize(Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();

        return determineBestSize(sizes, PICTURE_SIZE_MAX_WIDTH);
    }

    protected Camera.Size determineBestSize(List<Camera.Size> sizes, int widthThreshold) {
        Camera.Size bestSize = null;

        for (Camera.Size currentSize : sizes) {
            boolean isDesiredRatio = (currentSize.width / 4) == (currentSize.height / 3);
            boolean isBetterSize = (bestSize == null || currentSize.width > bestSize.width);
            boolean isInBounds = currentSize.width <= PICTURE_SIZE_MAX_WIDTH;

            if (isDesiredRatio && isInBounds && isBetterSize) {
                bestSize = currentSize;
            }
        }

        if (bestSize == null) {
            return sizes.get(0);
        }

        return bestSize;
    }
}