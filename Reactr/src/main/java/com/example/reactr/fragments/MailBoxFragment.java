package com.example.reactr.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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


public class MailBoxFragment extends SherlockFragment {

    private ReactorApi api;
    private ArrayList<MessageEntity> messageArray;
    private ArrayList<FriendEntity> friendEntities;
    private Handler handler;
    private MessageAdapter adapter;
    private ListView messageList;
    private View actionBarView;
    Integer m_PreviousTotalCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mail_box_layout, container, false);
        messageList = (ListView) view.findViewById(R.id.message_list);

        handler = new Handler();
        MainActivity ma = (MainActivity) getSherlockActivity();
        api = ma.getReactorApi();
        ReactrBase.showLoader(getSherlockActivity());
        Long maxMemory = Runtime.getRuntime().maxMemory();
        maxMemory.toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                messageArray = api.getMessages(0, 15);
                friendEntities = api.getFriends();
                adapter = new MessageAdapter(getSherlockActivity(), messageArray, friendEntities);
                handler.post(updateMessageList);
            }
        }).start();

        actionBarView = getSherlockActivity().getSupportActionBar().getCustomView();
        ((TextView) actionBarView.findViewById(R.id.barTitle)).setText("MAILBOX");
        ((ImageButton) actionBarView.findViewById(R.id.refreshButton)).setVisibility(View.VISIBLE);
        ((ImageButton) actionBarView.findViewById(R.id.refreshButton)).setOnClickListener(refreshListListener);
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setVisibility(View.VISIBLE);
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setImageResource(R.drawable.act_bar_make_photo);
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setOnClickListener(goToTakePhoto);

        messageList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                {
                    if (totalItemCount == 0 || adapter == null)
                        return;
                    if (m_PreviousTotalCount == totalItemCount)
                        return;
                    boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                    if (loadMore)
                    {
                        m_PreviousTotalCount = totalItemCount;
                        ReactrBase.showLoader(getSherlockActivity());
                        new LoadMessageAsyncTask().execute(totalItemCount, totalItemCount + 15);
                    }
                }
            }
        });

        return view;
    }

    Runnable updateMessageList = new Runnable() {
        @Override
        public void run() {
            if (messageArray.size() != 0) {
                messageList.setAdapter(adapter);
                ((MainActivity)getActivity()).updateMenu();
            } else {
                ReactrBase.switchFraagment(getSherlockActivity(), new CreatePhotoFragment());
            }
            ReactrBase.hideLoader();
        }
    };

    View.OnClickListener refreshListListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ReactrBase.showLoader(getSherlockActivity());
            m_PreviousTotalCount = 0;
            new LoadNewMessageAsyncTask().execute();
        }
    };

    View.OnClickListener goToTakePhoto = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ReactrBase.switchFraagment(getSherlockActivity(), new CreatePhotoFragment());
        }
    };

    class LoadMessageAsyncTask extends AsyncTask<Integer, Void, ArrayList<MessageEntity>>{

        @Override
        protected ArrayList<MessageEntity> doInBackground(Integer... voids) {
            return api.getMessages(voids[0], voids[1]);
        }

        @Override
        protected void onPostExecute(ArrayList<MessageEntity> messageEntities) {
            adapter.addMessagesInList(messageEntities);
            ReactrBase.hideLoader();
        }
    }

    class LoadNewMessageAsyncTask extends AsyncTask<Integer, Void, ArrayList<MessageEntity>>{

        @Override
        protected ArrayList<MessageEntity> doInBackground(Integer... voids) {
            return api.getMessages(0, 15);
        }

        @Override
        protected void onPostExecute(ArrayList<MessageEntity> messageEntities) {
            adapter.refreshList(messageEntities);
            ReactrBase.hideLoader();
        }
    }

    @Override
    public void onDestroyView() {
        ((ImageButton) actionBarView.findViewById(R.id.refreshButton)).setVisibility(View.INVISIBLE);
        super.onDestroyView();
    }
}
