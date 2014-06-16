package com.ffmpeg.lib.h264;

import android.graphics.Bitmap;

public interface H264Decoder {
	public boolean isStop();
	public void toStop();
	public void sendStream(byte[] data);
	public void sendStream(byte[] data,int start,int len);
	public void setSkipNalu(boolean skipNalu);
	public boolean getSkipNalu();
    public interface DecodeSuccCallback{
    	public void onDecodeSucc(H264Decoder decoder,Bitmap bitmap);
    }
}
