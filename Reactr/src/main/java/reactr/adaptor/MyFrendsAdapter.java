package reactr.adaptor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.reactr.R;
import com.example.reactr.reactr.models.FriendEntity;

import java.util.ArrayList;

public class MyFrendsAdapter extends FriendsAddedAdapter {
    public MyFrendsAdapter(Context ctx, ArrayList<FriendEntity> friendCollection) {
        super(ctx, friendCollection);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null)
            view = this.inflater.inflate(R.layout.my_friends_list, parent, false);

        TextView contactName = (TextView) view.findViewById(R.id.contact_name);
        TextView username = (TextView) view.findViewById(R.id.username);

        FriendEntity friendEntity = (FriendEntity) getItem(position);
        username.setText(friendEntity.getUsername());

        if(friendEntity.getNameInContacts() != null)
            contactName.setText(friendEntity.getNameInContacts());
        else
            contactName.setText("Сontact is not available");

        return view;
    }
}
