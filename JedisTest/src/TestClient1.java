

import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class TestClient1{
	static TestClient1 mTestClient1;
	int count = 0;
	byte[] buffer = new byte[1024000];
	
	Publisher pub = null;
	Subscriber sub = null;
	
	public static void main(String[] args){
		mTestClient1  = new TestClient1();
		
	}
	
	public TestClient1(){
		init();
	}
	
	public void init(){
        pub = new Publisher();//发布消息渠道
        sub = new Subscriber();//订阅消息
        sub.psub(); // 订阅一个频道
        pub.publish("my first"); //发布一个频道
	}
	
	public class Publisher {
		Jedis mJedis = JedisTools.getJedis();
	    public void publish(String msg) {
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("发布：news.share");
            //发布消息
            mJedis.publish("news.share", msg);
	    }
	    
	    public void publish(byte[] msg) {
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("发布：news.share");
            //发布消息
            mJedis.publish("news.share".getBytes(), msg);
	    }
	    
	    public void save(byte[] msg) {
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("发布：news.share");
            //发布消息
	    }
	}

	public class Subscriber {
		Jedis mJedis = JedisTools.getJedis();
		MyListener listener = new MyListener();
		MyBinaryJedisPubSub listener1 = new MyBinaryJedisPubSub();
	    public void psub() {
	        new Thread(new Runnable() {
	            @Override
	            public void run() {

	                System.out.println("订阅：news.share");
	                // 订阅得到信息在lister的onMessage(...)方法中进行处理
	                // 订阅多个频道
	                //mJedis.psubscribe(listener, new String[]{"news.share"});// 使用模式匹配的方式设置频道
	                System.out.println("订阅：news.share end");
	                mJedis.psubscribe(listener1, "news.share".getBytes());
	            }
	        }).start();
	    }
	}
	
	public class MyBinaryJedisPubSub extends BinaryJedisPubSub{

		@Override
		public void onMessage(byte[] channel, byte[] message) {
			// TODO Auto-generated method stub
	        System.out.println("-----MyBinaryJedisPubSub取得订阅的消息后的处理-----");
	        System.out.println(channel + "=" + message);
		}

		@Override
		public void onPMessage(byte[] pattern, byte[] channel, byte[] message) {
			// TODO Auto-generated method stub
	        System.out.println("-----MyBinaryJedisPubSub取得按表达式的方式订阅的消息后的处理-----");
	        System.out.println(new String(pattern) + "=" + new String(channel) + "=" + message.length);
	        buffer[buffer.length-1]=(byte)count++;
	        //pub.publish(buffer); //发布一个频道
		}

		@Override
		public void onSubscribe(byte[] channel, int subscribedChannels) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onUnsubscribe(byte[] channel, int subscribedChannels) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPUnsubscribe(byte[] pattern, int subscribedChannels) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPSubscribe(byte[] pattern, int subscribedChannels) {
			// TODO Auto-generated method stub

		}
		
	}
	
	public class MyListener extends JedisPubSub {

	    // 取得订阅的消息后的处理
	    public void onMessage(String channel, String message) {
	        System.out.println("-----取得订阅的消息后的处理-----");
	        System.out.println(channel + "=" + message);
	        pub.publish("my first"+(count++)); //发布一个频道
	    }

	    // 初始化订阅时候的处理
	    public void onSubscribe(String channel, int subscribedChannels) {
	        System.out.println("-----初始化订阅时候的处理-----");
	        System.out.println(channel + "=" + subscribedChannels);
	    }

	    // 取消订阅时候的处理
	    public void onUnsubscribe(String channel, int subscribedChannels) {
	        System.out.println("-----取消订阅时候的处理-----");
	        System.out.println(channel + "=" + subscribedChannels);
	    }

	    // 初始化按表达式的方式订阅时候的处理
	    public void onPSubscribe(String pattern, int subscribedChannels) {
	        System.out.println("-----初始化按表达式的方式订阅时候的处理-----");
	        System.out.println(pattern + "=" + subscribedChannels);
	    }

	    // 取消按表达式的方式订阅时候的处理
	    public void onPUnsubscribe(String pattern, int subscribedChannels) {
	        System.out.println("-----取消按表达式的方式订阅时候的处理-----");
	        System.out.println(pattern + "=" + subscribedChannels);
	    }


	    // 取得按表达式的方式订阅的消息后的处理
	    public void onPMessage(String pattern, String channel, String message) {
	        System.out.println("-----取得按表达式的方式订阅的消息后的处理-----");
	        System.out.println(pattern + "=" + channel + "=" + message);
	        buffer[buffer.length-1]=(byte)count++;
	        pub.publish(buffer); //发布一个频道
	    }
	}
}
