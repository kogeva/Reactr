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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.reactr.R;
import com.example.reactr.ReactrBase;
import com.example.reactr.reactr.models.MessageEntity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CreatePhotoFragment extends SherlockFragment implements SurfaceHolder.Callback {

    private Camera camera;
    private SurfaceView sfView;
    private SurfaceHolder sfHolder;
    private ImageButton shootButton;
    private ImageButton switchCamera;
    private ToggleButton toggleFlash;
    private MessageEntity messageEntity;
    private View actionBarView;
    public int currentCamera;

    public CreatePhotoFragment() {
    }

    public CreatePhotoFragment(MessageEntity messageEntity) {
        this.messageEntity = messageEntity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.camera_layout, container ,false);
        sfView = (SurfaceView) v.findViewById(R.id.bbyby);
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        getSherlockActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        shootButton = (ImageButton) v.findViewById(R.id.shootButton);
        switchCamera = (ImageButton) v.findViewById(R.id.toggle_button);
        toggleFlash = (ToggleButton) v.findViewById(R.id.switchCamera);
        sfHolder = sfView.getHolder();
        sfHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        sfHolder.addCallback(this);
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


        return preview;
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
                int previewSurfaceWidth = sfView.getWidth();
                int previewSurfaceHeight = sfView.getHeight();
                ViewGroup.LayoutParams lp = sfView.getLayoutParams();

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
                sfView.setLayoutParams(lp);
                camera.startPreview();
            }
            catch (IOException e){
                Log.d("CAMERA", e.getMessage());
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
    }

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
                ReactrBase.switchFraagment(getSherlockActivity(), new AddMessageFragment(data));
            else
                ReactrBase.switchFraagment(getSherlockActivity(), new AddMessageFragment(data, messageEntity));

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

}
