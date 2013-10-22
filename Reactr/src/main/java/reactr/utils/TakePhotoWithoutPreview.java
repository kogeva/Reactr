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

        Log.e("REACRT", "TakeWP construstor");
    }

    public void takeReaction(int messageid)
    {

        this.messageId = messageid;
        Log.e("REACRT", "TPWP takeReaction");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
               // fragment.setVisibilityOnTakeReaction(false);
               // camera=Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                camera.takePicture(null, null ,jpegCallback);
             //   fragment.setVisibilityOnTakeReaction(true);
                shootSound();
            //    camera.release();
            }
        },700);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        Log.e("REACRT", "TPWP surfaceCreated");

        try {
            camera = (Camera.getNumberOfCameras() == 1) ? Camera.open(0) : Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }
        catch (Exception e) {
//            camera.release();
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
        Log.e("REACRT", "TPWP surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.e("REACRT", "TPWP surfaceDestroyed");

    }

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.e("REACRT", "TPWP onPictureTaken");
            reactionPhoto = data;
            camera.release();
            camera = null;
            FileOutputStream outStream = null;
            try {
                Log.e("REACRT", "TPWP onPictureTaken in TRY");
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
        Log.e("REACRT", "shootSound");
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
