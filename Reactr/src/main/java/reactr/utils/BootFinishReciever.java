package reactr.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.reactr.LoadActivity;

/**
 * Created by Aleksey on 19.10.13.
 */

public class BootFinishReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent i = new Intent(context, LoadActivity.class);
           // i.addFlags(Intent.);
            //context.startActivity(i);


        }
    }

}
