package com.example.reactr;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CameraActivity extends Activity implements SurfaceHolder.Callback {
    private Camera camera;
    private byte[] arr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        //SurfaceView sfView = (SurfaceView) findViewById(R.id.surfaceView);
        //Button shootButton = (Button) findViewById(R.id.shoot_button);
       // SurfaceHolder holder = sfView.getHolder();
        //holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(camera == null)
        {
            try {
                camera = camera.open();
                camera.setPreviewDisplay(holder);
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
            arr = data;
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
        }
    };

    public void shootButtonClick(View v)
    {
        Toast.makeText(this, "Зачем вы нажали?", Toast.LENGTH_SHORT).show();
        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }
}
