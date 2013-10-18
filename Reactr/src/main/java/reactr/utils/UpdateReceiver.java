package reactr.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.reactr.LoadActivity;
import com.example.reactr.MainActivity;
import com.example.reactr.StartActivity;

/**
 * Created by Kykmyrna on 18.10.13.
 */
public class UpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
        if (isConnected)
            Log.i("INET", "connected" + isConnected);

        else
        {
           Log.i("INET", "not connected" +isConnected);
            Toast.makeText(context, "No internet connection!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent();
            i.setClassName("com.example.reactr", "com.example.reactr.StartActivity");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

}