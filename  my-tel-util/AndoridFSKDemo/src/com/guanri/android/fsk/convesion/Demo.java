package com.guanri.android.fsk.convesion;
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;

//import javax.sound.sampled.AudioInputStream;


public class Demo {

	/**
	 * @param args
	 */
	
	public static byte [] DecodeWaveFile(String FileName) {
		TFSK FSK = new TFSK();
		TWaveIn WaveIn = new TWaveIn();
		boolean fOK, notEof;
		//System.out.println("FSK Wave文件解码: " + FileName);
		byte [] Buffer = new byte [4096];
		int Size ;
		WaveIn.Open(FileName);
		FSK.Init(WaveIn.nSamplesPerSec);
		
		Size = 0;
		notEof = true;
		while ((Size < Buffer.length) & notEof) {
			notEof = WaveIn.Read();
			FSK.Data = WaveIn.Data;
			fOK = FSK.DecodeByte();
			if (fOK) {
				Buffer[Size++] = FSK.ByteValue;	
			}
		}
		WaveIn.Close();
		byte [] b = new byte[Size];
		for (int i = 0; i < Size; i ++) {
			b[i] = Buffer[i];
		}
		return b;
	}
	
	public static void EncodeWaveFile(String FileName, byte [] Buffer) {
		TFSK FSK = new TFSK();
		TWaveOut WaveOut = new TWaveOut();
		boolean fOK;
		int SampFreq = 11025;
		int i;
		
		FSK.Init(SampFreq);
		WaveOut.nSamplesPerSec = SampFreq;
		WaveOut.Create(FileName);
		
		
		for (i = 0; i < 100; i ++){ 		
			FSK.Sig0 = false;
			do {
				fOK = FSK.Encode();
				WaveOut.Write(FSK.Data);
			} while (!fOK);
		}
		
		for (i = 0; i < Buffer.length; i ++){ 		
			FSK.ByteValue = Buffer[i];
			do {
				fOK = FSK.EncodeByte();
				WaveOut.Write(FSK.Data);
			} while (!fOK);
		}
		
		for (i = 0; i < 500; i ++){ 		
			FSK.Sig0 = false;
			do {
				fOK = FSK.Encode();
				WaveOut.Write(FSK.Data);
			} while (!fOK);
		}
		WaveOut.Close();
	}
	static void putASC(byte[] Data) {
		int k = 0;
		//System.out.println("解码字符: " +  new String(Data, 0, Data.length));	
		for (int i = 0; i < Data.length; i ++) {
			if ((Data[i] >= 32)) Data[k ++] = Data[i];
		}
		System.out.println("解码字符: " +  new String(Data, 0, k));	
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName = "E:\\Test.wav";
		byte Data[];
		String Str;
		
		Str = "AAAAAAAAAA00000000001111111111aaaaaaaaaa";
		Str = "1111111111aaaaaaaaaa1111111111aaaaaaaaaa";
		
		System.out.println("编码字符: " + Str);
		
		fileName = "E:\\Test.wav";
		EncodeWaveFile(fileName, Str.getBytes());	
		

		fileName = "E:\\Test.wav";
		Data = DecodeWaveFile(fileName);
		putASC(Data);	
		
		fileName = "in.wav";
		Data = DecodeWaveFile(fileName);
		putASC(Data);
		
		//
		fileName = "in_1312945300909.wav";
		Data = DecodeWaveFile(fileName);
		putASC(Data);
		
		fileName = "in_1312945180025.wav";
		Data = DecodeWaveFile(fileName);
		putASC(Data);
		

			
		
	}

}
