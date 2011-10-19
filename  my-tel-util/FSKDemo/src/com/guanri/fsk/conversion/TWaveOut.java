package com.guanri.fsk.conversion;
//import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
//import java.nio.ByteBuffer;
//import java.nio.MappedByteBuffer;
//import java.nio.channels.FileChannel;
//import java.nio.channels.WritableByteChannel;


public class TWaveOut {
	File file = null;
	FileOutputStream out = null;
	static final short nChannels = 1;
	int nSamplesPerSec = 11025;
	static final short nBlockAlign = 2;
	//int Size = 0;
	
	void outwrite(String str) {
		byte[] b = str.getBytes();
		try {
			out.write(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	byte[] intto(int i) {
		byte[] b = new byte[4];
		b[0] = (byte) (i);
		b[1] = (byte) (i >> 8);
		b[2] = (byte) (i >> 16);
		b[3] = (byte) (i >> 24);
		return b;
	}
	
	void outwrite(int i) {
		byte[] b = new byte[4];
		b[0] = (byte) (i);
		b[1] = (byte) (i >> 8);
		b[2] = (byte) (i >> 16);
		b[3] = (byte) (i >> 24);
		try {
			out.write(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void outwrite(short i) {
		byte[] b = new byte[2];
		b[0] = (byte) (i);
		b[1] = (byte) (i >> 8);
		try {
			out.write(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	void Init() {
		//byte [] b = new byte[44];
		outwrite("RIFF");
		outwrite(0);
		outwrite("WAVE");
		outwrite("fmt ");
		outwrite(16);
		outwrite((short)1);
		outwrite((short)1);
		outwrite(nSamplesPerSec);
		outwrite(nSamplesPerSec * nBlockAlign);
		outwrite(nBlockAlign);
		outwrite((short)16);
		outwrite("data");
		outwrite(0);
	}
	public void Create(String FileName) {
		file = new File(FileName);
		
		try {
			out = new FileOutputStream(file);
			Init();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void Write(int Data) {
		outwrite((short)Data);
		//Size += 2;
	}
	
	public void Close () {
		try {
			int Size = (int) out.getChannel().size();
			
			out.getChannel().position(4);
			outwrite(Size - 8);
			
			out.getChannel().position(40);
			outwrite(Size - 44);
			
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
