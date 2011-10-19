package com.xys.ecg.device;


import com.xys.ecg.utils.TypeConversion;

public class DeviceRation {
	byte[]    szCollectorBtAddr = new byte[500];    // 采集器蓝牙地址                
	byte[]	szWebServiceAddrOfLan = new byte[100];  // 业务内网WebService地址
	byte[]	szValidateCode = new byte[100];          // 访问业务WebService时的验证码
	
	

	public byte[] getSzCollectorBtAddr() {
		return szCollectorBtAddr;
	}

	public void setSzCollectorBtAddr(byte[] szCollectorBtAddr) {
		this.szCollectorBtAddr = szCollectorBtAddr;
	}

	public byte[] getSzWebServiceAddrOfLan() {
		return szWebServiceAddrOfLan;
	}

	public void setSzWebServiceAddrOfLan(byte[] szWebServiceAddrOfLan) {
		this.szWebServiceAddrOfLan = szWebServiceAddrOfLan;
	}

	public byte[] getSzValidateCode() {
		return szValidateCode;
	}

	public void setSzValidateCode(byte[] szValidateCode) {
		this.szValidateCode = szValidateCode;
	}

	public DeviceRation(){
	}
	
	public DeviceRation(byte[] source){
		try{
			byte[] tempBytes0 = new byte[500];
			System.arraycopy(source, 0, tempBytes0, 0, 500);
			this.setSzCollectorBtAddr(tempBytes0);
			
			byte[] tempBytes = new byte[100];
			System.arraycopy(source, 500, tempBytes, 0, 100);
			this.setSzWebServiceAddrOfLan(tempBytes);
			
			byte[] tempBytes2 = new byte[100];
			System.arraycopy(source, 600, tempBytes2, 0, 100);
			this.setSzValidateCode(tempBytes2);
			
			
//			Class newoneClass = Class.forName("com.socket.DeviceRation");
//			Field[] fs = newoneClass.getDeclaredFields();
//		Method[] methods = getClass().getDeclaredMethods();  
//		int indexId = 0;
//			for (Field f : fs) {
//				String fieldType = f.getType().getSimpleName();
//				String fieldSetName = BeanRefUtil.parSetName(f.getName());
//				if (!BeanRefUtil.checkSetMet(methods, fieldSetName)) {
//					continue;
//				}
//				System.out.println(fieldType + ":" + fieldSetName);
//				if (fieldType.equals("short")) {
//					// 给变量赋值
//					Method mth = this.getClass().getMethod(fieldSetName,
//							f.getType());
//					mth.invoke(this, Short.valueOf(TypeConversion.bytesToShort(
//							source, indexId)));
//					indexId += 2;
//				} else if (fieldType.equals("byte[]")) {
//					// 获取数组的长度
//					String fieldGetName = BeanRefUtil.parGetName(f.getName());
//					Method getMth = this.getClass().getMethod(fieldGetName,
//							new Class[] {});
//					Object o = getMth.invoke(this, new Object[] {});
//					int length = Array.getLength(o);
//
//					byte[] tempBytes = new byte[length];
//					System.arraycopy(source, indexId, tempBytes, 0, length);
//					Method mth = this.getClass().getMethod(fieldSetName,
//							f.getType());
//					mth.invoke(this, tempBytes);
//					indexId += length;
//				}
//			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		new DeviceRation(null);
		
//		try{
//			//Field[] fs = Class.forName("DeviceRation").getFields();
//			//Field[] fields = f.getClass().getDeclaredFields();
//
//			Class newoneClass = Class.forName("DeviceRation");
//			Field[] fs = newoneClass.getDeclaredFields();
//			
//			
//			for(Field f:fs){
//				System.out.println(f.getName());
//			}
//			}catch(Exception e){
//				e.printStackTrace();
//			}
		
	}
	
}
