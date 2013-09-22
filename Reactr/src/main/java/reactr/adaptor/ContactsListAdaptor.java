package reactr.adaptor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reactr.MainActivity;
import com.example.reactr.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import reactr.network.ReactorApi;

public class ContactsListAdaptor extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<ArrayList<JSONObject>> groupUser;
    private JSONObject user;
    private LayoutInflater inflater;
    private ReactorApi api;
    private Integer groupeIndex;
    private Integer  elementIndex;

    public ContactsListAdaptor(Context context, ArrayList<ArrayList<JSONObject>> groupUser, ReactorApi api) {
        this.context = context;
        this.groupUser = groupUser;
        this.api = api;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return groupUser.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return groupUser.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return groupUser.get(i);
    }

    @Override
    public Object getChild(int i, int i2) {
        return groupUser.get(i).get(i2);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int i, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        if(view == null)
            view = inflater.inflate(R.layout.list_header_item, viewGroup, false);
        TextView headerText = (TextView) view.findViewById(R.id.headerText);
        if(i == 0)
            headerText.setText("Friends on Reactr");
        else
            headerText.setText("Invite friends from Contacts");

        return view;
    }

    @Override
    public View getChildView(final int groupePosition, final int childPosition, boolean b, View view, ViewGroup viewGroup) {
        if (view == null)
            view = inflater.inflate(R.layout.contact_item_list, viewGroup, false);

        TextView contactName = (TextView) view.findViewById(R.id.contact_name);
        TextView username = (TextView) view.findViewById(R.id.phone);
        ImageButton actionButton = (ImageButton) view.findViewById(R.id.actionButton);
        groupeIndex = groupePosition;
        elementIndex = childPosition;


        user = groupUser.get(groupePosition).get(childPosition);
        try {
            contactName.setText(user.getString("username"));
            username.setText((user.getString("phone").length() < 10) ? "0" + user.getString("phone") : user.getString("phone"));
        } catch (Exception e) {
            e.getMessage();
        }

        if(groupePosition == 0)
        {
            ImageButton button = (ImageButton) view.findViewById(R.id.actionButton);
            button.setImageResource(R.drawable.rsz_add_friend_gray);
        }

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(groupePosition == 0) {
                    user = groupUser.get(groupePosition).get(childPosition);
                    new Thread(addFriendTask).start();
                }
                else {
                    Uri uri = null;
                    try {
                        uri = Uri.parse("smsto:" + user.getString("phone"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                    String myUsername = ((MainActivity) context).getUsername();
                    String textMessage = "Add me on Reactr! Username: " + myUsername + "  http://reactrapp.com";
                    intent.putExtra("sms_body", textMessage);
                    context.startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return false;
    }

    Runnable addFriendTask = new Runnable() {
        @Override
        public void run() {
            try {
                groupeIndex.toString();
                elementIndex.toString();
                api.addFriend(user.getLong("phone"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
