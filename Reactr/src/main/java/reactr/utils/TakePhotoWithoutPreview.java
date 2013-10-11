package reactr.utils;

import android.content.Context;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class TakePhotoWithoutPreview implements SurfaceHolder.Callback {

    private Context context;
    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private int messageId;

    public TakePhotoWithoutPreview(Context context, SurfaceView surfaceView) {
        this.context = context;
        this.surfaceView = surfaceView;
        this.holder = this.surfaceView.getHolder();
        this.holder.addCallback(this);
        this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void takeReaction(int messageid)
    {
        this.messageId = messageid;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                camera.takePicture(null, null ,jpegCallback);
                shootSound();
            }
        },700);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera = (Camera.getNumberOfCameras() == 1) ? Camera.open(0) : Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        if(camera != null)
        {
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {
                camera.release();
                camera = null;
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            FileOutputStream outStream = null;
            try {
                outStream = context.openFileOutput((new Integer(messageId)).toString() + ".jpg", Context.MODE_PRIVATE);
                outStream.write(data);
                outStream.close();
            }
            catch (FileNotFoundException e){
                Log.d("CAMERA", e.getMessage());
            }
            catch (IOException e){
                Log.d("CAMERA", e.getMessage());
            }
            camera.release();
            camera = null;
        }
    };
    public void shootSound()
    {
        AudioManager meng = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int volume = meng.getStreamVolume( AudioManager.STREAM_NOTIFICATION);
        MediaPlayer _shootMP=null;
        if (volume != 0)
        {
            if (_shootMP == null)
                _shootMP = MediaPlayer.create(context.getApplicationContext(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            if (_shootMP != null)
                _shootMP.start();
        }
    }
}
