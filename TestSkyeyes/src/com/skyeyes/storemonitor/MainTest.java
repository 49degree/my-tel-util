package com.skyeyes.storemonitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import com.skyeyes.base.cmd.CommandControl;
import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.cmd.bean.impl.ReceivLogin;
import com.skyeyes.base.cmd.bean.impl.ReceiveChannelPic;
import com.skyeyes.base.cmd.bean.impl.ReceiveCountManu;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceChannelListStatus;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceChannelListStatus.ChannelStatus;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceRegisterInfo;
import com.skyeyes.base.cmd.bean.impl.ReceiveOpenCloseDoor;
import com.skyeyes.base.cmd.bean.impl.ReceiveReadDeviceList;
import com.skyeyes.base.cmd.bean.impl.ReceiveRealVideo;
import com.skyeyes.base.cmd.bean.impl.ReceiveVideoData;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.exception.NetworkException;
import com.skyeyes.base.network.impl.SkyeyeSocketClient;
import com.skyeyes.base.network.impl.SkyeyeSocketClient.SocketHandler;
import com.skyeyes.base.util.DateUtil;

public class MainTest {
	//static String userName ="389test";
	static String userName ="369test";
	static HashMap<Integer,ChannelStatus> mChannelListStatus = null;
	static int fileId = 0;
	static int channelCount = -1;
	static ReceiveOpenCloseDoor receiveOpenCloseDoor;
	private static class SocketHandlerImpl implements SocketHandler {
		
		@Override
		public void onReceiveCmd(final ReceiveCmdBean receiveCmdBean) {
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
				
			}else if(receiveCmdBean instanceof ReceiveRealVideo){
				
				
			}else if(receiveCmdBean instanceof ReceiveVideoData){
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
				
				
				new Thread(){
					public void run(){
						testResponseVideoData(skyeyeSocketClient,receiveCmdBean);
					}
				}.start();
				
				
				
			}else if(receiveCmdBean instanceof ReceiveDeviceRegisterInfo){
				channelCount = ((ReceiveDeviceRegisterInfo)receiveCmdBean).videoChannelCount;
			}else if(receiveCmdBean instanceof ReceiveCountManu){
				if(mCountManuCmdProcess!=null)
					mCountManuCmdProcess.onProcess((ReceiveCountManu)receiveCmdBean);
			}else if(receiveCmdBean instanceof ReceiveOpenCloseDoor){
				receiveOpenCloseDoor = (ReceiveOpenCloseDoor)receiveCmdBean;
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
	
	// 回复视频数据
	public static void testResponseVideoData(SkyeyeSocketClient skyeyeSocketClient,ReceiveCmdBean receiveCmdBean) {
		byte cmdId = receiveCmdBean.getCommandHeader().cmdId;
		SendObjectParams sendObjectParams = new SendObjectParams();
		sendObjectParams.setCommandHeader(receiveCmdBean.getCommandHeader());
		
		
		
		Object[] params = new Object[] {};
		try {
			sendObjectParams.setParams(REQUST.cmdRevFrame, params);
			sendObjectParams.getCommandHeader().cmdCode = 0 ;
			sendObjectParams.getCommandHeader().cmdId = cmdId;

			System.out.println("testResponseVideoData入参数："
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

	// 登陆
	public static void testEquitLogin(SkyeyeSocketClient skyeyeSocketClient) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] { 1, userName, userName,CommandControl.getDeviceId()};
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
		Object[] params = new Object[] {userName , userName };
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
	public static void getcmdEquitRegInfo(SkyeyeSocketClient skyeyeSocketClient) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] {};
		try {
			sendObjectParams.setParams(REQUST.cmdEquitRegInfo, params);
			System.out.println("cmdEquitRegInfo入参数：" + sendObjectParams.toString());
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
	
	static CountManuCmdProcess mCountManuCmdProcess = null;
	// 人流统计
	public static void getManucount(SkyeyeSocketClient skyeyeSocketClient) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		
		String dateTime = DateUtil.getTimeStringFormat(new Date(), DateUtil.TIME_FORMAT_YMD);
		Object[] params = new Object[] {"2014-05-01"+" 00:00:00"};
		try {
			sendObjectParams.setParams(REQUST.cmdReqAllManuByMouse, params);
			System.out.println("getManucount入参数：" + sendObjectParams.toString());
			mCountManuCmdProcess = new CountManuCmdProcess(REQUST.cmdReqAllManuByMouse,(String)params[0]);
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
	
	public static void getOpenCloseDoor(SkyeyeSocketClient skyeyeSocketClient) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		
		String dateTime = DateUtil.getTimeStringFormat(new Date(), DateUtil.TIME_FORMAT_YMD);
		Object[] params = new Object[] {"1978-05-01 00:00:00","2014-06-01 00:00:00"};
		try {
			sendObjectParams.setParams(REQUST.cmdReqOpenCloseDoorList, params);
			System.out.println("cmdReqOpenCloseDoorList入参数：" + sendObjectParams.toString());
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
	
	public static void getOpenCloseDoorInfo(SkyeyeSocketClient skyeyeSocketClient,String eventCode) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		
		String dateTime = DateUtil.getTimeStringFormat(new Date(), DateUtil.TIME_FORMAT_YMD);
		Object[] params = new Object[] {eventCode};
		try {
			sendObjectParams.setParams(REQUST.cmdReqOpenCloseDoorInfo, params);
			System.out.println("cmdReqOpenCloseDoorInfo入参数：" + sendObjectParams.toString());
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
	
	public static void getAlarmList(SkyeyeSocketClient skyeyeSocketClient) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		
		String dateTime = DateUtil.getTimeStringFormat(new Date(), DateUtil.TIME_FORMAT_YMD);
		Object[] params = new Object[] {"1978-05-01 00:00:00","2014-06-01 00:00:00"};
		try {
			sendObjectParams.setParams(REQUST.cmdReqAlarmList, params);
			System.out.println("cmdReqAlarmList入参数：" + sendObjectParams.toString());
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
	
	public static void getAlarmInfo(SkyeyeSocketClient skyeyeSocketClient,String eventCode) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		
		String dateTime = DateUtil.getTimeStringFormat(new Date(), DateUtil.TIME_FORMAT_YMD);
		Object[] params = new Object[] {eventCode};
		try {
			sendObjectParams.setParams(REQUST.cmdReqAlarmInfo, params);
			System.out.println("cmdReqAlarmInfo入参数：" + sendObjectParams.toString());
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
	
	public static void requstHistoryVideo(SkyeyeSocketClient skyeyeSocketClient,byte channelId) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] { channelId,"2014-05-25 12:00:00",(short)480};
		try {
			sendObjectParams.setParams(REQUST.cmdReqHistoryVideo, params);
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
	
	
	static SkyeyeSocketClient skyeyeSocketClient = null;
	public static void main(String[] args) {
		
		try {
			skyeyeSocketClient = new SkyeyeSocketClient(
					new SocketHandlerImpl(), false);
			skyeyeSocketClient.setServerAddr("113.106.89.91",4015);
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
		
		getcmdEquitRegInfo(skyeyeSocketClient);
		// 等待设备返回
		while (channelCount==-1)
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		getManucount(skyeyeSocketClient);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getOpenCloseDoor(skyeyeSocketClient);
		
		while (receiveOpenCloseDoor==null)
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		getAlarmList(skyeyeSocketClient);
		
		//getOpenCloseDoorInfo(skyeyeSocketClient,receiveOpenCloseDoor.openCloseDoorBeans.get(0).des);
		
		//getOpenCloseDoorInfo(skyeyeSocketClient,"123456789");
		//requstRealTimeVideo(skyeyeSocketClient,(byte)0x00);
		//requstHistoryVideo(skyeyeSocketClient,(byte)0x00);
		//new H264Player(new String[]{"testfile/video.data"});

//		requstStopVideo(skyeyeSocketClient);
		
		//changeDeviceStatus(skyeyeSocketClient);
		
		//System.out.println("testEquitListNoLogin");

		//testDeviceIp(skyeyeSocketClient);
	}


}
