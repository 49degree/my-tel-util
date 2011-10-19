package com.xys.ecg.file;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.os.Environment;
import android.os.Message;

import com.xys.ecg.activity.ECGApplication;
import com.xys.ecg.activity.ECG_Android;
import com.xys.ecg.activity.ECG_Android.MainEventHandler;
import com.xys.ecg.bean.EcgDataEntity;
import com.xys.ecg.bean.HandlerWhat;
import com.xys.ecg.log.Logger;
import com.xys.ecg.network.SoapUploadDataTool;
import com.xys.ecg.sqlite.RecordDB;
import com.xys.ecg.upload.UploadEcgInfoTool;
import com.xys.ecg.utils.TypeConversion;

public class EcgDataFileOperate {
	private MainEventHandler mainHandler;
	public static Logger logger = Logger.getLogger(EcgDataFileOperate.class);
	public final String PATH_SD = Environment.getExternalStorageDirectory().toString() + "/ecg/data/"; //获取SD卡路径
	private final int TIME_LIMIT = 2500; //40分钟保存包的个数，采样频率为300，一个数据包的字节数据为288，288/300 = 0.96S
	public static String gCurrentTime = null;//第一个包的时间
	public static String gCurrentTimeEx = null;
	public static String gFilePathEcg = null;//ecg历史数据
	public static String gFilePathEcgEx = null;//ecg历史数据
	public static String gFilePathAcc_X = null;//
	public static String gFilePathAcc_XEx = null;//
	public static String gFilePathAcc_Y = null;
	public static String gFilePathAcc_YEx = null;
	public static String gFilePathAcc_Z = null;
	public static String gFilePathAcc_ZEx = null;
	public static RandomAccessFile gOut_Ecg = null;
	public static RandomAccessFile gOut_EcgEx = null;
	public static RandomAccessFile gOut_X = null;
	public static RandomAccessFile gOut_XEx = null;
	public static RandomAccessFile gOut_Y = null;
	public static RandomAccessFile gOut_YEx = null;
	public static RandomAccessFile gOut_Z = null;
	public static RandomAccessFile gOut_ZEx = null;
	public EcgDataHead gEcgDataHead;
	public EcgDataHead gEcgDataHeadEx;
	public int gEcgCount = 0;//ecg写入数据的字节数
	public int gEcgCountEx = 0;//ecg写入数据的字节数
	public int gAccCountX = 0;//acc x写入数据的字节数
	public int gAccCountXEx= 0;//acc x写入数据的字节数
	public int gAccCountY = 0;//acc y写入数据的字节数
	public int gAccCountYEx = 0;//acc y写入数据的字节数
	public int gAccCountZ = 0;//acc z写入数据的字节数
	public int gAccCountZEx = 0;//acc z写入数据的字节数
	public int gCountPackets = 0;//当前已经保存的包的个数
	public int gCountPacketsEx = 0;//当前已经保存的包的个数
	private long battTime = 0;
	public EcgDataFileOperate(MainEventHandler mainHandler) {
		// TODO Auto-generated constructor stub
		this.mainHandler = mainHandler;
	}

	
		
	public boolean seveEcgDataList(List<EcgDataEntity> ecgDataEntityList){
		for(EcgDataEntity ecgDataEntity:ecgDataEntityList){
			saveEcgData(ecgDataEntity);
		}
		return true;
	}
	
	
	
	/*
	* 函数介绍：
	*           保存文件到SD卡
	* 输入参数：
	*           
	* 输出参数：
	*           
	* 返 回 值：
	*           
	* 备    注：
	*           
	*/
   public boolean saveEcgData(EcgDataEntity ecgDataEntity){
	   //此处进行电量判断
	   int batt =   ((byte)ecgDataEntity.getPacketHead().getPacketStatus()[0]>>1)&(byte)7;//去年高5位
	   if(batt == 0){
		    if(battTime == 0){
		    	Message msg = mainHandler.obtainMessage(HandlerWhat.FileOperate2MainEmpty, false);
			    mainHandler.sendMessage(msg);
			    battTime = new Date().getTime();
		    }else if(new Date().getTime()>=battTime+2*60*1000){
		    	Message msg = mainHandler.obtainMessage(HandlerWhat.FileOperate2MainEmpty, false);
			    mainHandler.sendMessage(msg);
			    battTime = new Date().getTime();
		    }

		} else if ((batt >= 1) && (batt <= 3)) {
			Message msg = mainHandler.obtainMessage(
					HandlerWhat.FileOperate2MainLow, false);
			mainHandler.sendMessage(msg);

		} else if ((batt >= 4) && (batt <= 6)) {
			Message msg = mainHandler.obtainMessage(
					HandlerWhat.FileOperate2MainNomal, false);
			mainHandler.sendMessage(msg);
		} else if (batt == 7) {
			Message msg = mainHandler.obtainMessage(
					HandlerWhat.FileOperate2MainHight, false);
			mainHandler.sendMessage(msg);
		} else {
			logger.error("The batt is Error");
		}

		byte[] dataTypeEcg = new byte[4];
	   byte[] dataTypeAccX = new byte[4];
	   byte[] dataTypeAccY = new byte[4];
	   byte[] dataTypeAccZ = new byte[4];
	   //构造数据包头
	   byte[][] ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);  
	   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	数据类型
	   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	数据类型
	   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	数据类型
	   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	数据类型
	   
	   if(createDir(PATH_SD)==null){
		   logger.debug("Create Save directory failed!");
		   return false; 
	   }
			
	   if(ecgDataEntity.getIsHisDate()){//是历史数据
		   if((ecgDataEntity.getPacketHead().getPacketStatus()[1]&0x01) == 0x01 ){//历史触摸数据
			   if(ecgDataEntity.isFirstData){//历史触摸数据开始
				   gCurrentTime = long2Date(TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0)*1000);//获取第一个数据的时间
				   gFilePathEcg = PATH_SD + gCurrentTime+"_Ecg.dat";
				   gFilePathAcc_X = PATH_SD + gCurrentTime+"_X.dat";
				   gFilePathAcc_Y = PATH_SD + gCurrentTime+"_Y.dat";
				   gFilePathAcc_Z = PATH_SD + gCurrentTime+"_Z.dat";
				   
				   try{
					   gOut_Ecg = new RandomAccessFile(new File(gFilePathEcg),"rw"); 
					   gOut_Ecg.seek(gOut_Ecg.length());
					   gOut_X = new RandomAccessFile(new File(gFilePathAcc_X),"rw");
					   gOut_X.seek(gOut_X.length());
					   gOut_Y = new RandomAccessFile(new File(gFilePathAcc_Y),"rw");
					   gOut_Y.seek(gOut_Y.length());
					   gOut_Z = new RandomAccessFile(new File(gFilePathAcc_Z),"rw");
					   gOut_Z.seek(gOut_Z.length());
					   //写数据库 7为导联式数据，8为触摸式数据
					   ECG_Android.gWriteRecord.insertRecord(this.getUserId(), 1000*TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0) , PATH_SD+gCurrentTime+".dat" , 8, 0, 0);
					   gEcgDataHead = new EcgDataHead();
					   //写ECG数据
					   
					   
					   //构造数据包头
					   ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);
					  
					   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	数据类型
					   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	数据类型
					   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	数据类型
					   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	数据类型

					   
					   
					  // gEcgDataHead.setEcgDataType(ecgDataEntity);   //设置Ecg数据类型
					   gEcgDataHead.setdataLength(0);    //设置数据长度				   
					  // gOut_Ecg.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);//写数据类型
					   gOut_Ecg.write(dataTypeEcg, 0, dataTypeEcg.length);   //写ECG数据类型
					   gOut_Ecg.write(gEcgDataHead.getDataLength(), 0, gEcgDataHead.getDataLength().length);//写数据长度
					   gOut_Ecg.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//写Ecg数据
					   gEcgCount += ecgDataEntity.getEcgPacket().getEcgData().length;//保存ECG数据长度
					   gOut_Ecg.seek(4);//移动到保存数据长度位置，写入byte[4];
					   gOut_Ecg.write(TypeConversion.intToBytes(gEcgCount));//写入文件长度
					   gOut_Ecg.seek(gOut_Ecg.length());
					   //写ACC_X数据
					 //  gEcgDataHead.setAccDataType(ecgDataEntity);
					   gEcgDataHead.setdataLength(0);
					  // gOut_X.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
					   gOut_X.write(dataTypeAccX, 0, dataTypeAccX.length);    
					   gOut_X.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
					   gOut_X.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//写ACC_x数据
					   gAccCountX += ecgDataEntity.getAccPacket().getAccAxisX().length;//保存ACC_X数据长度
					   gOut_X.seek(4);//移动到保存数据长度位置，写入byte[4];
					   gOut_X.write(TypeConversion.intToBytes(gAccCountX));//写入文件长度
					   gOut_X.seek(gOut_X.length());
					   //写ACC_Y数据
					 //  gOut_Y.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
					   gOut_Y.write(dataTypeAccY, 0, dataTypeAccY.length);  
					   gOut_Y.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
					   gOut_Y.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//写ACC_Y数据
					   gAccCountY += ecgDataEntity.getAccPacket().getAccAxisY().length;//保存ACC_Y数据长度
					   gOut_Y.seek(4);//移动到保存数据长度位置，写入byte[4];
					   gOut_Y.write(TypeConversion.intToBytes(gAccCountY));//写入文件长度
					   gOut_Y.seek(gOut_Y.length());
					   //写ACC_Z数据
					  // gOut_Z.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
					   gOut_Z.write(dataTypeAccZ, 0, dataTypeAccZ.length);  
					   gOut_Z.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
					   gOut_Z.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//写ACC_Z数据
					   gAccCountZ += ecgDataEntity.getAccPacket().getAccAxisZ().length;//保存ACC_Z数据长度
					   gOut_Z.seek(4);//移动到保存数据长度位置，写入byte[4];
					   gOut_Z.write(TypeConversion.intToBytes(gAccCountZ));//写入文件长度
					   gOut_Z.seek(gOut_Z.length());					   
					  
					   gCountPackets++;//已经保存的数据包的个数计数器,判断是否超过40分钟用
					   
				   }catch(Exception e){
					   logger.debug("Create new File failed!");
				   }
				   
			   }else{//历史触摸数据  不是开始
				   if((ecgDataEntity.getPacketHead().getPacketStatus()[1]&0x04) == 0x04 ){//历史数据结束
					   String finalPathFile = PATH_SD + gCurrentTime+"LC.dat";//最终合并后的文件的路径
					   combineFile(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z, finalPathFile);//合并四个临时文件
					   delete(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z);//删除临时文件
					   clear(true);//清除当前计数器
					   return true;
				   }
				   
				   if(gCountPackets>=TIME_LIMIT){//大于40分钟
					   String finalPathFile = PATH_SD + gCurrentTime+".dat";//最终合并后的文件的路径
					   combineFile(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z, finalPathFile);//合并四个临时文件
					   delete(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z);//删除临时文件
					   gCurrentTime = addCurrentTime(gCurrentTime, gCountPackets);//当前时间增加40分钟
					   clear(true);//清除当前计数器
					   gFilePathEcg = PATH_SD + gCurrentTime+"_Ecg.dat";
					   gFilePathAcc_X = PATH_SD + gCurrentTime+"_X.dat";
					   gFilePathAcc_Y = PATH_SD + gCurrentTime+"_Y.dat";
					   gFilePathAcc_Z = PATH_SD + gCurrentTime+"_Z.dat";
					   try{
						   gOut_Ecg = new RandomAccessFile(new File(gFilePathEcg),"rw"); 
						   gOut_Ecg.seek(gOut_Ecg.length());
						   gOut_X = new RandomAccessFile(new File(gFilePathAcc_X),"rw");
						   gOut_X.seek(gOut_X.length());
						   gOut_Y = new RandomAccessFile(new File(gFilePathAcc_Y),"rw");
						   gOut_Y.seek(gOut_Y.length());
						   gOut_Z = new RandomAccessFile(new File(gFilePathAcc_Z),"rw");
						   gOut_Z.seek(gOut_Z.length());
						   //写数据库 7为导联式数据，8为触摸式数据
						   ECG_Android.gWriteRecord.insertRecord(this.getUserId(), 1000*TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0) , PATH_SD+gCurrentTime+".dat" , 8, 0, 0);
						   gEcgDataHead = new EcgDataHead();
						   
						   
						   //构造数据包头
						   ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);
						   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	数据类型
						   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	数据类型
						   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	数据类型
						   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	数据类型
						   
						   //写ECG数据
						 //  gEcgDataHead.setEcgDataType(ecgDataEntity);   //设置Ecg数据类型
						   gEcgDataHead.setdataLength(0);    //设置数据长度				   
						 //  gOut_Ecg.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);//写数据类型
						   gOut_Ecg.write(dataTypeEcg, 0, dataTypeEcg.length);
						   gOut_Ecg.write(gEcgDataHead.getDataLength(), 0, gEcgDataHead.getDataLength().length);//写数据长度
						   gOut_Ecg.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//写Ecg数据
						   gEcgCount += ecgDataEntity.getEcgPacket().getEcgData().length;//保存ECG数据长度
						   gOut_Ecg.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_Ecg.write(TypeConversion.intToBytes(gEcgCount));//写入文件长度
						   gOut_Ecg.seek(gOut_Ecg.length());
						   //写ACC_X数据
						  // gEcgDataHead.setAccDataType(ecgDataEntity);
						   gEcgDataHead.setdataLength(0);
						 //  gOut_X.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
						   gOut_X.write(dataTypeAccX, 0, dataTypeAccX.length);
						   gOut_X.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
						   gOut_X.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//写ACC_x数据
						   gAccCountX += ecgDataEntity.getAccPacket().getAccAxisX().length;//保存ACC_X数据长度
						   gOut_X.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_X.write(TypeConversion.intToBytes(gAccCountX));//写入文件长度
						   gOut_X.seek(gOut_X.length());
						   //写ACC_Y数据
						   //gOut_Y.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
						   gOut_Y.write(dataTypeAccY, 0, dataTypeAccY.length);
						   gOut_Y.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
						   gOut_Y.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//写ACC_Y数据
						   gAccCountY += ecgDataEntity.getAccPacket().getAccAxisY().length;//保存ACC_Y数据长度
						   gOut_Y.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_Y.write(TypeConversion.intToBytes(gAccCountY));//写入文件长度
						   gOut_Y.seek(gOut_Y.length());
						   //写ACC_Z数据
						  // gOut_Z.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
						   gOut_Z.write(dataTypeAccZ, 0, dataTypeAccZ.length);
						   gOut_Z.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
						   gOut_Z.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//写ACC_Z数据
						   gAccCountZ += ecgDataEntity.getAccPacket().getAccAxisZ().length;//保存ACC_Z数据长度
						   gOut_Z.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_Z.write(TypeConversion.intToBytes(gAccCountZ));//写入文件长度
						   gOut_Z.seek(gOut_Z.length());					   
						  
						   gCountPackets++;//已经保存的数据包的个数计数器,判断是否超过40分钟用
						   
					   }catch(Exception e){
						   logger.debug("Create new File failed!");
					   }
					   
					   
				   }else{//小于40分钟
					   if((ecgDataEntity.getPacketHead().getPacketStatus()[1]&0x04) == 0x04 ){//历史数据结束
						   String finalPathFile = PATH_SD + gCurrentTime+"LC.dat";//最终合并后的文件的路径
						   combineFile(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z, finalPathFile);//合并四个临时文件
						   delete(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z);//删除临时文件
						   clear(true);//清除当前计数器
						   return true;
					   }
					   
					   try{
						   //写ECG数据
						   gOut_Ecg.seek(gOut_Ecg.length());
						   gOut_Ecg.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//写Ecg数据
						   gEcgCount += ecgDataEntity.getEcgPacket().getEcgData().length;//保存ECG数据长度
						   gOut_Ecg.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_Ecg.write(TypeConversion.intToBytes(gEcgCount));//写入文件长度
						   gOut_Ecg.seek(gOut_Ecg.length());
						   //写ACC_X数据
						   gOut_X.seek(gOut_X.length());
						   gOut_X.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//写ACC_x数据
						   gAccCountX += ecgDataEntity.getAccPacket().getAccAxisX().length;//保存ACC_X数据长度
						   gOut_X.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_X.write(TypeConversion.intToBytes(gAccCountX));//写入文件长度
						   gOut_X.seek(gOut_X.length());
						   //写ACC_Y数据
						   gOut_Y.seek(gOut_Y.length());
						   gOut_Y.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//写ACC_Y数据
						   gAccCountY += ecgDataEntity.getAccPacket().getAccAxisY().length;//保存ACC_Y数据长度
						   gOut_Y.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_Y.write(TypeConversion.intToBytes(gAccCountY));//写入文件长度
						   gOut_Y.seek(gOut_Y.length());
						   //写ACC_Z数据
						   gOut_Z.seek(gOut_Z.length());
						   gOut_Z.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//写ACC_Z数据
						   gAccCountZ += ecgDataEntity.getAccPacket().getAccAxisZ().length;//保存ACC_Z数据长度
						   gOut_Z.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_Z.write(TypeConversion.intToBytes(gAccCountZ));//写入文件长度
						   gOut_Z.seek(gOut_Z.length());					   
						  
						   gCountPackets++;//已经保存的数据包的个数计数器,判断是否超过40分钟用
						   
					   }catch(Exception e){
						   logger.debug("Save File failed!");
					   }
					   			   
				   }
				   				   
			   }
			   
		   }else if((ecgDataEntity.getPacketHead().getPacketStatus()[1]&0x01) == 0x00){//历史导联
			   if(ecgDataEntity.isFirstData){//历史导联数据开始
				   gCurrentTime = long2Date(TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0)*1000);//获取第一个数据的时间
				   gFilePathEcg = PATH_SD + gCurrentTime+"_Ecg.dat";
				   gFilePathAcc_X = PATH_SD + gCurrentTime+"_X.dat";
				   gFilePathAcc_Y = PATH_SD + gCurrentTime+"_Y.dat";
				   gFilePathAcc_Z = PATH_SD + gCurrentTime+"_Z.dat";
				   
				   try{
					   gOut_Ecg = new RandomAccessFile(new File(gFilePathEcg),"rw"); 
					   gOut_Ecg.seek(gOut_Ecg.length());
					   gOut_X = new RandomAccessFile(new File(gFilePathAcc_X),"rw");
					   gOut_X.seek(gOut_X.length());
					   gOut_Y = new RandomAccessFile(new File(gFilePathAcc_Y),"rw");
					   gOut_Y.seek(gOut_Y.length());
					   gOut_Z = new RandomAccessFile(new File(gFilePathAcc_Z),"rw");
					   gOut_Z.seek(gOut_Z.length());
					   //写数据库 7为导联式数据，8为触摸式数据
					   ECG_Android.gWriteRecord.insertRecord(this.getUserId(), 1000*TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0) , PATH_SD+gCurrentTime+".dat" , 7, 0, 0);
					   gEcgDataHead = new EcgDataHead();
					   //写ECG数据
					   
					   //构造数据包头
					   ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);
					  
					   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	数据类型
					   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	数据类型
					   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	数据类型
					   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	数据类型
					   
					   gEcgDataHead.setEcgDataType(ecgDataEntity);   //设置Ecg数据类型
					   gEcgDataHead.setdataLength(0);    //设置数据长度				   
					   //gOut_Ecg.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);//写数据类型
					   gOut_Ecg.write(dataTypeEcg, 0, dataTypeEcg.length);
					   gOut_Ecg.write(gEcgDataHead.getDataLength(), 0, gEcgDataHead.getDataLength().length);//写数据长度
					   gOut_Ecg.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//写Ecg数据
					   gEcgCount += ecgDataEntity.getEcgPacket().getEcgData().length;//保存ECG数据长度
					   gOut_Ecg.seek(4);//移动到保存数据长度位置，写入byte[4];
					   gOut_Ecg.write(TypeConversion.intToBytes(gEcgCount));//写入文件长度
					   gOut_Ecg.seek(gOut_Ecg.length());
					   //写ACC_X数据
					 //  gEcgDataHead.setAccDataType(ecgDataEntity);
					   gEcgDataHead.setdataLength(0);
					   //gOut_X.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
					   gOut_X.write(dataTypeAccX, 0, dataTypeAccX.length);
					   gOut_X.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
					   gOut_X.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//写ACC_x数据
					   gAccCountX += ecgDataEntity.getAccPacket().getAccAxisX().length;//保存ACC_X数据长度
					   gOut_X.seek(4);//移动到保存数据长度位置，写入byte[4];
					   gOut_X.write(TypeConversion.intToBytes(gAccCountX));//写入文件长度
					   gOut_X.seek(gOut_X.length());
					   //写ACC_Y数据
					   //gOut_Y.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
					   gOut_Y.write(dataTypeAccY, 0, dataTypeAccY.length);
					   gOut_Y.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
					   gOut_Y.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//写ACC_Y数据
					   gAccCountY += ecgDataEntity.getAccPacket().getAccAxisY().length;//保存ACC_Y数据长度
					   gOut_Y.seek(4);//移动到保存数据长度位置，写入byte[4];
					   gOut_Y.write(TypeConversion.intToBytes(gAccCountY));//写入文件长度
					   gOut_Y.seek(gOut_Y.length());
					   //写ACC_Z数据
					   //gOut_Z.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
					   gOut_Z.write(dataTypeAccZ, 0, dataTypeAccZ.length);
					   gOut_Z.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
					   gOut_Z.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//写ACC_Z数据
					   gAccCountZ += ecgDataEntity.getAccPacket().getAccAxisZ().length;//保存ACC_Z数据长度
					   gOut_Z.seek(4);//移动到保存数据长度位置，写入byte[4];
					   gOut_Z.write(TypeConversion.intToBytes(gAccCountZ));//写入文件长度
					   gOut_Z.seek(gOut_Z.length());					   
					  
					   gCountPackets++;//已经保存的数据包的个数计数器,判断是否超过40分钟用
					   
				   }catch(Exception e){
					   logger.debug("Create new File failed!");
				   }
				   
			   }else{//历史导联数据  不是开始
				   if((ecgDataEntity.getPacketHead().getPacketStatus()[1]&0x04) == 0x04 ){//历史数据结束
					   String finalPathFile = PATH_SD + gCurrentTime+"LC.dat";//最终合并后的文件的路径
					   combineFile(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z, finalPathFile);//合并四个临时文件
					   delete(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z);//删除临时文件
					   clear(true);//清除当前计数器
					   return true;
				   }
				   
				   if(gCountPackets>=TIME_LIMIT){//大于40分钟
					   String finalPathFile = PATH_SD + gCurrentTime+".dat";//最终合并后的文件的路径
					   combineFile(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z, finalPathFile);//合并四个临时文件
					   delete(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z);//删除临时文件
					   gCurrentTime = addCurrentTime(gCurrentTime, (gCountPackets));//当前时间增加40分钟
					   clear(true);//清除当前计数器
					   gFilePathEcg = PATH_SD + gCurrentTime+"_Ecg.dat";
					   gFilePathAcc_X = PATH_SD + gCurrentTime+"_X.dat";
					   gFilePathAcc_Y = PATH_SD + gCurrentTime+"_Y.dat";
					   gFilePathAcc_Z = PATH_SD + gCurrentTime+"_Z.dat";
					   try{
						   gOut_Ecg = new RandomAccessFile(new File(gFilePathEcg),"rw"); 
						   gOut_Ecg.seek(gOut_Ecg.length());
						   gOut_X = new RandomAccessFile(new File(gFilePathAcc_X),"rw");
						   gOut_X.seek(gOut_X.length());
						   gOut_Y = new RandomAccessFile(new File(gFilePathAcc_Y),"rw");
						   gOut_Y.seek(gOut_Y.length());
						   gOut_Z = new RandomAccessFile(new File(gFilePathAcc_Z),"rw");
						   gOut_Z.seek(gOut_Z.length());
						   //写数据库 7为导联式数据，8为触摸式数据
						   ECG_Android.gWriteRecord.insertRecord(this.getUserId(), 1000*TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0) , PATH_SD+gCurrentTime+".dat" , 7, 0, 0);
						   gEcgDataHead = new EcgDataHead();
						   //写ECG数据
						   
						   
						   //构造数据包头
						   ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);
						  
						   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	数据类型
						   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	数据类型
						   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	数据类型
						   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	数据类型
						   
						   gEcgDataHead.setEcgDataType(ecgDataEntity);   //设置Ecg数据类型
						   gEcgDataHead.setdataLength(0);    //设置数据长度				   
						   //gOut_Ecg.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);//写数据类型
						   gOut_Ecg.write(dataTypeEcg, 0, dataTypeEcg.length);
						   gOut_Ecg.write(gEcgDataHead.getDataLength(), 0, gEcgDataHead.getDataLength().length);//写数据长度
						   gOut_Ecg.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//写Ecg数据
						   gEcgCount += ecgDataEntity.getEcgPacket().getEcgData().length;//保存ECG数据长度
						   gOut_Ecg.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_Ecg.write(TypeConversion.intToBytes(gEcgCount));//写入文件长度
						   gOut_Ecg.seek(gOut_Ecg.length());
						   //写ACC_X数据
						 //  gEcgDataHead.setAccDataType(ecgDataEntity);
						   gEcgDataHead.setdataLength(0);
						  // gOut_X.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
						   gOut_X.write(dataTypeAccX, 0, dataTypeAccX.length);
						   gOut_X.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
						   gOut_X.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//写ACC_x数据
						   gAccCountX += ecgDataEntity.getAccPacket().getAccAxisX().length;//保存ACC_X数据长度
						   gOut_X.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_X.write(TypeConversion.intToBytes(gAccCountX));//写入文件长度
						   gOut_X.seek(gOut_X.length());
						   //写ACC_Y数据
						   //gOut_Y.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
						   gOut_Y.write(dataTypeAccY, 0, dataTypeAccY.length);
						   gOut_Y.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
						   gOut_Y.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//写ACC_Y数据
						   gAccCountY += ecgDataEntity.getAccPacket().getAccAxisY().length;//保存ACC_Y数据长度
						   gOut_Y.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_Y.write(TypeConversion.intToBytes(gAccCountY));//写入文件长度
						   gOut_Y.seek(gOut_Y.length());
						   //写ACC_Z数据
						   //gOut_Z.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
						   gOut_Z.write(dataTypeAccZ, 0, dataTypeAccZ.length);
						   gOut_Z.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
						   gOut_Z.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//写ACC_Z数据
						   gAccCountZ += ecgDataEntity.getAccPacket().getAccAxisZ().length;//保存ACC_Z数据长度
						   gOut_Z.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_Z.write(TypeConversion.intToBytes(gAccCountZ));//写入文件长度
						   gOut_Z.seek(gOut_Z.length());					   
						  
						   gCountPackets++;//已经保存的数据包的个数计数器,判断是否超过40分钟用
						   
					   }catch(Exception e){
						   logger.debug("Create new File failed!");
					   }
					   
					   
				   }else{//小于40分钟
					   
					   if((ecgDataEntity.getPacketHead().getPacketStatus()[1]&0x04) == 0x04 ){//历史数据结束
						   String finalPathFile = PATH_SD + gCurrentTime+"LC.dat";//最终合并后的文件的路径
						   combineFile(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z, finalPathFile);//合并四个临时文件
						   delete(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z);//删除临时文件
						   clear(true);//清除当前计数器
						   return true;
					   }
					   try{
						   //写ECG数据
						   gOut_Ecg.seek(gOut_Ecg.length());
						   gOut_Ecg.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//写Ecg数据
						   gEcgCount += ecgDataEntity.getEcgPacket().getEcgData().length;//保存ECG数据长度
						   gOut_Ecg.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_Ecg.write(TypeConversion.intToBytes(gEcgCount));//写入文件长度
						   gOut_Ecg.seek(gOut_Ecg.length());
						   //写ACC_X数据
						   gOut_X.seek(gOut_X.length());
						   gOut_X.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//写ACC_x数据
						   gAccCountX += ecgDataEntity.getAccPacket().getAccAxisX().length;//保存ACC_X数据长度
						   gOut_X.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_X.write(TypeConversion.intToBytes(gAccCountX));//写入文件长度
						   gOut_X.seek(gOut_X.length());
						   //写ACC_Y数据
						   gOut_Y.seek(gOut_Y.length());
						   gOut_Y.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//写ACC_Y数据
						   gAccCountY += ecgDataEntity.getAccPacket().getAccAxisY().length;//保存ACC_Y数据长度
						   gOut_Y.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_Y.write(TypeConversion.intToBytes(gAccCountY));//写入文件长度
						   gOut_Y.seek(gOut_Y.length());
						   //写ACC_Z数据
						   gOut_Z.seek(gOut_Z.length());
						   gOut_Z.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//写ACC_Z数据
						   gAccCountZ += ecgDataEntity.getAccPacket().getAccAxisZ().length;//保存ACC_Z数据长度
						   gOut_Z.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_Z.write(TypeConversion.intToBytes(gAccCountZ));//写入文件长度
						   gOut_Z.seek(gOut_Z.length());					   
						  
						   gCountPackets++;//已经保存的数据包的个数计数器,判断是否超过40分钟用
						   
					   }catch(Exception e){
						   logger.debug("Save File failed!");
					   }
				   					   
				   }
				   				   
			   }
			   					   		   
		   }else{
			   logger.debug("The Packets is Error");
		   }
	   }else{//当前数据
		   if((ecgDataEntity.getPacketHead().getPacketStatus()[1]&0x01) == 0x01 ){//当前触摸数据
			   if(ecgDataEntity.isFirstData){//当前触摸数据开始
				   gCurrentTimeEx = long2Date(TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0)*1000);//获取第一个数据的时间
				   gFilePathEcgEx = PATH_SD + gCurrentTimeEx+"_Ecg.dat";
				   gFilePathAcc_XEx = PATH_SD + gCurrentTimeEx+"_X.dat";
				   gFilePathAcc_YEx = PATH_SD + gCurrentTimeEx+"_Y.dat";
				   gFilePathAcc_ZEx = PATH_SD + gCurrentTimeEx+"_Z.dat";
				   
				   try{
					   gOut_EcgEx = new RandomAccessFile(new File(gFilePathEcgEx),"rw"); 
					   gOut_EcgEx.seek(gOut_EcgEx.length());
					   gOut_XEx = new RandomAccessFile(new File(gFilePathAcc_XEx),"rw");
					   gOut_XEx.seek(gOut_XEx.length());
					   gOut_YEx = new RandomAccessFile(new File(gFilePathAcc_YEx),"rw");
					   gOut_YEx.seek(gOut_YEx.length());
					   gOut_ZEx = new RandomAccessFile(new File(gFilePathAcc_ZEx),"rw");
					   gOut_ZEx.seek(gOut_ZEx.length());
					   //写数据库 7为导联式数据，8为触摸式数据
					   ECG_Android.gWriteRecord.insertRecord(this.getUserId(), 1000*TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0) , PATH_SD+gCurrentTimeEx+".dat" , 8, 0, 0);
					   gEcgDataHeadEx = new EcgDataHead();
					   
					   
					   //构造数据包头
					   ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);
					  
					   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	数据类型
					   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	数据类型
					   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	数据类型
					   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	数据类型
					   
					   //写ECG数据
					   gEcgDataHeadEx.setEcgDataType(ecgDataEntity);   //设置Ecg数据类型
					   gEcgDataHeadEx.setdataLength(0);    //设置数据长度				   
					  //gOut_EcgEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);//写数据类型
					   gOut_EcgEx.write(dataTypeEcg, 0, dataTypeEcg.length);
					   gOut_EcgEx.write(gEcgDataHeadEx.getDataLength(), 0, gEcgDataHeadEx.getDataLength().length);//写数据长度
					   gOut_EcgEx.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//写Ecg数据
					   gEcgCountEx += ecgDataEntity.getEcgPacket().getEcgData().length;//保存ECG数据长度
					   gOut_EcgEx.seek(4);//移动到保存数据长度位置，写入byte[4];
					   gOut_EcgEx.write(TypeConversion.intToBytes(gEcgCountEx));//写入文件长度
					   gOut_EcgEx.seek(gOut_EcgEx.length());
					   //写ACC_X数据
					 //  gEcgDataHeadEx.setAccDataType(ecgDataEntity);
					   gEcgDataHeadEx.setdataLength(0);
					   //gOut_XEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
					   gOut_XEx.write(dataTypeAccX, 0, dataTypeAccX.length);
					   gOut_XEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
					   gOut_XEx.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//写ACC_x数据
					   gAccCountXEx += ecgDataEntity.getAccPacket().getAccAxisX().length;//保存ACC_X数据长度
					   gOut_XEx.seek(4);//移动到保存数据长度位置，写入byte[4];
					   gOut_XEx.write(TypeConversion.intToBytes(gAccCountXEx));//写入文件长度
					   gOut_XEx.seek(gOut_XEx.length());
					   //写ACC_Y数据
					   //gOut_YEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
					   gOut_YEx.write(dataTypeAccY, 0, dataTypeAccY.length);
					   gOut_YEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
					   gOut_YEx.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//写ACC_Y数据
					   gAccCountYEx += ecgDataEntity.getAccPacket().getAccAxisY().length;//保存ACC_Y数据长度
					   gOut_YEx.seek(4);//移动到保存数据长度位置，写入byte[4];
					   gOut_YEx.write(TypeConversion.intToBytes(gAccCountYEx));//写入文件长度
					   gOut_YEx.seek(gOut_YEx.length());
					   //写ACC_Z数据
					   //gOut_ZEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
					   gOut_ZEx.write(dataTypeAccZ, 0, dataTypeAccZ.length);
					   gOut_ZEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
					   gOut_ZEx.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//写ACC_Z数据
					   gAccCountZEx += ecgDataEntity.getAccPacket().getAccAxisZ().length;//保存ACC_Z数据长度
					   gOut_ZEx.seek(4);//移动到保存数据长度位置，写入byte[4];
					   gOut_ZEx.write(TypeConversion.intToBytes(gAccCountZEx));//写入文件长度
					   gOut_ZEx.seek(gOut_ZEx.length());					   
					  
					   gCountPacketsEx++;//已经保存的数据包的个数计数器,判断是否超过40分钟用
					   
				   }catch(Exception e){
					   logger.debug("Create new File failed!");
				   }
				   
			   }else{//当前触摸数据  不是开始
				   if(gCountPacketsEx>=TIME_LIMIT){//大于40分钟
					   String finalPathFile = PATH_SD + gCurrentTimeEx+".dat";//最终合并后的文件的路径
					   combineFile(gFilePathEcgEx, gFilePathAcc_XEx, gFilePathAcc_YEx, gFilePathAcc_ZEx, finalPathFile);//合并四个临时文件
					   delete(gFilePathEcgEx, gFilePathAcc_XEx, gFilePathAcc_YEx, gFilePathAcc_ZEx);//删除临时文件
					   gCurrentTimeEx = addCurrentTime(gCurrentTimeEx, (gCountPacketsEx));//当前时间增加40分钟
					   clear(false);//清除当前计数器
					   gFilePathEcgEx = PATH_SD + gCurrentTimeEx+"_Ecg.dat";
					   gFilePathAcc_XEx = PATH_SD + gCurrentTimeEx+"_X.dat";
					   gFilePathAcc_YEx = PATH_SD + gCurrentTimeEx+"_Y.dat";
					   gFilePathAcc_ZEx = PATH_SD + gCurrentTimeEx+"_Z.dat";
					   try{
						   gOut_EcgEx = new RandomAccessFile(new File(gFilePathEcgEx),"rw"); 
						   gOut_EcgEx.seek(gOut_EcgEx.length());
						   gOut_XEx = new RandomAccessFile(new File(gFilePathAcc_XEx),"rw");
						   gOut_XEx.seek(gOut_XEx.length());
						   gOut_YEx = new RandomAccessFile(new File(gFilePathAcc_YEx),"rw");
						   gOut_YEx.seek(gOut_YEx.length());
						   gOut_ZEx = new RandomAccessFile(new File(gFilePathAcc_ZEx),"rw");
						   gOut_ZEx.seek(gOut_ZEx.length());
						   //写数据库 7为导联式数据，8为触摸式数据
						   ECG_Android.gWriteRecord.insertRecord(this.getUserId(), 1000*TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0) , PATH_SD+gCurrentTimeEx+".dat" , 8, 0, 0);
						   gEcgDataHeadEx = new EcgDataHead();
						   
						   
						   //构造数据包头
						   ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);
						  
						   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	数据类型
						   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	数据类型
						   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	数据类型
						   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	数据类型
						   
						   
						   //写ECG数据
						   gEcgDataHeadEx.setEcgDataType(ecgDataEntity);   //设置Ecg数据类型
						   gEcgDataHeadEx.setdataLength(0);    //设置数据长度				   
						   //gOut_EcgEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);//写数据类型
						   gOut_EcgEx.write(dataTypeEcg, 0, dataTypeEcg.length);
						   gOut_EcgEx.write(gEcgDataHeadEx.getDataLength(), 0, gEcgDataHeadEx.getDataLength().length);//写数据长度
						   gOut_EcgEx.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//写Ecg数据
						   gEcgCountEx += ecgDataEntity.getEcgPacket().getEcgData().length;//保存ECG数据长度
						   gOut_EcgEx.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_EcgEx.write(TypeConversion.intToBytes(gEcgCountEx));//写入文件长度
						   gOut_EcgEx.seek(gOut_EcgEx.length());
						   //写ACC_X数据
						 //  gEcgDataHeadEx.setAccDataType(ecgDataEntity);
						   gEcgDataHeadEx.setdataLength(0);
						   //gOut_XEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
						   gOut_XEx.write(dataTypeAccX, 0, dataTypeAccX.length);
						   gOut_XEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
						   gOut_XEx.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//写ACC_x数据
						   gAccCountXEx += ecgDataEntity.getAccPacket().getAccAxisX().length;//保存ACC_X数据长度
						   gOut_XEx.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_XEx.write(TypeConversion.intToBytes(gAccCountXEx));//写入文件长度
						   gOut_XEx.seek(gOut_XEx.length());
						   //写ACC_Y数据
						   //gOut_YEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
						   gOut_YEx.write(dataTypeAccY, 0, dataTypeAccY.length);
						   gOut_YEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
						   gOut_YEx.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//写ACC_Y数据
						   gAccCountYEx += ecgDataEntity.getAccPacket().getAccAxisY().length;//保存ACC_Y数据长度
						   gOut_YEx.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_YEx.write(TypeConversion.intToBytes(gAccCountYEx));//写入文件长度
						   gOut_YEx.seek(gOut_YEx.length());
						   //写ACC_Z数据
						   //gOut_ZEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
						   gOut_ZEx.write(dataTypeAccZ, 0, dataTypeAccZ.length);
						   gOut_ZEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
						   gOut_ZEx.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//写ACC_Z数据
						   gAccCountZEx += ecgDataEntity.getAccPacket().getAccAxisZ().length;//保存ACC_Z数据长度
						   gOut_ZEx.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_ZEx.write(TypeConversion.intToBytes(gAccCountZEx));//写入文件长度
						   gOut_ZEx.seek(gOut_ZEx.length());					   
						  
						   gCountPacketsEx++;//已经保存的数据包的个数计数器,判断是否超过40分钟用
						   
					   }catch(Exception e){
						   logger.debug("Create new File failed!");
					   }
					   
					   
				   }else{//小于40分钟
					   try{
						   //写ECG数据
						   gOut_EcgEx.seek(gOut_EcgEx.length());
						   gOut_EcgEx.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//写Ecg数据
						   gEcgCountEx += ecgDataEntity.getEcgPacket().getEcgData().length;//保存ECG数据长度
						   gOut_EcgEx.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_EcgEx.write(TypeConversion.intToBytes(gEcgCountEx));//写入文件长度
						   gOut_EcgEx.seek(gOut_EcgEx.length());
						   //写ACC_X数据
						   gOut_XEx.seek(gOut_XEx.length());
						   gOut_XEx.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//写ACC_x数据
						   gAccCountXEx += ecgDataEntity.getAccPacket().getAccAxisX().length;//保存ACC_X数据长度
						   gOut_XEx.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_XEx.write(TypeConversion.intToBytes(gAccCountXEx));//写入文件长度
						   gOut_XEx.seek(gOut_XEx.length());
						   //写ACC_Y数据
						   gOut_YEx.seek(gOut_YEx.length());
						   gOut_YEx.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//写ACC_Y数据
						   gAccCountYEx += ecgDataEntity.getAccPacket().getAccAxisY().length;//保存ACC_Y数据长度
						   gOut_YEx.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_YEx.write(TypeConversion.intToBytes(gAccCountYEx));//写入文件长度
						   gOut_YEx.seek(gOut_YEx.length());
						   //写ACC_Z数据
						   gOut_ZEx.seek(gOut_ZEx.length());
						   gOut_ZEx.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//写ACC_Z数据
						   gAccCountZEx += ecgDataEntity.getAccPacket().getAccAxisZ().length;//保存ACC_Z数据长度
						   gOut_ZEx.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_ZEx.write(TypeConversion.intToBytes(gAccCountZEx));//写入文件长度
						   gOut_ZEx.seek(gOut_ZEx.length());					   
						  
						   gCountPacketsEx++;//已经保存的数据包的个数计数器,判断是否超过40分钟用
						   
					   }catch(Exception e){
						   logger.debug("Save File failed!");
					   }
					   			   
				   }
				   				   
			   }
			   
		   }else if((ecgDataEntity.getPacketHead().getPacketStatus()[1]&0x01) == 0x00){//当前导联
			   if(ecgDataEntity.isFirstData){//当前导联数据开始
				   gCurrentTimeEx = long2Date(TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0)*1000);//获取第一个数据的时间
				   gFilePathEcgEx = PATH_SD + gCurrentTimeEx+"_Ecg.dat";
				   gFilePathAcc_XEx = PATH_SD + gCurrentTimeEx+"_X.dat";
				   gFilePathAcc_YEx = PATH_SD + gCurrentTimeEx+"_Y.dat";
				   gFilePathAcc_ZEx = PATH_SD + gCurrentTimeEx+"_Z.dat";
				   
				   try{
					   gOut_EcgEx = new RandomAccessFile(new File(gFilePathEcgEx),"rw"); 
					   gOut_EcgEx.seek(gOut_EcgEx.length());
					   gOut_XEx = new RandomAccessFile(new File(gFilePathAcc_XEx),"rw");
					   gOut_XEx.seek(gOut_XEx.length());
					   gOut_YEx = new RandomAccessFile(new File(gFilePathAcc_YEx),"rw");
					   gOut_YEx.seek(gOut_YEx.length());
					   gOut_ZEx = new RandomAccessFile(new File(gFilePathAcc_ZEx),"rw");
					   gOut_ZEx.seek(gOut_ZEx.length());
					   //写数据库 7为导联式数据，8为触摸式数据
					   ECG_Android.gWriteRecord.insertRecord(this.getUserId(), 1000*TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0) , PATH_SD+gCurrentTimeEx+".dat" , 7, 0, 0);
					   gEcgDataHeadEx = new EcgDataHead();
					   
					   
					   //构造数据包头
					   ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);
					  
					   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	数据类型
					   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	数据类型
					   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	数据类型
					   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	数据类型
					   
					   
					   //写ECG数据
					   gEcgDataHeadEx.setEcgDataType(ecgDataEntity);   //设置Ecg数据类型
					   gEcgDataHeadEx.setdataLength(0);    //设置数据长度				   
					  // gOut_EcgEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);//写数据类型
					   gOut_EcgEx.write(dataTypeEcg, 0, dataTypeEcg.length);
					   gOut_EcgEx.write(gEcgDataHeadEx.getDataLength(), 0, gEcgDataHeadEx.getDataLength().length);//写数据长度
					   gOut_EcgEx.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//写Ecg数据
					   gEcgCountEx += ecgDataEntity.getEcgPacket().getEcgData().length;//保存ECG数据长度
					   gOut_EcgEx.seek(4);//移动到保存数据长度位置，写入byte[4];
					   gOut_EcgEx.write(TypeConversion.intToBytes(gEcgCountEx));//写入文件长度
					   gOut_EcgEx.seek(gOut_EcgEx.length());
					   //写ACC_X数据
					 //  gEcgDataHeadEx.setAccDataType(ecgDataEntity);
					   gEcgDataHeadEx.setdataLength(0);
					   //gOut_XEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
					   gOut_XEx.write(dataTypeAccX, 0, dataTypeAccX.length);
					   gOut_XEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
					   gOut_XEx.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//写ACC_x数据
					   gAccCountXEx += ecgDataEntity.getAccPacket().getAccAxisX().length;//保存ACC_X数据长度
					   gOut_XEx.seek(4);//移动到保存数据长度位置，写入byte[4];
					   gOut_XEx.write(TypeConversion.intToBytes(gAccCountXEx));//写入文件长度
					   
					   gOut_XEx.seek(gOut_XEx.length());
					   //写ACC_Y数据
					   //gOut_YEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
					   gOut_YEx.write(dataTypeAccY, 0, dataTypeAccY.length);
					   gOut_YEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
					   gOut_YEx.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//写ACC_Y数据
					   gAccCountYEx += ecgDataEntity.getAccPacket().getAccAxisY().length;//保存ACC_Y数据长度
					   gOut_YEx.seek(4);//移动到保存数据长度位置，写入byte[4];
					   gOut_YEx.write(TypeConversion.intToBytes(gAccCountYEx));//写入文件长度
					   gOut_YEx.seek(gOut_YEx.length());
					   //写ACC_Z数据
					  // gOut_ZEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
					   gOut_ZEx.write(dataTypeAccZ, 0, dataTypeAccZ.length);
					   gOut_ZEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
					   gOut_ZEx.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//写ACC_Z数据
					   gAccCountZEx += ecgDataEntity.getAccPacket().getAccAxisZ().length;//保存ACC_Z数据长度
					   gOut_ZEx.seek(4);//移动到保存数据长度位置，写入byte[4];
					   //gOut_ZEx.writeInt(gAccCountZEx);//写入文件长度
					   gOut_ZEx.write(TypeConversion.intToBytes(gAccCountZEx));//写入文件长度
					   
					   gOut_ZEx.seek(gOut_ZEx.length());					   
					  
					   gCountPacketsEx++;//已经保存的数据包的个数计数器,判断是否超过40分钟用
					   
				   }catch(Exception e){
					   logger.debug("Create new File failed!");
				   }
				   
			   }else{//当前导联数据  不是开始
				   if(gCountPacketsEx>=TIME_LIMIT){//大于40分钟
					   String finalPathFile = PATH_SD + gCurrentTimeEx+".dat";//最终合并后的文件的路径
					   combineFile(gFilePathEcgEx, gFilePathAcc_XEx, gFilePathAcc_YEx, gFilePathAcc_ZEx, finalPathFile);//合并四个临时文件
					   delete(gFilePathEcgEx, gFilePathAcc_XEx, gFilePathAcc_YEx, gFilePathAcc_ZEx);//删除临时文件
					   gCurrentTimeEx = addCurrentTime(gCurrentTimeEx, (gCountPacketsEx));//当前时间增加40分钟
					   
					   clear(false);//清除当前计数器
					   gFilePathEcgEx = PATH_SD + gCurrentTimeEx+"_Ecg.dat";
					   gFilePathAcc_XEx = PATH_SD + gCurrentTimeEx+"_X.dat";
					   gFilePathAcc_YEx = PATH_SD + gCurrentTimeEx+"_Y.dat";
					   gFilePathAcc_ZEx = PATH_SD + gCurrentTimeEx+"_Z.dat";
					   
					   try{
						   gOut_EcgEx = new RandomAccessFile(new File(gFilePathEcgEx),"rw"); 
						   gOut_EcgEx.seek(gOut_EcgEx.length());
						   gOut_XEx = new RandomAccessFile(new File(gFilePathAcc_XEx),"rw");
						   gOut_XEx.seek(gOut_XEx.length());
						   gOut_YEx = new RandomAccessFile(new File(gFilePathAcc_YEx),"rw");
						   gOut_YEx.seek(gOut_YEx.length());
						   gOut_ZEx = new RandomAccessFile(new File(gFilePathAcc_ZEx),"rw");
						   gOut_ZEx.seek(gOut_ZEx.length());
						   //写数据库 7为导联式数据，8为触摸式数据
						   ECG_Android.gWriteRecord.insertRecord(this.getUserId(), date2Long(gCurrentTimeEx) , PATH_SD+gCurrentTimeEx+".dat" , 7, 0, 0);
						   gEcgDataHeadEx = new EcgDataHead();
						   
						   
						   //构造数据包头
//						   ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);
//						  
//						   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	数据类型
//						   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	数据类型
//						   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	数据类型
//						   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	数据类型
						   
						   
						   //写ECG数据
						   gEcgDataHeadEx.setEcgDataType(ecgDataEntity);   //设置Ecg数据类型
						   gEcgDataHeadEx.setdataLength(0);    //设置数据长度				   
						  // gOut_EcgEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);//写数据类型
						   gOut_EcgEx.write(dataTypeEcg,0, dataTypeEcg.length);
						   logger.info("Write ECG type leng="+dataTypeEcg.length+"");
						   gOut_EcgEx.write(gEcgDataHeadEx.getDataLength(), 0, gEcgDataHeadEx.getDataLength().length);//写数据长度
						   gOut_EcgEx.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//写Ecg数据
						   gEcgCountEx += ecgDataEntity.getEcgPacket().getEcgData().length;//保存ECG数据长度
						   gOut_EcgEx.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_EcgEx.write(TypeConversion.intToBytes(gEcgCountEx));//写入文件长度
						   logger.info("Write ECG length: leng="+gOut_EcgEx.length()+"");
						   gOut_EcgEx.seek(gOut_EcgEx.length());
						   //写ACC_X数据
						//   gEcgDataHeadEx.setAccDataType(ecgDataEntity);
						   gEcgDataHeadEx.setdataLength(0);
						   //gOut_XEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
						   gOut_XEx.write(dataTypeAccX, 0, dataTypeAccX.length);
						   logger.info("Write ACC_X length: leng="+dataTypeAccX.length+"");
						   gOut_XEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
						   gOut_XEx.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//写ACC_x数据
						   gAccCountXEx += ecgDataEntity.getAccPacket().getAccAxisX().length;//保存ACC_X数据长度
						   gOut_XEx.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_XEx.write(TypeConversion.intToBytes(gAccCountXEx));//写入文件长度
						   gOut_XEx.seek(gOut_XEx.length());
						   //写ACC_Y数据
						   //gOut_YEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
						   gOut_YEx.write(dataTypeAccY, 0, dataTypeAccY.length);
						   logger.info("Write ACC_Y type: leng="+dataTypeAccY.length+"");
						   gOut_YEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
						   gOut_YEx.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//写ACC_Y数据
						   gAccCountYEx += ecgDataEntity.getAccPacket().getAccAxisY().length;//保存ACC_Y数据长度
						   gOut_YEx.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_YEx.write(TypeConversion.intToBytes(gAccCountYEx));//写入文件长度
						   logger.info("Write ACC_Y length: leng="+dataTypeAccX.length+"");
						   gOut_YEx.seek(gOut_YEx.length());
						   //写ACC_Z数据
						   //gOut_ZEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
						   gOut_ZEx.write(dataTypeAccZ, 0, dataTypeAccZ.length);
						   logger.info("Write ACC_Z type: leng="+dataTypeAccY.length+"");
						   gOut_ZEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
						   gOut_ZEx.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//写ACC_Z数据
						   gAccCountZEx += ecgDataEntity.getAccPacket().getAccAxisZ().length;//保存ACC_Z数据长度
						   gOut_ZEx.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_ZEx.write(TypeConversion.intToBytes(gAccCountZEx));//写入文件长度
						   gOut_ZEx.seek(gOut_ZEx.length());					   
						  
						   gCountPacketsEx++;//已经保存的数据包的个数计数器,判断是否超过40分钟用
						   
					   }catch(Exception e){
						   logger.debug("Create new File failed!");
					   }
					   
					   
				   }else{//小于40分钟
					   try{
						   //写ECG数据
						   gOut_EcgEx.seek(gOut_EcgEx.length());
						   gOut_EcgEx.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//写Ecg数据
						   gEcgCountEx += ecgDataEntity.getEcgPacket().getEcgData().length;//保存ECG数据长度
						   gOut_EcgEx.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_EcgEx.write(TypeConversion.intToBytes(gEcgCountEx));//写入文件长度
						   gOut_EcgEx.seek(gOut_EcgEx.length());
						   //写ACC_X数据
						   gOut_XEx.seek(gOut_XEx.length());
						   gOut_XEx.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//写ACC_x数据
						   gAccCountXEx += ecgDataEntity.getAccPacket().getAccAxisX().length;//保存ACC_X数据长度
						   gOut_XEx.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_XEx.write(TypeConversion.intToBytes(gAccCountXEx));//写入文件长度
						   gOut_XEx.seek(gOut_XEx.length());
						   //写ACC_Y数据
						   gOut_YEx.seek(gOut_YEx.length());
						   gOut_YEx.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//写ACC_Y数据
						   gAccCountYEx += ecgDataEntity.getAccPacket().getAccAxisY().length;//保存ACC_Y数据长度
						   gOut_YEx.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_YEx.write(TypeConversion.intToBytes(gAccCountYEx));//写入文件长度
						   gOut_YEx.seek(gOut_YEx.length());
						   //写ACC_Z数据
						   gOut_ZEx.seek(gOut_ZEx.length());
						   gOut_ZEx.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//写ACC_Z数据
						   gAccCountZEx += ecgDataEntity.getAccPacket().getAccAxisZ().length;//保存ACC_Z数据长度
						   gOut_ZEx.seek(4);//移动到保存数据长度位置，写入byte[4];
						   gOut_ZEx.write(TypeConversion.intToBytes(gAccCountZEx));//写入文件长度
						   gOut_ZEx.seek(gOut_ZEx.length());					   
						  
						   gCountPacketsEx++;//已经保存的数据包的个数计数器,判断是否超过40分钟用
						   
					   }catch(Exception e){
						   logger.debug("Save File failed!");
					   }
				   					   
				   }
				   				   
			   }
			   					   		   
		   }else{
			   logger.debug("The Packets is Error");
			   return false;
		   }
	   }
	   
		return true;
  }
	
   /**
   *
   * 删除四个临时文件
   *
   */
     public static void delete(final String sourceEcg, final String sourceAcc_x,final String sourceAcc_y,final String sourceAcc_z){
    	 try {   
  		   File ecg = new File(sourceEcg);
  		   ecg.delete();
  		   File acc_x = new File(sourceAcc_x);
  		   acc_x.delete();
  		   File acc_y = new File(sourceAcc_y);
  		   acc_y.delete();
  		   File acc_z = new File(sourceAcc_z);
  		   acc_z.delete();
  		   
    	  }catch(Exception e){
  			  logger.debug("Delete File failed!");
  		  }
    	 
     }
   
     /**
      *
      * 合并四个临时文件
      *
      */
	 public static void combineFile( final String sourceEcg, final String sourceAcc_x,final String sourceAcc_y,final String sourceAcc_z,final String destEcg) {
		   try {   
		   File inEcg = new File(sourceEcg);
		  
		   File inAcc_x = new File(sourceAcc_x);
		  
		   File inAcc_y = new File(sourceAcc_y);
		  
		   File inAcc_z = new File(sourceAcc_z);
		  
		   File out = new File(destEcg);
		   FileInputStream inFile1 = new FileInputStream(inEcg);
		   FileInputStream inFile2 = new FileInputStream(inAcc_x);
		   FileInputStream inFile3 = new FileInputStream(inAcc_y);
		   FileInputStream inFile4 = new FileInputStream(inAcc_z);
		  
		   
		   FileOutputStream outFile = new FileOutputStream(out);
		   byte[] buffer = new byte[1024];
		   int i = 0;
		   while ((i = inFile1.read(buffer)) != -1) {
		   outFile.write(buffer, 0, i);
		   }//end while
		   
		   i=0;
		   while ((i = inFile2.read(buffer)) != -1) {
			   outFile.write(buffer, 0, i);
			   }
		   i=0;
		   while ((i = inFile3.read(buffer)) != -1) {
			   outFile.write(buffer, 0, i);
			   }
		   i=0;
		   while ((i = inFile4.read(buffer)) != -1) {
			   outFile.write(buffer, 0, i);
			   }
		 
		   inFile1.close();
		   inFile2.close();
		   inFile3.close();
		   inFile4.close();
		   outFile.close();
		   }catch (Exception e) {
              logger.debug("合并文件出错！");
		   }
    }
	
	
	
	public ByteArrayOutputStream readEcgData(){
		
		return null;
	}
	
	
	
	 /**
     * 根据当前计数器，以当前保存时间加上时间增量
     * @author peng
     *
     */
	public String addCurrentTime(String current, int count){
		String addTime = null;
		long nTime;
		double fCount = 0.96*count;
		try{
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
			nTime = (long) ((sf.parse(current)).getTime() + fCount*1000);
			SimpleDateFormat sfEx = new SimpleDateFormat("yyyyMMddHHmmss");
			addTime = sfEx.format(new Date(nTime));	
			
		}catch(Exception e){
			logger.debug("addCurrentTime is Failed");
		}
		
		return addTime;
	}
	/***
	 * long转化为字符日期
	 * 
	 ***
     */
	public String long2Date(long time){   
		String strTime = null;
		try{
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
			strTime = sf.format(new Date(time));
		}catch(Exception e){
			logger.debug("Translate Data failed!");
		}
		return strTime;
	}
	
	/***
	 * long转化为字符日期
	 * 
	 ***
     */
	
	public long date2Long(String formatDate){
		long lTime = 0;//返回的时间，毫秒
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			lTime = (sf.parse(formatDate)).getTime();
		} catch (Exception e) {
			logger.debug("date2Long Error!"+e.getMessage());
		}
		return lTime;
	}
	
	
	/*
	 * 从XML中获取用户ID
	 * 
	*/
	
	public int getUserId(){
		int userId = 0;;
		String strId;
		EcgXmlFileOperate config =  new EcgXmlFileOperate("Device",ECGApplication.getInstance());
		try {
		
			strId = config.selectEcgXmlNode("PatientPkID").getParentNodeAttributeValue();
			userId = Integer.parseInt(strId);
			config.close();
		} catch (Exception e) {
			logger.debug("获取用户ID失败！");
		}
		//获取病人ID字符串;
		return userId;
	} 
	
	
	/*
	* 清除当前计数器
	*  
	*/
	public void clear(boolean type){//TRUE时清除历史数据计数器，FALSE时清除当前数据计数器
		if(type){
			 gEcgCount = 0;//ecg写入数据的字节数
			 gAccCountX = 0;//acc x写入数据的字节数
			 gAccCountY = 0;//acc y写入数据的字节数
			 gAccCountZ = 0;//acc z写入数据的字节数
			 gCountPackets = 0;//当前已经保存的包的个数
			
		}else{
			gEcgCountEx = 0;
			gAccCountXEx = 0;
			gAccCountYEx = 0;
			gAccCountZEx= 0;
			gCountPacketsEx = 0;
		}
		
	}
	
	
	/**
	 * 判断目录是否存在，不存在则创建文件夹，成功返回文件夹的路径，失败返回空
	 * @param filePath
	 */
	public String createDir(String filePath) {
		File fileDir = null; // 文件流变量
		boolean hasDir = false; // 标示文件流对象是否存在
		fileDir = new File(filePath); // 生成文件流对象
		hasDir = fileDir.exists(); // 判断文件流对象是否存在
		if (!hasDir) {
			String[] fileDirs = filePath.split("/");
			StringBuffer fileDirStr = new StringBuffer();
			for(int i=0;i<fileDirs.length;i++){
				fileDir = new File(fileDirStr.append("/").append(fileDirs[i]).toString());
				if(!fileDir.exists()){
					hasDir = fileDir.mkdir();
				}
			}
			//hasDir = fileDir.mkdir();
		}
		//判断是否成功
		if(!hasDir){
			filePath = null;
		}
		return filePath;
	}
	
	
	
	/*
	* 内部类，解析并构造要保存的文件的头
	*  
	*/
	public static class EcgDataHead{
		public  byte[] dataType = new byte[4];
		public  byte[] dataLength = new byte[4];
		public EcgDataHead(){}
		public  void setEcgDataType(EcgDataEntity ecgDataEntity){//设置ECG数据类型
		byte[] ecgType = new byte[2]; 
		byte[] ecgLead = new byte[3];
		ecgType = ecgDataEntity.getEcgPacket().getEcgDataType();//得到Type
		ecgLead = ecgDataEntity.getEcgPacket().getEcgDataLead();//得到Lead
		if(300==TypeConversion.bytesToShort(ecgType, 0)){
			this.dataType[0] = 0x01;
		}else if(360 == TypeConversion.bytesToShort(ecgType,0)){
			this.dataType[0] = 0x03;
		}else{
			logger.debug("未知ECG频率");
		}
	
		this.dataType[3] = ecgLead[0];
		this.dataType[2] = ecgLead[1];
		if(((ecgDataEntity.getPacketHead().getPacketStatus()[1] )&0x01) == 0x01){//如果为触摸式数据
			 this.dataType[1] = (byte) ((byte)ecgLead[2]|(byte)0x02);//ecglead最高两位被占用？？
		}else if(((ecgDataEntity.getPacketHead().getPacketStatus()[1] )&0x01) == 0x00){//导联式数据
			this.dataType[1] = ecgLead[2];
		}else{
				//数据包错误
			logger.error("The data is Invalid!");
		}
		}
		
        public  void setAccDataType(EcgDataEntity ecgDataEntity){//设置Acc数据类型
			byte[] accType = new byte[2];
			byte Axis = 0 ;
			accType = ecgDataEntity.getAccPacket().getAccType();
			Axis = ecgDataEntity.getAccPacket().getAccStatus();//包含Acc x,y,z哪几个导联的数据
			if(75 == TypeConversion.bytesToShort(accType, 0)){
				this.dataType[0] = 0x02;
			}else{
				logger.debug("未知ACC频率");
			}
			
			this.dataType[3] = Axis;//bit[5,6,7]表示包含哪几个方向的数据
			
		}
        
        public byte[] getDataType(){
        	return this.dataType;
        }
        
        public byte[] getDataLength(){
        	return this.dataLength;
        }
        
		public void setdataLength(int length){//设置数据长度
			
			this.dataLength = TypeConversion.intToBytes(length); 
		}
   }
}
