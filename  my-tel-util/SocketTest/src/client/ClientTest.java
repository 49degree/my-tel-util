package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientTest {
	public static void main(String[] args){
		try{
			Socket sin = new Socket("127.0.0.1",8181);


			new ClientThread(sin).start();
			
			OutputStream outStream = sin.getOutputStream();
			PrintWriter out = new PrintWriter(outStream,true);
			
			out.println("can me connet");
			
			InputStream cmd = System.in;			
			byte[] buffer = new byte[2048];
			int cmdLth = 0;
			System.out.println("read");
			while((cmdLth=cmd.read(buffer))>0){
				byte[] b = new byte[cmdLth-2];
				System.arraycopy(buffer, 0, b, 0, cmdLth-2);
				String inStr = new String(b);
				System.out.println("input:"+cmdLth+":"+inStr);			
				out.println(inStr);
				//out.flush();
				if(inStr.equals("exit")){
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	static class  ClientThread extends Thread{
		Socket sin = null;
		public ClientThread(Socket sin){
			this.sin = sin;
		}
		
		public void run(){
		try{
			Scanner inStream = new Scanner(sin.getInputStream());
			while(inStream.hasNext()){
				String line = inStream.nextLine();
				System.out.println("server say:"+line);
			}
			sin.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		}
	}
}
