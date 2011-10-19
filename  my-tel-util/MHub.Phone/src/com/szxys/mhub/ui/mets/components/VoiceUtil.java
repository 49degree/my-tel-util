package com.szxys.mhub.ui.mets.components;

import com.szxys.mhub.R;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

public class VoiceUtil {
    int[] voiceRes = {
    		R.raw.mets_getup,
    		R.raw.mets_sleep
    };
    public static final int GETUP_VOICE = R.raw.mets_getup;
    public static final int SLEEP_VOICE = R.raw.mets_sleep;
  
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
			Toast.makeText(context, "引用资源出错或者播放器出错了！", Toast.LENGTH_LONG).show();
		}
		 voiceMP.start();
	}
    
}