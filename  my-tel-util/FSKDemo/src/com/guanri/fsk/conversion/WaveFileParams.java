package com.guanri.fsk.conversion;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import com.guanri.fsk.utils.TypeConversion;

public class WaveFileParams {

	public String riff_ckid = "RIFF";//ckid  4  "RIFF" 标识 
	public int riff_cksize = 0;//cksize  4  文件大小  ; 这个大小不包括 ckid 和 cksize 本身, 下面的子块大小也是这样  RIFF 头 
	public String riff_fccType = "WAVE";//fccType 4  类型, 这里是 "WAVE" 标识
	public String fmt_ckid = "fmt ";//ckid  4 "fmt " 标识
	public int fmt_cksize = 16;//cksize  4 块大小; 对 PCM 编码这里是 16, 其他编码也不小于 16
	public short fmt_wFormatTag = (short)1;//wFormatTag  2 编码格式; 1 表示是 PCM 编码    
	public short fmt_nChannels = (short)1;//nChannels  2 声道数; 1 是单声道、2 是立体声 
	public int fmt_nSamplesPerSec = 0;//nSamplesPerSec  4 采样频率(每秒的样本数); 譬如 44100 
	public int fmt_nAvgBytesPerSec = 0;//nAvgBytesPerSec 4 传输速率 = 采样频率 * 每次采样大小, 单位是字节
	public short fmt_nBlockAlign = 0;//nBlockAlign  2 每次采样的大小 = 采样精度 * 声道数 / 8(因单位是字节所以要/8); 这也是字节对齐的最小单位, 譬如 16bit 立体声在这里的值是 4 字节 
	public short fmt_wBitsPerSample = 0;//wBitsPerSample  2 采样精度; 譬如 16bit 在这里的值就是 16 
	
	public String data_ckid = "data";//ckid  4 "data" 标识 
	public int data_cksize = 0;//cksize  4 块大小
	public byte[] data = null;//data 子块 采样数据  双声道数据排列: 左右左右...; 8bit: 0-255, 16bit: -32768-32767 
	
	public FskEnCodeResult fskEnCodeResult = null;
	public FskCodeParams fskCodeParams = null;
	
	public WaveFileParams(FskCodeParams fskCodeParams,FskEnCodeResult fskEnCodeResult){
		this.fskCodeParams = fskCodeParams;
		this.fskEnCodeResult = fskEnCodeResult;
		riff_cksize = fskEnCodeResult.index+36;
		fmt_nSamplesPerSec = fskCodeParams.getSampleF();
		//先计算采样精度,再计算采样大小，再计算传输速率
		fmt_wBitsPerSample =  (short)(fskCodeParams.getSampleByteLength()*8);
		fmt_nBlockAlign = (short)(fmt_wBitsPerSample*fmt_nChannels/8);
		fmt_nAvgBytesPerSec = fmt_nSamplesPerSec*fmt_nBlockAlign;
		data_cksize = fskEnCodeResult.index;
		
		data = new byte[data_cksize];
		System.arraycopy(fskEnCodeResult.code, 0, data, 0, data_cksize);
	}
	
	
	public WaveFileParams(FskCodeParams fskCodeParams){
		this.fskCodeParams = fskCodeParams;
		riff_cksize = 36;
		fmt_nSamplesPerSec = fskCodeParams.getSampleF();
		//先计算采样精度,再计算采样大小，再计算传输速率
		fmt_wBitsPerSample =  (short)(fskCodeParams.getSampleByteLength()*8);
		fmt_nBlockAlign = (short)(fmt_wBitsPerSample*fmt_nChannels/8);
		fmt_nAvgBytesPerSec = fmt_nSamplesPerSec*fmt_nBlockAlign;
		data_cksize = 0;
	}
	
	/**
	 * 保存文件
	 * @param filePath
	 */
	String filePath = null;
	FileOutputStream fout = null;
	public void createFile(String filePath){
		this.filePath = filePath;
		byte[] waveByte = parseWaveToByte();
		
		try{
			File waveFile = new File(this.filePath);
			if(!waveFile.exists()){
				waveFile.createNewFile();
			}
			
			fout = new FileOutputStream(waveFile);
			fout.write(waveByte);
			fout.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void appendData(byte[] data){
		if(filePath!=null){
			try{
				File waveFile = new File(this.filePath);
				if(!waveFile.exists()){
					waveFile.createNewFile();
					byte[] waveByte = parseWaveToByte();
					fout = new FileOutputStream(waveFile);
					fout.write(waveByte);
				}else if(fout==null){
					fout = new FileOutputStream(waveFile);
				}
				fout.write(data);
				fout.flush();
			}catch(Exception e){
				e.printStackTrace();
			}
		}

	}
	
	public void closeFile(){
		try{

			FileChannel ch = fout.getChannel();
			ch.position(4);
			int size = (int) ch.size();
			fout.write(TypeConversion.intToBytes(size - 8));
			ch.position(40);
			fout.write(TypeConversion.intToBytes(size - 44));
			fout.flush();
			fout.close();
			fout = null;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 构造数据
	 * @return
	 */
	public byte[] parseWaveToByte(){
		byte[] waveByte = new byte[44+data_cksize];
		int index = 0;
		byte [] riff_ckidByte = riff_ckid.getBytes();
		System.arraycopy(riff_ckidByte, 0, waveByte, index, riff_ckidByte.length);
		index +=riff_ckidByte.length;
		
		System.arraycopy(TypeConversion.intToBytes(riff_cksize), 0, waveByte, index, 4);
		index +=4;
		
		byte [] riff_fccTypeByte = riff_fccType.getBytes();
		System.arraycopy(riff_fccTypeByte, 0, waveByte, index, riff_fccTypeByte.length);
		index +=riff_fccTypeByte.length;
		
		byte [] fmt_ckidByte = fmt_ckid.getBytes();
		System.arraycopy(fmt_ckidByte, 0, waveByte, index, fmt_ckidByte.length);
		index +=fmt_ckidByte.length;
		
		System.arraycopy(TypeConversion.intToBytes(fmt_cksize), 0, waveByte, index, 4);
		index +=4;
		
		
		System.arraycopy(TypeConversion.shortToBytes(fmt_wFormatTag), 0, waveByte, index, 2);
		index +=2;
		
		System.arraycopy(TypeConversion.shortToBytes(fmt_nChannels), 0, waveByte, index, 2);
		index +=2;

		
		System.arraycopy(TypeConversion.intToBytes(fmt_nSamplesPerSec), 0, waveByte, index, 4);
		index +=4;
		
		System.arraycopy(TypeConversion.intToBytes(fmt_nAvgBytesPerSec), 0, waveByte, index, 4);
		index +=4;
		
		System.arraycopy(TypeConversion.shortToBytes(fmt_nBlockAlign), 0, waveByte, index, 2);
		index +=2;
		
		System.arraycopy(TypeConversion.shortToBytes(fmt_wBitsPerSample), 0, waveByte, index, 2);
		index +=2;
		
		byte [] data_ckidByte = data_ckid.getBytes();
		System.arraycopy(data_ckidByte, 0, waveByte, index, data_ckidByte.length);
		index +=data_ckidByte.length;
		
		System.arraycopy(TypeConversion.intToBytes(data_cksize), 0, waveByte, index, 4);
		index +=4;
		if(data!=null&&data.length>0){
			System.arraycopy(data, 0, waveByte, index, data.length);
			index +=data.length;
		}
		return waveByte;
	}
	
	
}
