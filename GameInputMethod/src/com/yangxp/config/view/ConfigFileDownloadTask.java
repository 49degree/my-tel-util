package com.yangxp.config.view;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.WindowManager;

import com.op.iplay.service.IKeyMappingService;
import com.yangxp.config.MainApplication;
import com.yangxp.config.bean.Mappings;
import com.yangxp.config.business.SettingController;
import com.yangxp.config.download.HttpFileDownload;
import com.yangxp.config.pushconfig.HaierParseDownConfig;
import com.yangxp.ginput.R;

public class ConfigFileDownloadTask extends AsyncTask<String, Integer, Integer>{
	public static final String SAVE_PATH = "/mnt/sdcard/gameconfig/down/";
	private HttpFileDownload mHttpFileDownload = null; 
	private LoadingView mLoadingView = null;
	private Context mContext = null;
	private IKeyMappingService mIKeyMappingService = null; 
	private String mFileUrl = null;
	private PackageInfo mPackageinfo;  
	private String mPackageName = "";
	
	private ConfigFileDownloadListener mConfigFileDownloadListener;
    private static final int TIMEOUT_WHAT = 1;
    private boolean timeOut = false;
	
	//在客户端覆写onServiceConnected方法,当服务绑定成功会调用此回调函数  
	private ServiceConnection sc = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mIKeyMappingService = IKeyMappingService.Stub.asInterface(service);//(IKeyMappingService) service.; // 通过IBinder获取Service
			
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			mIKeyMappingService = null; 
		}
	};  
	
	public ConfigFileDownloadTask(Context context,String packageName){
		mContext = context;
		mPackageName = packageName;
	}
	
    private Handler mTimeOutHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		switch(msg.what){
    		case TIMEOUT_WHAT:
    			timeOut = true;
    			break;
    		default:
    			break;
    		}
    	}
    	
    };
	
	
    /**
     * Runs on the UI thread before {@link #doInBackground}.
     *
     * @see #onPostExecute
     * @see #doInBackground
     */
    protected void onPreExecute() {
    	mLoadingView = new LoadingView(mContext);
    	mLoadingView.show();
		try {
			Intent intent = new Intent("com.op.iplay.service.IKeyMappingService");  
			mContext.bindService(intent, sc, Context.BIND_AUTO_CREATE);
			mPackageinfo = MainApplication.getInstance().getPackageManager().getPackageInfo(mPackageName, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mTimeOutHandler.sendEmptyMessageDelayed(TIMEOUT_WHAT, 5000);
    }
    
    /**
     * <p>Applications should preferably override {@link #onCancelled(Object)}.
     * This method is invoked by the default implementation of
     * {@link #onCancelled(Object)}.</p>
     * 
     * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and
     * {@link #doInBackground(Object[])} has finished.</p>
     *
     * @see #onCancelled(Object) 
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    protected void onCancelled() {
    	mLoadingView.hide();
    }
    
    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * 
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param result The result of the operation computed by {@link #doInBackground}.
     *
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object) 
     */
    @SuppressWarnings({"UnusedDeclaration"})
    protected void onPostExecute(final Integer result) {
    	mLoadingView.hide();
    	if(result==R.string.info_down_success){
    		final HaierParseDownConfig p = new HaierParseDownConfig(mHttpFileDownload.getSavePath()+File.separator+mHttpFileDownload.getFilename());
    		if(p.getFileController()==null){
    			showInfoByString(R.string.info_down_file_format_error);
        		return;
    		}
    		
    		if(!mPackageinfo.packageName.equals(p.getFileController().getApp().getName())){
    			showInfoByString(R.string.info_down_file_package_error);
    			return ;
    		}else if(mPackageinfo.versionCode!=p.getFileController().getAppMap().appVersion){
    			showInfoByString(R.string.info_down_file_version_error);
    			return ;
    		}
    		
    		//判断当前是否存在配置数据
    		SettingController settingController = new SettingController(mPackageName,true);
    		try {
    			List<Mappings> keyMappings = settingController.getMappings();
    			if(keyMappings!=null&&keyMappings.size()>0){
    				//询问用户是否需要更新
    				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { 
    		            @Override 
    		            public void onClick(DialogInterface dialog, int which) { 
    		                // TODO Auto-generated method stub  
    		            	dialog.dismiss();
    		            	parseConfigInfo(p,result);
    		            } 
    		        };
    		        showAlertDialog(listener);
    		        return;
    			}
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		parseConfigInfo(p,result);

    	}else{
    		showInfoByString(result);
    	}
		try {
			mContext.unbindService(sc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
    
    private void parseConfigInfo(HaierParseDownConfig p,Integer result){
		p.getFileController().saveOrUpdate();
		if(mConfigFileDownloadListener!=null)
			mConfigFileDownloadListener.onSucess();
		showInfoByString(result);
    }
	
	@Override
	protected Integer doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		while(mIKeyMappingService == null && !timeOut)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		if(mIKeyMappingService != null){
			try {
				mFileUrl = mIKeyMappingService.getMappingFileUrlForPackage(mPackageinfo.packageName,mPackageinfo.versionCode,mPackageinfo.versionName);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//mFileUrl = "http://172.0.0.120:8080/GamePad/iLauncher.data";
		if(mFileUrl==null){
			return R.string.info_down_no_file;
		}
		mHttpFileDownload = new HttpFileDownload(mFileUrl,SAVE_PATH);
		if(mHttpFileDownload.download())
			return R.string.info_down_success;
		else
			return R.string.info_down_file_failure;
	}
	
	private void showAlertDialog(DialogInterface.OnClickListener sureListener) {
		//create ui
		
		//use custom UI.
		//View v = View.inflate(mContext, R.layout.activity_main, null);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("");
		builder.setMessage(R.string.info_down_file_override_local);

        builder.setPositiveButton(R.string.info_sure, sureListener);
		builder.setNegativeButton(R.string.info_canncel, new DialogInterface.OnClickListener() {
            @Override 
            public void onClick(DialogInterface dialog, int which) { 
                // TODO Auto-generated method stub 
            	dialog.dismiss();
            } 
        });
		// builder.setView(v);//use custom view
		AlertDialog dialog = builder.create();//need a <span style="font-family: 'Microsoft YaHei';">AlertDialog</span>
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//use alert.
		// dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
		dialog.show();
		
		//set dialog size and position.
		/*WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
		WindowManager wm=(WindowManager) getSystemService(WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		lp.width=(int) (display.getWidth()*0.8);
		lp.height=(int) (display.getHeight()*0.5);
		lp.x=0;
		lp.y=0;
		dialog.getWindow().setAttributes(lp);*/
	}
	
	
	
	
	public ConfigFileDownloadListener getConfigFileDownloadListener() {
		return mConfigFileDownloadListener;
	}


	public void setConfigFileDownloadListener(
			ConfigFileDownloadListener mConfigFileDownloadListener) {
		this.mConfigFileDownloadListener = mConfigFileDownloadListener;
	}


	protected void showInfoByString(int rid){
		InformationBar informationBar = new InformationBar(mContext);
		informationBar.informationBarShow();
		informationBar.showInformations(rid);
	}
	
	public interface ConfigFileDownloadListener{
		public void onSucess();
	}

}
