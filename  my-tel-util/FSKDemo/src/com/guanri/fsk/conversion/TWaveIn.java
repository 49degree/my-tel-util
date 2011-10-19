package com.guanri.fsk.conversion;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class TWaveIn {
	File file = null;
	InputStream in = null;
	short nChannels = 0;
	int nSamplesPerSec = 0;
	short nBlockAlign = 0;
	short ChSize = 0;
	int Data;
	
	short bytestoshort (byte[] b, int offset) {
		short Result;
		Result = 0;
		offset += 2;
		Result <<= 8;
		Result |= (b[--offset] & 0xFF);
		Result <<= 8;
		Result |= (b[--offset] & 0xFF);
		return Result;
	}
	
	int bytestoint (byte[] b, int offset) {
		int Result;
		Result = 0;
		offset += 4;
		Result <<= 8;
		Result |= (b[--offset] & 0xFF);
		Result <<= 8;
		Result |= (b[--offset] & 0xFF);
		Result <<= 8;
		Result |= (b[--offset] & 0xFF);
		Result <<= 8;
		Result |= (b[--offset] & 0xFF);
		return Result;
	}
	
	void Init(){
		byte [] b; 
		int bytesRead = 0;
		b = new byte[44];
		try {
			bytesRead = in.read(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (bytesRead >= b.length) {
			nChannels = bytestoshort(b, 22);
			nSamplesPerSec = bytestoint(b, 24);
			nBlockAlign = bytestoshort(b, 32);
			ChSize = (short) (nBlockAlign / nChannels);
		}

	}
	public void Open(String FileName) {
		file = new File(FileName);
		
		try {
			in = new FileInputStream(file);				
			Init();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean Read() {
		int bytesRead = 0;
		byte [] b = new byte[nBlockAlign];
		try {
			bytesRead = in.read(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (bytesRead >= b.length) {
			switch (ChSize) {
			case 1: 
				Data = b[0];
				break;
			case 2:
				Data = bytestoshort(b, 0);
				break;
			case 4:
				Data = bytestoint(b, 0);
			default:
				Data = 0;
			}
			return true;
		} else
			return false;
		
		
	}

	public void Close (){
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
