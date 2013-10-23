package reactr.adaptor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jelvix.reactr.MainActivity;
import com.jelvix.reactr.R;

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
    private ReactorApi reactorApi;

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
            actionButton.setImageResource(R.drawable.add_friend);
        else {
            actionButton.setImageResource(R.drawable.envelope);
        }


        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(groupePosition == 0) {
                    user = groupUser.get(groupePosition).get(childPosition);
                    try {
                        new AddFriendAsyncTask(childPosition).execute(user.getLong("phone"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {

                    JSONObject curentUser = (JSONObject) getChild(groupePosition, childPosition);

                    Uri uri = null;
                    try {
                        uri = Uri.parse("smsto:" + curentUser.getString("phone"));
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

    class AddFriendAsyncTask extends AsyncTask<Long, Integer, Boolean>
    {
        private int position;

        public AddFriendAsyncTask(Integer position) {
            this.position = position;
        }

        @Override
        protected Boolean doInBackground(Long... friendNumberPhone) {
            if (reactorApi == null)
                reactorApi = ((MainActivity) context).getReactorApi();
            return reactorApi.addFriend(friendNumberPhone[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(context, "Friend added", Toast.LENGTH_SHORT).show();
                groupUser.get(0).remove(position);
                notifyDataSetChanged();
            }
        }
    }
}
