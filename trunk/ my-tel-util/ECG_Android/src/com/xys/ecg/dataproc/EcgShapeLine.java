package com.xys.ecg.dataproc;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import cn.szxys.CurveChart;

import com.xys.ecg.activity.ECGApplication;
import com.xys.ecg.bean.ShapeLineEntity;
import com.xys.ecg.business.EcgBusiness;
import com.xys.ecg.log.Logger;
import com.xys.ecg.utils.TypeConversion;


/**
 * �����ĵ�����ͼ
 * @author yangkele
 *
 */
public class EcgShapeLine {
	public static Logger logger = Logger.getLogger(EcgShapeLine.class);
	private final int DRAW_BG = -2130507570;//��ͼ���򱳾���ɫ
	//private final int DRAW_BG = Color.rgb(0, 115, 189);//��ͼ���򱳾���ɫ
	private CurveChart curveChart = null;
	private boolean suspendDraw = false;
	private int drawType = 0;//��ͼ���ͣ�0ΪECG��ͼ�� 1,2,3ΪACC_X,Y,Z��ͼ�� 4Ϊ���ʻ�ͼ
	private short pointToOne = 1;//���ݲ��������ϳ�һ���㣬Ĭ��Ϊ���ϳɣ����Ƕ���ECGҪ4����ϳ�1����
	
	
	Handler handler = new Handler();
	public EcgShapeLine(CurveChart curveChart, int type){
		
		
		//����Ϊ���Դ��룬������CODEING
		this.curveChart = curveChart;
		drawType = type;
		//curveChart.setGridColor(Color.WHITE);
		
		curveChart.setXYMaxMinValue(0, 0, 0, 255);
		curveChart.setBackgroundColor(DRAW_BG);
		if(drawType==0){//ECGҪ4����ϳ�1����
			pointToOne = 4;
		}
		if(drawType>0){
			curveChart.setHasGridView(false);
		}
        curveChart.addCurveLine(1, null);
       
	}
	
	/**
	 * ��̬��ͼ
	 * @param shapeLineEntity
	 */
	public void  pushData(final Object shapeLineEntity){
		this.shapeLineEntity = (ShapeLineEntity)shapeLineEntity;

		if(ecgShapeLineTask!=null){
			
			synchronized(ecgShapeLineTask){
				ecgShapeLineTask.notify();
			}
		}else{
			ecgShapeLineTask = new Thread(new EcgShapeLineTask());
			ecgShapeLineTask.start();
		}
		

	}
	
	
	
	private Thread ecgShapeLineTask = null;
	private ShapeLineEntity shapeLineEntity = null;
	
	/**
	 * ���������߳�
	 * @author yangkele
	 *
	 */
	public class EcgShapeLineTask implements Runnable{
		private int sleepTime = 0;//˯��ʱ�� EcgBusiness.sleepTime��ƽ����
		private int dataLength = 0;//ÿ�ε�������
		private int drawLength = 0;//ÿ�ε�������,��0��ʼ
		private int[] data = new int[drawLength];
		byte[] datas = null;
		public void run(){
			while (true) {
				//logger.info("show the ecgShapeLineTask!");
				if (drawType == 0) {
					datas = shapeLineEntity.getEcgDataEntity().getEcgPacket().getEcgData();
					this.draw();
				} else if (drawType == 1) {
					datas = shapeLineEntity.getEcgDataEntity().getAccPacket().getAccAxisX();
					this.draw();
				} else if (drawType == 2) {
					datas = shapeLineEntity.getEcgDataEntity().getAccPacket().getAccAxisY();
					this.draw();
				} else if (drawType == 3) {
					datas = shapeLineEntity.getEcgDataEntity().getAccPacket().getAccAxisZ();
					this.draw();
				} else {
					int[] dataECGRate = {shapeLineEntity.getEcgTate()};
					//Log.d("dataECGRate",""+dataECGRate[0]);
					curveChart.pushCurveLineData(1, dataECGRate); 
					if (!suspendDraw) {
						curveChart.postInvalidate();
					}
				}
			
				try {
					synchronized (ecgShapeLineTask) {
						ecgShapeLineTask.wait();// ��ͣ
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		/**
		 * ��ͼ
		 */
		private void draw(){
			if(datas!=null){
				//curveChart.setLiveCurveMoveDirection(true);
				dataLength = datas.length;//��Ҫ���Ƶ����ݰ��ĳ���
				drawLength = dataLength/(pointToOne*2);//����pointToOne������ƽ��ֵ����һ���㣬һ�����ݰ���2�λ��꣬����һ�λ��Ƶĵ���Ϊ���ݰ�/4
				data = new int[drawLength];//�������ݰ�
				sleepTime = (EcgBusiness.sleepTime)/2;
				
				short tempValue = 0 ; 
				for(int i=0,j=0;i<dataLength;){
					tempValue = 0;
					for(int step=0;step<pointToOne;step++){//����pointToOne������ƽ��ֵ����һ����
						tempValue+=TypeConversion.bytesToShort(new byte[]{datas[i++],(byte)0x00}, 0);
					}
					data[j++] = (short)tempValue/pointToOne;
					//Log.d("data",j-1+":"+data[j-1]);
					if(j==drawLength){
						j=0;
						curveChart.pushCurveLineData(1, data);
						if(!suspendDraw){
							curveChart.postInvalidate();
						}
						if(i<dataLength-1){
							try{
								ecgShapeLineTask.sleep(sleepTime);
							}catch(InterruptedException ie){
							}
						}

					}

				}
			}
		}
		
	}

	public boolean getSuspendDraw() {
		return suspendDraw;
	}

	public void setSuspendDraw(boolean suspendDraw) {
		this.suspendDraw = suspendDraw;
	}
	
	
}
