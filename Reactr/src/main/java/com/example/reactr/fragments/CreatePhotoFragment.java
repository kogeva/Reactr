package com.example.reactr.fragments;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.reactr.MainActivity;
import com.example.reactr.R;
import com.example.reactr.ReactrBase;
import com.example.reactr.reactr.models.CameraSurfaceView;
import com.example.reactr.reactr.models.MessageEntity;

public class CreatePhotoFragment extends SherlockFragment implements SurfaceHolder.Callback {


    private ImageButton shootButton;
    private ImageButton switchCamera;
    private ToggleButton toggleFlash;
    private MessageEntity messageEntity;
    private View actionBarView;
    public int currentCamera;

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

        if(currentCamera == Camera.CameraInfo.CAMERA_FACING_FRONT)
            toggleFlash.setBackgroundColor(000);

        actionBarView = getSherlockActivity().getSupportActionBar().getCustomView();
        ((TextView) actionBarView.findViewById(R.id.barTitle)).setText("TAKE PICTURE");

        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setVisibility(View.VISIBLE);
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setImageResource(R.drawable.dots_menu);
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setOnClickListener(goToGalleryClick);
        //*************************************
        getActivity().getWindow().setFormat(PixelFormat.UNKNOWN);
        cameraSurfaceView = (CameraSurfaceView)v.findViewById(R.id.campreview);
        surfaceHolder = cameraSurfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        drawingView = new DrawingView(this.getActivity().getBaseContext());

        if(currentCamera == 0)
            drawingView.setVisibility(View.VISIBLE);
        else
            drawingView.setVisibility(View.GONE);
        LayoutParams layoutParamsDrawing
                = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
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
            camera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);
        }
    };

    private View.OnClickListener switchCameraClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CreatePhotoFragment frag = new CreatePhotoFragment();
            if(currentCamera == 1)
            {
                frag.currentCamera = 0;
                drawingView.setVisibility(View.GONE);
            }
            else
            {
                frag.currentCamera = 1;
                drawingView.setVisibility(View.VISIBLE);
            }


            ReactrBase.switchFraagment(getSherlockActivity(), frag);
        }
    };

    View.OnClickListener goToGalleryClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
       //     drawingView.setVisibility(View.GONE);
            //*******************************
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Upload photo from library?");

            CharSequence[] cs;
            cs = new CharSequence[]{"YES", getResources().getString(R.string.cancel)};

            builder.setItems(cs, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    if (which == 0) {
                        Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        if (messageEntity != null)
                            ((MainActivity)getActivity()).setMessageEntity(messageEntity);
                        startActivityForResult(i, ((MainActivity) getActivity()).getResultLoadImage());
                    }
                    if (which == 1) {
                     //   drawingView.setVisibility(View.GONE);
                        dialog.cancel();
                    }
                }
            });
            builder.setInverseBackgroundForced(true);
            AlertDialog dialog = builder.create();
            dialog.show();
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
    //*********************************************************************************************
    public void touchFocus(final Rect tfocusRect){
        Log.d("CAMERA", "TOUCHFOCUS");
        if(currentCamera == 0)
        {
           drawingView.setVisibility(View.VISIBLE);
            Log.d("CAMERA", "VISIBLE");

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
    }


    AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){

        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            // TODO Auto-generated method stub
            if (arg0){
                Log.d("CAMERA", "cancelAutoFocus");
                camera.cancelAutoFocus();
             //   cameraSurfaceView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                Log.d("CAMERA", "GONE");
            }
            drawingView.setVisibility(View.GONE);
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
       //     drawingView.setVisibility(View.GONE);
            if (messageEntity == null)
                ReactrBase.switchFraagment(getSherlockActivity(), new AddMessageFragment(data, currentCamera));
            else
                ReactrBase.switchFraagment(getSherlockActivity(), new AddMessageFragment(data, messageEntity, currentCamera));
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
        if(camera == null)
        {
            try {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                camera = camera.open(currentCamera);
                Camera.Parameters parameters = camera.getParameters();

                Camera.Size previewSize = camera.getParameters().getPreviewSize();
                float aspect = (float) previewSize.width / previewSize.height;

                int previewSurfaceWidth = cameraSurfaceView.getWidth();
                int previewSurfaceHeight = cameraSurfaceView.getHeight();
                ViewGroup.LayoutParams lp = cameraSurfaceView.getLayoutParams();

                if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
                {
                    // портретный вид
                    camera.setDisplayOrientation(90);
                }
                else
                {
                    // ландшафтный
                    camera.setDisplayOrientation(0);
                }

                List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
                Camera.Size avgSize = getAvgPictureZise((ArrayList<Camera.Size>) camera.getParameters().getSupportedPictureSizes());
                parameters.setPictureSize(avgSize.width, avgSize.height);
                camera.setParameters(parameters);
                camera.setPreviewDisplay(holder);
//                cameraSurfaceView.setLayoutParams(lp);
                camera.startPreview();
            }
            catch (IOException e){
                Log.d("CAMERA", e.getMessage());
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }
    //******************************


    private Camera.Size getAvgPictureZise (ArrayList<Camera.Size> sizes)
    {
        double avg = sizes.size() / 2;
        return sizes.get(new Double(avg).intValue());
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

        public void setHaveFace(boolean h){
            haveFace = h;
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
}