package com.custom.update;

public abstract class Handler {
	public Message obtainMessage(int id){
		return new Message(id);
	}
	public Message obtainMessage(int id,Object o){
		return new Message(id,o);
	}
	public void sendMessage(Message msg){
		handleMessage(msg);
	}
	
	public abstract void handleMessage(Message msg);
	
	
	public class Message{
		public int what = -1;
		public Object obj = -1;
		public Message(int id){
			this.what = id;
		}
		public Message(int id,Object o){
			this.what = id;
			this.obj = o;
		}
	}
}
