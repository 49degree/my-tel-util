package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerTest {
	
	public ServerTest(String name,Socket sin){
		InnerServ in = new InnerServ(name,sin);
	}
	
	
	public static void main(String[] args){
		try{
			ServerSocket serv = new ServerSocket(8181);
			System.out.println("server is started..");
			int i = 0;
			while(true){
				Socket sin = serv.accept();
				System.out.println("conecting...");
				new ServerTest("client"+(++i),sin); 
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	
	}
	
	
	public class InnerServ implements Runnable{
		Thread t = null;
		Socket sin = null;
		public InnerServ(String name,Socket sin){

			this.sin = sin;
			t = new Thread(this,name);
			t.start();
		}
		public void run(){
			try{
				InputStream inStream = sin.getInputStream();
				OutputStream outStream = sin.getOutputStream();
				
				
				
				Scanner in = new Scanner(inStream);
				PrintWriter out = new PrintWriter(outStream,true);
				out.println("Hello,I'm receive you!/n Enter BYE to exit!");
				boolean done = false;
				while(!done&& in.hasNext()){
					String line = in.nextLine();
					System.out.println("recev msg:"+line);
					out.println("i'm recev msg:"+line);
					if(line.equals("exit")){
						done = true;
					}
				}
				System.out.println("done");
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				try{
					sin.close();
				}catch(IOException e){
					
				}
				
			}

			
		}
	}

}
