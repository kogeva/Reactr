package com.example.reactr.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.reactr.R;
import com.example.reactr.ReactrBase;
import com.example.reactr.reactr.models.MessageEntity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import reactr.utils.TakePhotoWithoutPreview;

public class ShowMessageFragment extends SherlockFragment{

    private MessageEntity message;
    private ImageView photoView;
    private ImageView reactionPhotoView;
    private ImageButton replyButton;
    private ImageButton closeButton;
    private SurfaceView surfaceView;
    private Bitmap photo;
    private Bitmap reactionPhoto;
    private Handler handler;
    private Camera camera;
    private TakePhotoWithoutPreview ph;

    public ShowMessageFragment(MessageEntity message) {
        this.message = message;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_message_layout, container, false);
        TextView text = (TextView) view.findViewById(R.id.message_text);
        photoView = (ImageView) view.findViewById(R.id.photo_view);
        reactionPhotoView = (ImageView) view.findViewById(R.id.reaction_photo_view);
        surfaceView = (SurfaceView) view.findViewById(R.id.hiddenPreview);
        replyButton = (ImageButton) view.findViewById(R.id.replyButton);
        closeButton = (ImageButton) view.findViewById(R.id.closeButton);
        replyButton.setOnClickListener(replyMessage);
        closeButton.setOnClickListener(closeClickListener);

        handler = new Handler();

        ReactrBase.showLoader(getSherlockActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                photo = downloadImage(message.getPhoto());
                if (!message.getReactionPhoto().equals("null"))
                    reactionPhoto = downloadImage(message.getReactionPhoto());
                handler.post(updateImageView);
            }
        }).start();

        if(!message.getIsRead())
            ph = new TakePhotoWithoutPreview(getSherlockActivity(), surfaceView);
        text.setText(message.getText());

        return view;
    }

    private Bitmap downloadImage (String url)
    {
        try {
            URL address = new URL(url);
            Bitmap bmp = BitmapFactory.decodeStream(address.openConnection().getInputStream());
            return bmp;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Runnable updateImageView = new Runnable() {
        @Override
        public void run() {
            photoView.setImageBitmap(RotateBitmap(photo, 90));
            if (reactionPhoto != null)
                reactionPhotoView.setImageBitmap(RotateBitmap(reactionPhoto, 90));

            if(!message.getIsRead())
                ph.takeReaction(message.getId());
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
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
