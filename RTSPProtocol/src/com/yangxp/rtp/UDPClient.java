package com.yangxp.rtp;

import it.sauronsoftware.base64.Base64;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import org.apache.log4j.Logger;


public class UDPClient {
	public static void main(String[] args){
		new UDPClient(5006).init();
	}
	
	static Logger logger = Logger.getLogger(UDPClient.class);
	private static final int TIMEOUT = 5000;  //设置接收数据的超时时间
	private static final int MAXNUM = 5;      //设置重发数据的最多次数
	public static final int MTU = 1400;
	int mPort ;
	RandomAccessFile file;
	public UDPClient(int port){
		mPort = port;
		try {
			File f = new File("d:/h264_local");
			if(f.exists())
				f.delete();
			file = new RandomAccessFile("d:/h264_local","rw");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	public void init(){
		String str_send = "Hello UDPserver";
		new Thread(){
			public void run(){
				try{
					byte[] buf = new byte[MTU];
					//客户端在9000端口监听接收到的数据
					DatagramSocket ds = new DatagramSocket(mPort);
					//InetAddress loc = InetAddress.getLocalHost();
					//定义用来发送数据的DatagramPacket实例
					//DatagramPacket dp_send= new DatagramPacket(str_send.getBytes(),str_send.length(),loc,3000);
					//定义用来接收数据的DatagramPacket实例
					DatagramPacket dp_receive = new DatagramPacket(buf, MTU);
					//数据发向本地3000端口
					ds.setSoTimeout(TIMEOUT);              //设置接收数据时阻塞的最长时间
					int tries = 0;                         //重发数据的次数
					boolean receivedResponse = false;     //是否接收到数据的标志位
					//直到接收到数据，或者重发次数达到预定值，则退出循环
					while(!receivedResponse && tries<MAXNUM){
						try{
							//接收从服务端发送回来的数据
							ds.receive(dp_receive); 
							
					        //logger.debug(mPort+":length：" + dp_receive.getLength()+":header:"+TypeConversion.byte2hex(buf, 0, 12));
							RTPpacket mRTPpacket = unPackageHeader(buf,dp_receive.getLength());
							logger.debug(mPort+":length：" + dp_receive.getLength()+":header:"+mRTPpacket.toString());    
							
						}catch(InterruptedIOException e){
							//如果接收数据时阻塞超时，重发并减少一次重发的次数
							tries += 1;
							System.out.println("Time out," + (MAXNUM - tries) + " more tries..." );
						}
					}
					ds.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}.start();
	}  
	
	
	class   RTPpacket
	{  
	    byte version;              //2 bits !这个域长度为2比特，标出了RTP的最近版本。当前的版本为2.0
	    byte padding;              //1 bit  ! （填充）: 这个域长度为1比特，如果P被置位，包在结尾处包含有一个或多个附加的填充字节，这些填充字节不是有效负荷的一部分。填充是一些需要固定块大小的加密算法所要求的，或是为了在低层PDU搬运RTP包。
	    byte extension;            //1 bit  !这个域长度为1比特，如果被置位，固定的头后面紧跟了一个头的扩展。
	    byte cc;                   //4 bits !CSRC count (CC): 这个域长度为4比特。这个域表示了跟在固定头后面的CSRC标识符的数目。 如前所述，这个域只有在通过一个混合器才有非零值          
	    byte marker;               //1 bit   !这个域长度为1比特，如果M被置位，表示一些重要的项目如帧边界在包中被标记。例如，如果包中有几个比特的当前帧，连同前一帧，那么RTP的这一位就被置位
	    byte pt;                   //7 bits  !Payload type (PT) （有效负荷类型(音视频的编码、采样)）: 这个域长度为7比特，PT指示的是有RTP包中的有效负荷的类型。RTP音频视频简介(AVP)包含了一个默认的有效负荷类型码到有效负荷格式的映射。附加的有效负荷类型可以向IANA注册  
	    int seq_no;                //16 bits !这个域长度为16个比特，每送一个RTP包数目就增加一，初始值被设为一个随机数。接收方不仅可以用这个序列号检测包丢失，也可以重组包序列
	    long timestamp;            //32 bits !Time stamp（时间戳）: 这个域长度为32个比特，时间戳反映了RTP数据包的头一个字节的采样时刻。 采样时刻必须是由一个单调线性增加的时钟产生，这样做是为了接收方的同步和抖动计算。初始值必须为随机数，这是为了避免对原码的攻击。例如，如果RTP源使用了一个编码器， 缓冲20ms的音频数据，那么RTP时间戳必须每个包增加160，无论包是被传递了还是被丢失了。
	    long ssrc;             	   //32 bits !这个域长度为32比特，这个域表示了正在为会话产生RTP包的源。这个标识符是随机选中的，目的是为了避免同一个RTP会话中两个源有相同的标识符。
	    List<Long> ccList;		   //32 bits 这个列表标识了在这个包中对有效负荷起作用的所有源。标识符的最大数目限定为15，这是由CC域所限定的（全零在CC域中是被禁止的）。如果有超过15个的分配源，只有前15个被标识
	    byte[] payload;      	   //!< the payload including payload headers  
	    long paylen;               //32bits !< length of payload in bytes 
	    
	    public String toString(){
	    	StringBuffer temp = new StringBuffer();
	    	
	    	temp.append("version").append("=").append(version).append(";");
	    	temp.append("padding").append("=").append(padding).append(";");
	    	temp.append("extension").append("=").append(extension).append(";");
	    	temp.append("cc").append("=").append(cc).append(";");
	    	temp.append("marker").append("=").append(marker).append(";");
	    	temp.append("pt").append("=").append(pt).append(";");
	    	temp.append("seq_no").append("=").append(seq_no).append(";");
	    	temp.append("timestamp").append("=").append(timestamp).append(";");
	    	temp.append("ssrc").append("=").append(ssrc).append(";");
	    	return temp.toString();
	    }
	}
	
	boolean start = false;
	boolean hasSps = false;
	
	int seq = 0;
	
	public RTPpacket unPackageHeader(byte[] buffer,int len){
		RTPpacket rtpPacket = new RTPpacket();
		
		rtpPacket.version= (byte)((buffer[0]&0xC0)>>6);
		rtpPacket.padding= (byte)((buffer[0]&0x40)>>5);
		rtpPacket.extension= (byte)((buffer[0]&0x10)>>4);
		rtpPacket.cc= (byte)(buffer[0]&0x0F);
		
		rtpPacket.marker= (byte)((buffer[1]&0x80)>>7);
		rtpPacket.pt= (byte)((buffer[1]&0x7F)>>1);
		
		rtpPacket.seq_no = ((buffer[2]&0xFF)<<8)|(buffer[3]&0xFF);
		
		//rtpPacket.timestamp = TypeConversion.bytesToLongEX(buffer, 4)&0xFFFFFFFFL;
		if(seq>=rtpPacket.seq_no||(seq+1)!=rtpPacket.seq_no){
			logger.debug("----------------------");
		}
		seq = rtpPacket.seq_no;
		
		rtpPacket.timestamp = 0;
		for (int i = 4; i < 8; i++) {
			rtpPacket.timestamp <<= 8;
			rtpPacket.timestamp |= (buffer[i] & 0xff);
		}
		
		rtpPacket.ssrc = 0;
		for (int i = 8; i < 12; i++) {
			rtpPacket.ssrc <<= 8;
			rtpPacket.ssrc |= (buffer[i] & 0xff);
		}
		
		
//		U indicator有以下格式：
//		+---------------+
//
//		|0|1|2|3|4|5|6|7|
//
//		+-+-+-+-+-+-+-+-+
//
//		|F|NRI| Type |
//
//		+---------------+
//		F: 1 个比特.
//		  forbidden_zero_bit. 在 H.264 规范中规定了这一位必须为 0.
//		NRI: 2 个比特.
//		  nal_ref_idc. 取 00 ~ 11, 似乎指示这个 NALU 的重要性, 如 00 的 NALU 解码器可以丢弃它而不影响图像的回放. 不过一般情况下不太关心
//		FU指示字节的类型域 Type=28表示FU-A。NRI域的值必须根据分片NAL单元的NRI域的值设置。
//		
//		FU header的格式如下：
//		+---------------+
//
//		|0|1|2|3|4|5|6|7|
//
//		+-+-+-+-+-+-+-+-+
//
//		|S|E|R| Type |
//
//		+---------------+
//		S: 1 bit
//		当设置成1,开始位指示分片NAL单元的开始。当跟随的FU荷载不是分片NAL单元荷载的开始，开始位设为0。
//		E: 1 bit
//		当设置成1, 结束位指示分片NAL单元的结束，即, 荷载的最后字节也是分片NAL单元的最后一个字节。当跟随的FU荷载不是分片NAL单元的最后分片,结束位设置为0。 
//		R: 1 bit 保留位必须设置为0，接收者必须忽略该位。
//		Type: 5 bits
//		NAL单元荷载类型定义见下表
//		表1. 单元类型以及荷载结构总结
//		Type Packet Type name
//		---------------------------------------------------------
//		0 undefined -
//		1-23 NAL unit Single NAL unit packet per H.264
//		24 STAP-A Single-time aggregation packet
//		25 STAP-B Single-time aggregation packet
//		26 MTAP16 Multi-time aggregation packet
//		27 MTAP24 Multi-time aggregation packet
//		28 FU-A Fragmentation unit
//		29 FU-B Fragmentation unit
//		30-31 undefined -		
		
		byte nal = (byte)(buffer[12] & 0x1f); // 获取FU indicator的类型域，
		byte flag = (byte)((buffer[13] & 0xFF)>>6) ; // 获取FU header的前2位，判断当前是分包的开始、中间或结束
		byte flag1 = (byte)(buffer[13] & 0x1f) ; // 获取FU header的后5位
		//原始的NAL头的前三位为FU indicator的前三位，原始的NAL头的后五位为FU header的后五位，FU indicator与FU header的剩余位数根据实际情况决定。
		byte nal_fua = (byte)((buffer[12] & 0xe0 ) | (buffer[13] & 0x1f )); // FU_A nal
		logger.debug("nal:"+nal+":flag:"+flag+":flag1:"+flag1+":nal_fua:"+nal_fua);
		
		int beging = 14;
		
		if (nal == 0x1c ){ // 判断NAL的类型为0x1c=28，说明是FU-A分片
			if (flag ==2){// 开始(S当设置成1,开始位指示分片NAL单元的开始。当跟随的FU荷载不是分片NAL单元荷载的开始，开始位设为0。)
				buffer[9]=(byte)0x00;
				buffer[10]=(byte)0x00;
				buffer[11]=(byte)0x00;
				buffer[12]=(byte)0x01;
				buffer[13]= nal_fua;
				beging = 9;
			}else if (flag == 1 ) {// 结束(E:1 bit当设置成1, 结束位指示分片NAL单元的结束，即, 荷载的最后字节也是分片NAL单元的最后一个字节。当跟随的FU荷载不是分片NAL单元的最后分片,结束位设置为0。) 
				beging = 14;
			}else {// 中间
				beging = 14;
			}
		}else if(nal==7||nal==8){ // sps/pps
			if(!hasSps){
				buffer[8]=(byte)0x00;
				buffer[9]=(byte)0x00;
				buffer[10]=(byte)0x00;
				buffer[11]=(byte)0x01;
				beging = 8; 
				start = true;
			}else{
				logger.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				return rtpPacket;
			}
			
			
			byte[] va = new byte[len-12];
			sun.misc.BASE64Encoder decoder = new sun.misc.BASE64Encoder();    
			System.arraycopy(buffer, 12, va, 0, va.length);
			logger.error((nal==8?"pps=":"sps=")+new String(decoder.encode(va)));
			if(nal==7){
				//hasSps = true;
			}
		}else{//单包数据
			logger.debug("=================================================================");
			buffer[8]=(byte)0x00;
			buffer[9]=(byte)0x00;
			buffer[10]=(byte)0x00;
			buffer[11]=(byte)0x01;
			beging = 8; 
		}
		
		
		if(start){
			try {	
				if(file!=null){
					//DatagramPacket packet=new DatagramPacket(h264,ret, address,5000);
					//socket.send(packet);
					logger.debug("=1111111");
					file.write(buffer, beging, len-beging);
				}
	
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		

		return rtpPacket;
		
	}
	
} 
