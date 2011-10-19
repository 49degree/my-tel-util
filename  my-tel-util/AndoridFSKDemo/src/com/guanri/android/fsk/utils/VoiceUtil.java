package com.guanri.android.fsk.utils;


import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;


public class VoiceUtil {

  
    public static MediaPlayer voiceMP = new MediaPlayer();
    
	public static void playVoice(Context context,int voiceId) { 
		
		if(voiceMP == null) {
			voiceMP = MediaPlayer.create(context, null);
		} else if (voiceMP.isPlaying()) {
			voiceMP.pause();
		}
		
		voiceMP.release();
		
		try {
			voiceMP = MediaPlayer.create(context, voiceId);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "引用资源出错或者播放器出错了！", Toast.LENGTH_LONG).show();
		}
		Log.e("test","开始播放");
		 voiceMP.start();
	}
    
}