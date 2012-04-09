package longma.achai;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class InstallAppDemo extends Activity implements OnClickListener{
	private static String logPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "guanri"+File.separator;

	Button btn_install = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_test);
        btn_install = (Button)this.findViewById(R.id.btn_install);

        btn_install.setOnClickListener(this);

    }

    
    @Override
    public void onClick(View v){
    	switch(v.getId()){
    	case R.id.btn_install:
    		new Thread(new TestRunner(this)).start();
    		break;
    	default:
    		break;
    	}
    	
    }
}