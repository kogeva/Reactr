package com.example.reactr.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.text.format.Time;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.reactr.MainActivity;
import com.example.reactr.R;
import com.example.reactr.ReactrBase;
import com.example.reactr.reactr.models.MessageEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import reactr.network.ReactorApi;
import reactr.utils.ImageHelper;
import reactr.utils.TakePhotoWithoutPreview;

public class ShowMessageFragment extends SherlockFragment {

    private MessageEntity message;
    private ImageView photoView;
    public ImageView reactionPhotoView;
    private ImageButton replyButton;
    private ImageButton closeButton;
    private ImageButton saveButton;
    private SurfaceView surfaceView;
    private Bitmap photo;
    public Bitmap reactionPhoto;
    public Handler handler;
    private Camera camera;
    private TakePhotoWithoutPreview ph;
    private TextView text;
    private boolean reaction = false;
    private boolean animateReaction = false;
    private View actionBarView;

    private Animation animationFadeIn, animationFadeOut;

    public ShowMessageFragment(MessageEntity message) {
        this.message = message;
    }

    public ImageButton getReplyButton()
    {
        return replyButton;
    }
    public ImageButton getSaveButton ()
    {
        return closeButton;
    }
    public ImageButton getCloseButton ()
    {
        return closeButton;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_message_layout, container, false);
        text = (TextView) view.findViewById(R.id.message_text);

        photoView = (ImageView) view.findViewById(R.id.photo_view);
        reactionPhotoView = (ImageView) view.findViewById(R.id.reaction_photo_view);
        surfaceView = (SurfaceView) view.findViewById(R.id.hiddenPreview);
        replyButton = (ImageButton) view.findViewById(R.id.replyButton);
        closeButton = (ImageButton) view.findViewById(R.id.closeButton);

        replyButton.setOnClickListener(replyMessage);
        closeButton.setOnClickListener(closeClickListener);
        reactionPhotoView.setOnClickListener(switchPhotos);

        saveButton = (ImageButton) view.findViewById(R.id.downloadPhoto);
        saveButton.setOnClickListener(saveToGallery);

        handler = new Handler();
        text.setText(message.getText());

        ReactrBase.showLoader(getSherlockActivity());

        if ((!message.getIsRead()) && (!message.getFromMe()) && message.getReactionPhoto().equals("null")){
            ph = new TakePhotoWithoutPreview(getSherlockActivity(), surfaceView, this);
        }

        if (message.getFromMe()){
            Log.d("latuhov", "replyButton");
            replyButton.setVisibility(View.INVISIBLE);
        }


        if (message.getText().length() == 0)
            text.setVisibility(View.INVISIBLE);
        else
            text.setVisibility(View.VISIBLE);


        new Thread(new Runnable() {
            @Override
            public void run() {
                photo = downloadImage(message.getPhoto());

                if (!message.getReactionPhoto().equals("null"))
                    reactionPhoto = downloadImage(message.getReactionPhoto());

                handler.post(updateImageView);
            }
        }).start();

        actionBarView = getSherlockActivity().getSupportActionBar().getCustomView();
        ((TextView) actionBarView.findViewById(R.id.barTitle)).setText("REACTR");
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setVisibility(View.INVISIBLE);
        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setImageResource(R.drawable.back_btn);
        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setOnClickListener(goBackClick);

        animationFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        animationFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout);

        return view;
    }

    private Bitmap downloadImage(String url) {
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

        for (int i = 1; i < 30; i++) {
            try {
                bmp = BitmapFactory.decodeStream(image, null, options);
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
public void setVisibilityOnTakeReaction(boolean visible)
{
    if(!visible)
    {
    closeButton.setVisibility(View.INVISIBLE);
    replyButton.setVisibility(View.INVISIBLE);
    saveButton.setVisibility(View.INVISIBLE);
    }
    else
    {
        closeButton.setVisibility(View.VISIBLE);
        replyButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);
    }
}
    private Runnable updateImageView = new Runnable() {
        @Override
        public void run() {
            photoView.setImageBitmap(photo);
            if (reactionPhoto != null) {
                setDecorationPhoto();
            }
            if ((!message.getIsRead()) && (!message.getFromMe())) {
                //прочтение сообщения
                ReactorApi ra = ((MainActivity) getActivity()).getReactorApi();
                ra.readMessage(String.valueOf(message.getId()));
                ((MainActivity) getActivity()).updateMenu();
            }
            ReactrBase.hideLoader();
            if ((!message.getIsRead()) && (!message.getFromMe()) && message.getReactionPhoto().equals("null")) {
                ph.takeReaction(message.getId());
                animateReaction=true;
                Log.d("SHOWMESSAGE", "animate=true");
            }
        }
    };

    View.OnClickListener closeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ReactrBase.switchFraagment(getSherlockActivity(), new MailBoxFragment());
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

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
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

            CharSequence[] cs;
            if (reactionPhoto != null)
                cs = new CharSequence[]{getResources().getString(R.string.sv_ph_mes), getResources().getString(R.string.sv_ph_react),
                        getResources().getString(R.string.cancel)};
            else
                cs = new CharSequence[]{getResources().getString(R.string.sv_ph_mes), getResources().getString(R.string.cancel)};
            builder.setItems(cs, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Time now = new Time();
                    now.setToNow();
                    String str = "IMG_" + now.year + "_" + now.month + "." + now.monthDay + "_" + now.hour + ":" + now.minute + ":" + now.second;
                    if (which == 0) {
                        savePhotoToMyAlbum(str, photo);
                    }
                    if (which == 1 && reactionPhoto != null) {
                        savePhotoToMyAlbum(str, reactionPhoto);
                    }
                    if (which == 2 || (which == 1 && reactionPhoto == null)) {
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

            Log.d("SHOWMESSAGE", "SWITCHPHOTOS");
            photoView.startAnimation(animationFadeOut);
            reactionPhotoView.startAnimation(animationFadeOut);

            if (!reaction) {
                photoView.setImageBitmap(reactionPhoto);
                Bitmap rounded_bm = ImageHelper.getRoundedCornerBitmap(photo, Color.WHITE, getActivity().getApplicationContext());
                reactionPhotoView.setImageBitmap(rounded_bm);
            } else {
                photoView.setImageBitmap(photo);
                Bitmap rounded_bm = ImageHelper.getRoundedCornerBitmap(reactionPhoto, Color.WHITE, getActivity().getApplicationContext());
                reactionPhotoView.setImageBitmap(rounded_bm);
            }
            reaction = !reaction;
            photoView.startAnimation(animationFadeIn);
            reactionPhotoView.startAnimation(animationFadeIn);

        }
    };


    private void savePhotoToMyAlbum(String filename, Bitmap bitmap) {
        String fullPath = Environment
                .getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM
                + "/Reactr/";
        File dir = new File(fullPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File image = new File(fullPath, filename + ".jpg");
        boolean success = false;
        // Encode the file as a PNG image.
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (success) {
            Toast.makeText(getActivity().getApplicationContext(), "Image saved",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Error during image saving", Toast.LENGTH_LONG).show();
        }
        //*******************************************
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        String currentPath = fullPath + File.separator + filename + ".jpg";
        File f = new File(currentPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    public boolean confirmReaction()
    {
        final Boolean isConfirm = false;

        //*******************
      AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
        builder.setMessage("Do you wish to share your reaction with your friend?");

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                class SendReactionPhotoAsyncTask extends AsyncTask<Void, Void, Boolean>{

                    @Override
                    protected void onPreExecute() {
                        ReactrBase.showLoader(getSherlockActivity());
                        super.onPreExecute();
                    }

                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        ReactorApi api = ((MainActivity) getSherlockActivity()).getReactorApi();
                        return api.sendMessages(message.getFrom_user().toString(), message.getText(), photo, reactionPhoto);
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        ReactrBase.hideLoader();
                        super.onPostExecute(result);
                        if(result)
                            Toast.makeText(getSherlockActivity(), "Reaction sent", Toast.LENGTH_SHORT).show();
                    }
                }
                new SendReactionPhotoAsyncTask().execute();
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


        return true;
    }

    public void setDecorationPhoto ()
    {
        if(animateReaction){
            Log.d("SHOWMESSAGE", "animate");
            Bitmap rounded_bm = ImageHelper.getRoundedCornerBitmap(reactionPhoto, Color.WHITE, getActivity().getApplicationContext());
            reactionPhotoView.setImageBitmap(rounded_bm);
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale);
            reactionPhotoView.startAnimation(animation);
            animateReaction=false;
        }
        else
        {
            reaction=true;
            Bitmap rounded_bm = ImageHelper.getRoundedCornerBitmap(photo, Color.WHITE, getActivity().getApplicationContext());
            reactionPhotoView.setImageBitmap(rounded_bm);
            photoView.setImageBitmap(reactionPhoto);
        }
        Log.d("SHOWMESSAGE", "out_of_animate");

    }

    View.OnClickListener goBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
             ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setImageResource(R.drawable.to_menu);
            ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setOnClickListener(((MainActivity) getSherlockActivity()).toogleMenu);
             ReactrBase.switchFraagment(getSherlockActivity(), new MailBoxFragment());
        }
    };
}
