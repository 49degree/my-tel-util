package cn.szxys;

import java.util.HashMap;

import java.util.Iterator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xys.ecg.activity.ECGApplication;

public class CurveChart extends SurfaceView implements SurfaceHolder.Callback {
	public String TAG = "ShapeLine";
	
	private SurfaceHolder holder = null; // ���ƶ���
	private int canvasWidth = 2000;//�����Ŀ��
	private int canvasHeight = 200;//�����ĸ߶�
	private int screenWidth; // ��Ļ�Ŀ�
	private int screenHeight; // ��Ļ�ĸ�
	private int screenIndexLeftX; // ��Ļ���½ǵ�X����
	private int screenIndexLeftY; // ��Ļ���½ǵ�Y����
	
	private float xMinValue = 0;//����X���ʶ����С����Ĭ��Ϊ0��
	private float xMaxValue;//����X���ʶ�������
	private float yMinValue = 0;//����Y���ʶ����С����Ĭ��Ϊ0��
	private float yMaxValue;//����Y���ʶ�������
	private float xValuePerPix = 1F;//ÿ���ر�ʶX�᳤�� Ĭ��Ϊ1
	private float yValuePerPix = 1F;//ÿ���ر�ʶY�᳤�� Ĭ��Ϊ1
	
	private Canvas canvas = null;//����
	private YLine yLine = null;//X�����
	private XLine xLine = null;//Y�����
	private GridLine gridLine = null;//�������
	
	
	
	
	private HashMap<String,LiveCurve> listLiveCurve = new HashMap<String,LiveCurve>();/* ���߼��� */


	private short edgeWidth = 5;//�߾�

	public CurveChart(Context context) {
		super(context);
		//Log.d(TAG,"ShapeLine1");
		holder = getHolder();
		holder.addCallback(this);
		setFocusable(true);  //ȷ�����ǵ�View�ܻ�����뽹��
		setFocusableInTouchMode(true);  //ȷ���ܽ��յ������¼�
	}

	public CurveChart(Context context, AttributeSet attrs) 
	{
		super(context,attrs);	
		//Log.d(TAG,"ShapeLine2");
		holder = getHolder();
		holder.addCallback(this);
		
		xLine = new XLine();//��ʼ��X����
		yLine = new YLine();//��ʼ��Y����
		gridLine = new GridLine();//��ʼ���������

		setFocusable(true);  //ȷ�����ǵ�View�ܻ�����뽹��
		setFocusableInTouchMode(true);  //ȷ���ܽ��յ������¼�
	}
	
	public CurveChart(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context,attrs,defStyle);
		//Log.d(TAG,"ShapeLine3");
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d(TAG,"surfaceChanged");

	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG,"surfaceCreated");
		canvas = holder.lockCanvas();
		init();
		holder.unlockCanvasAndPost(canvas);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		//Log.d(TAG,"surfaceDestroyed");

	}

	
	
	/**
	 * ��ʼ������
	 */
	public void init(){
		canvasWidth = canvas.getWidth();
		canvasHeight = canvas.getHeight();

		screenIndexLeftX = getLeft();
		screenIndexLeftY = getBottom();
		
		screenHeight=ECGApplication.getInstance().getScreenHeight();
		screenWidth=ECGApplication.getInstance().getScreenWidth();
		
		//����listLiveCurve����ͼ
		Iterator<String> iterator = listLiveCurve.keySet().iterator();
		while(iterator.hasNext()){
			String liveCurveId = iterator.next();
			LiveCurve tempLiveCurve = listLiveCurve.get(liveCurveId);
			int[] tempDataArray = null;
			if(tempLiveCurve.dataIndex>0){
				tempDataArray = new int[tempLiveCurve.dataIndex]; 
				System.arraycopy(tempLiveCurve.dataArray, 0,tempDataArray , 0, tempLiveCurve.dataIndex);
			}
			tempLiveCurve = new LiveCurve(tempDataArray);
			listLiveCurve.put(liveCurveId, tempLiveCurve);
		}
		
	}
	
	public void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		if(canvas!=null){
			try{
				if(xLine!=null){
					//Log.d(TAG,canvas.getWidth()+":"+canvas.getHeight());
					xLine.drawXLine(canvas);
				}
				if(yLine!=null){
					yLine.drawYLine(canvas);
				}
				if(gridLine!=null&&gridLine.hasGridView){
					gridLine.drawGrid(canvas);
				}
				drawAllCurveLine(canvas);
			}catch(Exception e){
				e.printStackTrace();
				
			}
		}
	}
	
	 
	/**
	 * ����ҳ���ػ�
	 */
	public void postInvalidate() {
		super.postInvalidate();
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

		}
		return true;
	}
	
	
	
	
	/**
	 * �������߲���������
	 * @param curveId
	 * @param data
	 */
	public void addCurveLine(int curveId, int[] datas) {
		
		String curveIdStr = String.valueOf(curveId);
		if(listLiveCurve.containsKey(curveIdStr)){
			listLiveCurve.get(curveIdStr).addData(datas);
		}else{
			listLiveCurve.put(curveIdStr, new LiveCurve(datas));
		}
	}
	
	
	/**
	 *  ���߸�������
	 * @param curveId
	 * @param data
	 */
	public boolean pushCurveLineData(int curveId, int[] datas) {
		String curveIdStr = String.valueOf(curveId);
		if(listLiveCurve.containsKey(curveIdStr)){
			listLiveCurve.get(curveIdStr).addData(datas);
			return true;
		}else{
			return false;	
		}
	}

	/**
	 *  ������������
	 * @param canvas
	 */
	private void drawAllCurveLine(Canvas canvas) {
		//Log.d("postInvalidate","drawAllCurveLine");
		//����listLiveCurve����ͼ
		if(canvas!=null){
			//Log.d("postInvalidate","drawAllCurveLine");
			Iterator<String> iterator = listLiveCurve.keySet().iterator();
			while(iterator.hasNext()){
				listLiveCurve.get(iterator.next()).drawCurveLine(canvas);
			}
		}
	}

	/**
	 * ����X,Y���ʶ�����ݷ�Χ
	 * ������X,Y��ÿ���ر�ʶ�ĳ���
	 * @param xMinValue
	 * @param xMaxValue 
	 * @param yMinValue
	 * @param yMaxValue 
	 */
	public void setXYMaxMinValue(float xMinValue,float xMaxValue,float yMinValue,float yMaxValue){
		//Log.d(TAG,"setXYMaxMinValue");
		this.xMinValue = xMinValue;
		this.xMaxValue = xMaxValue;
		
		this.yMinValue = yMinValue;
		this.yMaxValue = yMaxValue;
	}
	
	/**
	 * ����ĸ߶Ⱥÿ��
	 * @param gridWidth
	 * @param gridHeight
	 */
	public void setGridWithHeight(short gridWidth,short gridHeight){
		if(gridLine!=null){
			gridLine.setGridWithHeight(gridWidth, gridHeight);
		}
	}
	
	/**
	 * �Ƿ�������
	 * @param hasGrid
	 */
	public void setHasGridView(boolean hasGrid){
		if(gridLine!=null){
			gridLine.setHasGridView(hasGrid);
		}
	}
	
	/**
	 * ������ 
	 */
	private class LiveCurve {
		//public short curveId; // ����Id
		public int clr = Color.WHITE; // ���ߵ���ɫ
		public short width = 1; // ���ߵĿ��
		public int[] dataArray = null;//Y���Ԫ��
		public int dataIndex = 0;//Y���Ԫ�ص�ĩβ�±� 0��ʶΪ��
		
		private int drawCanvasWidth = canvasWidth-2*edgeWidth;//�ɻ������ߵ���Ļ���
		private int drawCanvasHeight = canvasHeight-2*edgeWidth;//�ɻ������ߵ���Ļ�߶�
		
		public Path linePath = null;//����path����
		Paint paint = null;
		/**
		 * Ĭ�Ϲ��캯��
		 */
		public LiveCurve(){
			dataArray = new int[drawCanvasWidth];

		}
		/**
		 * ���캯��
		 * @param datas
		 */
		public LiveCurve(int[] datas){
			this();
			this.addData(datas);
		}
		/**
		 * ������������
		 * @param datas
		 */
		public void addData(int[] datas){
			if(datas==null){
				return ;
			}
			int datasLength = datas.length;
			//Log.d("datasLength",datasLength+"");
			if(datasLength>=dataArray.length){//��������ݳ��ȴ��ڿ����ɳ��ȣ����������ݵĺ�����ȫ������
				System.arraycopy(datas, datasLength - dataArray.length, dataArray, 0, dataArray.length);//��datas������COPY��Y���Ԫ�ص�ĩβ
				dataIndex = dataArray.length;
			}else{
				/*
				 * dataArray.length-dataIndex��ʾ���ж��ٿյ�λ��
				 * datas.length-(dataArray.length-dataIndex)��ʾ��Ҫ��ĩβ��ն��ٸ�λ�ò��ܷ��������ӵ�datas
				 * dataIndex-clearNun����ɱ����ĳ���
				 */
				int clearNun = datasLength-(dataArray.length-dataIndex);
				
				if(clearNun>0){
					int leavLength = dataIndex-clearNun;//��ʾ�ɱ����ĳ���
					int[] leavInt = new int[leavLength];
					System.arraycopy(dataArray, clearNun, leavInt, 0, leavLength);
					System.arraycopy(leavInt, 0, dataArray, 0, leavLength);//��Y���Ԫ����ǰ�ƶ�clearNun���ȣ�����ͷ����
					System.arraycopy(datas, 0, dataArray, leavLength, datasLength);//��datas������COPY��Y���Ԫ�ص�ĩβ
					dataIndex = dataArray.length;
				}else{
					System.arraycopy(datas, 0, dataArray, dataIndex, datasLength);//��datas������COPY��Y���Ԫ�ص�ĩβ
					dataIndex+=datasLength;
				}
			}
		}
		
		int step = 0;
		
		/**
		 * ��������
		 * @param canvas
		 */
		public void drawCurveLine(Canvas canvas){
			if(dataIndex<1||dataIndex>dataArray.length){
				return ;
			}
			
			linePath = new Path();
			paint = new Paint();
			paint.setColor(clr);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(width);
			


			//���ֵ���������Сֵ
			if(xMaxValue>xMinValue){//����ÿ���ر�ʶX�᳤�� Ĭ��Ϊ1
				xValuePerPix = Math.abs((xMaxValue-xMinValue)/drawCanvasWidth);
			}else{
				xMaxValue = drawCanvasWidth;
				xMinValue = 0;
			}
			//���ֵ���������Сֵ
			if(yMaxValue>yMinValue){//����ÿ���ر�ʶY�᳤�� Ĭ��Ϊ1
				yValuePerPix = Math.abs((yMaxValue-yMinValue)/drawCanvasHeight);
			}else{
				yMaxValue = drawCanvasHeight;
				yMinValue = 0;
			}
			
			//Log.d("dataArray",yValuePerPix+":"+yMaxValue+":"+yMinValue);
			
			float moveDirection = -1;//-1��ʾ�����������,1���෴
			float pointX = drawCanvasWidth+edgeWidth;
			float pointY = drawCanvasHeight+edgeWidth-(Math.abs(dataArray[dataIndex-1]-yMinValue))/yValuePerPix;//Y������ڻ���������λ��
			//Log.d("dataArray",dataIndex-1+":"+dataArray[dataIndex-1]+":"+pointY);
			linePath.moveTo(pointX, pointY);//��ʼ�ĵ����꣬��ʾ���������ƶ�
			for(int i=dataIndex-2;i>=0;i--){
				pointX +=  moveDirection;
				pointY = drawCanvasHeight+edgeWidth-(Math.abs(dataArray[i]-yMinValue))/yValuePerPix;//Y������ڻ���������λ��
				//Log.d("dataArray",i+":"+dataArray[i]+":"+pointY);
				linePath.lineTo(pointX, pointY);
			}
			canvas.drawPath(linePath, paint);
		}
	}
	
	
	/**
	 * X �����
	 * @author Administrator
	 *
	 */
	private class XLine{


		private int xAxisColor = Color.GRAY; // Ĭ��x�����ɫ
		private float xAxisWidth = 0.5f; // Ĭ��x��Ŀ��
		private boolean xAxisVisible = true; // x���Ƿ�ɼ���Ĭ�Ͽɼ�
		/**
		 *  ����X��
		 * @param canvas
		 */
		public void drawXLine(Canvas canvas) {
			Paint xPaint = new Paint();
			xPaint.setColor(xAxisColor);
			if (!xAxisVisible)
				xPaint.setAlpha(1);
			xPaint.setStyle(Paint.Style.STROKE);
			xPaint.setStrokeWidth(xAxisWidth);
			// ��ֱ��
			float xStartPointX = edgeWidth; /* X�����ʼ������ */
			float xStartPointY = canvasHeight - edgeWidth; /* X�����ʼ������ */
			float xEndPointX = canvasWidth-edgeWidth; /* X��Ľ��������� */
			float xEndPointY = xStartPointY; /* X��Ľ��������� */
			
			//Log.d("drawXLine","drawXLine");
			canvas.drawLine(xStartPointX, xStartPointY, xEndPointX,xEndPointY, xPaint);
		}		
		
	}
	
	/**
	 * Y �����
	 * @author Administrator
	 *
	 */
	private class YLine{
		private boolean yAxisVisible = true; // Y���Ƿ�ɼ���Ĭ�Ͽɼ�
		private int yAxisColor = Color.GRAY; // Ĭ�� Y �����ɫ
		private Paint yPaint; // �� Y �����ʽ����
		private float yAxisWidth = 0.5f; // Ĭ��x,y��Ŀ��
		/**
		 *  �� Y ��
		 * @param canvas
		 */
		private void drawYLine(Canvas canvas) {
			float yAxisStartX = edgeWidth; // ��ʼ�� X ����
			float yAxisStartY = canvasHeight-edgeWidth; // ��ʼ�� Y ����
			float xEndPointX = edgeWidth; /* X��Ľ��������� */
			float xEndPointY = edgeWidth; /* X��Ľ��������� */
			YAxisPaint();
			canvas.drawLine(yAxisStartX, yAxisStartY, xEndPointX, xEndPointY, yPaint);
			
			yAxisStartX = canvasWidth - edgeWidth; // ��ʼ�� X ����
			yAxisStartY = canvasHeight-edgeWidth; // ��ʼ�� Y ����
			xEndPointX = canvasWidth - edgeWidth; /* X��Ľ��������� */
			xEndPointY = edgeWidth; /* X��Ľ��������� */
			// ��� Y ��ͣ�����ұ�
			canvas.drawLine(canvasWidth - edgeWidth, canvasHeight-edgeWidth, canvasWidth - edgeWidth, edgeWidth, yPaint);

		}
		
		/**
		 *  �� Y ��Ļ���
		 */
		private void YAxisPaint() {
			yPaint = new Paint();
			yPaint.setColor(yAxisColor);
			if (!yAxisVisible)
				yPaint.setAlpha(1);
			yPaint.setStyle(Paint.Style.STROKE);
			yPaint.setStrokeWidth(yAxisWidth);
		}
	}

	/**
	 * �������
	 * @author Administrator
	 *
	 */
	private class GridLine {
		private short width = 10;
		private short height = 10;
		private int gridColor = Color.GRAY; // ����Ĭ�ϵ���ɫ
		private float gridWidth = 0.2f; // ������Ĭ�ϵĿ��
		private boolean hasGridView = true;//�Ƿ�������Ĭ����

		public GridLine() {
		}
		/**
		 * ����ĸ߶Ⱥÿ��
		 * @param gridWidth
		 * @param gridHeight
		 */
		public void setGridWithHeight(short gridWidth,short gridHeight){
			this.width = gridWidth;
			this.height = gridHeight;
		}
		
		/**
		 * �Ƿ�������
		 * @param hasGrid
		 */
		public void setHasGridView(boolean hasGrid){
			this.hasGridView = hasGrid;
		}

		/**
		 * ��������
		 * 
		 * @param canvas
		 */
		private void drawGrid(Canvas canvas) {
			Paint gridPaint = new Paint();
			gridPaint.setColor(gridColor);
			gridPaint.setStyle(Paint.Style.STROKE);
			gridPaint.setStyle(Paint.Style.STROKE);
			gridPaint.setStrokeWidth(gridWidth);
			
			// ������ 
			float gridStartX = edgeWidth+width; // ��ʼ�� X ����
			float gridStartY = canvasHeight-edgeWidth; // ��ʼ�� Y ����
			float gridEndPointX = gridStartX; /* X��Ľ��������� */
			float gridEndPointY = edgeWidth; /* X��Ľ��������� */
			while (true) {
				if (gridStartX >= (canvasWidth-edgeWidth)) {
					break;
				}
				canvas.drawLine(gridStartX, gridStartY, gridEndPointX, gridEndPointY, gridPaint);
				gridEndPointX = gridStartX = gridStartX+width;
				
			}

			// �����ǻ�����
			gridStartX = edgeWidth; // ��ʼ�� X ����
			gridStartY = canvasHeight-edgeWidth; // ��ʼ�� Y ����
			gridEndPointX = canvasWidth - edgeWidth; /* X��Ľ��������� */
			gridEndPointY = gridStartY; /* X��Ľ��������� */
			
			while (true) {
				if (gridStartY < edgeWidth) {
					break;
				}
				canvas.drawLine(gridStartX, gridStartY, gridEndPointX, gridEndPointY, gridPaint);
				gridEndPointY = gridStartY = gridStartY-height;
				
			}

		}
	}

	
	/**
	 * �̶ȵ�����
	 */
	private class SingXAxisMarkLabel {
		private short xPos = 0; // ��ʾ�̶ȴ�С
		private float stepDistance; // ��ʾ��X��ĳ���(ռ�ö��ٵ�dip)
		private float xScaleHeight ; // ��ʾ�̶ȵĸ߶�
		private float xScaleWidth; // ��ʾ�̶ȵĿ��
		public int xMarkColor;
		private String label; // ��ʾ�̶ȱ���ı�
		public int labelColor;
		public int label_size;

		public SingXAxisMarkLabel() {};

		public SingXAxisMarkLabel(short xPos, float stepDistance,
				float xScaleWidth, float xScaleHeight,int xMarkColor,String label, int labelColor,
				int label_size) {
			this.xPos = xPos;
			this.stepDistance = stepDistance;
			this.xScaleHeight = xScaleHeight;
			this.xScaleWidth = xScaleWidth;
			this.xMarkColor = xMarkColor;
			this.label = label;
			this.labelColor = labelColor;
			this.label_size = label_size;
			
		}
	}

	

	/**
	 *  Y��Ŀ̶ȱ���� 
	 */
	private class SingYAxisMarkLabel {

		public short yPos; // ��ʾ�̶ȴ�С
		public float yScaleHeight; // ��ʾ�̶ȵĸ߶�
		public float yScaleWidth; // ��ʾ�̶ȵĿ��
		public int yMarkColor;
		public String label; // ��ʾ�̶ȱ���ı�
		public int labelColor;
		public int label_size;

		public SingYAxisMarkLabel() {};

		public SingYAxisMarkLabel(short yPos, float yMarkWidth,
				float yMarkHeight, int yMarkColor, String label, int labelColor,
				int label_size) {
			this.yPos = yPos;
			this.yScaleHeight = yScaleHeight;
			this.yScaleWidth = yScaleWidth;
			this.yMarkColor = yMarkColor;
			this.label = label;
			this.labelColor = labelColor;
			this.label_size = label_size;
		}
	}

	/**
	 *  �ı���
	 */
	private class TextOutString {
		public short xTop; // �ı����Ͻǵĺ�����
		public short yTop; // �ı����Ͻǵ�������
		public short width; // �ı��Ŀ��
		public short height; // �ı��ĸ߶�
		public String text; // �ı�������
		public boolean bold; // �Ƿ��б߿�
		public boolean verticalAlgin; // �Ƿ�����
		public Paint textOutPaint;

		// ���캯��
		public TextOutString() {}
		public TextOutString(short xTop, short yTop, short width, short height,
				String text, short font_size, short font_color, boolean bold,
				boolean verticalAlgin) {
			this.textOutPaint = new Paint();

			this.xTop = xTop;
			this.yTop = yTop;
			this.width = width;
			this.height = height;
			this.text = text;
			this.textOutPaint.setTextSize(font_size);
			this.textOutPaint.setColor(font_color);
			this.bold = bold;
			this.verticalAlgin = verticalAlgin;
		}
	}

}
