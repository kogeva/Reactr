package reactr.adaptor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import reactr.network.ReactorApi;


public class MessageAdapter extends BaseAdapter {

    private ArrayList<MessageEntity> messages;
    ArrayList<FriendEntity> friends;
    private LayoutInflater inflater;
    private Context ctx;

    public MessageAdapter(Context ctx, ArrayList<MessageEntity> messagess, ArrayList<FriendEntity> friends) {
        this.ctx = ctx;
        this.friends = friends;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null)
            view = inflater.inflate(R.layout.message_list_item, parent, false);

        TextView date = (TextView) view.findViewById(R.id.date);
        TextView user = (TextView) view.findViewById(R.id.userText);
        TextView reaction = (TextView) view.findViewById(R.id.isReaction);
        ImageView typeMessage = (ImageView) view.findViewById(R.id.typeMessage);

        final MessageEntity message = (MessageEntity) getItem(position);

        //*******
        if (!message.getIsRead() && !message.getFromMe()) {
            view.setBackgroundResource(R.drawable.unread);
        } else {
            view.setBackgroundColor(Color.WHITE);
        }

        String preDate = message.getCreatedAt();
        String formattedDate = preDate;//.substring(5, 10) + "-" + preDate.substring(0,4) + " " + preDate.substring(10);

        message.setUsernameWithFriends(friends);

        if (message.getFromMe()) {
            user.setText(message.getToUsername());
            typeMessage.setImageResource(R.drawable.rsz_arrow_forward_black);
        } else {
            user.setText(message.getUsername());
            typeMessage.setImageResource(R.drawable.rsz_camera_mailbox2);
        }

        if (!message.getDeleted()) {
            reaction.setBackgroundColor(Color.parseColor("#00dcee"));
            reaction.setText("REACTION");
            if (message.getReactionPhoto().equals("null"))
                reaction.setVisibility(View.INVISIBLE);
            else
                reaction.setVisibility(View.VISIBLE);
        } else {
            reaction.setBackgroundColor(Color.parseColor("#D6614D"));
            reaction.setText("EXPIRED");
            reaction.setVisibility(View.VISIBLE);
        }

        date.setText(formattedDate);

        if (message.getIsRead() && message.getFromMe()) {
            date.setText(date.getText() + " - Delivered");
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReactrBase.switchFraagment((SherlockFragmentActivity) ctx, new ShowMessageFragment(message));
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return confirmDeleteMessage(message, position);
            }
        });

        view.setOnFocusChangeListener(myOnFocusChangeListener);
        return view;
    }


    View.OnFocusChangeListener myOnFocusChangeListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (!hasFocus) {
                v.setBackgroundColor(Color.WHITE);
            } else {
                v.setBackgroundColor(R.drawable.unread);
            }
        }
    };

    public boolean confirmDeleteMessage(final MessageEntity message, final int position) {
        final Boolean isConfirm = false;

        final Context context = this.ctx;

  AlertDialog.Builder  builder = new AlertDialog.Builder(context);
        builder.setMessage("Delete message?");
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                class SendReactionPhotoAsyncTask extends AsyncTask<Void, Void, Boolean> {
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        ReactorApi api = ((MainActivity) context).getReactorApi();
                        return api.deleteMessage(message.getId());
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (result) {
                            messages.remove(position);
                            Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                    }
                }
                new SendReactionPhotoAsyncTask().execute();
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    public void addMessagesInList(ArrayList<MessageEntity> newMessages)
    {
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    public void refreshList(ArrayList<MessageEntity> newMessages)
    {
        messages.removeAll(messages);
        messages = newMessages;
        notifyDataSetChanged();
    }
}
