package reactr.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


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
            view.setBackgroundResource(R.drawable.unread);
        }
        else{
            view.setBackgroundColor(Color.WHITE);
        }
        //*******
        String formattedDate = convertDate(message.getCreatedAt());


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

        date.setText(formattedDate);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReactrBase.switchFraagment((SherlockFragmentActivity) ctx, new ShowMessageFragment(message));
            }
        });

        view.setOnTouchListener(myOnToucListener);
        view.setOnFocusChangeListener(myOnFocusChangeListener);
        return  view;
    }

    private String convertDate (String fromDate)
    {
        DateFormat formatter;
        String toReturn="";
        Date date=new Date();
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = formatter.parse(fromDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar myCal = new GregorianCalendar();
        myCal.setTime(date);

        String month="";
        if(date.getMonth()<10){
            month="0"+String.valueOf(date.getMonth()+1);
        }
        else{
            month=String.valueOf(date.getMonth());
        }
        String year=String.valueOf(1900+date.getYear());
        String day="";
        myCal.get(Calendar.DAY_OF_MONTH);
        if(date.getDay()<10){
            day="0"+String.valueOf(date.getDay());
        }
        else{
            day=String.valueOf(date.getDay());
        }
        day=String.valueOf(myCal.get(Calendar.DAY_OF_MONTH));
        toReturn = month+"-"+day
                +"-"+year+" "+String.valueOf(date.getHours())+":"+
                String.valueOf(date.getMinutes())+":"+String.valueOf(date.getSeconds());
        return toReturn;
    }

    //*******
    View.OnTouchListener myOnToucListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.setBackgroundResource(R.drawable.unread);
            return false;
        }
    };
    View.OnFocusChangeListener myOnFocusChangeListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!hasFocus){
                v.setBackgroundColor(Color.WHITE);
            }
        }
    };

}
