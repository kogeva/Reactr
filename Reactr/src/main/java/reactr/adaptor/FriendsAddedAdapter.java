package reactr.adaptor;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reactr.MainActivity;
import com.example.reactr.R;
import com.example.reactr.reactr.models.FriendEntity;

import java.util.ArrayList;

import reactr.network.ReactorApi;

public class FriendsAddedAdapter extends BaseAdapter {

    protected ArrayList<FriendEntity> friendCollection;
    private ReactorApi reactorApi;
    private Context context;
    protected LayoutInflater inflater;

    public FriendsAddedAdapter(Context ctx, ArrayList<FriendEntity> friendCollection) {
        this.friendCollection = friendCollection;
        this.inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = ctx;
    }

    @Override
    public int getCount() {
        return friendCollection.size();
    }

    @Override
    public Object getItem(int position) {
        return friendCollection.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null)
            view = inflater.inflate(R.layout.friend_added_you_item_list, parent, false);

        TextView contactName = (TextView) view.findViewById(R.id.contact_name);
        TextView username = (TextView) view.findViewById(R.id.username);
        final CheckBox isConfirm = (CheckBox) view.findViewById(R.id.is_confirm);

        FriendEntity friendEntity = (FriendEntity) getItem(position);

        if(friendEntity.getNameInContacts() != null)
            contactName.setText(friendEntity.getNameInContacts());
        else
            contactName.setText("Сontact is not available");

        if(friendEntity.isConfirmed())
            isConfirm.setChecked(true);
        else
            isConfirm.setChecked(false);

        username.setText(friendEntity.getUsername());

        isConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendEntity friendEntity = (FriendEntity) getItem(position);

                if (isConfirm.isChecked()) {
                    new AddFriendAsyncTask().execute(friendEntity);
                } else {
                    new DeleteFriendAsyncTask(position).execute(friendEntity);
                }
            }
        });

        return view;
    }

    class AddFriendAsyncTask extends AsyncTask<FriendEntity, Integer, Void>
    {

        @Override
        protected Void doInBackground(FriendEntity... friendEntities) {

            reactorApi = ((MainActivity) context).getReactorApi();
            reactorApi.addFriend(friendEntities[0].getPhone());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(context, "Friend added", Toast.LENGTH_SHORT).show();
        }
    }

    class DeleteFriendAsyncTask extends AsyncTask<FriendEntity, Integer, Boolean>
    {
        private Integer position;

        DeleteFriendAsyncTask(Integer position) {
            this.position = position;
        }

        @Override
        protected Boolean doInBackground(FriendEntity... friendEntities) {

            if(reactorApi == null)
                reactorApi = ((MainActivity) context).getReactorApi();

            return reactorApi.deleteFriend(friendEntities[0].getId());
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                Toast.makeText(context, "Friend deleted", Toast.LENGTH_SHORT).show();
                friendCollection.remove(position);
            }
        }
    }
}
