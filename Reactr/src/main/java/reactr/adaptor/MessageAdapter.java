package reactr.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.reactr.MainActivity;
import com.example.reactr.R;
import com.example.reactr.ReactrBase;
import com.example.reactr.fragments.ShowMessageFragment;
import com.example.reactr.reactr.models.FriendEntity;
import com.example.reactr.reactr.models.MessageEntity;

import java.util.ArrayList;


public class MessageAdapter extends BaseAdapter {

    private ArrayList<MessageEntity> messages;
    ArrayList<FriendEntity> friends;
    private LayoutInflater inflater;
    private Context ctx;

    public MessageAdapter(Context ctx, ArrayList<MessageEntity> messagess, ArrayList<FriendEntity> friends) {
        this.ctx = ctx;
        this.friends  = friends;
        this.messages = messagess;
        this.inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return messages.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null)
            view = inflater.inflate(R.layout.message_list_item, parent, false);

        TextView date         = (TextView) view.findViewById(R.id.date);
        TextView user         = (TextView) view.findViewById(R.id.userText);
        TextView reaction     = (TextView) view.findViewById(R.id.isReaction);
        ImageView typeMessage = (ImageView) view.findViewById(R.id.typeMessage);

        final MessageEntity message = (MessageEntity) getItem(position);
        
        //*******
        if(!message.getIsRead()&&!message.getFromMe()){
         //   view.setBackgroundColor(Color.rgb(101, 167, 210));
            view.setBackgroundColor(Color.LTGRAY);
        }
        else{
            view.setBackgroundColor(Color.WHITE);
        }
        //*******


        message.setUsernameWithFriends(friends);

        if(message.getFromMe()){
            user.setText(MainActivity.getUsername());
            typeMessage.setImageResource(R.drawable.rsz_arrow_forward_black);
        }
        else {
            user.setText(message.getUsername());
            typeMessage.setImageResource(R.drawable.rsz_camera_mailbox2);
        }

        if (message.getReactionPhoto().equals("null"))
            reaction.setVisibility(View.INVISIBLE);
        else
            reaction.setVisibility(View.VISIBLE);

        date.setText(message.getCreatedAt());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReactrBase.switchFraagment((SherlockFragmentActivity) ctx, new ShowMessageFragment(message));
            }
        });
        return  view;
    }
}
