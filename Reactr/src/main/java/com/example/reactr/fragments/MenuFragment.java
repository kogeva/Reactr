package com.example.reactr.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.reactr.MainActivity;
import com.example.reactr.R;
import com.example.reactr.StartActivity;
import com.example.reactr.reactr.models.MenuItem;

import java.util.ArrayList;

import reactr.adaptor.MenuAdapter;
import reactr.utils.ReactrConstants;

public class MenuFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_menu, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
        menuItems.add(new MenuItem("Send Photo", "0", 0));


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Integer mes=((MainActivity) getActivity()).getReactorApi().newMessages();
        if(mes!=0)
            menuItems.add(new MenuItem("Mailbox", mes.toString(), 1));
        else
            menuItems.add(new MenuItem("Mailbox", "0", 1));

        menuItems.add(new MenuItem("Friends", "0", 2));
        menuItems.add(new MenuItem("Settings", "0", 3));
        menuItems.add(new MenuItem("About Reactr", "0", 4));
        menuItems.add(new MenuItem("Privacy", "0", 5));
        menuItems.add(new MenuItem("Terms", "0" , 6));
        menuItems.add(new MenuItem("Contact Us", "0" , 7));
        menuItems.add(new MenuItem("Logout", "0" , 8));

        MenuAdapter menuAdapter = new MenuAdapter(getActivity(), menuItems);
        setListAdapter(menuAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d("MESSSSAGE", (new Integer(position)).toString());
        Fragment newContent = null;
        switch (position){
            case 0:
                newContent = new CreatePhotoFragment();
                break;
            case 1:
                newContent = new MailBoxFragment();
                break;
            case 2:
                newContent = new MyFriendsFragment();
                break;
            case 3:
 //               newContent = new AddMessageFragment();
                break;

            case 4:
                newContent = new StaticInfoFragment(ReactrConstants.ABOUT_REACTR);
                break;
            case 5:
                newContent = new StaticInfoFragment(ReactrConstants.PRIVACY);
                break;
            case 6:
                newContent = new StaticInfoFragment(ReactrConstants.TERMS);
                break;
            case 7:
                newContent = new StaticInfoFragment(ReactrConstants.CONTACT_US);
                break;

            case 8:
                ((MainActivity ) getActivity()).removeSessionHash();
                ((MainActivity ) getActivity()).startActivity(new Intent(getActivity(), StartActivity.class));
                break;
        }
        ((MainActivity) getActivity()).toggle();
        if(newContent != null)
            switchFraagment(newContent);
    }

    private void switchFraagment(Fragment fragment)
    {
        if(getActivity() == null)
            return;

        if(getActivity() instanceof MainActivity) {
            MainActivity ma = (MainActivity) getActivity();
            ma.switchContent(fragment);
        }
    }
    @Override
    public void onResume() {

       /* Toast.makeText(getActivity().getBaseContext(), "onResume", Toast.LENGTH_SHORT).show();

        MenuAdapter menuAdapter;
        menuAdapter= (MenuAdapter) getListAdapter();

        MenuItem mi= (MenuItem)menuAdapter.getItem(1);
        mi.setName("NAMRRWrwrwr");
        mi.setCountNewMessage("911");*/
        super.onResume();

    }


    public void hi() {

        Toast.makeText(getActivity().getBaseContext(), "hi", Toast.LENGTH_SHORT).show();

    }
}