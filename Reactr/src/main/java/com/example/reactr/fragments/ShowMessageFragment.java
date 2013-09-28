package com.example.reactr.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.Time;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.reactr.MainActivity;
import com.example.reactr.R;
import com.example.reactr.ReactrBase;
import com.example.reactr.reactr.models.MessageEntity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import reactr.network.ReactorApi;
import reactr.utils.ImageHelper;
import reactr.utils.TakePhotoWithoutPreview;

public class ShowMessageFragment extends SherlockFragment{

    private MessageEntity message;
    private ImageView photoView;
    private ImageView reactionPhotoView;
    private ImageButton replyButton;
    private ImageButton closeButton;
    private ImageButton saveButton;
    private SurfaceView surfaceView;
    private Bitmap photo;
    private Bitmap reactionPhoto;
    private Handler handler;
    private Camera camera;
    private TakePhotoWithoutPreview ph;
    private TextView text;
    private boolean reaction=false;

    public ShowMessageFragment(MessageEntity message) {
        this.message = message;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_message_layout, container, false);
         text = (TextView) view.findViewById(R.id.message_text);

        photoView         = (ImageView) view.findViewById(R.id.photo_view);
        reactionPhotoView = (ImageView) view.findViewById(R.id.reaction_photo_view);
        surfaceView       = (SurfaceView) view.findViewById(R.id.hiddenPreview);
        replyButton       = (ImageButton) view.findViewById(R.id.replyButton);
        closeButton       = (ImageButton) view.findViewById(R.id.closeButton);

        replyButton.setOnClickListener(replyMessage);
        closeButton.setOnClickListener(closeClickListener);
        reactionPhotoView.setOnClickListener(switchPhotos);

        saveButton = (ImageButton) view.findViewById(R.id.downloadPhoto);
        saveButton.setOnClickListener(saveToGallery);

        handler = new Handler();

        ReactrBase.showLoader(getSherlockActivity());

        if(!message.getIsRead())
            ph = new TakePhotoWithoutPreview(getSherlockActivity(), surfaceView);
        text.setText(message.getText());
        long memory = Runtime.getRuntime().maxMemory();

        new Thread(new Runnable() {
            @Override
            public void run() {
                photo = downloadImage(message.getPhoto());
                if (!message.getReactionPhoto().equals("null"))
                    reactionPhoto = downloadImage(message.getReactionPhoto());
                handler.post(updateImageView);
            }
        }).start();

        return view;
    }

    private Bitmap downloadImage (String url)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        InputStream image = null;
        Bitmap bmp = null;

        Display display = getSherlockActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        try {
            URL address = new URL(url);
            image = address.openConnection().getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 1; i < 30 ; i++)
        {
            try {
                bmp = BitmapFactory.decodeStream( image , null, options);
                if (bmp != null)
                    return bmp;
            } catch (Exception e) {
                e.getMessage();
            } catch (Error e2) {
                e2.getMessage();
            }
            if (bmp != null)
                break;
        }
        return bmp;
    }

    private Runnable updateImageView = new Runnable() {
        @Override
        public void run() {
            photoView.setImageBitmap(photo);
            if (reactionPhoto != null)
            {
                //для округления изображения
                Bitmap rounded_bm= ImageHelper.getRoundedCornerBitmap(reactionPhoto, Color.WHITE, getActivity().getApplicationContext());
                reactionPhotoView.setImageBitmap(rounded_bm);
            }

            //***********************************************
            if((message.getIsRead()==false)&&(message.getFromMe()==false))
            {
                //прочтение сообщения
                ReactorApi ra= ((MainActivity) getActivity()).getReactorApi();
                ra.readMessage(String.valueOf(message.getId()));
                ((MainActivity)getActivity()).updateMenu();
                ph.takeReaction(message.getId());
            }

            ReactrBase.hideLoader();
        }
    };

    View.OnClickListener closeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ReactrBase.switchFraagment(getSherlockActivity(),new MailBoxFragment());
        }
    };


    private View.OnClickListener switchPhoto = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    private View.OnClickListener replyMessage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CreatePhotoFragment fragment = new CreatePhotoFragment(message);
            ReactrBase.switchFraagment(getSherlockActivity(), fragment);
        }
    };

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap newBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        source.recycle();
        return newBitmap;
    }

    private View.OnClickListener saveToGallery = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //сохранение в галерею (диалог для выбора какое изображение сохранить)
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select a photo to save");

            CharSequence[]cs;
            if(reactionPhoto!=null)
                cs= new CharSequence[]{getResources().getString(R.string.sv_ph_mes), getResources().getString(R.string.sv_ph_react),
                        getResources().getString(R.string.cancel)};
            else
                cs= new CharSequence[]{getResources().getString(R.string.sv_ph_mes), getResources().getString(R.string.cancel)};
            builder.setItems(cs, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Time now = new Time();
                    now.setToNow();
                    String str="IMG_"+now.year+"_"+now.month+"."+now.monthDay+"_"+now.hour+":"+now.minute+":"+now.second;
                    if(which==0){
                        Toast.makeText(getActivity().getBaseContext(), "Saved photo to GALLERY as "+str, Toast.LENGTH_SHORT).show();
                        MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), photo, str, "description");
                    }
                    if(which==1&&reactionPhoto!=null){
                        Toast.makeText(getActivity().getBaseContext(), "Saved reaction to GALLERY as "+str, Toast.LENGTH_SHORT).show();
                        MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), reactionPhoto, str, "description");
                    }
                    if(which==2||(which==1&&reactionPhoto==null)){
                    dialog.cancel();
                    }
                }
            });
            builder.setInverseBackgroundForced(true);

            AlertDialog dialog = builder.create();
            WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
            wmlp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            wmlp.x = 25;   //x position
            wmlp.y = 100;   //y position
            dialog.show();
        }
    };
    //переключение фото с "маленького на большое"
    private View.OnClickListener switchPhotos = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!reaction)
            {
                photoView.setImageBitmap(reactionPhoto);
                Bitmap rounded_bm= ImageHelper.getRoundedCornerBitmap(photo, Color.WHITE, getActivity().getApplicationContext());
                reactionPhotoView.setImageBitmap(rounded_bm);
            }
            else{
                photoView.setImageBitmap(photo);
                Bitmap rounded_bm= ImageHelper.getRoundedCornerBitmap(reactionPhoto, Color.WHITE, getActivity().getApplicationContext());
                reactionPhotoView.setImageBitmap(rounded_bm);
            }
            reaction=!reaction;
        }
    };
}
