package reactr.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 * Created by vova on 29.09.13.
 */
public class FriendsDBManager extends SQLiteOpenHelper {

    private ContentValues contentValues;
    private SQLiteDatabase sqLiteDatabase;
    private Cursor cursor;

    public FriendsDBManager(Context context) {
        super(context, "reactrDB", null, 1);
        contentValues = new ContentValues();
        sqLiteDatabase = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table friend ("
                + "id integer primary key autoincrement,"
                + "friend_id integer,"
                + "name text,"
                + "phone text"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    }

    public SQLiteDatabase getDescriptor ()
    {
        if(sqLiteDatabase == null)
            return this.getWritableDatabase();
        else
            return sqLiteDatabase;
    }

    public Boolean insetFriendRow (int friendId, String friendName, String friendPhone)
    {
        contentValues.put("friend_id", friendId);
        contentValues.put("name", friendName);
        contentValues.put("phone", friendPhone);

        Long result = sqLiteDatabase.insert("friend", null, contentValues);

        return (result != -1) ? true : false;
    }

    public HashMap<Integer, String> getFriends()
    {
        HashMap<Integer, String> friendsCollection = new HashMap<Integer, String>();

        cursor = sqLiteDatabase.query("friend", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idFriend = cursor.getColumnIndex("friend_id");
            int friendName = cursor.getColumnIndex("name");

            do {
                friendsCollection.put(cursor.getInt(idFriend), cursor.getString(friendName));
            } while (cursor.moveToNext());
        } else
            cursor.close();

        return friendsCollection;
    }

    public String getFriendById (Integer id)
    {
        String[] args = { id.toString() };

        cursor = sqLiteDatabase.query("friend", null, "friend_id = ?", args, null, null, null);

        if (cursor.moveToFirst()) {
            int idFriend = cursor.getColumnIndex("name");
            return cursor.getString(idFriend);
        } else
            cursor.close();
        return null;
    }

    public Boolean editUserNameFriend (Integer id, String userName)
    {
        String[] args = { id.toString() };

        contentValues.put("name", userName);

        int result = sqLiteDatabase.update("friend", contentValues, "friend_id = ?", args);

        return (result > 0) ? true : false;
    }
}
