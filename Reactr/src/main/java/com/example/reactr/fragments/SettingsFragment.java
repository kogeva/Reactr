package com.example.reactr.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.reactr.MainActivity;
import com.example.reactr.R;

import reactr.network.ReactorApi;

/**
 * Created by vova on 26.09.13.
 */
public class SettingsFragment extends SherlockFragment{

    private EditText username;
    private EditText phoneNumber;
    private EditText email;
    private Switch privacyMessageSwitch;
    private ReactorApi reactorApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_layout, container, false);

        username = (EditText) view.findViewById(R.id.editUsername);
        phoneNumber = (EditText) view.findViewById(R.id.editPhoneNumber);
        email = (EditText) view.findViewById(R.id.editEmail);
        privacyMessageSwitch = (Switch) view.findViewById(R.id.switchPrivacyMessage);

        privacyMessageSwitch.setOnCheckedChangeListener(switchPrivacyListener);

        reactorApi = ((MainActivity) getSherlockActivity()).getReactorApi();

        return view;
    }

    private CompoundButton.OnCheckedChangeListener switchPrivacyListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean state) {
            new SettingsAsyncTask().execute(state);
        }
    };

    private class SettingsAsyncTask extends AsyncTask<Boolean, Integer, Boolean>
    {

        @Override
        protected Boolean doInBackground(Boolean... params) {
            return reactorApi.setPrivacyMessage(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean state) {
            if (!state)
                privacyMessageSwitch.toggle();
        }
    };
}
