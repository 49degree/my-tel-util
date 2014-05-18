package com.skyeyes.storemonitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.skyeyes.base.cmd.CommandControl;
import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.cmd.bean.impl.ReceivLogin;
import com.skyeyes.base.cmd.bean.impl.ReceiveChannelPic;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceChannelListStatus;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceChannelListStatus.ChannelStatus;
import com.skyeyes.base.cmd.bean.impl.ReceiveReadDeviceList;
import com.skyeyes.base.cmd.bean.impl.ReceiveRealVideo;
import com.skyeyes.base.cmd.bean.impl.ReceiveVideoData;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.exception.NetworkException;
import com.skyeyes.base.network.impl.SkyeyeSocketClient;
import com.skyeyes.base.network.impl.SkyeyeSocketClient.SocketHandler;

public class MainTest {
	static HashMap<Integer,ChannelStatus> mChannelListStatus = null;
	static int fileId = 0;
	private static class SocketHandlerImpl implements SocketHandler {
		
		@Override
		public void onReceiveCmd(ReceiveCmdBean receiveCmdBean) {
			// TODO Auto-generated method stub
			System.out.println("解析报文成功:" + (receiveCmdBean!=null?receiveCmdBean.toString():"receiveCmdBean is null"));
			if (receiveCmdBean instanceof ReceivLogin) {
				CommandControl.loginId = ((ReceivLogin) receiveCmdBean)
						.getCommandHeader().loginId;
			} else if (receiveCmdBean instanceof ReceiveReadDeviceList) {
				CommandControl.deviceIdList = ((ReceiveReadDeviceList) receiveCmdBean).deviceCodeList;
			}else if(receiveCmdBean instanceof ReceiveDeviceChannelListStatus){
				mChannelListStatus = ((ReceiveDeviceChannelListStatus)receiveCmdBean).mChannelListStatus;
			}else if(receiveCmdBean instanceof ReceiveChannelPic){
				File f = new File("testfile");
				if(!f.exists())
					f.mkdir();
				f = new File("testfile/"+(fileId++)+".jpg");
				try {
					f.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					FileOutputStream in = new FileOutputStream(f);
					try {
						in.write(((ReceiveChannelPic)receiveCmdBean).pic);
						in.flush();
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else if(receiveCmdBean instanceof ReceiveRealVideo||
					receiveCmdBean instanceof ReceiveVideoData){
				File f = new File("testfile");
				if(!f.exists())
					f.mkdir();
				f = new File("testfile/video.data");
				if(!f.exists()){
					try {
						f.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				try {
					
					FileOutputStream in = new FileOutputStream(f,true);
					try {
						if(receiveCmdBean instanceof ReceiveVideoData){
							in.write(((ReceiveVideoData)receiveCmdBean).data);
						}else
							//in.write(((ReceiveRealVideo)receiveCmdBean).header);
						in.flush();
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}

		@Override
		public void onCmdException(CommandParseException ex) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSocketException(NetworkException ex) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSocketClosed() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setSkyeyeSocketClient(SkyeyeSocketClient skyeyeSocketClient) {
			// TODO Auto-generated method stub
			
		}

	};

	// 登陆
	public static void testEquitLogin(SkyeyeSocketClient skyeyeSocketClient) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] { 1, "369test", "369test",CommandControl.getDeviceId()};
		try {
			sendObjectParams.setParams(REQUST.cmdEquitLogin, params);

			System.out.println("testLoginStore入参数："
					+ sendObjectParams.toString());
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			skyeyeSocketClient.sendCmd(sendObjectParams);
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 读取设备IP
	public static void testEquitListNoLogin(
			SkyeyeSocketClient skyeyeSocketClient) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] { "369test", "369test" };
		//params = new Object[]{};
		try {

			sendObjectParams.setParams(REQUST.cmdUserEquitListNOLogin, params);

			System.out.println("testEquitListNoLogin入参数："
					+ sendObjectParams.toString());
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			skyeyeSocketClient.sendCmd(sendObjectParams);
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 读取设备IP
	public static void testDeviceIp(SkyeyeSocketClient skyeyeSocketClient) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] {};
		try {
			sendObjectParams.setParams(REQUST.cmdReadDeviceIp, params);
			System.out
					.println("testDeviceIp入参数：" + sendObjectParams.toString());
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			skyeyeSocketClient.sendCmd(sendObjectParams);
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 读设备工作布防状态
	public static void readDeviceStatus(SkyeyeSocketClient skyeyeSocketClient) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] {};
		try {
			sendObjectParams.setParams(REQUST.cmdGetActive, params);
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			skyeyeSocketClient.sendCmd(sendObjectParams);
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 读设备运行环境状态
	public static void readDeviceEnv(SkyeyeSocketClient skyeyeSocketClient) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] {};
		try {
			sendObjectParams.setParams(REQUST.cmdReadDeviceEnv, params);
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			skyeyeSocketClient.sendCmd(sendObjectParams);
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void requstRealTimeVideo(SkyeyeSocketClient skyeyeSocketClient,byte channelId) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] { channelId };
		try {
			sendObjectParams.setParams(REQUST.cmdReqRealVideo, params);
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			skyeyeSocketClient.sendCmd(sendObjectParams);
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void requstStopVideo(SkyeyeSocketClient skyeyeSocketClient) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] {  };
		try {
			sendObjectParams.setParams(REQUST.cmdReqStopVideo, params);
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			skyeyeSocketClient.sendCmd(sendObjectParams);
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 获取当前设备IO口状态
	public static void getEquitIO(SkyeyeSocketClient skyeyeSocketClient) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] { 1 };
		try {
			sendObjectParams.setParams(REQUST.cmdGetEquitIO, params);
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			skyeyeSocketClient.sendCmd(sendObjectParams);
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 设置当前设备IO口状态  结果返回数据为空。。。。。。
	public static void setEquitIO(SkyeyeSocketClient skyeyeSocketClient) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] { 1, 1 };
		try {
			sendObjectParams.setParams(REQUST.cmdSetEquitIO, params);
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			skyeyeSocketClient.sendCmd(sendObjectParams);
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 设备状态变更事件	未调通
	public static void changeDeviceStatus(SkyeyeSocketClient skyeyeSocketClient) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] {};
		try {
			sendObjectParams.setParams(REQUST.cmdPushActive, params);
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			skyeyeSocketClient.sendCmd(sendObjectParams);
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 设备通道列表及状态
	public static void getChannelListStatus(SkyeyeSocketClient skyeyeSocketClient) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] {};
		try {
			sendObjectParams.setParams(REQUST.cmdReqVideoChannelListStatus, params);
			System.out.println("getChannelListStatus入参数：" + sendObjectParams.toString());
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			skyeyeSocketClient.sendCmd(sendObjectParams);
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// 设备通道列表及状态
	public static void getChannelPic(SkyeyeSocketClient skyeyeSocketClient,byte channelId) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		System.out.println("channelId:"+channelId);
		Object[] params = new Object[] {channelId};
		try {
			sendObjectParams.setParams(REQUST.cmdReqVideoChannelPic, params);
			System.out.println("getChannelPic入参数：" + sendObjectParams.toString());
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			skyeyeSocketClient.sendCmd(sendObjectParams);
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		SkyeyeSocketClient skyeyeSocketClient = null;
		try {
			skyeyeSocketClient = new SkyeyeSocketClient(
					new SocketHandlerImpl(), false);
		} catch (NetworkException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		testEquitListNoLogin(skyeyeSocketClient);//查询设备测试
		
		// 等待设备返回
		while (CommandControl.getDeviceId().equals(""))
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		testEquitLogin(skyeyeSocketClient);
		// 等待设备返回
		while (CommandControl.loginId == 0)
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		getChannelPic(skyeyeSocketClient,(byte)0);
		
		while (fileId==0)
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
//		getChannelListStatus(skyeyeSocketClient);
//		// 等待设备返回
//		while (mChannelListStatus==null)
//			try {
//				Thread.sleep(50);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		for(int id:mChannelListStatus.keySet()){
//			System.out.println("id:"+id);
//			getChannelPic(skyeyeSocketClient,(byte)id);
//		}
		
		

		requstRealTimeVideo(skyeyeSocketClient,(byte)0);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//new H264Player(new String[]{"testfile/video.data"});

//		requstStopVideo(skyeyeSocketClient);
		
		//changeDeviceStatus(skyeyeSocketClient);
		
		//System.out.println("testEquitListNoLogin");

		//testDeviceIp(skyeyeSocketClient);
	}
}
