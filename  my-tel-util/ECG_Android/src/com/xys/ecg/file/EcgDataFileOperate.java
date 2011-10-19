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
	public final String PATH_SD = Environment.getExternalStorageDirectory().toString() + "/ecg/data/"; //��ȡSD��·��
	private final int TIME_LIMIT = 2500; //40���ӱ�����ĸ���������Ƶ��Ϊ300��һ�����ݰ����ֽ�����Ϊ288��288/300 = 0.96S
	public static String gCurrentTime = null;//��һ������ʱ��
	public static String gCurrentTimeEx = null;
	public static String gFilePathEcg = null;//ecg��ʷ����
	public static String gFilePathEcgEx = null;//ecg��ʷ����
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
	public int gEcgCount = 0;//ecgд�����ݵ��ֽ���
	public int gEcgCountEx = 0;//ecgд�����ݵ��ֽ���
	public int gAccCountX = 0;//acc xд�����ݵ��ֽ���
	public int gAccCountXEx= 0;//acc xд�����ݵ��ֽ���
	public int gAccCountY = 0;//acc yд�����ݵ��ֽ���
	public int gAccCountYEx = 0;//acc yд�����ݵ��ֽ���
	public int gAccCountZ = 0;//acc zд�����ݵ��ֽ���
	public int gAccCountZEx = 0;//acc zд�����ݵ��ֽ���
	public int gCountPackets = 0;//��ǰ�Ѿ�����İ��ĸ���
	public int gCountPacketsEx = 0;//��ǰ�Ѿ�����İ��ĸ���
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
	* �������ܣ�
	*           �����ļ���SD��
	* ���������
	*           
	* ���������
	*           
	* �� �� ֵ��
	*           
	* ��    ע��
	*           
	*/
   public boolean saveEcgData(EcgDataEntity ecgDataEntity){
	   //�˴����е����ж�
	   int batt =   ((byte)ecgDataEntity.getPacketHead().getPacketStatus()[0]>>1)&(byte)7;//ȥ���5λ
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
	   //�������ݰ�ͷ
	   byte[][] ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);  
	   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	��������
	   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	��������
	   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	��������
	   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	��������
	   
	   if(createDir(PATH_SD)==null){
		   logger.debug("Create Save directory failed!");
		   return false; 
	   }
			
	   if(ecgDataEntity.getIsHisDate()){//����ʷ����
		   if((ecgDataEntity.getPacketHead().getPacketStatus()[1]&0x01) == 0x01 ){//��ʷ��������
			   if(ecgDataEntity.isFirstData){//��ʷ�������ݿ�ʼ
				   gCurrentTime = long2Date(TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0)*1000);//��ȡ��һ�����ݵ�ʱ��
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
					   //д���ݿ� 7Ϊ����ʽ���ݣ�8Ϊ����ʽ����
					   ECG_Android.gWriteRecord.insertRecord(this.getUserId(), 1000*TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0) , PATH_SD+gCurrentTime+".dat" , 8, 0, 0);
					   gEcgDataHead = new EcgDataHead();
					   //дECG����
					   
					   
					   //�������ݰ�ͷ
					   ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);
					  
					   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	��������
					   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	��������
					   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	��������
					   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	��������

					   
					   
					  // gEcgDataHead.setEcgDataType(ecgDataEntity);   //����Ecg��������
					   gEcgDataHead.setdataLength(0);    //�������ݳ���				   
					  // gOut_Ecg.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);//д��������
					   gOut_Ecg.write(dataTypeEcg, 0, dataTypeEcg.length);   //дECG��������
					   gOut_Ecg.write(gEcgDataHead.getDataLength(), 0, gEcgDataHead.getDataLength().length);//д���ݳ���
					   gOut_Ecg.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//дEcg����
					   gEcgCount += ecgDataEntity.getEcgPacket().getEcgData().length;//����ECG���ݳ���
					   gOut_Ecg.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
					   gOut_Ecg.write(TypeConversion.intToBytes(gEcgCount));//д���ļ�����
					   gOut_Ecg.seek(gOut_Ecg.length());
					   //дACC_X����
					 //  gEcgDataHead.setAccDataType(ecgDataEntity);
					   gEcgDataHead.setdataLength(0);
					  // gOut_X.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
					   gOut_X.write(dataTypeAccX, 0, dataTypeAccX.length);    
					   gOut_X.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
					   gOut_X.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//дACC_x����
					   gAccCountX += ecgDataEntity.getAccPacket().getAccAxisX().length;//����ACC_X���ݳ���
					   gOut_X.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
					   gOut_X.write(TypeConversion.intToBytes(gAccCountX));//д���ļ�����
					   gOut_X.seek(gOut_X.length());
					   //дACC_Y����
					 //  gOut_Y.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
					   gOut_Y.write(dataTypeAccY, 0, dataTypeAccY.length);  
					   gOut_Y.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
					   gOut_Y.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//дACC_Y����
					   gAccCountY += ecgDataEntity.getAccPacket().getAccAxisY().length;//����ACC_Y���ݳ���
					   gOut_Y.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
					   gOut_Y.write(TypeConversion.intToBytes(gAccCountY));//д���ļ�����
					   gOut_Y.seek(gOut_Y.length());
					   //дACC_Z����
					  // gOut_Z.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
					   gOut_Z.write(dataTypeAccZ, 0, dataTypeAccZ.length);  
					   gOut_Z.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
					   gOut_Z.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//дACC_Z����
					   gAccCountZ += ecgDataEntity.getAccPacket().getAccAxisZ().length;//����ACC_Z���ݳ���
					   gOut_Z.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
					   gOut_Z.write(TypeConversion.intToBytes(gAccCountZ));//д���ļ�����
					   gOut_Z.seek(gOut_Z.length());					   
					  
					   gCountPackets++;//�Ѿ���������ݰ��ĸ���������,�ж��Ƿ񳬹�40������
					   
				   }catch(Exception e){
					   logger.debug("Create new File failed!");
				   }
				   
			   }else{//��ʷ��������  ���ǿ�ʼ
				   if((ecgDataEntity.getPacketHead().getPacketStatus()[1]&0x04) == 0x04 ){//��ʷ���ݽ���
					   String finalPathFile = PATH_SD + gCurrentTime+"LC.dat";//���պϲ�����ļ���·��
					   combineFile(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z, finalPathFile);//�ϲ��ĸ���ʱ�ļ�
					   delete(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z);//ɾ����ʱ�ļ�
					   clear(true);//�����ǰ������
					   return true;
				   }
				   
				   if(gCountPackets>=TIME_LIMIT){//����40����
					   String finalPathFile = PATH_SD + gCurrentTime+".dat";//���պϲ�����ļ���·��
					   combineFile(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z, finalPathFile);//�ϲ��ĸ���ʱ�ļ�
					   delete(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z);//ɾ����ʱ�ļ�
					   gCurrentTime = addCurrentTime(gCurrentTime, gCountPackets);//��ǰʱ������40����
					   clear(true);//�����ǰ������
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
						   //д���ݿ� 7Ϊ����ʽ���ݣ�8Ϊ����ʽ����
						   ECG_Android.gWriteRecord.insertRecord(this.getUserId(), 1000*TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0) , PATH_SD+gCurrentTime+".dat" , 8, 0, 0);
						   gEcgDataHead = new EcgDataHead();
						   
						   
						   //�������ݰ�ͷ
						   ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);
						   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	��������
						   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	��������
						   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	��������
						   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	��������
						   
						   //дECG����
						 //  gEcgDataHead.setEcgDataType(ecgDataEntity);   //����Ecg��������
						   gEcgDataHead.setdataLength(0);    //�������ݳ���				   
						 //  gOut_Ecg.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);//д��������
						   gOut_Ecg.write(dataTypeEcg, 0, dataTypeEcg.length);
						   gOut_Ecg.write(gEcgDataHead.getDataLength(), 0, gEcgDataHead.getDataLength().length);//д���ݳ���
						   gOut_Ecg.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//дEcg����
						   gEcgCount += ecgDataEntity.getEcgPacket().getEcgData().length;//����ECG���ݳ���
						   gOut_Ecg.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_Ecg.write(TypeConversion.intToBytes(gEcgCount));//д���ļ�����
						   gOut_Ecg.seek(gOut_Ecg.length());
						   //дACC_X����
						  // gEcgDataHead.setAccDataType(ecgDataEntity);
						   gEcgDataHead.setdataLength(0);
						 //  gOut_X.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
						   gOut_X.write(dataTypeAccX, 0, dataTypeAccX.length);
						   gOut_X.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
						   gOut_X.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//дACC_x����
						   gAccCountX += ecgDataEntity.getAccPacket().getAccAxisX().length;//����ACC_X���ݳ���
						   gOut_X.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_X.write(TypeConversion.intToBytes(gAccCountX));//д���ļ�����
						   gOut_X.seek(gOut_X.length());
						   //дACC_Y����
						   //gOut_Y.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
						   gOut_Y.write(dataTypeAccY, 0, dataTypeAccY.length);
						   gOut_Y.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
						   gOut_Y.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//дACC_Y����
						   gAccCountY += ecgDataEntity.getAccPacket().getAccAxisY().length;//����ACC_Y���ݳ���
						   gOut_Y.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_Y.write(TypeConversion.intToBytes(gAccCountY));//д���ļ�����
						   gOut_Y.seek(gOut_Y.length());
						   //дACC_Z����
						  // gOut_Z.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
						   gOut_Z.write(dataTypeAccZ, 0, dataTypeAccZ.length);
						   gOut_Z.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
						   gOut_Z.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//дACC_Z����
						   gAccCountZ += ecgDataEntity.getAccPacket().getAccAxisZ().length;//����ACC_Z���ݳ���
						   gOut_Z.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_Z.write(TypeConversion.intToBytes(gAccCountZ));//д���ļ�����
						   gOut_Z.seek(gOut_Z.length());					   
						  
						   gCountPackets++;//�Ѿ���������ݰ��ĸ���������,�ж��Ƿ񳬹�40������
						   
					   }catch(Exception e){
						   logger.debug("Create new File failed!");
					   }
					   
					   
				   }else{//С��40����
					   if((ecgDataEntity.getPacketHead().getPacketStatus()[1]&0x04) == 0x04 ){//��ʷ���ݽ���
						   String finalPathFile = PATH_SD + gCurrentTime+"LC.dat";//���պϲ�����ļ���·��
						   combineFile(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z, finalPathFile);//�ϲ��ĸ���ʱ�ļ�
						   delete(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z);//ɾ����ʱ�ļ�
						   clear(true);//�����ǰ������
						   return true;
					   }
					   
					   try{
						   //дECG����
						   gOut_Ecg.seek(gOut_Ecg.length());
						   gOut_Ecg.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//дEcg����
						   gEcgCount += ecgDataEntity.getEcgPacket().getEcgData().length;//����ECG���ݳ���
						   gOut_Ecg.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_Ecg.write(TypeConversion.intToBytes(gEcgCount));//д���ļ�����
						   gOut_Ecg.seek(gOut_Ecg.length());
						   //дACC_X����
						   gOut_X.seek(gOut_X.length());
						   gOut_X.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//дACC_x����
						   gAccCountX += ecgDataEntity.getAccPacket().getAccAxisX().length;//����ACC_X���ݳ���
						   gOut_X.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_X.write(TypeConversion.intToBytes(gAccCountX));//д���ļ�����
						   gOut_X.seek(gOut_X.length());
						   //дACC_Y����
						   gOut_Y.seek(gOut_Y.length());
						   gOut_Y.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//дACC_Y����
						   gAccCountY += ecgDataEntity.getAccPacket().getAccAxisY().length;//����ACC_Y���ݳ���
						   gOut_Y.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_Y.write(TypeConversion.intToBytes(gAccCountY));//д���ļ�����
						   gOut_Y.seek(gOut_Y.length());
						   //дACC_Z����
						   gOut_Z.seek(gOut_Z.length());
						   gOut_Z.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//дACC_Z����
						   gAccCountZ += ecgDataEntity.getAccPacket().getAccAxisZ().length;//����ACC_Z���ݳ���
						   gOut_Z.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_Z.write(TypeConversion.intToBytes(gAccCountZ));//д���ļ�����
						   gOut_Z.seek(gOut_Z.length());					   
						  
						   gCountPackets++;//�Ѿ���������ݰ��ĸ���������,�ж��Ƿ񳬹�40������
						   
					   }catch(Exception e){
						   logger.debug("Save File failed!");
					   }
					   			   
				   }
				   				   
			   }
			   
		   }else if((ecgDataEntity.getPacketHead().getPacketStatus()[1]&0x01) == 0x00){//��ʷ����
			   if(ecgDataEntity.isFirstData){//��ʷ�������ݿ�ʼ
				   gCurrentTime = long2Date(TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0)*1000);//��ȡ��һ�����ݵ�ʱ��
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
					   //д���ݿ� 7Ϊ����ʽ���ݣ�8Ϊ����ʽ����
					   ECG_Android.gWriteRecord.insertRecord(this.getUserId(), 1000*TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0) , PATH_SD+gCurrentTime+".dat" , 7, 0, 0);
					   gEcgDataHead = new EcgDataHead();
					   //дECG����
					   
					   //�������ݰ�ͷ
					   ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);
					  
					   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	��������
					   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	��������
					   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	��������
					   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	��������
					   
					   gEcgDataHead.setEcgDataType(ecgDataEntity);   //����Ecg��������
					   gEcgDataHead.setdataLength(0);    //�������ݳ���				   
					   //gOut_Ecg.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);//д��������
					   gOut_Ecg.write(dataTypeEcg, 0, dataTypeEcg.length);
					   gOut_Ecg.write(gEcgDataHead.getDataLength(), 0, gEcgDataHead.getDataLength().length);//д���ݳ���
					   gOut_Ecg.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//дEcg����
					   gEcgCount += ecgDataEntity.getEcgPacket().getEcgData().length;//����ECG���ݳ���
					   gOut_Ecg.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
					   gOut_Ecg.write(TypeConversion.intToBytes(gEcgCount));//д���ļ�����
					   gOut_Ecg.seek(gOut_Ecg.length());
					   //дACC_X����
					 //  gEcgDataHead.setAccDataType(ecgDataEntity);
					   gEcgDataHead.setdataLength(0);
					   //gOut_X.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
					   gOut_X.write(dataTypeAccX, 0, dataTypeAccX.length);
					   gOut_X.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
					   gOut_X.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//дACC_x����
					   gAccCountX += ecgDataEntity.getAccPacket().getAccAxisX().length;//����ACC_X���ݳ���
					   gOut_X.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
					   gOut_X.write(TypeConversion.intToBytes(gAccCountX));//д���ļ�����
					   gOut_X.seek(gOut_X.length());
					   //дACC_Y����
					   //gOut_Y.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
					   gOut_Y.write(dataTypeAccY, 0, dataTypeAccY.length);
					   gOut_Y.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
					   gOut_Y.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//дACC_Y����
					   gAccCountY += ecgDataEntity.getAccPacket().getAccAxisY().length;//����ACC_Y���ݳ���
					   gOut_Y.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
					   gOut_Y.write(TypeConversion.intToBytes(gAccCountY));//д���ļ�����
					   gOut_Y.seek(gOut_Y.length());
					   //дACC_Z����
					   //gOut_Z.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
					   gOut_Z.write(dataTypeAccZ, 0, dataTypeAccZ.length);
					   gOut_Z.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
					   gOut_Z.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//дACC_Z����
					   gAccCountZ += ecgDataEntity.getAccPacket().getAccAxisZ().length;//����ACC_Z���ݳ���
					   gOut_Z.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
					   gOut_Z.write(TypeConversion.intToBytes(gAccCountZ));//д���ļ�����
					   gOut_Z.seek(gOut_Z.length());					   
					  
					   gCountPackets++;//�Ѿ���������ݰ��ĸ���������,�ж��Ƿ񳬹�40������
					   
				   }catch(Exception e){
					   logger.debug("Create new File failed!");
				   }
				   
			   }else{//��ʷ��������  ���ǿ�ʼ
				   if((ecgDataEntity.getPacketHead().getPacketStatus()[1]&0x04) == 0x04 ){//��ʷ���ݽ���
					   String finalPathFile = PATH_SD + gCurrentTime+"LC.dat";//���պϲ�����ļ���·��
					   combineFile(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z, finalPathFile);//�ϲ��ĸ���ʱ�ļ�
					   delete(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z);//ɾ����ʱ�ļ�
					   clear(true);//�����ǰ������
					   return true;
				   }
				   
				   if(gCountPackets>=TIME_LIMIT){//����40����
					   String finalPathFile = PATH_SD + gCurrentTime+".dat";//���պϲ�����ļ���·��
					   combineFile(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z, finalPathFile);//�ϲ��ĸ���ʱ�ļ�
					   delete(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z);//ɾ����ʱ�ļ�
					   gCurrentTime = addCurrentTime(gCurrentTime, (gCountPackets));//��ǰʱ������40����
					   clear(true);//�����ǰ������
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
						   //д���ݿ� 7Ϊ����ʽ���ݣ�8Ϊ����ʽ����
						   ECG_Android.gWriteRecord.insertRecord(this.getUserId(), 1000*TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0) , PATH_SD+gCurrentTime+".dat" , 7, 0, 0);
						   gEcgDataHead = new EcgDataHead();
						   //дECG����
						   
						   
						   //�������ݰ�ͷ
						   ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);
						  
						   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	��������
						   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	��������
						   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	��������
						   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	��������
						   
						   gEcgDataHead.setEcgDataType(ecgDataEntity);   //����Ecg��������
						   gEcgDataHead.setdataLength(0);    //�������ݳ���				   
						   //gOut_Ecg.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);//д��������
						   gOut_Ecg.write(dataTypeEcg, 0, dataTypeEcg.length);
						   gOut_Ecg.write(gEcgDataHead.getDataLength(), 0, gEcgDataHead.getDataLength().length);//д���ݳ���
						   gOut_Ecg.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//дEcg����
						   gEcgCount += ecgDataEntity.getEcgPacket().getEcgData().length;//����ECG���ݳ���
						   gOut_Ecg.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_Ecg.write(TypeConversion.intToBytes(gEcgCount));//д���ļ�����
						   gOut_Ecg.seek(gOut_Ecg.length());
						   //дACC_X����
						 //  gEcgDataHead.setAccDataType(ecgDataEntity);
						   gEcgDataHead.setdataLength(0);
						  // gOut_X.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
						   gOut_X.write(dataTypeAccX, 0, dataTypeAccX.length);
						   gOut_X.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
						   gOut_X.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//дACC_x����
						   gAccCountX += ecgDataEntity.getAccPacket().getAccAxisX().length;//����ACC_X���ݳ���
						   gOut_X.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_X.write(TypeConversion.intToBytes(gAccCountX));//д���ļ�����
						   gOut_X.seek(gOut_X.length());
						   //дACC_Y����
						   //gOut_Y.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
						   gOut_Y.write(dataTypeAccY, 0, dataTypeAccY.length);
						   gOut_Y.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
						   gOut_Y.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//дACC_Y����
						   gAccCountY += ecgDataEntity.getAccPacket().getAccAxisY().length;//����ACC_Y���ݳ���
						   gOut_Y.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_Y.write(TypeConversion.intToBytes(gAccCountY));//д���ļ�����
						   gOut_Y.seek(gOut_Y.length());
						   //дACC_Z����
						   //gOut_Z.write(gEcgDataHead.getDataType(), 0, gEcgDataHead.getDataType().length);
						   gOut_Z.write(dataTypeAccZ, 0, dataTypeAccZ.length);
						   gOut_Z.write(gEcgDataHead.getDataLength(),0,gEcgDataHead.getDataLength().length);
						   gOut_Z.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//дACC_Z����
						   gAccCountZ += ecgDataEntity.getAccPacket().getAccAxisZ().length;//����ACC_Z���ݳ���
						   gOut_Z.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_Z.write(TypeConversion.intToBytes(gAccCountZ));//д���ļ�����
						   gOut_Z.seek(gOut_Z.length());					   
						  
						   gCountPackets++;//�Ѿ���������ݰ��ĸ���������,�ж��Ƿ񳬹�40������
						   
					   }catch(Exception e){
						   logger.debug("Create new File failed!");
					   }
					   
					   
				   }else{//С��40����
					   
					   if((ecgDataEntity.getPacketHead().getPacketStatus()[1]&0x04) == 0x04 ){//��ʷ���ݽ���
						   String finalPathFile = PATH_SD + gCurrentTime+"LC.dat";//���պϲ�����ļ���·��
						   combineFile(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z, finalPathFile);//�ϲ��ĸ���ʱ�ļ�
						   delete(gFilePathEcg, gFilePathAcc_X, gFilePathAcc_Y, gFilePathAcc_Z);//ɾ����ʱ�ļ�
						   clear(true);//�����ǰ������
						   return true;
					   }
					   try{
						   //дECG����
						   gOut_Ecg.seek(gOut_Ecg.length());
						   gOut_Ecg.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//дEcg����
						   gEcgCount += ecgDataEntity.getEcgPacket().getEcgData().length;//����ECG���ݳ���
						   gOut_Ecg.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_Ecg.write(TypeConversion.intToBytes(gEcgCount));//д���ļ�����
						   gOut_Ecg.seek(gOut_Ecg.length());
						   //дACC_X����
						   gOut_X.seek(gOut_X.length());
						   gOut_X.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//дACC_x����
						   gAccCountX += ecgDataEntity.getAccPacket().getAccAxisX().length;//����ACC_X���ݳ���
						   gOut_X.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_X.write(TypeConversion.intToBytes(gAccCountX));//д���ļ�����
						   gOut_X.seek(gOut_X.length());
						   //дACC_Y����
						   gOut_Y.seek(gOut_Y.length());
						   gOut_Y.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//дACC_Y����
						   gAccCountY += ecgDataEntity.getAccPacket().getAccAxisY().length;//����ACC_Y���ݳ���
						   gOut_Y.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_Y.write(TypeConversion.intToBytes(gAccCountY));//д���ļ�����
						   gOut_Y.seek(gOut_Y.length());
						   //дACC_Z����
						   gOut_Z.seek(gOut_Z.length());
						   gOut_Z.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//дACC_Z����
						   gAccCountZ += ecgDataEntity.getAccPacket().getAccAxisZ().length;//����ACC_Z���ݳ���
						   gOut_Z.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_Z.write(TypeConversion.intToBytes(gAccCountZ));//д���ļ�����
						   gOut_Z.seek(gOut_Z.length());					   
						  
						   gCountPackets++;//�Ѿ���������ݰ��ĸ���������,�ж��Ƿ񳬹�40������
						   
					   }catch(Exception e){
						   logger.debug("Save File failed!");
					   }
				   					   
				   }
				   				   
			   }
			   					   		   
		   }else{
			   logger.debug("The Packets is Error");
		   }
	   }else{//��ǰ����
		   if((ecgDataEntity.getPacketHead().getPacketStatus()[1]&0x01) == 0x01 ){//��ǰ��������
			   if(ecgDataEntity.isFirstData){//��ǰ�������ݿ�ʼ
				   gCurrentTimeEx = long2Date(TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0)*1000);//��ȡ��һ�����ݵ�ʱ��
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
					   //д���ݿ� 7Ϊ����ʽ���ݣ�8Ϊ����ʽ����
					   ECG_Android.gWriteRecord.insertRecord(this.getUserId(), 1000*TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0) , PATH_SD+gCurrentTimeEx+".dat" , 8, 0, 0);
					   gEcgDataHeadEx = new EcgDataHead();
					   
					   
					   //�������ݰ�ͷ
					   ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);
					  
					   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	��������
					   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	��������
					   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	��������
					   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	��������
					   
					   //дECG����
					   gEcgDataHeadEx.setEcgDataType(ecgDataEntity);   //����Ecg��������
					   gEcgDataHeadEx.setdataLength(0);    //�������ݳ���				   
					  //gOut_EcgEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);//д��������
					   gOut_EcgEx.write(dataTypeEcg, 0, dataTypeEcg.length);
					   gOut_EcgEx.write(gEcgDataHeadEx.getDataLength(), 0, gEcgDataHeadEx.getDataLength().length);//д���ݳ���
					   gOut_EcgEx.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//дEcg����
					   gEcgCountEx += ecgDataEntity.getEcgPacket().getEcgData().length;//����ECG���ݳ���
					   gOut_EcgEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
					   gOut_EcgEx.write(TypeConversion.intToBytes(gEcgCountEx));//д���ļ�����
					   gOut_EcgEx.seek(gOut_EcgEx.length());
					   //дACC_X����
					 //  gEcgDataHeadEx.setAccDataType(ecgDataEntity);
					   gEcgDataHeadEx.setdataLength(0);
					   //gOut_XEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
					   gOut_XEx.write(dataTypeAccX, 0, dataTypeAccX.length);
					   gOut_XEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
					   gOut_XEx.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//дACC_x����
					   gAccCountXEx += ecgDataEntity.getAccPacket().getAccAxisX().length;//����ACC_X���ݳ���
					   gOut_XEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
					   gOut_XEx.write(TypeConversion.intToBytes(gAccCountXEx));//д���ļ�����
					   gOut_XEx.seek(gOut_XEx.length());
					   //дACC_Y����
					   //gOut_YEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
					   gOut_YEx.write(dataTypeAccY, 0, dataTypeAccY.length);
					   gOut_YEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
					   gOut_YEx.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//дACC_Y����
					   gAccCountYEx += ecgDataEntity.getAccPacket().getAccAxisY().length;//����ACC_Y���ݳ���
					   gOut_YEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
					   gOut_YEx.write(TypeConversion.intToBytes(gAccCountYEx));//д���ļ�����
					   gOut_YEx.seek(gOut_YEx.length());
					   //дACC_Z����
					   //gOut_ZEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
					   gOut_ZEx.write(dataTypeAccZ, 0, dataTypeAccZ.length);
					   gOut_ZEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
					   gOut_ZEx.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//дACC_Z����
					   gAccCountZEx += ecgDataEntity.getAccPacket().getAccAxisZ().length;//����ACC_Z���ݳ���
					   gOut_ZEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
					   gOut_ZEx.write(TypeConversion.intToBytes(gAccCountZEx));//д���ļ�����
					   gOut_ZEx.seek(gOut_ZEx.length());					   
					  
					   gCountPacketsEx++;//�Ѿ���������ݰ��ĸ���������,�ж��Ƿ񳬹�40������
					   
				   }catch(Exception e){
					   logger.debug("Create new File failed!");
				   }
				   
			   }else{//��ǰ��������  ���ǿ�ʼ
				   if(gCountPacketsEx>=TIME_LIMIT){//����40����
					   String finalPathFile = PATH_SD + gCurrentTimeEx+".dat";//���պϲ�����ļ���·��
					   combineFile(gFilePathEcgEx, gFilePathAcc_XEx, gFilePathAcc_YEx, gFilePathAcc_ZEx, finalPathFile);//�ϲ��ĸ���ʱ�ļ�
					   delete(gFilePathEcgEx, gFilePathAcc_XEx, gFilePathAcc_YEx, gFilePathAcc_ZEx);//ɾ����ʱ�ļ�
					   gCurrentTimeEx = addCurrentTime(gCurrentTimeEx, (gCountPacketsEx));//��ǰʱ������40����
					   clear(false);//�����ǰ������
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
						   //д���ݿ� 7Ϊ����ʽ���ݣ�8Ϊ����ʽ����
						   ECG_Android.gWriteRecord.insertRecord(this.getUserId(), 1000*TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0) , PATH_SD+gCurrentTimeEx+".dat" , 8, 0, 0);
						   gEcgDataHeadEx = new EcgDataHead();
						   
						   
						   //�������ݰ�ͷ
						   ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);
						  
						   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	��������
						   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	��������
						   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	��������
						   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	��������
						   
						   
						   //дECG����
						   gEcgDataHeadEx.setEcgDataType(ecgDataEntity);   //����Ecg��������
						   gEcgDataHeadEx.setdataLength(0);    //�������ݳ���				   
						   //gOut_EcgEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);//д��������
						   gOut_EcgEx.write(dataTypeEcg, 0, dataTypeEcg.length);
						   gOut_EcgEx.write(gEcgDataHeadEx.getDataLength(), 0, gEcgDataHeadEx.getDataLength().length);//д���ݳ���
						   gOut_EcgEx.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//дEcg����
						   gEcgCountEx += ecgDataEntity.getEcgPacket().getEcgData().length;//����ECG���ݳ���
						   gOut_EcgEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_EcgEx.write(TypeConversion.intToBytes(gEcgCountEx));//д���ļ�����
						   gOut_EcgEx.seek(gOut_EcgEx.length());
						   //дACC_X����
						 //  gEcgDataHeadEx.setAccDataType(ecgDataEntity);
						   gEcgDataHeadEx.setdataLength(0);
						   //gOut_XEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
						   gOut_XEx.write(dataTypeAccX, 0, dataTypeAccX.length);
						   gOut_XEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
						   gOut_XEx.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//дACC_x����
						   gAccCountXEx += ecgDataEntity.getAccPacket().getAccAxisX().length;//����ACC_X���ݳ���
						   gOut_XEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_XEx.write(TypeConversion.intToBytes(gAccCountXEx));//д���ļ�����
						   gOut_XEx.seek(gOut_XEx.length());
						   //дACC_Y����
						   //gOut_YEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
						   gOut_YEx.write(dataTypeAccY, 0, dataTypeAccY.length);
						   gOut_YEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
						   gOut_YEx.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//дACC_Y����
						   gAccCountYEx += ecgDataEntity.getAccPacket().getAccAxisY().length;//����ACC_Y���ݳ���
						   gOut_YEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_YEx.write(TypeConversion.intToBytes(gAccCountYEx));//д���ļ�����
						   gOut_YEx.seek(gOut_YEx.length());
						   //дACC_Z����
						   //gOut_ZEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
						   gOut_ZEx.write(dataTypeAccZ, 0, dataTypeAccZ.length);
						   gOut_ZEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
						   gOut_ZEx.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//дACC_Z����
						   gAccCountZEx += ecgDataEntity.getAccPacket().getAccAxisZ().length;//����ACC_Z���ݳ���
						   gOut_ZEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_ZEx.write(TypeConversion.intToBytes(gAccCountZEx));//д���ļ�����
						   gOut_ZEx.seek(gOut_ZEx.length());					   
						  
						   gCountPacketsEx++;//�Ѿ���������ݰ��ĸ���������,�ж��Ƿ񳬹�40������
						   
					   }catch(Exception e){
						   logger.debug("Create new File failed!");
					   }
					   
					   
				   }else{//С��40����
					   try{
						   //дECG����
						   gOut_EcgEx.seek(gOut_EcgEx.length());
						   gOut_EcgEx.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//дEcg����
						   gEcgCountEx += ecgDataEntity.getEcgPacket().getEcgData().length;//����ECG���ݳ���
						   gOut_EcgEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_EcgEx.write(TypeConversion.intToBytes(gEcgCountEx));//д���ļ�����
						   gOut_EcgEx.seek(gOut_EcgEx.length());
						   //дACC_X����
						   gOut_XEx.seek(gOut_XEx.length());
						   gOut_XEx.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//дACC_x����
						   gAccCountXEx += ecgDataEntity.getAccPacket().getAccAxisX().length;//����ACC_X���ݳ���
						   gOut_XEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_XEx.write(TypeConversion.intToBytes(gAccCountXEx));//д���ļ�����
						   gOut_XEx.seek(gOut_XEx.length());
						   //дACC_Y����
						   gOut_YEx.seek(gOut_YEx.length());
						   gOut_YEx.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//дACC_Y����
						   gAccCountYEx += ecgDataEntity.getAccPacket().getAccAxisY().length;//����ACC_Y���ݳ���
						   gOut_YEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_YEx.write(TypeConversion.intToBytes(gAccCountYEx));//д���ļ�����
						   gOut_YEx.seek(gOut_YEx.length());
						   //дACC_Z����
						   gOut_ZEx.seek(gOut_ZEx.length());
						   gOut_ZEx.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//дACC_Z����
						   gAccCountZEx += ecgDataEntity.getAccPacket().getAccAxisZ().length;//����ACC_Z���ݳ���
						   gOut_ZEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_ZEx.write(TypeConversion.intToBytes(gAccCountZEx));//д���ļ�����
						   gOut_ZEx.seek(gOut_ZEx.length());					   
						  
						   gCountPacketsEx++;//�Ѿ���������ݰ��ĸ���������,�ж��Ƿ񳬹�40������
						   
					   }catch(Exception e){
						   logger.debug("Save File failed!");
					   }
					   			   
				   }
				   				   
			   }
			   
		   }else if((ecgDataEntity.getPacketHead().getPacketStatus()[1]&0x01) == 0x00){//��ǰ����
			   if(ecgDataEntity.isFirstData){//��ǰ�������ݿ�ʼ
				   gCurrentTimeEx = long2Date(TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0)*1000);//��ȡ��һ�����ݵ�ʱ��
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
					   //д���ݿ� 7Ϊ����ʽ���ݣ�8Ϊ����ʽ����
					   ECG_Android.gWriteRecord.insertRecord(this.getUserId(), 1000*TypeConversion.bytesToLong(ecgDataEntity.getDataPacketTime(), 0) , PATH_SD+gCurrentTimeEx+".dat" , 7, 0, 0);
					   gEcgDataHeadEx = new EcgDataHead();
					   
					   
					   //�������ݰ�ͷ
					   ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);
					  
					   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	��������
					   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	��������
					   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	��������
					   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	��������
					   
					   
					   //дECG����
					   gEcgDataHeadEx.setEcgDataType(ecgDataEntity);   //����Ecg��������
					   gEcgDataHeadEx.setdataLength(0);    //�������ݳ���				   
					  // gOut_EcgEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);//д��������
					   gOut_EcgEx.write(dataTypeEcg, 0, dataTypeEcg.length);
					   gOut_EcgEx.write(gEcgDataHeadEx.getDataLength(), 0, gEcgDataHeadEx.getDataLength().length);//д���ݳ���
					   gOut_EcgEx.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//дEcg����
					   gEcgCountEx += ecgDataEntity.getEcgPacket().getEcgData().length;//����ECG���ݳ���
					   gOut_EcgEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
					   gOut_EcgEx.write(TypeConversion.intToBytes(gEcgCountEx));//д���ļ�����
					   gOut_EcgEx.seek(gOut_EcgEx.length());
					   //дACC_X����
					 //  gEcgDataHeadEx.setAccDataType(ecgDataEntity);
					   gEcgDataHeadEx.setdataLength(0);
					   //gOut_XEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
					   gOut_XEx.write(dataTypeAccX, 0, dataTypeAccX.length);
					   gOut_XEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
					   gOut_XEx.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//дACC_x����
					   gAccCountXEx += ecgDataEntity.getAccPacket().getAccAxisX().length;//����ACC_X���ݳ���
					   gOut_XEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
					   gOut_XEx.write(TypeConversion.intToBytes(gAccCountXEx));//д���ļ�����
					   
					   gOut_XEx.seek(gOut_XEx.length());
					   //дACC_Y����
					   //gOut_YEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
					   gOut_YEx.write(dataTypeAccY, 0, dataTypeAccY.length);
					   gOut_YEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
					   gOut_YEx.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//дACC_Y����
					   gAccCountYEx += ecgDataEntity.getAccPacket().getAccAxisY().length;//����ACC_Y���ݳ���
					   gOut_YEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
					   gOut_YEx.write(TypeConversion.intToBytes(gAccCountYEx));//д���ļ�����
					   gOut_YEx.seek(gOut_YEx.length());
					   //дACC_Z����
					  // gOut_ZEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
					   gOut_ZEx.write(dataTypeAccZ, 0, dataTypeAccZ.length);
					   gOut_ZEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
					   gOut_ZEx.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//дACC_Z����
					   gAccCountZEx += ecgDataEntity.getAccPacket().getAccAxisZ().length;//����ACC_Z���ݳ���
					   gOut_ZEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
					   //gOut_ZEx.writeInt(gAccCountZEx);//д���ļ�����
					   gOut_ZEx.write(TypeConversion.intToBytes(gAccCountZEx));//д���ļ�����
					   
					   gOut_ZEx.seek(gOut_ZEx.length());					   
					  
					   gCountPacketsEx++;//�Ѿ���������ݰ��ĸ���������,�ж��Ƿ񳬹�40������
					   
				   }catch(Exception e){
					   logger.debug("Create new File failed!");
				   }
				   
			   }else{//��ǰ��������  ���ǿ�ʼ
				   if(gCountPacketsEx>=TIME_LIMIT){//����40����
					   String finalPathFile = PATH_SD + gCurrentTimeEx+".dat";//���պϲ�����ļ���·��
					   combineFile(gFilePathEcgEx, gFilePathAcc_XEx, gFilePathAcc_YEx, gFilePathAcc_ZEx, finalPathFile);//�ϲ��ĸ���ʱ�ļ�
					   delete(gFilePathEcgEx, gFilePathAcc_XEx, gFilePathAcc_YEx, gFilePathAcc_ZEx);//ɾ����ʱ�ļ�
					   gCurrentTimeEx = addCurrentTime(gCurrentTimeEx, (gCountPacketsEx));//��ǰʱ������40����
					   
					   clear(false);//�����ǰ������
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
						   //д���ݿ� 7Ϊ����ʽ���ݣ�8Ϊ����ʽ����
						   ECG_Android.gWriteRecord.insertRecord(this.getUserId(), date2Long(gCurrentTimeEx) , PATH_SD+gCurrentTimeEx+".dat" , 7, 0, 0);
						   gEcgDataHeadEx = new EcgDataHead();
						   
						   
						   //�������ݰ�ͷ
//						   ecgPacketHeader = UploadEcgInfoTool.getEcgPacketHeader(ecgDataEntity);
//						  
//						   System.arraycopy(ecgPacketHeader[0], 0 , dataTypeEcg, 0 , 4);//4bytes	��������
//						   System.arraycopy(ecgPacketHeader[1],0 , dataTypeAccX,0 , 4);//4bytes	��������
//						   System.arraycopy(ecgPacketHeader[2],0 , dataTypeAccY,0 , 4);//4bytes	��������
//						   System.arraycopy(ecgPacketHeader[3],0 , dataTypeAccZ,0 , 4);//4bytes	��������
						   
						   
						   //дECG����
						   gEcgDataHeadEx.setEcgDataType(ecgDataEntity);   //����Ecg��������
						   gEcgDataHeadEx.setdataLength(0);    //�������ݳ���				   
						  // gOut_EcgEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);//д��������
						   gOut_EcgEx.write(dataTypeEcg,0, dataTypeEcg.length);
						   logger.info("Write ECG type leng="+dataTypeEcg.length+"");
						   gOut_EcgEx.write(gEcgDataHeadEx.getDataLength(), 0, gEcgDataHeadEx.getDataLength().length);//д���ݳ���
						   gOut_EcgEx.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//дEcg����
						   gEcgCountEx += ecgDataEntity.getEcgPacket().getEcgData().length;//����ECG���ݳ���
						   gOut_EcgEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_EcgEx.write(TypeConversion.intToBytes(gEcgCountEx));//д���ļ�����
						   logger.info("Write ECG length: leng="+gOut_EcgEx.length()+"");
						   gOut_EcgEx.seek(gOut_EcgEx.length());
						   //дACC_X����
						//   gEcgDataHeadEx.setAccDataType(ecgDataEntity);
						   gEcgDataHeadEx.setdataLength(0);
						   //gOut_XEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
						   gOut_XEx.write(dataTypeAccX, 0, dataTypeAccX.length);
						   logger.info("Write ACC_X length: leng="+dataTypeAccX.length+"");
						   gOut_XEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
						   gOut_XEx.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//дACC_x����
						   gAccCountXEx += ecgDataEntity.getAccPacket().getAccAxisX().length;//����ACC_X���ݳ���
						   gOut_XEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_XEx.write(TypeConversion.intToBytes(gAccCountXEx));//д���ļ�����
						   gOut_XEx.seek(gOut_XEx.length());
						   //дACC_Y����
						   //gOut_YEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
						   gOut_YEx.write(dataTypeAccY, 0, dataTypeAccY.length);
						   logger.info("Write ACC_Y type: leng="+dataTypeAccY.length+"");
						   gOut_YEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
						   gOut_YEx.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//дACC_Y����
						   gAccCountYEx += ecgDataEntity.getAccPacket().getAccAxisY().length;//����ACC_Y���ݳ���
						   gOut_YEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_YEx.write(TypeConversion.intToBytes(gAccCountYEx));//д���ļ�����
						   logger.info("Write ACC_Y length: leng="+dataTypeAccX.length+"");
						   gOut_YEx.seek(gOut_YEx.length());
						   //дACC_Z����
						   //gOut_ZEx.write(gEcgDataHeadEx.getDataType(), 0, gEcgDataHeadEx.getDataType().length);
						   gOut_ZEx.write(dataTypeAccZ, 0, dataTypeAccZ.length);
						   logger.info("Write ACC_Z type: leng="+dataTypeAccY.length+"");
						   gOut_ZEx.write(gEcgDataHeadEx.getDataLength(),0,gEcgDataHeadEx.getDataLength().length);
						   gOut_ZEx.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//дACC_Z����
						   gAccCountZEx += ecgDataEntity.getAccPacket().getAccAxisZ().length;//����ACC_Z���ݳ���
						   gOut_ZEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_ZEx.write(TypeConversion.intToBytes(gAccCountZEx));//д���ļ�����
						   gOut_ZEx.seek(gOut_ZEx.length());					   
						  
						   gCountPacketsEx++;//�Ѿ���������ݰ��ĸ���������,�ж��Ƿ񳬹�40������
						   
					   }catch(Exception e){
						   logger.debug("Create new File failed!");
					   }
					   
					   
				   }else{//С��40����
					   try{
						   //дECG����
						   gOut_EcgEx.seek(gOut_EcgEx.length());
						   gOut_EcgEx.write(ecgDataEntity.getEcgPacket().getEcgData(), 0, ecgDataEntity.getEcgPacket().getEcgData().length);//дEcg����
						   gEcgCountEx += ecgDataEntity.getEcgPacket().getEcgData().length;//����ECG���ݳ���
						   gOut_EcgEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_EcgEx.write(TypeConversion.intToBytes(gEcgCountEx));//д���ļ�����
						   gOut_EcgEx.seek(gOut_EcgEx.length());
						   //дACC_X����
						   gOut_XEx.seek(gOut_XEx.length());
						   gOut_XEx.write(ecgDataEntity.getAccPacket().getAccAxisX(), 0 ,ecgDataEntity.getAccPacket().getAccAxisX().length);//дACC_x����
						   gAccCountXEx += ecgDataEntity.getAccPacket().getAccAxisX().length;//����ACC_X���ݳ���
						   gOut_XEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_XEx.write(TypeConversion.intToBytes(gAccCountXEx));//д���ļ�����
						   gOut_XEx.seek(gOut_XEx.length());
						   //дACC_Y����
						   gOut_YEx.seek(gOut_YEx.length());
						   gOut_YEx.write(ecgDataEntity.getAccPacket().getAccAxisY(), 0 ,ecgDataEntity.getAccPacket().getAccAxisY().length);//дACC_Y����
						   gAccCountYEx += ecgDataEntity.getAccPacket().getAccAxisY().length;//����ACC_Y���ݳ���
						   gOut_YEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_YEx.write(TypeConversion.intToBytes(gAccCountYEx));//д���ļ�����
						   gOut_YEx.seek(gOut_YEx.length());
						   //дACC_Z����
						   gOut_ZEx.seek(gOut_ZEx.length());
						   gOut_ZEx.write(ecgDataEntity.getAccPacket().getAccAxisZ(), 0 ,ecgDataEntity.getAccPacket().getAccAxisZ().length);//дACC_Z����
						   gAccCountZEx += ecgDataEntity.getAccPacket().getAccAxisZ().length;//����ACC_Z���ݳ���
						   gOut_ZEx.seek(4);//�ƶ����������ݳ���λ�ã�д��byte[4];
						   gOut_ZEx.write(TypeConversion.intToBytes(gAccCountZEx));//д���ļ�����
						   gOut_ZEx.seek(gOut_ZEx.length());					   
						  
						   gCountPacketsEx++;//�Ѿ���������ݰ��ĸ���������,�ж��Ƿ񳬹�40������
						   
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
   * ɾ���ĸ���ʱ�ļ�
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
      * �ϲ��ĸ���ʱ�ļ�
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
              logger.debug("�ϲ��ļ�����");
		   }
    }
	
	
	
	public ByteArrayOutputStream readEcgData(){
		
		return null;
	}
	
	
	
	 /**
     * ���ݵ�ǰ���������Ե�ǰ����ʱ�����ʱ������
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
	 * longת��Ϊ�ַ�����
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
	 * longת��Ϊ�ַ�����
	 * 
	 ***
     */
	
	public long date2Long(String formatDate){
		long lTime = 0;//���ص�ʱ�䣬����
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			lTime = (sf.parse(formatDate)).getTime();
		} catch (Exception e) {
			logger.debug("date2Long Error!"+e.getMessage());
		}
		return lTime;
	}
	
	
	/*
	 * ��XML�л�ȡ�û�ID
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
			logger.debug("��ȡ�û�IDʧ�ܣ�");
		}
		//��ȡ����ID�ַ���;
		return userId;
	} 
	
	
	/*
	* �����ǰ������
	*  
	*/
	public void clear(boolean type){//TRUEʱ�����ʷ���ݼ�������FALSEʱ�����ǰ���ݼ�����
		if(type){
			 gEcgCount = 0;//ecgд�����ݵ��ֽ���
			 gAccCountX = 0;//acc xд�����ݵ��ֽ���
			 gAccCountY = 0;//acc yд�����ݵ��ֽ���
			 gAccCountZ = 0;//acc zд�����ݵ��ֽ���
			 gCountPackets = 0;//��ǰ�Ѿ�����İ��ĸ���
			
		}else{
			gEcgCountEx = 0;
			gAccCountXEx = 0;
			gAccCountYEx = 0;
			gAccCountZEx= 0;
			gCountPacketsEx = 0;
		}
		
	}
	
	
	/**
	 * �ж�Ŀ¼�Ƿ���ڣ��������򴴽��ļ��У��ɹ������ļ��е�·����ʧ�ܷ��ؿ�
	 * @param filePath
	 */
	public String createDir(String filePath) {
		File fileDir = null; // �ļ�������
		boolean hasDir = false; // ��ʾ�ļ��������Ƿ����
		fileDir = new File(filePath); // �����ļ�������
		hasDir = fileDir.exists(); // �ж��ļ��������Ƿ����
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
		//�ж��Ƿ�ɹ�
		if(!hasDir){
			filePath = null;
		}
		return filePath;
	}
	
	
	
	/*
	* �ڲ��࣬����������Ҫ������ļ���ͷ
	*  
	*/
	public static class EcgDataHead{
		public  byte[] dataType = new byte[4];
		public  byte[] dataLength = new byte[4];
		public EcgDataHead(){}
		public  void setEcgDataType(EcgDataEntity ecgDataEntity){//����ECG��������
		byte[] ecgType = new byte[2]; 
		byte[] ecgLead = new byte[3];
		ecgType = ecgDataEntity.getEcgPacket().getEcgDataType();//�õ�Type
		ecgLead = ecgDataEntity.getEcgPacket().getEcgDataLead();//�õ�Lead
		if(300==TypeConversion.bytesToShort(ecgType, 0)){
			this.dataType[0] = 0x01;
		}else if(360 == TypeConversion.bytesToShort(ecgType,0)){
			this.dataType[0] = 0x03;
		}else{
			logger.debug("δ֪ECGƵ��");
		}
	
		this.dataType[3] = ecgLead[0];
		this.dataType[2] = ecgLead[1];
		if(((ecgDataEntity.getPacketHead().getPacketStatus()[1] )&0x01) == 0x01){//���Ϊ����ʽ����
			 this.dataType[1] = (byte) ((byte)ecgLead[2]|(byte)0x02);//ecglead�����λ��ռ�ã���
		}else if(((ecgDataEntity.getPacketHead().getPacketStatus()[1] )&0x01) == 0x00){//����ʽ����
			this.dataType[1] = ecgLead[2];
		}else{
				//���ݰ�����
			logger.error("The data is Invalid!");
		}
		}
		
        public  void setAccDataType(EcgDataEntity ecgDataEntity){//����Acc��������
			byte[] accType = new byte[2];
			byte Axis = 0 ;
			accType = ecgDataEntity.getAccPacket().getAccType();
			Axis = ecgDataEntity.getAccPacket().getAccStatus();//����Acc x,y,z�ļ�������������
			if(75 == TypeConversion.bytesToShort(accType, 0)){
				this.dataType[0] = 0x02;
			}else{
				logger.debug("δ֪ACCƵ��");
			}
			
			this.dataType[3] = Axis;//bit[5,6,7]��ʾ�����ļ������������
			
		}
        
        public byte[] getDataType(){
        	return this.dataType;
        }
        
        public byte[] getDataLength(){
        	return this.dataLength;
        }
        
		public void setdataLength(int length){//�������ݳ���
			
			this.dataLength = TypeConversion.intToBytes(length); 
		}
   }
}
