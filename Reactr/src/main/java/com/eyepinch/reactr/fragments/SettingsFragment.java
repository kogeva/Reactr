package com.eyepinch.reactr.fragments;

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
import com.eyepinch.reactr.MainActivity;
import com.eyepinch.reactr.R;

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
        privacyMessageSwitch.setChecked(((MainActivity) getSherlockActivity()).isPrivacyMessage());

        privacyMessageSwitch.setOnCheckedChangeListener(switchPrivacyListener);
        saveButton.setOnClickListener(saveClickListener);

        reactorApi = ((MainActivity) getSherlockActivity()).getReactorApi();

        setDataInInput();

        actionBarView = getSherlockActivity().getSupportActionBar().getCustomView();
        ((TextView) actionBarView.findViewById(R.id.barTitle)).setText("SETTINGS");
        ((ImageButton) actionBarView.findViewById(R.id.barItem)).setVisibility(View.INVISIBLE);
        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setImageResource(R.drawable.to_menu);
        ((ImageButton) actionBarView.findViewById(R.id.toggleMenu)).setOnClickListener(((MainActivity) getSherlockActivity()).toogleMenu);


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
            if(isValidEmail(email.getText().toString()))
                if(booleanIsValidPhone(phoneNumber.getText().toString()))
                    new EditDataAsyncTask().execute(phoneNumber.getText().toString(), email.getText().toString());
                else
                    Toast.makeText(getSherlockActivity(), "Invalid  phone number", Toast.LENGTH_SHORT).show();

            else
                Toast.makeText(getSherlockActivity(), "Invalid email address", Toast.LENGTH_SHORT).show();
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
            else
                ((MainActivity) getSherlockActivity()).setAppSettings("privacy_message", new Boolean(privacyMessageSwitch.isChecked()).toString());
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

    private  boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private Boolean booleanIsValidPhone (String phone) {
        return  (phone.length() > 9 && phone.length() <= 10) ? true : false;
    }
}
