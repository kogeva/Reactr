package reactr.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.example.reactr.ReactrBase;
import com.example.reactr.fragments.ShowMessageFragment;

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
    private byte[] reactionPhoto;
    ShowMessageFragment fragment;

    public TakePhotoWithoutPreview(Context context, SurfaceView surfaceView, ShowMessageFragment fragment) {
        this.fragment = fragment;
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
               // fragment.setVisibilityOnTakeReaction(false);
                camera.takePicture(null, null ,jpegCallback);
             //   fragment.setVisibilityOnTakeReaction(true);
                shootSound();
            }
        },700);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        try {
            camera = (Camera.getNumberOfCameras() == 1) ? Camera.open(0) : Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }
        catch (Exception e) {
            camera.release();
            camera = null;
        }

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

            reactionPhoto = data;

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
            fragment.reactionPhoto = fragment.RotateBitmap(getReactionPhoto(),-90);
            fragment.reactionPhotoView.setVisibility(View.VISIBLE);
            fragment.setDecorationPhoto();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            fragment.handler.post(showConfirmDialog);
                        }
                    }, 1000);
                }
            }).start();


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

    public Bitmap getReactionPhoto ()
    {
        if (reactionPhoto != null)
            return BitmapFactory.decodeByteArray(reactionPhoto, 0, reactionPhoto.length);
        else
            return null;
    }

    private Runnable showConfirmDialog = new Runnable() {
        @Override
        public void run() {
            fragment.confirmReaction();
        }
    };
}
