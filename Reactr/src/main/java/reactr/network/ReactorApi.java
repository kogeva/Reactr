package reactr.network;

import android.graphics.Bitmap;
import android.util.Log;

import com.eyepinch.reactr.reactr.models.FriendEntity;
import com.eyepinch.reactr.reactr.models.MessageEntity;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import com.eyepinch.reactr.reactr.models.ReactrConstants;

public class ReactorApi {
    private int userId;
    private String session_token;
    private String apiUrl = "http://api.reactrapp.com";
    private NetworkManager networkManager;
    private HashMap<String, ContentBody> postParams;
    private JSONObject jsonData;
    private static ReactorApi reactorApi;

    private final String CHECK_EMAIL_AND_PASSWORD = apiUrl + "/checkUsernameAndEmail/";
    private final String EDIT_USER_DATA = apiUrl + "/editUserData/";
    private final String SET_PRIVACY_MESSAGE = apiUrl + "/setPrivacyMessage/";
    private final String REGISTRATION = apiUrl + "/registration/";
    private final String GET_USER_METHOD = apiUrl + "/getFriends/";
    private final String GET_WHO_ADD_ME = apiUrl + "/getWhoAddMe/";
    private final String ADD_FRIEND = apiUrl + "/addFriend/";
    private final String BLOCK_FRIEND = apiUrl + "/blockFriend/";
    private final String DELETE_FRIEND = apiUrl + "/deleteFriend/";
    private final String USER_IN_SYSTEM = apiUrl + "/checkUserInSystem/";
    private final String SEARCH_FRIEND = apiUrl + "/searchFriends/";
    private final String SEND_MESSAGES = apiUrl + "/sendMessages/";
    private final String DELETE_MESSAGE = apiUrl + "/deleteMessage/";
    private final String GET_MESSAGES = apiUrl + "/getMessages/";
    private final String LOGIN = apiUrl + "/login/";
    private final String REMIND_PASSWORD = apiUrl + "/remindPassword/";
    private final String ST_INFO = apiUrl + "/getStaticInfo/";
    private final String READ_MSG = apiUrl + "/readMessages/";
    private final String COUNT_MESSAGES = apiUrl + "/countNotReadMessage/";

    private ReactorApi(int userId, String session_token) {
        this.userId = userId;
        this.session_token = session_token;
        networkManager = new NetworkManager();
    }

    public static ReactorApi init(int userId, String session_token) {
        if (reactorApi != null)
            return reactorApi;
        else
            return new ReactorApi(userId, session_token);
    }

    public static ReactorApi getInstance() {
        return reactorApi;
    }

    public ArrayList<String> checkUsernameAndEmail(String username, String email) {
        ArrayList<String> errors = new ArrayList<String>();
        postParams = new HashMap<String, ContentBody>();

        try {
            if (username != null)
                postParams.put("username", new StringBody(username));
            if (email != null)
                postParams.put("email", new StringBody(email));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        try {
            jsonData = new JSONObject(networkManager.sendRequest(CHECK_EMAIL_AND_PASSWORD, postParams));
            if (jsonData.get("status").equals("success")) {
                for (int i = 0; i <= jsonData.getJSONArray("fields").length(); i++)
                    errors.add((String) ((JSONObject) jsonData.getJSONArray("fields").get(i)).keys().next());
                return errors;
            }
        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        return errors;
    }

    public JSONObject registration(String email, String password, String username, String phone, String deviceToken) {
        ArrayList<String> errors = new ArrayList<String>();
        postParams = new HashMap<String, ContentBody>();

        try {
            postParams.put("email", new StringBody(email));
            postParams.put("password", new StringBody(password));
            postParams.put("username", new StringBody(username));
            postParams.put("phone", new StringBody(phone));
            postParams.put("device_token", new StringBody(deviceToken));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        try {
            jsonData = new JSONObject(networkManager.sendRequest(REGISTRATION, postParams));
            if (jsonData.get("status").equals("success") || jsonData.get("status").equals("failed")) {
                return jsonData;
            }
        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        return jsonData;
    }

    public JSONObject login(String email, String password, String deviceToken) {
        postParams = new HashMap<String, ContentBody>();

        try {
            postParams.put("email", new StringBody(email));
            postParams.put("password", new StringBody(password));
            postParams.put("device_token", new StringBody((deviceToken.length() > 0) ? deviceToken : "android"));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }

        try {
            jsonData = new JSONObject(networkManager.sendRequest(LOGIN, postParams));
            if (jsonData.get("status").equals("success")) {
                return jsonData;
            }
        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        return jsonData;
    }


    public boolean remindPassword(String email, String phone) {
        postParams = new HashMap<String, ContentBody>();

        try {
            postParams.put("phone", new StringBody(phone));
            postParams.put("email", new StringBody(email));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }

        try {
            jsonData = new JSONObject(networkManager.sendRequest(REMIND_PASSWORD, postParams));
            if (jsonData.get("status").equals("success")) {
                return true;
            }
        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        return false;
    }

    public JSONArray checkUserInSystem(String phones) {
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
            if (jsonData.get("status").equals("success")) {
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
        } catch (Exception e) {
            Log.d("Reactor API: ", e.getMessage());
        }
        try {
            jsonData = new JSONObject(networkManager.sendRequest(GET_USER_METHOD, postParams));
            if (jsonData.get("status").equals("success")) {
                JSONArray friendsJSONArray = (JSONArray) jsonData.get("friends");
                for (int i = 0; i < friendsJSONArray.length(); i++) {
                    JSONObject friendJson = friendsJSONArray.getJSONObject(i);
                    FriendEntity friendEntity = new FriendEntity(
                            friendJson.getInt("id"),
                            friendJson.getString("username"),
                            friendJson.getLong("phone"),
                            friendJson.getBoolean("privacy_message"),
                            friendJson.getBoolean("confirmed"),
                            friendJson.getBoolean("blocked"),
                            (!friendJson.isNull("blocked_me")) ? friendJson.getBoolean("blocked_me") : false
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


    public ArrayList<FriendEntity> getWhoAddMe() {
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
            if (jsonData.get("status").equals("success")) {
                JSONArray friendsJSONArray = (JSONArray) jsonData.get("friends");
                for (int i = 0; i < friendsJSONArray.length(); i++) {
                    JSONObject friendJson = friendsJSONArray.getJSONObject(i);
                    FriendEntity friendEntity = new FriendEntity(
                            friendJson.getInt("id"),
                            friendJson.getString("username"),
                            friendJson.getLong("phone"),
                            friendJson.getBoolean("privacy_message"),
                            false,
                            false,
                            false
                    );
                    friendCollection.add(friendEntity);
                }
                return friendCollection;
            }
        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }

        return friendCollection;
    }

    public boolean addFriend(Long number) {
        postParams = new HashMap<String, ContentBody>();

        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
            postParams.put("friend_phone", new StringBody(number.toString()));

            jsonData = new JSONObject(networkManager.sendRequest(ADD_FRIEND, postParams));

            if (jsonData.get("status").equals("success")) {
                return true;
            }

        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }

        return false;
    }

    public ArrayList<FriendEntity> searchFriends(String username) {
        ArrayList<FriendEntity> friends = new ArrayList<FriendEntity>();

        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
            postParams.put("friend_username", new StringBody(username));

            jsonData = new JSONObject(networkManager.sendRequest(SEARCH_FRIEND, postParams));

            if (jsonData.get("status").equals("success")) {
                JSONArray friendsJsonArray = jsonData.getJSONArray("friends");
                for (int i = 0; i < friendsJsonArray.length(); i++) {
                    JSONObject friendJsonObject = (JSONObject) friendsJsonArray.get(i);
                    friends.add(new FriendEntity(
                            friendJsonObject.getInt("id"),
                            friendJsonObject.getString("username"),
                            friendJsonObject.getLong("phone"),
                            false,
                            false,
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

    public ArrayList<FriendEntity> searchFriendsWithoutMe(String username, String my_name) {
        ArrayList<FriendEntity> friends = new ArrayList<FriendEntity>();
        String temp_str;

        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
            postParams.put("friend_username", new StringBody(username));

            jsonData = new JSONObject(networkManager.sendRequest(SEARCH_FRIEND, postParams));

            if (jsonData.get("status").equals("success")) {
                JSONArray friendsJsonArray = jsonData.getJSONArray("friends");
                for (int i = 0; i < friendsJsonArray.length(); i++) {
                    JSONObject friendJsonObject = (JSONObject) friendsJsonArray.get(i);
                    temp_str = friendJsonObject.getString("username");
                    if (!temp_str.equals(my_name)) {
                        friends.add(new FriendEntity(
                                friendJsonObject.getInt("id"),
                                friendJsonObject.getString("username"),
                                friendJsonObject.getLong("phone"),
                                false,
                                false,
                                false,
                                false
                        ));
                    }
                }
            }
        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        return friends;
    }

    public boolean sendMessages(String friendIds, String text, Bitmap photo, Bitmap reactionPhoto) {
        postParams = new HashMap<String, ContentBody>();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] photoByteArray = stream.toByteArray();
        stream.reset();
        if (reactionPhoto != null) {
            //75
            reactionPhoto.compress(Bitmap.CompressFormat.JPEG, 75, stream);
            byte[] reactionPhotoByteArray = stream.toByteArray();
        }

        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
            postParams.put("friend_ids", new StringBody(friendIds));
            postParams.put("text", new StringBody(text));
            postParams.put("photo", new ByteArrayBody(photoByteArray, "image/jpeg", "name"));
            if (reactionPhoto != null)
                postParams.put("reaction_photo", new ByteArrayBody(stream.toByteArray(), "image/jpeg", "name"));
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

    public ArrayList<MessageEntity> getMessages(Integer from, Integer to) {
        postParams = new HashMap<String, ContentBody>();
        ArrayList<MessageEntity> messageArray = new ArrayList<MessageEntity>();

        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
            postParams.put("from", new StringBody(from.toString()));
            postParams.put("to", new StringBody(to.toString()));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }

        try {
            jsonData = new JSONObject(networkManager.sendRequest(GET_MESSAGES, postParams));
            if (jsonData.get("status").equals("success")) {
                JSONArray messageJSONArray = (JSONArray) jsonData.getJSONArray("messages");

                for (int i = 0; i < messageJSONArray.length(); i++) {
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
                            (!messageJson.getString("is_read").equals("null")) ? messageJson.getBoolean("is_read") : false,
                            messageJson.getString("username"),
                            messageJson.getString("to_username"),
                            messageJson.getBoolean("deleted")
                    );

                    int timeZone = Integer.parseInt(messageJson.getJSONObject("created_at").getString("timezone_type"));
                    messageEntity.setCreatedAt(convertTime(messageEntity.getCreatedAt(), timeZone));

                    messageArray.add(i, messageEntity);
                }
            }
        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        return messageArray;
    }


    public HashMap<String, String> loadStInfo() {

        HashMap<String, String> st_info_hm = new HashMap<String, String>();
        try {
            DefaultHttpClient hc = new DefaultHttpClient();
            ResponseHandler<String> res = new BasicResponseHandler();
            HttpPost postMethod = new HttpPost(ST_INFO);
            String response = hc.execute(postMethod, res);
            JSONObject json = new JSONObject(response);
            JSONObject urls = json.getJSONObject("static_info");

            st_info_hm.put(ReactrConstants.ABOUT_REACTR, urls.getString(ReactrConstants.ABOUT_REACTR));
            st_info_hm.put(ReactrConstants.PRIVACY, urls.getString(ReactrConstants.PRIVACY));
            st_info_hm.put(ReactrConstants.TERMS, urls.getString(ReactrConstants.TERMS));
            st_info_hm.put(ReactrConstants.CONTACT_US, urls.getString(ReactrConstants.CONTACT_US));
        } catch (Exception e) {
            System.out.println("Exp=" + e);
        }
        return st_info_hm;
    }

    public int countOfnewMessages() {
        postParams = new HashMap<String, ContentBody>();
        int countOfMessages = 0;
        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }

        try {
            jsonData = new JSONObject(networkManager.sendRequest(COUNT_MESSAGES, postParams));
            if (jsonData.get("status").equals("success"))
                countOfMessages = Integer.parseInt(jsonData.get("count").toString());

        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        return countOfMessages;
    }

    public boolean readMessage(String id_mes) {
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
            return result;

        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }

        return false;
    }

    public boolean setPrivacyMessage(boolean state) {
        postParams = new HashMap<String, ContentBody>();
        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
            postParams.put("privacy_message", new StringBody(new Integer(state ? 1 : 0).toString()));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        try {
            jsonData = new JSONObject(networkManager.sendRequest(SET_PRIVACY_MESSAGE, postParams));
            Boolean result = (jsonData.get("status").equals("success")) ? true : false;
            return result;

        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }

        return false;
    }

    public ArrayList<String> editUserData(String phone, String email) {
        ArrayList<String> errors = new ArrayList<String>();
        postParams = new HashMap<String, ContentBody>();

        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
            if (phone != null)
                postParams.put("phone", new StringBody(phone));
            if (email != null)
                postParams.put("email", new StringBody(email));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        try {
            jsonData = new JSONObject(networkManager.sendRequest(EDIT_USER_DATA, postParams));

            if (jsonData.get("status").equals("failed")) {
                for (int i = 0; i < jsonData.getJSONArray("errors").length(); i++) {
                    errors.add((String) jsonData.getJSONArray("errors").get(i));
                }
            }
            return errors;
        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        return null;
    }

    public Boolean deleteFriend(Integer friendId) {
        postParams = new HashMap<String, ContentBody>();
        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
            postParams.put("friend_id", new StringBody(friendId.toString()));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        try {
            jsonData = new JSONObject(networkManager.sendRequest(DELETE_FRIEND, postParams));
            Boolean result = (jsonData.get("status").equals("sucess")) ? true : false;
            return result;

        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        return false;
    }

    public Boolean blockFriend(Integer friendId, Boolean isBlock) {
        postParams = new HashMap<String, ContentBody>();
        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
            postParams.put("friend_id", new StringBody(friendId.toString()));
            postParams.put("set_block", new StringBody(new Integer((isBlock) ? 1 : 0).toString()));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        try {
            jsonData = new JSONObject(networkManager.sendRequest(BLOCK_FRIEND, postParams));
            Boolean result = (jsonData.get("status").equals("sucess")) ? true : false;
            return result;

        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        return false;
    }

    public boolean deleteMessage(Integer messageId) {
        postParams = new HashMap<String, ContentBody>();
        try {
            postParams.put("user_id", new StringBody((new Integer(userId)).toString()));
            postParams.put("session_hash", new StringBody(session_token));
            postParams.put("message_id", new StringBody(messageId.toString()));
        } catch (UnsupportedEncodingException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        try {
            jsonData = new JSONObject(networkManager.sendRequest(DELETE_MESSAGE, postParams));
            Boolean result = (jsonData.get("status").equals("success")) ? true : false;
            return result;

        } catch (JSONException exp) {
            Log.d("Reactor API: ", exp.getMessage());
        }
        return false;
    }


    private String convertTime(String timeFromServer, int timezone) {

        String toReturn = "";

        Calendar cal = Calendar.getInstance();
        Date dateToCal = null;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dateToCal = formatter.parse(timeFromServer);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        cal.setTime(dateToCal);

        int dayLightSaving = cal.get(Calendar.DST_OFFSET);
        cal.setTimeInMillis(cal.getTimeInMillis() - dayLightSaving);

        TimeZone z = cal.getTimeZone();
        int offset = z.getRawOffset();
        int offsetHrs = offset / 1000 / 60 / 60;
        int offsetMins = offset / 1000 / 60 % 60;
        // Subtract offset of your current TimeZone
        cal.add(Calendar.HOUR_OF_DAY, (offsetHrs));
        cal.add(Calendar.MINUTE, (offsetMins));
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        toReturn = sdf.format(cal.getTime());

        return toReturn;
    }
}
