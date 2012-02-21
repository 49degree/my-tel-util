package com.custom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.custom.network.HttpRequest;

public class CustomDemo extends Activity implements OnClickListener{
	//String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "customDemo"+File.separator; 
	final static String TAG = "CustomDemo";
	
    /** Called when the activity is first created. */
	Button button = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        
        button = (Button)this.findViewById(R.id.button);
        button.setOnClickListener(this);
        
    }
    
    public void onClick(View v){
    	switch(v.getId()){
    	case R.id.button:
    		new CustomUtils(this).wakeUpApp();
    		break;
    	default:
    		break;
    	}
    	
    		
    }
    
}