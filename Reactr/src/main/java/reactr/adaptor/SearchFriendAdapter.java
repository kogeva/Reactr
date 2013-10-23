package reactr.adaptor;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jelvix.reactr.MainActivity;
import com.jelvix.reactr.R;
import com.jelvix.reactr.reactr.models.FriendEntity;

import java.util.ArrayList;

import reactr.network.ReactorApi;


public class SearchFriendAdapter extends FriendsAddedAdapter {

    private Context context;
    private ReactorApi api;
    private FriendEntity friend;

    public SearchFriendAdapter(Context ctx, ArrayList<FriendEntity> friendCollection, ReactorApi api) {
        super(ctx, friendCollection);
        this.api = api;
        context = ctx;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = this.inflater.inflate(R.layout.search_friends_list_item, parent, false);

        TextView username = (TextView) view.findViewById(R.id.username);
        ImageButton addButton = (ImageButton) view.findViewById(R.id.addFriend);

        friend = (FriendEntity) this.getItem(position);

        username.setText(friend.getUsername());
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddFriendAsyncTask().execute((FriendEntity)getItem(position));
            }
        });

        return view;
    }

    class AddFriendAsyncTask extends AsyncTask<FriendEntity, Integer, Boolean>
    {
        @Override
        protected Boolean doInBackground(FriendEntity... friendEntities) {
            if(api == null)
                api = ((MainActivity) context).getReactorApi();

            return api.addFriend(friendEntities[0].getPhone());
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result)
                Toast.makeText(context, "Friend added", Toast.LENGTH_SHORT).show();
        }
    }
}
