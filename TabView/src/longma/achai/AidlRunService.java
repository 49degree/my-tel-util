package longma.achai;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class AidlRunService extends Service{
	
    @Override
    public void onCreate(){
    	super.onCreate();
    	Log.e("AidlRunService","onCreate~~~~~~~~~");
    	
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
    
}
