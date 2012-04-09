package longma.achai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import longma.achai.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class FavActivity extends Activity {
	
	private Button favBack; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_fav);
		   
		initTitle();
		initfavlistView();
		   
	}
	
	
    private void initTitle() {
    	favBack = (Button) findViewById(R.id.title_back);
    	favBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}


	private ListView favlist;
    private void initfavlistView() {
    	favlist = (ListView) findViewById(R.id.fav_listview);
    	List<HashMap<String, String>> result = new ArrayList<HashMap<String,String>>();
    	HashMap<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("money","118鍏�");
        
        HashMap<String, String> resultMap2 = new HashMap<String, String>();
        resultMap2.put("money","138鍏�");
        
        result.add(resultMap);
        result.add(resultMap2);
        
        SimpleAdapter favAdapter = new SimpleAdapter(this, result, R.layout.content_fav_item, new String[]{"money"}, new int[]{R.id.textView1});
        favlist.setAdapter(favAdapter);
        
	}
}
