package com.guanri.android.jpos.pos;

/**
 * PC的串口对象
 * @author Administrator
 *
 */
public class SerialPortPC extends SerialPortImp{
//    private Enumeration portList;//PC接口列表
//    private CommPortIdentifier portId;//COM口信息
//    private SerialPort serialPort;//COM口对象
//	
//	public PCSerialPort(String device,int boundRate){
//		this.device = device;
//		this.bountRate= boundRate;
//		portList = CommPortIdentifier.getPortIdentifiers();//获取端口列表
//        while (portList.hasMoreElements()) {//遍历端口列表
//            portId = (CommPortIdentifier) portList.nextElement();
//            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
//                if (portId.getName().equals(this.device)) {
//                	//打开端口
//                    try {
//                        serialPort = (SerialPort) portId.open(this.device, 2000);
//                    } catch (PortInUseException e) {
//                    	e.printStackTrace();
//                    }
//                    //获取输入输出对象
//                    try {
//                    	mOutputStream = serialPort.getOutputStream();
//                        mInputStream = serialPort.getInputStream();
//                    } catch (IOException e) {
//                    	e.printStackTrace();
//                    	serialPort.close();
//                    }
//                    //设置端口参数
//                    try {
//                        serialPort.setSerialPortParams(9600,
//                            SerialPort.DATABITS_8,
//                            SerialPort.STOPBITS_1,
//                            SerialPort.PARITY_NONE);
//                    } catch (UnsupportedCommOperationException e) {
//                    	e.printStackTrace();
//                    }
//                }
//            }
//        }
//		
//	}
//	
//	/**
//	 * 关闭端口
//	 */
	public void portClose(){
		//serialPort.close();
	}

}
