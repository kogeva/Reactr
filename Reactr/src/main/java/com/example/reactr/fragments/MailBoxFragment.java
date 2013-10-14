package com.example.reactr.fragments;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.reactr.MainActivity;
import com.example.reactr.R;
import com.example.reactr.ReactrBase;
import com.example.reactr.reactr.models.FriendEntity;
import com.example.reactr.reactr.models.MessageEntity;

import java.util.ArrayList;

import reactr.adaptor.MessageAdapter;
import reactr.network.ReactorApi;
import reactr.utils.MoreListHelper;


public class MailBoxFragment extends SherlockFragment {

    private ReactorApi api;
    private ArrayList<MessageEntity> messageArray;
    private ArrayList<FriendEntity> friendEntities;
    private Handler handler;
    private MessageAdapter adapter;
    private ListView messageList;
    private View actionBarView;
    private MoreListHelper<String> moreListHelper;
    private boolean showLoader=true;

    public MailBoxFragment(boolean loader) {
        showLoader=loader;
    }
    public MailBoxFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mail_box_layout, container, false);
        messageList = (ListView) view.findViewById(R.id.message_list);

        handler = new Handler();
        MainActivity ma = (MainActivity) getSherlockActivity();
        api = ma.getReactorApi();
        if(showLoader)
        ReactrBase.showLoader(getSherlockActivity());
        Long maxMemory = Runtime.getRuntime().maxMemory();
        maxMemory.toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
             //   messageArray = api.getMessagesFromTo(0, 15);
                messageArray = api.getMessages();
                friendEntities = api.getFriends();
                adapter = new MessageAdapter(getSherlockActivity(), messageArray, friendEntities);
                handler.post(updateMessageList);
            }
        }).start();


        actionBarView = getSherlockActivity().getSupportActionBar().getCustomView();
        ((TextView) actionBarView.findViewById(R.id.barTitle)).setText("MAILBOX");
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setVisibility(View.VISIBLE);
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setImageResource(R.drawable.act_bar_make_photo);
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setOnClickListener(goToTakePhoto);

        //********кнопка для обновления мейлбокса
        ((ImageButton) actionBarView.findViewById(R.id.refreshItem)).setVisibility(View.INVISIBLE);
        ((ImageButton) actionBarView.findViewById(R.id.refreshItem)).setOnClickListener(refreshMailBox);

        return view;
    }


    Runnable updateMessageList = new Runnable() {
        @Override
        public void run() {
            if (messageArray.size() != 0) {

                //******************************
                //moreListHelper - для догрузки сообщений в Лист. Не работает с нащими сообщениями
                /*
                moreListHelper = new MoreListHelper<String>(getActivity(), messageList, messageArray, friendEntities) {
                    @Override
                    public void onLoadItems(ArrayList<MessageEntity> items, int itemsPerPage) {
                        // Метод подгрузки выполняется в отдельном потоке.
                        Log.d("More","onLoadItems");
                        items.addAll(messageArray);
                    /*    for (int i = 0; i < itemsPerPage; i++) {
                            items.addAll(messageArray);
                        }
                    }
                };
                moreListHelper.setItemsPerPage(5);
                //   moreListHelper.createAdapter(android.R.layout.simple_list_item_1);
                //  setListAdapter(moreListHelper.getAdapter());
                messageList.setAdapter(adapter);
                moreListHelper.loadItems();
                //getListView().setOnItemClickListener(this);
                */
                //******************************

                messageList.setAdapter(adapter);

                ((MainActivity)getActivity()).updateMenu();
            } else {
                ReactrBase.switchFraagment(getSherlockActivity(), new CreatePhotoFragment());
            }
            if(showLoader){
            ReactrBase.hideLoader();}
            showLoader=true;
        }
    };

    View.OnClickListener goToTakePhoto = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ReactrBase.switchFraagment(getSherlockActivity(), new CreatePhotoFragment());
        }
    };
    View.OnClickListener refreshMailBox = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("refreshMailBox","refreshMailBox");

          ((ImageButton) actionBarView.findViewById(R.id.refreshItem)).setImageResource(R.drawable.loading);
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.circle_anim);
            ((ImageButton)actionBarView.findViewById(R.id.refreshItem)).startAnimation(animation);

            handler.post(refreshMessageList);
            /*
            Fragment newContent = new MailBoxFragment();

            if(getActivity() == null)
                return;

            if(getActivity() instanceof MainActivity) {
                MainActivity ma = (MainActivity) getActivity();
                ma.switchContent(newContent);
            }*/
        }
    };

    Runnable refreshMessageList = new Runnable() {
        @Override
        public void run() {
         /*   if (messageArray.size() != 0) {
                messageList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                ((MainActivity)getActivity()).updateMenu();
            } else {
                ReactrBase.switchFraagment(getSherlockActivity(), new CreatePhotoFragment());
            }
            try {
                // Имитируем тяжелую загрузку.
                Thread.sleep(2000);
            } catch (InterruptedException e) {}*/

            Fragment newContent = new MailBoxFragment(false);

            if(getActivity() == null)
                return;

            if(getActivity() instanceof MainActivity) {
                MainActivity ma = (MainActivity) getActivity();
                ma.switchContent(newContent);
            }

          ((ImageButton) actionBarView.findViewById(R.id.refreshItem)).setImageResource(R.drawable.refresh_mailbox);
         ((ImageButton)actionBarView.findViewById(R.id.refreshItem)).clearAnimation();
        }
    };
}
