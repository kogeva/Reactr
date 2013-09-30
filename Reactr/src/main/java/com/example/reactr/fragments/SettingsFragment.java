package com.example.reactr.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.reactr.MainActivity;
import com.example.reactr.R;

import java.util.ArrayList;

import reactr.network.ReactorApi;

/**
 * Created by vova on 26.09.13.
 */
public class SettingsFragment extends SherlockFragment{

    private EditText username;
    private EditText phoneNumber;
    private EditText email;
    private Button saveButton;
    private Switch privacyMessageSwitch;
    private ReactorApi reactorApi;
    private View actionBarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_layout, container, false);

        username = (EditText) view.findViewById(R.id.editUsername);
        phoneNumber = (EditText) view.findViewById(R.id.editPhoneNumber);
        email = (EditText) view.findViewById(R.id.editEmail);
        saveButton = (Button) view.findViewById(R.id.saveButton);
        privacyMessageSwitch = (Switch) view.findViewById(R.id.switchPrivacyMessage);

        privacyMessageSwitch.setOnCheckedChangeListener(switchPrivacyListener);
        saveButton.setOnClickListener(saveClickListener);

        reactorApi = ((MainActivity) getSherlockActivity()).getReactorApi();

        setDataInInput();

        actionBarView = getSherlockActivity().getSupportActionBar().getCustomView();
        ((TextView) actionBarView.findViewById(R.id.barTitle)).setText("SETTINGS");
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setVisibility(View.INVISIBLE);
//        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setImageResource(R.drawable.to_menu);
//        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setPadding(10, 14, 43, 14);

        return view;
    }

    private CompoundButton.OnCheckedChangeListener switchPrivacyListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean state) {
            new SettingsAsyncTask().execute(state);
        }
    };

    private View.OnClickListener saveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new EditDataAsyncTask().execute(phoneNumber.getText().toString(), email.getText().toString());
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

    private class EditDataAsyncTask extends AsyncTask<String , Integer, ArrayList<String>>
    {

        @Override
        protected ArrayList<String> doInBackground (String... params) {
            ArrayList<String> errors = new ArrayList<String>();

            String phone = null;
            String email = null;

            if(!params[0].equals(((MainActivity) getSherlockActivity()).getPhone()))
                phone = params[0];
            else
                errors.add("This phone already exists in the system");

            if(!params[1].equals(((MainActivity) getSherlockActivity()).getEmail()))
                email = params[1];
            else
                errors.add("This email already exists in the system");

            if(phone != null || email != null)
                return  reactorApi.editUserData(phone, email);
            return errors;
        }

        @Override
        protected void onPostExecute(ArrayList<String> errors) {
            if(errors.size() > 0)
            {
                for (int i = 0; i < errors.size(); i++)
                {
                    Toast.makeText(getSherlockActivity(), errors.get(i), Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getSherlockActivity(), "Changed", Toast.LENGTH_SHORT).show();
                ((MainActivity) getSherlockActivity()).setAppSettings("phone", phoneNumber.getText().toString());
                ((MainActivity) getSherlockActivity()).setAppSettings("email", email.getText().toString());
            }
        }
    };

    private void setDataInInput ()
    {
        username.setText(((MainActivity) getSherlockActivity()).getUsername());
        email.setText(((MainActivity) getSherlockActivity()).getEmail());
        phoneNumber.setText(((MainActivity) getSherlockActivity()).getPhone());
        privacyMessageSwitch.setChecked(((MainActivity) getSherlockActivity()).isPrivacyMessage());
    }
}
