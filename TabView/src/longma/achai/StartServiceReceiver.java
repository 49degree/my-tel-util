package longma.achai;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartServiceReceiver extends BroadcastReceiver {  
    @Override      
    public void onReceive(Context context, Intent intent) { 
    	Log.e("Alarmreceiver","onReceive");
        Intent i = new Intent();               
        i.setClass(context, AidlRunService.class);                    
        context.startService(i);  
    }
}
