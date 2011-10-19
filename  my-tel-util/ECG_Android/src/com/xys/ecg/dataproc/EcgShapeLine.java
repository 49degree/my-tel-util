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
 * 绘制心电曲线图
 * @author yangkele
 *
 */
public class EcgShapeLine {
	public static Logger logger = Logger.getLogger(EcgShapeLine.class);
	private final int DRAW_BG = -2130507570;//绘图区域背景颜色
	//private final int DRAW_BG = Color.rgb(0, 115, 189);//绘图区域背景颜色
	private CurveChart curveChart = null;
	private boolean suspendDraw = false;
	private int drawType = 0;//绘图类型，0为ECG绘图， 1,2,3为ACC_X,Y,Z绘图， 4为心率绘图
	private short pointToOne = 1;//根据采样多个点合成一个点，默认为不合成，但是对于ECG要4个点合成1个点
	
	
	Handler handler = new Handler();
	public EcgShapeLine(CurveChart curveChart, int type){
		
		
		//以下为测试代码，请重新CODEING
		this.curveChart = curveChart;
		drawType = type;
		//curveChart.setGridColor(Color.WHITE);
		
		curveChart.setXYMaxMinValue(0, 0, 0, 255);
		curveChart.setBackgroundColor(DRAW_BG);
		if(drawType==0){//ECG要4个点合成1个点
			pointToOne = 4;
		}
		if(drawType>0){
			curveChart.setHasGridView(false);
		}
        curveChart.addCurveLine(1, null);
       
	}
	
	/**
	 * 动态绘图
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
	 * 更新数据线程
	 * @author yangkele
	 *
	 */
	public class EcgShapeLineTask implements Runnable{
		private int sleepTime = 0;//睡眠时间 EcgBusiness.sleepTime求平均数
		private int dataLength = 0;//每段的数据量
		private int drawLength = 0;//每段的数据量,从0开始
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
						ecgShapeLineTask.wait();// 暂停
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		/**
		 * 绘图
		 */
		private void draw(){
			if(datas!=null){
				//curveChart.setLiveCurveMoveDirection(true);
				dataLength = datas.length;//需要绘制的数据包的长度
				drawLength = dataLength/(pointToOne*2);//根据pointToOne点数求平均值绘制一个点，一个数据包分2次绘完，所以一次绘制的点数为数据包/4
				data = new int[drawLength];//绘制数据包
				sleepTime = (EcgBusiness.sleepTime)/2;
				
				short tempValue = 0 ; 
				for(int i=0,j=0;i<dataLength;){
					tempValue = 0;
					for(int step=0;step<pointToOne;step++){//根据pointToOne点数求平均值绘制一个点
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
