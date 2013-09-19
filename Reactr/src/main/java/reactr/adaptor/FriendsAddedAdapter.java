package reactr.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.reactr.R;
import com.example.reactr.reactr.models.FriendEntity;

import java.util.ArrayList;

public class FriendsAddedAdapter extends BaseAdapter {

    private ArrayList<FriendEntity> friendCollection;
    protected LayoutInflater inflater;

    public FriendsAddedAdapter(Context ctx, ArrayList<FriendEntity> friendCollection) {
        this.friendCollection = friendCollection;
        this.inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null)
            view = inflater.inflate(R.layout.friend_added_you_item_list, parent, false);

        TextView contactName = (TextView) view.findViewById(R.id.contact_name);
        TextView username = (TextView) view.findViewById(R.id.username);
        CheckBox isConfirm = (CheckBox) view.findViewById(R.id.is_confirm);

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

        return view;
    }
}
