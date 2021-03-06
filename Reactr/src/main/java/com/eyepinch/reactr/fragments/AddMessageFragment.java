package com.eyepinch.reactr.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.eyepinch.reactr.MainActivity;
import com.eyepinch.reactr.R;
import com.eyepinch.reactr.ReactrBase;
import com.eyepinch.reactr.reactr.models.MessageEntity;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import reactr.network.ReactorApi;
import reactr.utils.BitmapMemoryManager;

public class AddMessageFragment extends SherlockFragment{

    private Bitmap photo;
    private EditText text;
    private ImageButton addText;
    private ImageButton closePhotoPreview;
    private MessageEntity messageEntity;
    private ReactorApi reactorApi;
    private ImageView reactionPhoto;
    private View actionBarView;
    private Context context;

    public AddMessageFragment(byte[] photo, int camId) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        if (camId == 1)
            this.photo = RotateBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length, options), -90);
        if (camId == 0)
            this.photo = RotateBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length, options), 90);
        if (camId == -1)
            this.photo = BitmapFactory.decodeByteArray(photo, 0, photo.length, options);
        this.photo = BitmapMemoryManager.codec(this.photo, Bitmap.CompressFormat.JPEG, 75);
    }

    public AddMessageFragment(byte[] photo, MessageEntity messageEntity, int camId) {
        if (camId == 1)
            this.photo = RotateBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length), -90);
        if (camId == 0)
            this.photo = RotateBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length), 90);
        if (camId == -1)
            this.photo = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        this.messageEntity=messageEntity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.add_message_layout, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        context = getSherlockActivity();
        ImageButton sendPhotoButton = (ImageButton) view.findViewById(R.id.sendPhoto);
        addText = (ImageButton) view.findViewById(R.id.addText);
        closePhotoPreview = (ImageButton) view.findViewById(R.id.closePhotoPreview);
        text = (EditText) view.findViewById(R.id.message_edit_text);
        reactionPhoto = (ImageView) view.findViewById(R.id.reactionPhoto);

/*        if (messageEntity != null && getReactionPhotoFromStorage(messageEntity.getId()) != null)
        {
            Bitmap toReactionPhoto = getReactionPhotoFromStorage(messageEntity.getId());
            toReactionPhoto = ImageHelper.getRoundedCornerBitmap(toReactionPhoto, Color.WHITE, getActivity().getApplicationContext());
            reactionPhoto.setImageBitmap(toReactionPhoto);
        }*/


        imageView.setImageBitmap(photo);
        sendPhotoButton.setOnClickListener(sendPhotoClick);
        closePhotoPreview.setOnClickListener(closePhotoPreviewClick);
        addText.setOnClickListener(addTextClick);
        text.setOnFocusChangeListener( new MyFocusChangeListener());
        text.setVisibility(View.INVISIBLE);
        text.setOnKeyListener(addTextKeyListener);

        actionBarView = getSherlockActivity().getSupportActionBar().getCustomView();
        ((TextView) actionBarView.findViewById(R.id.barTitle)).setText("ADD MESSAGE");
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setVisibility(View.INVISIBLE);
        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setImageResource(R.drawable.to_menu);
        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setOnClickListener(((MainActivity) getSherlockActivity()).toogleMenu);


        text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        return view;
    }

    private View.OnClickListener sendPhotoClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final MainActivity ma = (MainActivity) getSherlockActivity();
            if(messageEntity == null)
                ma.switchContent(new SelectFriendsFragment(photo, text.getText().toString()));
            else
            {
                ReactrBase.showLoader(getSherlockActivity());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                    ///    Bitmap reaction = null;
                     ///   if(getReactionPhotoFromStorage(messageEntity.getId()) != null)
                     ///       reaction = getReactionPhotoFromStorage(messageEntity.getId());
                        ((MainActivity)getSherlockActivity()).getReactorApi()
                                .sendMessages(
                                        new Integer(messageEntity.getFrom_user()).toString(),
                                        text.getText().toString(),
                                        photo,
                                        null//reaction
                                );
                        ReactrBase.hideLoader();
                        ma.switchContent(new MailBoxFragment());
                    }
                }).start();
            }
        }
    };


    View.OnClickListener addTextClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            text.setVisibility(View.VISIBLE);
            text.setFocusable(true);
            text.requestFocus();

            final InputMethodManager inputMethodManager = (InputMethodManager) getSherlockActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(text, InputMethodManager.SHOW_IMPLICIT);
        }
    };

    View.OnClickListener closePhotoPreviewClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ReactrBase.switchFraagment(getSherlockActivity(), new CreatePhotoFragment());
        }
    };

    View.OnKeyListener addTextKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER) &&
            view.getId() == text.getId())
            {
                if (text.getText().toString().length() == 0)
                    text.setVisibility(View.INVISIBLE);
                InputMethodManager imm = (InputMethodManager)getSherlockActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
            }
            return false;
        }
    };

    private Bitmap getReactionPhotoFromStorage(int id)
    {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        try {
            FileInputStream fis = getSherlockActivity().openFileInput((new Integer(id)).toString() + ".jpg");
            byte[] input = new byte[fis.available()];
            int len = 0;
            while ((len = fis.read(input)) != -1) { byteArray.write(input, 0, len); }
            return RotateBitmap(BitmapFactory.decodeByteArray(byteArray.toByteArray(), 0, (byteArray.toByteArray()).length), -90);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public  Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap newBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        source.recycle();
        return newBitmap;
    }
    private class MyFocusChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus){
            if(v.getId() == R.id.message_edit_text && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                text.setVisibility(View.INVISIBLE);
            }
        }
    }
}
