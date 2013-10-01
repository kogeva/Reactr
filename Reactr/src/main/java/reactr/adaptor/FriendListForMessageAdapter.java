package reactr.adaptor;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.reactr.R;
import com.example.reactr.reactr.models.FriendEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.widget.CompoundButton.OnCheckedChangeListener;

public class FriendListForMessageAdapter extends FriendsAddedAdapter {

    private HashMap<Integer, Integer> userIdStack = new HashMap<Integer, Integer>();
    private String stringFriendIds;

    public FriendListForMessageAdapter(Context ctx, ArrayList<FriendEntity> friendCollection) {
        super(ctx, friendCollection);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = this.inflater.inflate(R.layout.friend_added_you_item_list, parent, false);

        final FriendEntity friendEntity = (FriendEntity) getItem(position);

        TextView contactName = (TextView) view.findViewById(R.id.contact_name);
        TextView username = (TextView) view.findViewById(R.id.username);
        CheckBox isConfirm = (CheckBox) view.findViewById(R.id.is_confirm);

        FriendEntity friend = (FriendEntity) getItem(position);

        if(friend.getBlockedMe())
        {
            isConfirm.setText("You are blocked");
            isConfirm.setButtonDrawable(R.drawable.christ_white);
            isConfirm.setTextColor(Color.RED);
            isConfirm.setClickable(false);
        }

        contactName.setText(friendEntity.getNameInContacts());
        username.setText(friendEntity.getUsername());

        isConfirm.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    userIdStack.put(position, friendEntity.getId());
                }
                else
                    userIdStack.remove(position);
            }
        });

        return view;
    }

    public String getFriendIds()
    {
        stringFriendIds = new String();
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : userIdStack.entrySet()) {
            if(i == 0)
                stringFriendIds = stringFriendIds.concat(entry.getValue().toString());
            else
                stringFriendIds = stringFriendIds.concat(new String(",")).concat(entry.getValue().toString());
            i++;
        }
        return stringFriendIds;
    }
}
