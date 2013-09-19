package reactr.network;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.reactr.reactr.models.FriendEntity;
import com.example.reactr.reactr.models.MessageEntity;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import reactr.utils.ReactrConstants;

public class ReactorApi {
    private int userId;
    private String session_token;
    private String apiUrl = "http://api.reactrapp.com";
    private NetworkManager networkManager;
    private HashMap<String, ContentBody> postParams;
    private JSONObject jsonData;
    private static ReactorApi reactorApi;

    private final String CHECK_EMAIL_AND_PASSWORD = apiUrl + "/heckUsernameAndEmail/";
    private final String GET_USER_METHOD          = apiUrl + "/getFriends/";
    private final String GET_WHO_ADD_ME           = apiUrl + "/getWhoAddMe/";
    private final String ADD_FRIEND               = apiUrl + "/addFriend/";
    private final String USER_IN_SYSTEM           = apiUrl + "/checkUserInSystem/";
    private final String SEARCH_FRIEND            = apiUrl + "/searchFriends/";
    private final String SEND_MESSAGES            = apiUrl + "/sendMessages/";
    private final String GET_MESSAGES             = apiUrl + "/getMessages/";
    private final String LOGIN                    = apiUrl + "/login/";
    private final String ST_INFO           = apiUrl + "/getStaticInfo/";
    private final String READ_MSG           = apiUrl + "/readMessages/";

    private ReactorApi(int userId, String session_token) {
        this.userId = userId;
        this.session_token = session_token;
        networkManager = new NetworkManager();
    }

    public static ReactorApi init(int userId, String session_token)
    {
        if(reactorApi != null)
            return reactorApi;
        else
            return new ReactorApi(userId, session_token);
    }

    public static ReactorApi getInstance()
    {
        return reactorApi;
    }

    public void checkUsernameAndEmail(String username, String email)
    {
        postParams = new HashMap<String, ContentBody>();

        try {
            if (username != null)
                postParams.put("username", new StringBody(username));
            if (username != null)
                postParams.put("email", new StringBody(email));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        try {
            jsonData = new JSONObject(networkManager.sendRequest(CHECK_EMAIL_AND_PASSWORD, postParams));
            if(jsonData.get("status").equals("success"))
            {
            }
        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
//        return null;
    }

    public JSONObject login(String email, String password)
    {
        postParams = new HashMap<String, ContentBody>();

        try {
            postParams.put("email", new StringBody(email));
            postParams.put("password", new StringBody(password));
            postParams.put("device_token", new StringBody("android"));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }

        try {
            jsonData = new JSONObject(networkManager.sendRequest(LOGIN, postParams));
            if(jsonData.get("status").equals("success"))
            {
                return jsonData;
            }
        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        return null;

    }

    public JSONArray checkUserInSystem(String phones)
    {
        postParams = new HashMap<String, ContentBody>();
        ArrayList<FriendEntity> friendCollection = new ArrayList<FriendEntity>();

        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
            postParams.put("phones", new StringBody(phones));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        try {
            jsonData = new JSONObject(networkManager.sendRequest(USER_IN_SYSTEM, postParams));
            if(jsonData.get("status").equals("success"))
            {
                return jsonData.getJSONArray("users");
            }
        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        return null;
    }

    public ArrayList<FriendEntity> getFriends() {
        postParams = new HashMap<String, ContentBody>();
        ArrayList<FriendEntity> friendCollection = new ArrayList<FriendEntity>();

        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        } catch (Exception e)
        {
            Log.d("Reactor API: ", e.getMessage());
        }
        try {
            jsonData = new JSONObject(networkManager.sendRequest(GET_USER_METHOD, postParams));
            if(jsonData.get("status").equals("success"))
            {
                JSONArray friendsJSONArray = (JSONArray) jsonData.get("friends");
                for (int i = 0; i < friendsJSONArray.length(); i++)
                {
                    JSONObject friendJson = friendsJSONArray.getJSONObject(i);
                    FriendEntity friendEntity = new FriendEntity(
                            friendJson.getInt("id"),
                            friendJson.getString("username"),
                            friendJson.getLong("phone"),
                            friendJson.getBoolean("privacy_message"),
                            friendJson.getBoolean("confirmed")
                    );
                    friendCollection.add(friendEntity);
                }
                return friendCollection;
            }
        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
            return null;
        }
        return null;
    }


    public ArrayList<FriendEntity> getWhoAddMe()
    {
        postParams = new HashMap<String, ContentBody>();
        ArrayList<FriendEntity> friendCollection = new ArrayList<FriendEntity>();

        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        try {
            jsonData = new JSONObject(networkManager.sendRequest(GET_WHO_ADD_ME, postParams));
            if(jsonData.get("status").equals("success"))
            {
                JSONArray friendsJSONArray = (JSONArray)jsonData.get("friends");
                for (int i = 0; i < friendsJSONArray.length(); i++)
                {
                    JSONObject friendJson = friendsJSONArray.getJSONObject(i);
                    FriendEntity friendEntity = new FriendEntity(
                            friendJson.getInt("id"),
                            friendJson.getString("username"),
                            friendJson.getLong("phone"),
                            friendJson.getBoolean("privacy_message"),
                            false
                    );
                    friendCollection.add(friendEntity);
                }
                return friendCollection;
            }
        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }

        return  friendCollection;
    }

    public boolean addFriend(Long number)
    {
        postParams = new HashMap<String, ContentBody>();

        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
            postParams.put("friend_phone", new StringBody(number.toString()));

            jsonData = new JSONObject(networkManager.sendRequest(ADD_FRIEND, postParams));

            if(jsonData.get("status").equals("success"))
            {
                return true;
            }

        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }

        return false;
    }

    public ArrayList<FriendEntity> searchFriends(String username)
    {
        ArrayList<FriendEntity> friends = new ArrayList<FriendEntity>();

        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
            postParams.put("friend_username", new StringBody(username));

            jsonData = new JSONObject(networkManager.sendRequest(SEARCH_FRIEND, postParams));

            if(jsonData.get("status").equals("success"))
            {
                JSONArray friendsJsonArray = jsonData.getJSONArray("friends");
                for (int i = 0; i < friendsJsonArray.length(); i++)
                {
                    JSONObject  friendJsonObject = (JSONObject) friendsJsonArray.get(i);
                    friends.add(new FriendEntity(
                            friendJsonObject.getInt("id"),
                            friendJsonObject.getString("username"),
                            friendJsonObject.getLong("phone"),
                            false,
                            false
                    ));
                }
            }
        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        return friends;
    }

    public boolean sendMessages(String friendIds, String text, Bitmap photo, Bitmap reactionPhoto)
    {
        postParams = new HashMap<String, ContentBody>();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] photoByteArray = stream.toByteArray();
        stream.reset();
        if(reactionPhoto != null)
        {
            reactionPhoto.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] reactionPhotoByteArray = stream.toByteArray();
        }

        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
            postParams.put("friend_ids", new StringBody(friendIds));
            postParams.put("text", new StringBody(text));
            postParams.put("photo", new ByteArrayBody(photoByteArray, "image/jpeg","name"));
            if(reactionPhoto != null)
                postParams.put("reaction_photo", new ByteArrayBody(stream.toByteArray(), "image/jpeg","name"));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        try {
            jsonData = new JSONObject(networkManager.sendRequest(SEND_MESSAGES, postParams));

            return (jsonData.get("status").equals("success")) ? true : false;

        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }

        return false;
    }

    public ArrayList<MessageEntity> getMessages()
    {
        postParams = new HashMap<String, ContentBody>();
        ArrayList<MessageEntity> messageArray = new ArrayList<MessageEntity>();

        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }

        try {
            jsonData = new JSONObject(networkManager.sendRequest(GET_MESSAGES, postParams));
            if(jsonData.get("status").equals("success"))
            {
                JSONArray messageJSONArray = (JSONArray) jsonData.getJSONArray("messages");

                for (int i = 0; i < messageJSONArray.length(); i++)
                {
                    JSONObject messageJson = messageJSONArray.getJSONObject(i);
                    MessageEntity messageEntity = new MessageEntity(
                            messageJson.getInt("id"),
                            messageJson.getInt("from_user"),
                            messageJson.getInt("to_user"),
                            messageJson.getString("text"),
                            messageJson.getString("photo"),
                            messageJson.getString("reaction_photo"),
                            messageJson.getJSONObject("created_at").getString("date"),
                            messageJson.getBoolean("from_me"),
                            (!messageJson.getString("is_read").equals("null")) ? messageJson.getBoolean("is_read") : false

                    );
                    messageArray.add(i, messageEntity);
                }
            }
        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        return messageArray;
    }

    public HashMap<String, String> loadStInfo()
    {

        String toRet="";
        HashMap<String, String> st_info_hm = new HashMap<String, String>();
        try {

            DefaultHttpClient hc = new DefaultHttpClient();
            ResponseHandler<String> res = new BasicResponseHandler();
            HttpPost postMethod = new HttpPost(ST_INFO);
            String response = hc.execute(postMethod, res);

            JSONObject json = new JSONObject(response);

            JSONObject urls = json.getJSONObject("static_info");


            st_info_hm.put(ReactrConstants.ABOUT_REACTR,urls.getString(ReactrConstants.ABOUT_REACTR));

            st_info_hm.put(ReactrConstants.PRIVACY,urls.getString(ReactrConstants.PRIVACY));

            st_info_hm.put(ReactrConstants.TERMS,urls.getString(ReactrConstants.TERMS));

            st_info_hm.put(ReactrConstants.CONTACT_US,urls.getString(ReactrConstants.CONTACT_US));


        } catch (Exception e) {
            System.out.println("Exp=" + e);
        }
        return st_info_hm;
    }

    public int newMessages()
    {
        postParams = new HashMap<String, ContentBody>();
        int toRet=0;
        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }

        try {
            jsonData = new JSONObject(networkManager.sendRequest(GET_MESSAGES, postParams));
            if(jsonData.get("status").equals("success"))
            {
                JSONArray messageJSONArray = (JSONArray) jsonData.getJSONArray("messages");

                for (int i = 0; i < messageJSONArray.length(); i++)
                {
                    JSONObject messageJson = messageJSONArray.getJSONObject(i);

                    if(messageJson.getString("is_read").equals("null")&&!messageJson.getBoolean("from_me")){
                        toRet++;
                    }



                }
            }
        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        return toRet;
    }


    public boolean readMess(String id_mes)
    {
        postParams = new HashMap<String, ContentBody>();
        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
            postParams.put("message_id", new StringBody(id_mes));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        try {
            jsonData = new JSONObject(networkManager.sendRequest(READ_MSG, postParams));
            Boolean result = (jsonData.get("status").equals("success")) ? true : false;
            return result ;

        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }

        return false;
    }


}
