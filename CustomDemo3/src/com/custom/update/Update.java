package com.custom.update;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Update extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	Button btn = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btn = (Button)this.findViewById(R.id.update_btn);
        
    }
    
    @Override
    public void onClick(View v){
    	
    }
}