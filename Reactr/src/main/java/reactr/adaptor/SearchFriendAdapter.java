package reactr.adaptor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reactr.R;
import com.example.reactr.reactr.models.FriendEntity;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = this.inflater.inflate(R.layout.search_friends_list_item, parent, false);

        TextView username = (TextView) view.findViewById(R.id.username);
        ImageButton addButton = (ImageButton) view.findViewById(R.id.addFriend);

        friend = (FriendEntity) this.getItem(position);

        username.setText(friend.getUsername());
        addButton.setOnClickListener(addFriendClick);


        return view;
    }

    View.OnClickListener addFriendClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new Thread(addFriendTask).start();
            Toast.makeText(context, "Friend added", Toast.LENGTH_LONG).show();
        }
    };

    Runnable addFriendTask = new Runnable() {
        @Override
        public void run() {
            api.addFriend(friend.getPhone());
        }
    };
}
