package com.example.reactr;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.reactr.reactr.models.FriendEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReactrBase {
    private static ProgressDialog progressDialog;

    public static HashMap<Long, String> getContacts(Context ctx)
    {
        HashMap<Long, String> contacts = new HashMap<Long, String>();

        Uri queryUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
        };

        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";

        CursorLoader cursorLoader = new CursorLoader(ctx, queryUri, projection, selection, null, null);

        Cursor cursor = cursorLoader.loadInBackground();

        while (cursor.moveToNext())
        {
            String str = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    .replaceAll("[^0-9]", "");
            try {
                contacts.put(Long.parseLong(str), cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            } catch (Exception e)
            {

            }
        }
        return contacts;
    }

    public static ArrayList<FriendEntity> addInFriendContactName(ArrayList<FriendEntity> friends, HashMap<Long, String> contacts)
    {
        for (int i = 0; i < friends.size(); i++)
        {
            for (Map.Entry<Long, String> entryContacts : contacts.entrySet())
            {
                if (entryContacts.getKey().equals(friends.get(i).getPhone()))
                    friends.get(i).setNameInContacts(entryContacts.getValue());
            }
        }
        return friends;
    }

    public static ArrayList<FriendEntity> mergeWhoAddMe(ArrayList<FriendEntity> whoAddMe , ArrayList<FriendEntity> myFriends)
    {
        for (int i = 0; i < whoAddMe.size(); i++)
        {
            for (int index = 0; index < myFriends.size(); index++)
            {
                if (whoAddMe.get(i).getId() == myFriends.get(index).getId())
                {
                    whoAddMe.get(i).setConfirmed(true);
                }
            }
        }
        return whoAddMe;
    }

    public static void switchFraagment(SherlockFragmentActivity activity, SherlockFragment fragment)
    {
        if(activity == null)
            return;

        if(activity instanceof MainActivity) {
            MainActivity ma = (MainActivity) activity;
            ma.switchContent(fragment);
        }
    }

    public static HashMap<String, String> getAppProperty(Context context)
    {
        HashMap<String, String> property = new HashMap<String, String>();
        SharedPreferences preferences = context.getSharedPreferences("reactrPrefer", context.MODE_PRIVATE);

        property.put("user_id", new Integer(preferences.getInt("user_id", 0)).toString());
        property.put("session_hash", preferences.getString("session_hash", null));
        property.put("username", preferences.getString("username", null));
        //property.put("privacy_message", preferences.getString("username", null));

        return property;
    }

    public static String getPhonesString(HashMap<Long, String> contacts)
    {
        String contactsString = new String();
        for (Map.Entry<Long, String> entryContacts : contacts.entrySet())
        {
            contactsString  = contactsString.concat(entryContacts.getKey().toString()+",");
        }
        contactsString = contactsString.substring(0, contactsString.length() -1);
        return  contactsString;
    }

    public static ArrayList<ArrayList<JSONObject>> buildDataForContactLIst(JSONArray userInSystem, ArrayList<FriendEntity> myFriends, HashMap<Long, String> contacts)
    {
        ArrayList<JSONObject> users = new ArrayList<JSONObject>();
        ArrayList<JSONObject> reactrUsers = new ArrayList<JSONObject>();
        ArrayList<JSONObject> contactUsers = new ArrayList<JSONObject>();
        ArrayList<ArrayList<JSONObject>> groups = new ArrayList<ArrayList<JSONObject>>();

        try {
            for (int i = 0; i < userInSystem.length(); i++)
            {
                users.add(userInSystem.getJSONObject(i));
            }

            for (int i = 0; i < users.size(); i++)
            {
                for (int index = 0; index < myFriends.size(); index++)
                {
                    if(!users.get(i).isNull("id") && users.get(i).getInt("id") == myFriends.get(index).getId())
                        users.remove(i);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < users.size(); i++)
        {
            if(users.get(i).isNull("id"))
                contactUsers.add(users.get(i));
            else
                reactrUsers.add(users.get(i));
        }

        for (int i = 0; i < contactUsers.size(); i++)
        {
            try {
                contactUsers.get(i).put("username", contacts.get(contactUsers.get(i).getLong("phone")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        groups.add(reactrUsers);
        groups.add(contactUsers);

        return groups;
    }

    public static void showLoader(Context context)
    {
        progressDialog  = ProgressDialog.show(context , "Loading", "Please Wait");
    }

    public static void hideLoader()
    {
        if(progressDialog != null)
            progressDialog.dismiss();
    }
}
