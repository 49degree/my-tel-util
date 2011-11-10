package com.guanri.fsk.audio;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import com.guanri.fsk.conversion.FskCodeParams;

public class AudioOperatorFactory {
	private static HashMap<String,AudioOperatorImp> audioMap = new HashMap<String,AudioOperatorImp>();
	private static FskCodeParams fskCodeParams = new FskCodeParams(2200,1200,11025,2,1200);
	/**
	 * 获取音频对象
	 * @param className com.guanri.fsk.pcaudio.AudioOperator
	 */
	public static AudioOperatorImp getAudioOperator(String className) throws Exception{
		if(audioMap.containsKey(className)){
			return audioMap.get(className);
		}else{
			//获取音频输入输出对象
			AudioOperatorImp audioTempOperator = null;
			try{
				Class audioOperatorClass = Class.forName(className);
				Constructor constructor = audioOperatorClass.getConstructor(FskCodeParams.class);
				audioTempOperator = (AudioOperatorImp)constructor.newInstance(fskCodeParams);
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}
			audioMap.put(className,audioTempOperator);
			return audioTempOperator;
		}
	}
	

}
