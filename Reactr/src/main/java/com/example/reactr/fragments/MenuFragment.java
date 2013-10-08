package com.example.reactr.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.reactr.AndroidCamera;
import com.example.reactr.MainActivity;
import com.example.reactr.R;
import com.example.reactr.StartActivity;
import com.example.reactr.reactr.models.MenuItem;

import java.util.ArrayList;

import reactr.adaptor.MenuAdapter;
import com.example.reactr.reactr.models.ReactrConstants;

public class MenuFragment extends ListFragment {

     MenuAdapter st_m_adptr;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_menu, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
        menuItems.add(new MenuItem("Take Photo", "0", 0));

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Integer mes = ((MainActivity) getActivity()).getReactorApi().countOfnewMessages();
        menuItems.add(new MenuItem("Mailbox", mes.toString(), 1));
        menuItems.add(new MenuItem("Friends", "0", 2));
        menuItems.add(new MenuItem("Settings", "0", 3));
        menuItems.add(new MenuItem("About Reactr", "0", 4));
        menuItems.add(new MenuItem("Privacy", "0", 5));
        menuItems.add(new MenuItem("Terms", "0" , 6));
        menuItems.add(new MenuItem("Contact Us", "0" , 7));
        menuItems.add(new MenuItem("Logout", "0" , 8));
        st_m_adptr = new MenuAdapter(getActivity(), menuItems);
        setListAdapter(st_m_adptr);
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
                newContent = new SettingsFragment();
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
        st_m_adptr.setSelect(position);
        setListAdapter(st_m_adptr);
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
    public void updateMenu() {
        MenuItem mi = (MenuItem)st_m_adptr.getItem(1);

        Integer mes = ((MainActivity) getActivity()).getReactorApi().countOfnewMessages();
        mi.setCountNewMessage(String.valueOf(mes));
        setListAdapter(st_m_adptr);
    }

    private void updateMenuColor(int i) {
        MenuItem mi = (MenuItem)st_m_adptr.getItem(i);
        Integer mes = ((MainActivity) getActivity()).getReactorApi().countOfnewMessages();
        mi.setCountNewMessage(String.valueOf(mes));
        setListAdapter(st_m_adptr);
    }

}