package reactr.adaptor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.reactr.MainActivity;
import com.example.reactr.R;
import com.example.reactr.reactr.models.FriendEntity;

import java.util.ArrayList;

import reactr.network.ReactorApi;
import reactr.utils.FriendsDBManager;

public class MyFrendsAdapter extends FriendsAddedAdapter {

    private AlertDialog.Builder contextMenuDialog;
    private Dialog editUserDialog;
    private Context context;
    private CharSequence[] itemsContextMenu = {"Edit Name", "Delete", "Block", "Cancel"};
    private MyFrendsAdapter myFrendsAdapter;
    private ReactorApi reactorApi;

    public MyFrendsAdapter(Context ctx, ArrayList<FriendEntity> friendCollection) {
        super(ctx, friendCollection);
        context = ctx;
        myFrendsAdapter = this;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null)
            view = this.inflater.inflate(R.layout.my_friends_list, parent, false);

        TextView contactName = (TextView) view.findViewById(R.id.contact_name);
        TextView username = (TextView) view.findViewById(R.id.username);

        final FriendEntity friendEntity = (FriendEntity) getItem(position);
        username.setText(friendEntity.getUsername());

        if(friendEntity.getNameInContacts() != null)
            contactName.setText(friendEntity.getNameInContacts());
        else
            contactName.setText("Сontact is not available");

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                contextMenuDialog = new AlertDialog.Builder(context);
                contextMenuDialog.setTitle(friendEntity.getNameInContacts());
                contextMenuDialog.setItems(itemsContextMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item){
                            case 0:
                                showEditFriendDialog(position, view);
                                break;
                            case 1:
                                deleteFriend(friendEntity.getId(), view, position);
                                break;

                        }
                    }
                });
                AlertDialog alert = contextMenuDialog.create();
                alert.show();
            }
        });

        return view;
    }

    private void showEditFriendDialog(final int position, final View elementView)
    {
        String usernameText = ((FriendEntity) getItem(position)).getNameInContacts();
        final int friendId = ((FriendEntity) getItem(position)).getId();

        editUserDialog = new Dialog(context);
        editUserDialog.setTitle(usernameText);
        editUserDialog.setContentView(R.layout.friend_edit_username_dialog);
        final EditText username = (EditText) editUserDialog.findViewById(R.id.editUsername);
        Button saveButton = (Button) editUserDialog.findViewById(R.id.button);

        username.setText(usernameText);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendsDBManager friendsDBManager = new FriendsDBManager(context);
                friendsDBManager.editUserNameFriend(friendId, username.getText().toString());
                        ((TextView) elementView.findViewById(R.id.contact_name)).setText(username.getText().toString());
                editUserDialog.dismiss();
            }
        });
        editUserDialog.show();
    }

    private void deleteFriend(int friendId, final View elementView, final int position)
    {
        class DeleteFriendAsyncTask extends AsyncTask<Integer, Integer, Boolean>
        {

            @Override
            protected Boolean doInBackground(Integer... ints) {
                if(reactorApi == null)
                    reactorApi = ((MainActivity) context).getReactorApi();

                return reactorApi.deleteFriend(ints[0]);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result)
                {
                    removeFromCollection(position);
                }
            }
        }

        new DeleteFriendAsyncTask().execute(friendId);
    }

    private void removeFromCollection (int position)
    {
        friendCollection.remove(position);
        this.notifyDataSetChanged();
    }
}