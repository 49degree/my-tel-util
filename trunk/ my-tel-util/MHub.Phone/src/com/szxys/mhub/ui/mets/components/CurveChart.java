package com.szxys.mhub.ui.mets.components;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Iterator;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CurveChart extends SurfaceView implements SurfaceHolder.Callback {
	public String TAG = "ShapeLine";
	
	private SurfaceHolder holder = null; // 控制对象
	private int canvasWidth = 2000;//画布的宽度
	private int canvasHeight = 200;//画布的高度
	//private int screenWidth; // 屏幕的宽
	//private int screenHeight; // 屏幕的高
	private int screenIndexLeftX; // 屏幕左下角的X坐标
	private int screenIndexLeftY; // 屏幕左下角的Y坐标
	
	private float xMinValue = 0;//设置X轴标识的最小数（默认为0）
	private float xMaxValue;//设置X轴标识的最大数
	private float yMinValue = 0;//设置Y轴标识的最小数（默认为0）
	private float yMaxValue;//设置Y轴标识的最大数
	private float xValuePerPix = 1F;//每像素标识X轴长度 默认为1
	private float yValuePerPix = 1F;//每像素标识Y轴长度 默认为1
	
	private Canvas canvas = null;//画布
	private YLine yLine = null;//X轴对象
	private XLine xLine = null;//Y轴对象
	private GridLine gridLine = null;//网格对象
	
	
	
	
	private HashMap<String,LiveCurve> listLiveCurve = new HashMap<String,LiveCurve>();/* 曲线集合 */


	private short edgeWidth = 15;//边距

	public CurveChart(Context context) {
		super(context);
		//Log.d(TAG,"ShapeLine1");
		holder = getHolder();
		holder.addCallback(this);
		setFocusable(true);  //确保我们的View能获得输入焦点
		setFocusableInTouchMode(true);  //确保能接收到触屏事件
	}

	public CurveChart(Context context, AttributeSet attrs) 
	{
		super(context,attrs);	
		Log.i(TAG,"ShapeLine2");
		holder = getHolder();
		holder.addCallback(this);
		
		xLine = new XLine();//初始化X对象
		yLine = new YLine();//初始化Y对象
		gridLine = new GridLine();//初始化网格对象

		setFocusable(true);  //确保我们的View能获得输入焦点
		setFocusableInTouchMode(true);  //确保能接收到触屏事件
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
		postInvalidate();
		holder.unlockCanvasAndPost(canvas);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		//Log.d(TAG,"surfaceDestroyed");

	}

	
	
	/**
	 * 初始化变量
	 */
	public void init(){
		canvasWidth = canvas.getWidth();
		canvasHeight = canvas.getHeight();

		screenIndexLeftX = getLeft();
		screenIndexLeftY = getBottom();
		
		Log.d(TAG,canvasWidth+":"+canvasHeight+":"+screenIndexLeftX+":"+screenIndexLeftY);
		
		//screenHeight=ECGApplication.getInstance().getScreenHeight();
		//screenWidth=ECGApplication.getInstance().getScreenWidth();
		
		//遍历listLiveCurve，绘图
		Iterator<String> iterator = listLiveCurve.keySet().iterator();
		while(iterator.hasNext()){
			String liveCurveId = iterator.next();
			LiveCurve tempLiveCurve = listLiveCurve.get(liveCurveId);
			int clr = tempLiveCurve.clr;
			float[] tempDataArray = null;
			if(tempLiveCurve.dataIndex>0){
				tempDataArray = new float[tempLiveCurve.dataIndex]; 
				System.arraycopy(tempLiveCurve.dataArray, 0,tempDataArray , 0, tempLiveCurve.dataIndex);
			}
			tempLiveCurve = new LiveCurve(tempDataArray,tempLiveCurve.maxData);
			tempLiveCurve.setColor(clr);
			listLiveCurve.put(liveCurveId, tempLiveCurve);
		}
		
	}
	
	public void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Log.d(TAG,"onDraw");
		if(canvas!=null){
			try{
				if(xLine!=null){
					xLine.drawXLine(canvas);
				}
				if(yLine!=null){
					yLine.drawYLine(canvas);
				}
				if(gridLine!=null&&gridLine.hasGridView){
					gridLine.drawGrid(canvas);
				}
				drawXAxisMarkLabel(canvas);
				drawYAxisMarkLabel(canvas);
				drawYTAxisMarkLabel(canvas);
				drawText(canvas);
				drawXTLineList(canvas);
				drawYTLineList(canvas);
				drawAllCurveLine(canvas);
			}catch(Exception e){
				e.printStackTrace();
				
			}
		}
	}
	
	 
	/**
	 * 触发页面重绘
	 */
	public void postInvalidate() {
		super.postInvalidate();
		Log.d(TAG,"onDraw");
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

		}
		return true;
	}
	
	
	
	
	/**
	 * 增加曲线并插入数据
	 * @param curveId
	 * @param data
	 */
	public void addCurveLine(int curveId, float[] datas,float maxData) {
		
		String curveIdStr = String.valueOf(curveId);
		if(listLiveCurve.containsKey(curveIdStr)){
			listLiveCurve.get(curveIdStr).addData(datas);
		}else{
			listLiveCurve.put(curveIdStr, new LiveCurve(datas,maxData));
		}
	}
	
	/**
	 * 增加曲线并插入数据
	 * @param curveId
	 * @param data
	 */
	public void addCurveLine(int curveId, float[] datas,int color,float maxValue) {
		String curveIdStr = String.valueOf(curveId);
		if(listLiveCurve.containsKey(curveIdStr)){
			listLiveCurve.get(curveIdStr).addData(datas);
			listLiveCurve.get(curveIdStr).setColor(color);
		}else{
			LiveCurve curv = new LiveCurve(datas,maxValue);
			curv.setColor(color);
			listLiveCurve.put(curveIdStr, curv);
		}
		
		
	}
	
	
    //设置曲线的颜色
    public void setCurveColor(int curveId,int clr)
    {
		String curveIdStr = String.valueOf(curveId);
		if(listLiveCurve.containsKey(curveIdStr)){
			listLiveCurve.get(curveIdStr).setColor(clr);
		}
		
    }
	/**
	 *  曲线更新数据
	 * @param curveId
	 * @param data
	 */
	public boolean pushCurveLineData(int curveId, float[] datas) {
		String curveIdStr = String.valueOf(curveId);
		if(listLiveCurve.containsKey(curveIdStr)){
			listLiveCurve.get(curveIdStr).addData(datas);
			return true;
		}else{
			return false;	
		}
	}

	/**
	 *  绘制数据曲线
	 * @param canvas
	 */
	private void drawAllCurveLine(Canvas canvas) {
		//Log.d("postInvalidate","drawAllCurveLine");
		//遍历listLiveCurve，绘图
		if(canvas!=null){
			//Log.d("postInvalidate","drawAllCurveLine");
			Iterator<String> iterator = listLiveCurve.keySet().iterator();
			while(iterator.hasNext()){
				listLiveCurve.get(iterator.next()).drawCurveLine(canvas);
			}
		}
	}

	/**
	 * 设置X,Y轴标识的数据范围
	 * 并计算X,Y轴每像素标识的长度
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
	 * 网格的高度好宽度
	 * @param gridWidth
	 * @param gridHeight
	 */
	public void setGridWithHeight(short gridWidth,short gridHeight){
		if(gridLine!=null){
			gridLine.setGridWithHeight(gridWidth, gridHeight);
		}
	}
	
	/**
	 * 是否有网格
	 * @param hasGrid
	 */
	public void setHasGridView(boolean hasGrid){
		if(gridLine!=null){
			gridLine.setHasGridView(hasGrid);
		}
	}
	
	/**
	 * 曲线类 
	 */
	private class LiveCurve {
		//public short curveId; // 曲线Id
		public int clr = Color.WHITE; // 曲线的颜色
		public short width = 1; // 曲线的宽度
		public float[] dataArray = null;//Y轴的元素
		public int dataIndex = 0;//Y轴的元素的末尾下标 0标识为空
		public float maxData = 0;//Y轴的元素的末尾下标 0标识为空
		
		private int drawCanvasWidth = canvasWidth-2*edgeWidth;//可绘制曲线的屏幕宽度
		private int drawCanvasHeight = canvasHeight-2*edgeWidth;//可绘制曲线的屏幕高度
		
		public Path linePath = null;//画线path对象
		Paint paint = null;
		/**
		 * 默认构造函数
		 */
		public LiveCurve(int dataLength){
			dataArray = new float[dataLength];

		}
		/**
		 * 构造函数
		 * @param datas
		 */
		public LiveCurve(float[] datas,float maxData){
			this(datas.length);
			this.maxData = maxData;
			this.addData(datas);
		}
		
		public void setColor(int clr){
			this.clr = clr;
		}
		/**
		 * 增加曲线坐标
		 * @param datas
		 */
		public void addData(float[] datas){
			if(datas==null){
				return ;
			}
			int datasLength = datas.length;
			//Log.d("datasLength",datasLength+"");
			if(datasLength>=dataArray.length){//如果新数据长度大于可容纳长度，则以新数据的后边填充全部数据
				System.arraycopy(datas, datasLength - dataArray.length, dataArray, 0, dataArray.length);//把datas的数据COPY到Y轴的元素的末尾
				dataIndex = dataArray.length;
			}else{
				/*
				 * dataArray.length-dataIndex表示还有多少空的位置
				 * datas.length-(dataArray.length-dataIndex)表示需要在末尾清空多少个位置才能放下新增加的datas
				 * dataIndex-clearNun计算可保留的长度
				 */
				int clearNun = datasLength-(dataArray.length-dataIndex);
				
				if(clearNun>0){
					int leavLength = dataIndex-clearNun;//表示可保留的长度
					float[] leavInt = new float[leavLength];
					System.arraycopy(dataArray, clearNun, leavInt, 0, leavLength);
					System.arraycopy(leavInt, 0, dataArray, 0, leavLength);//把Y轴的元素往前移动clearNun长度，并把头丢弃
					System.arraycopy(datas, 0, dataArray, leavLength, datasLength);//把datas的数据COPY到Y轴的元素的末尾
					dataIndex = dataArray.length;
				}else{
					System.arraycopy(datas, 0, dataArray, dataIndex, datasLength);//把datas的数据COPY到Y轴的元素的末尾
					dataIndex+=datasLength;
				}
			}
		}
		
		//int step = 0;
		
		/**
		 * 绘制曲线
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
			
			//Log.d("dataArray",yValuePerPix+":"+yMaxValue+":"+yMinValue);
			if(dataArray!=null&&dataArray.length>0){
				//最大值必须大于最小值
				xValuePerPix = ((float)dataArray.length/drawCanvasWidth);
				//最大值必须大于最小值
				if(yMaxValue>yMinValue){//计算每像素标识Y轴长度 默认为1
					yValuePerPix = Math.abs((maxData-yMinValue)/drawCanvasHeight);
				}
				Log.e("dataArray0",drawCanvasWidth+":"+drawCanvasHeight);
				Log.e("dataArray",xValuePerPix+":"+yValuePerPix);
				float pointX = edgeWidth;
				float pointY = drawCanvasHeight+edgeWidth-(Math.abs(dataArray[0]-yMinValue))/yValuePerPix;//Y轴相对于画布的像素位置
				//Log.d("dataArray",dataIndex-1+":"+dataArray[dataIndex-1]+":"+pointY);
				linePath.moveTo(pointX, pointY);//开始的点坐标，表示从右往左移动
				if(dataArray.length>1){
					for(int i=1;i<dataArray.length;i++){
						pointX = edgeWidth+i/xValuePerPix;//Y轴相对于画布的像素位置
						pointY = drawCanvasHeight+edgeWidth-(Math.abs(dataArray[i]-yMinValue))/yValuePerPix;//Y轴相对于画布的像素位置
						//Log.e("dataArray",i+":"+dataArray[i]+":"+pointX);
						linePath.lineTo(pointX, pointY);
					}
				}
				canvas.drawPath(linePath, paint);
			}

		}
	}
	
	
	/**
	 * X 轴对象
	 * @author Administrator
	 *
	 */
	private class XLine{


		private int xAxisColor = Color.GRAY; // 默认x轴的颜色
		private float xAxisWidth = 0.5f; // 默认x轴的宽度
		private boolean xAxisVisible = true; // x轴是否可见，默认可见
		/**
		 *  绘制X轴
		 * @param canvas
		 */
		public void drawXLine(Canvas canvas) {
			Paint xPaint = new Paint();
			xPaint.setColor(xAxisColor);
			if (!xAxisVisible)
				xPaint.setAlpha(1);
			xPaint.setStyle(Paint.Style.STROKE);
			xPaint.setStrokeWidth(xAxisWidth);
			// 画直线
			float xStartPointX = edgeWidth; /* X轴的起始横坐标 */
			float xStartPointY = canvasHeight - edgeWidth; /* X轴的起始纵坐标 */
			float xEndPointX = canvasWidth-edgeWidth; /* X轴的结束横坐标 */
			float xEndPointY = xStartPointY; /* X轴的结束纵坐标 */
			
			//Log.d("drawXLine","drawXLine");
			canvas.drawLine(xStartPointX, xStartPointY, xEndPointX,xEndPointY, xPaint);
		}		
		
	}
	
	/**
	 * 画任意X 轴对象
	 * @author Administrator
	 *
	 */
	private class XTLine{
		private int xAxisColor = Color.GRAY; // 默认x轴的颜色
		private float xAxisWidth = 0.5f; // 默认x轴的宽度
		private boolean xAxisVisible = true; // x轴是否可见，默认可见
		float yStartPointX = 0f;
		float maxValue = 0f;
		public XTLine(float yPoint,float maxValue){
			yStartPointX = yPoint;
			this.maxValue = maxValue;
		}
		/**
		 *  绘制X轴
		 * @param canvas
		 */
		public void drawXLine(Canvas canvas) {
			Paint xPaint = new Paint();
			xPaint.setColor(xAxisColor);
			if (!xAxisVisible)
				xPaint.setAlpha(1);
			xPaint.setStyle(Paint.Style.STROKE);
			xPaint.setStrokeWidth(xAxisWidth);
			// 画直线
			float xStartPointX = edgeWidth; /* X轴的起始横坐标 */
			float xEndPointX = canvasWidth-edgeWidth; /* X轴的结束横坐标 */
			
    		int drawCanvasHeight = canvasHeight-2*edgeWidth;//可绘制曲线的屏幕高度
    		float yValuePerPix = 1;
			//最大值必须大于最小值
			if(yMaxValue>yMinValue){//计算每像素标识Y轴长度 默认为1
				yValuePerPix = Math.abs((maxValue-yMinValue)/drawCanvasHeight);
			}else{
				yMaxValue = drawCanvasHeight;
				yMinValue = 0;
			}
			float yPoint = edgeWidth+(Math.abs(yStartPointX-yMinValue))/yValuePerPix;//x轴相对于画布的像素位置
			canvas.drawLine(xStartPointX, yPoint, xEndPointX,yPoint, xPaint);
		}		
		
	}
	
	List<XTLine> xTLineList = new ArrayList<XTLine>();
	public void addXTLine(float yPoint,float maxValue){
		xTLineList.add(new XTLine(yPoint,maxValue));
	}
	
    //画任意Y轴
    private void drawXTLineList(Canvas canvas){
    	if(yTLineList!=null){
    		for(XTLine xTLine:xTLineList){
    			xTLine.drawXLine(canvas);
    		}
    	}
    }
	
	/**
	 * Y 轴对象
	 * @author Administrator
	 *
	 */
	private class YLine{
		private boolean yAxisVisible = true; // Y轴是否可见，默认可见
		private int yAxisColor = Color.GRAY; // 默认 Y 轴的颜色
		private Paint yPaint; // 画 Y 轴的样式设置
		private float yAxisWidth = 0.5f; // 默认x,y轴的宽度
		/**
		 *  画 Y 轴
		 * @param canvas
		 */
		private void drawYLine(Canvas canvas) {
			float yAxisStartX = edgeWidth; // 起始的 X 坐标
			float yAxisStartY = canvasHeight-edgeWidth; // 起始的 Y 坐标
			float xEndPointX = edgeWidth; /* X轴的结束横坐标 */
			float xEndPointY = edgeWidth; /* X轴的结束纵坐标 */
			YAxisPaint();
			canvas.drawLine(yAxisStartX, yAxisStartY, xEndPointX, xEndPointY, yPaint);
			
			yAxisStartX = canvasWidth - edgeWidth; // 起始的 X 坐标
			yAxisStartY = canvasHeight-edgeWidth; // 起始的 Y 坐标
			xEndPointX = canvasWidth - edgeWidth; /* X轴的结束横坐标 */
			xEndPointY = edgeWidth; /* X轴的结束纵坐标 */
			// 如果 Y 轴停靠在右边
			canvas.drawLine(canvasWidth - edgeWidth, canvasHeight-edgeWidth, canvasWidth - edgeWidth, edgeWidth, yPaint);

		}
		
		/**
		 *  画 Y 轴的画笔
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
	 * 任意Y 轴对象
	 * @author Administrator
	 *
	 */
	private class YTLine{
		private boolean yAxisVisible = true; // Y轴是否可见，默认可见
		private int yAxisColor = Color.GRAY; // 默认 Y 轴的颜色
		private Paint yPaint; // 画 Y 轴的样式设置
		private float yAxisWidth = 0.5f; // 默认x,y轴的宽度
		float xAxisStartX = 0f;
		public YTLine(float xPoint){
			xAxisStartX = xPoint;
			
		}
		/**
		 *  画 Y 轴
		 * @param canvas
		 */
		private void drawYLine(Canvas canvas) {
			float yAxisStartY = canvasHeight-edgeWidth; // 起始的 Y 坐标
			float xEndPointY = edgeWidth; /*结束Y坐标 */
			
			int drawCanvasWidth = canvasWidth-2*edgeWidth;//可绘制曲线的屏幕宽度
			//最大值必须大于最小值
			if(xMaxValue>xMinValue){//计算每像素标识X轴长度 默认为1
				xValuePerPix = Math.abs((xMaxValue-xMinValue)/drawCanvasWidth);
			}
    		float xPoint = edgeWidth+(Math.abs(xAxisStartX-xMinValue))/xValuePerPix;//x轴相对于画布的像素位置
			YAxisPaint();
			canvas.drawLine(xPoint, yAxisStartY, xPoint, xEndPointY, yPaint);
		}
		
		/**
		 *  画 Y 轴的画笔
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
	
	List<YTLine> yTLineList = new ArrayList<YTLine>();
	public void addYTLine(float xPoint){
		yTLineList.add(new YTLine(xPoint));
	}
	
    //画任意Y轴
    private void drawYTLineList(Canvas canvas){
    	if(yTLineList!=null){
    		for(YTLine yTLine:yTLineList){
    			yTLine.drawYLine(canvas);
    		}
    	}
    }	
	
	/**
	 * 网格对象
	 * @author Administrator
	 *
	 */
	private class GridLine {
		private short width = 10;
		private short height = 10;
		private int gridColor = Color.GRAY; // 网格默认的颜色
		private float gridWidth = 0.2f; // 网格线默认的宽带
		private boolean hasGridView = true;//是否有网格，默认有

		public GridLine() {
		}
		/**
		 * 网格的高度好宽度
		 * @param gridWidth
		 * @param gridHeight
		 */
		public void setGridWithHeight(short gridWidth,short gridHeight){
			this.width = gridWidth;
			this.height = gridHeight;
		}
		
		/**
		 * 是否有网格
		 * @param hasGrid
		 */
		public void setHasGridView(boolean hasGrid){
			this.hasGridView = hasGrid;
		}

		/**
		 * 绘制网格
		 * 
		 * @param canvas
		 */
		private void drawGrid(Canvas canvas) {
			Paint gridPaint = new Paint();
			gridPaint.setColor(gridColor);
			gridPaint.setStyle(Paint.Style.STROKE);
			gridPaint.setStyle(Paint.Style.STROKE);
			gridPaint.setStrokeWidth(gridWidth);
			
			// 画横线 
			float gridStartX = edgeWidth+width; // 起始的 X 坐标
			float gridStartY = canvasHeight-edgeWidth; // 起始的 Y 坐标
			float gridEndPointX = gridStartX; /* X轴的结束横坐标 */
			float gridEndPointY = edgeWidth; /* X轴的结束纵坐标 */
//			while (true) {
//				if (gridStartX >= (canvasWidth-edgeWidth)) {
//					break;
//				}
//				canvas.drawLine(gridStartX, gridStartY, gridEndPointX, gridEndPointY, gridPaint);
//				gridEndPointX = gridStartX = gridStartX+width;
//				
//			}

			// 以下是画纵线
			gridStartX = edgeWidth; // 起始的 X 坐标
			gridStartY = canvasHeight-edgeWidth; // 起始的 Y 坐标
			gridEndPointX = canvasWidth - edgeWidth; /* X轴的结束横坐标 */
			gridEndPointY = gridStartY; /* X轴的结束纵坐标 */
			
			while (true) {
				if (gridStartY < edgeWidth) {
					break;
				}
				canvas.drawLine(gridStartX, gridStartY, gridEndPointX, gridEndPointY, gridPaint);
				gridEndPointY = gridStartY = gridStartY-height;
				
			}

		}
	}

	
	/*刻度的配置*/
	private class SingXAxisMarkLabel {
			public float xPos;            //表示刻度大小
			public float stepDistance;  //表示在X轴的长度(占用多少的dip)
			public Paint xScalePaint;   //表示刻度的标记
			public float xScaleHeight;    //表示刻度的高度
			public float xScaleWidth;     //表示刻度的宽度
			
			public String label;        //表示刻度标记文本
	        public Paint labelPaint;    //表示刻度标记样式
			
	        public SingXAxisMarkLabel(){};
			public SingXAxisMarkLabel(float xPos,float stepDistance,float xScaleWidth,float xScaleHeight,Paint PaintxScale,String label,Paint labelPaint)
			{
				this.xPos = xPos;
				this.stepDistance = stepDistance;
				this.xScaleHeight = xScaleHeight;
	            this.xScaleWidth = xScaleWidth;
	            this.xScalePaint = PaintxScale;
				
				this.label = label;
				this.labelPaint = labelPaint;
			}
	}

	/* X轴增加刻度的集合   */
	private List<SingXAxisMarkLabel> listsingXAxisMarkLabel = new ArrayList<SingXAxisMarkLabel>();
  	/* X轴是否增加了标记  */
	private boolean IsAddXAxisMarkLabel = false;      //用来标识是否增加了X轴上的标记
    //增加X轴刻度标记
    public void addXAxisMarkLabel(float xPos,float stepDistance,float xMarkWidth,float xMarkHeight,int xMarkColor,String label,int labelColor,int label_size)
    {
    	Paint xPaint = new Paint();
    	xPaint.setStyle(Paint.Style.STROKE);
    	xPaint.setStrokeWidth(xMarkWidth);
    	xPaint.setColor(xMarkColor);
		
    	Paint labelPaint = new Paint();
	    labelPaint.setColor(labelColor);
	    labelPaint.setTextSize(label_size);
		labelPaint.setTextAlign(Paint.Align.CENTER);

    	SingXAxisMarkLabel tempsingXAxisMarkLabel = new SingXAxisMarkLabel(xPos,stepDistance,xMarkWidth,xMarkHeight,xPaint,label,labelPaint);
    	listsingXAxisMarkLabel.add(tempsingXAxisMarkLabel);
    	IsAddXAxisMarkLabel = true;
    	
    }
    
    //画X轴上的标记
    private void drawXAxisMarkLabel(Canvas canvas){
    	if(IsAddXAxisMarkLabel&&listsingXAxisMarkLabel!=null){
    		float pointY = canvasHeight-2;
    		float pointX = 0f;
    		
    		int drawCanvasWidth = canvasWidth-2*edgeWidth;//可绘制曲线的屏幕宽度
    		
    		
			//最大值必须大于最小值
			if(xMaxValue>xMinValue){//计算每像素标识X轴长度 默认为1
				xValuePerPix = Math.abs((xMaxValue-xMinValue)/drawCanvasWidth);
			}else{
				xMaxValue = drawCanvasWidth;
				xMinValue = 0;
			}

    		for(SingXAxisMarkLabel tempsingXAxisMarkLabel:listsingXAxisMarkLabel){
    			pointX = edgeWidth+(Math.abs(tempsingXAxisMarkLabel.stepDistance-xMinValue))/xValuePerPix;//Y轴相对于画布的像素位置
		    	//需求不需要X轴的刻度，刻度值
    			//canvas.drawLine(x,xStartPointY+xAxisWidth,x,y+3, listsingXAxisMarkLabel.get(i).xScalePaint);
    			canvas.drawText(tempsingXAxisMarkLabel.label,pointX,pointY, tempsingXAxisMarkLabel.labelPaint);	
    		}
    	}
    }	

	/* Y轴是否增加了标记    */
	private boolean IsAddYAxisMarkLabel;      //用来标识是否增加了Y轴上的标记
	/* Y'轴是否增加了标记  */
	private boolean IsAddYTAxisMarkLabel;      //用来标识是否增加了Y'轴上的标记
	/*  Y轴刻度标记的集合     */
	private List<SingYAxisMarkLabel> listsingYAxisMarkLabel = new ArrayList<SingYAxisMarkLabel>();
	/*  Y'轴刻度标记的集合     */
	private List<SingYAxisMarkLabel> listsingYTAxisMarkLabel = new ArrayList<SingYAxisMarkLabel>();
	/* Y轴的刻度标记类   */
	private class SingYAxisMarkLabel {

			public float yPos;            //表示刻度大小
			public Paint yScalePaint;   //表示刻度的标记
			public float yScaleHeight;    //表示刻度的高度
			public float yScaleWidth;     //表示刻度的宽度
			
			public String label;        //表示刻度标记文本
	        public Paint labelPaint;    //表示刻度标记样式
			
	        public SingYAxisMarkLabel(){};
			public SingYAxisMarkLabel(float yPos,float yScaleWidth,float yScaleHeight,Paint PaintxScale,String label,Paint labelPaint)
			{
				this.yPos = yPos;
				this.yScaleHeight = yScaleHeight;
	            this.yScaleWidth = yScaleWidth;
	            this.yScalePaint = PaintxScale;
				
				this.label = label;
				this.labelPaint = labelPaint;
			}
	}

	
    
    //增加Y轴刻度标记
    public void addYAxisMarkLabel(float yPos, float yMarkWidth, float yMarkHeight,int yMarkColor,String label,int labelColor,int label_size)
    {
    	Paint yPaint = new Paint();
    	yPaint.setStyle(Paint.Style.STROKE);
    	yPaint.setStrokeWidth(yMarkWidth);
    	yPaint.setColor(yMarkColor);
    	
    	Paint labelPaint = new Paint();
	    labelPaint.setColor(labelColor);
	    labelPaint.setTextSize(label_size);
		
		SingYAxisMarkLabel tempsingYAxisMarkLabel = new SingYAxisMarkLabel(yPos,yMarkWidth,yMarkHeight,yPaint,label,labelPaint);
		listsingYAxisMarkLabel.add(tempsingYAxisMarkLabel);  
		IsAddYAxisMarkLabel = true;
    }
    
    //增加Y'轴刻度标记
    public void addYTAxisMarkLabel(float yPos, float yMarkWidth, float yMarkHeight,int yMarkColor,String label,int labelColor,int label_size)
    {
    	Paint yPaint = new Paint();
    	yPaint.setStyle(Paint.Style.STROKE);
    	yPaint.setStrokeWidth(yMarkWidth);
    	yPaint.setColor(yMarkColor);
    	
    	Paint labelPaint = new Paint();
	    labelPaint.setColor(labelColor);
	    labelPaint.setTextSize(label_size);
		
		SingYAxisMarkLabel tempsingYTAxisMarkLabel = new SingYAxisMarkLabel(yPos,yMarkWidth,yMarkHeight,yPaint,label,labelPaint);
		listsingYTAxisMarkLabel.add(tempsingYTAxisMarkLabel);  
		IsAddYTAxisMarkLabel = true;
    }
	
    //画Y轴上的标记
    private void drawYAxisMarkLabel(Canvas canvas)
    {
    	/*  如果IsAddYAxisMarkLabel为false直接返回    */
    	if(IsAddYAxisMarkLabel&&listsingYAxisMarkLabel!=null){
    		float pointY = canvasHeight-edgeWidth;
    		float pointX = 2;
    		
    		int drawCanvasHeight = canvasHeight-2*edgeWidth;//可绘制曲线的屏幕高度

			//最大值必须大于最小值
			if(yMaxValue>yMinValue){//计算每像素标识Y轴长度 默认为1
				yValuePerPix = Math.abs((yMaxValue-yMinValue)/drawCanvasHeight);
			}else{
				yMaxValue = drawCanvasHeight;
				yMinValue = 0;
			}
    		for(SingYAxisMarkLabel tempsingYAxisMarkLabel:listsingYAxisMarkLabel){
    			pointY = drawCanvasHeight+edgeWidth-(tempsingYAxisMarkLabel.yPos)/yValuePerPix;//Y轴相对于画布的像素位置
		    	//需求不需要X轴的刻度，刻度值
    			//canvas.drawLine(x,xStartPointY+xAxisWidth,x,y+3, listsingXAxisMarkLabel.get(i).xScalePaint);
    			canvas.drawText(tempsingYAxisMarkLabel.label,pointX,pointY, tempsingYAxisMarkLabel.labelPaint);	
    		}
    	}
    }
    
    //画Y'轴上的标记
    private void drawYTAxisMarkLabel(Canvas canvas)
    {

    	/*  如果IsAddYAxisMarkLabel为false直接返回    */
    	if(IsAddYTAxisMarkLabel&&listsingYTAxisMarkLabel!=null){
    		float pointY = canvasHeight-edgeWidth;
    		float pointX = canvasWidth-edgeWidth+2;
    		
    		int drawCanvasHeight = canvasHeight-2*edgeWidth;//可绘制曲线的屏幕高度
//    		int yTMaxValue = 378;
//    		float yTValuePerPix = 1f;
//			//最大值必须大于最小值
//			if(yTMaxValue>yMinValue){//计算每像素标识Y轴长度 默认为1
//				yTValuePerPix = Math.abs((yTMaxValue-yMinValue)/drawCanvasHeight);
//			}else{
//				yTMaxValue = drawCanvasHeight;
//				yMinValue = 0;
//			}
			
			//最大值必须大于最小值
			if(yMaxValue>yMinValue){//计算每像素标识Y轴长度 默认为1
				yValuePerPix = Math.abs((yMaxValue-yMinValue)/drawCanvasHeight);
			}else{
				yMaxValue = drawCanvasHeight;
				yMinValue = 0;
			}
    		for(SingYAxisMarkLabel tempsingYAxisMarkLabel:listsingYTAxisMarkLabel){
    			pointY = drawCanvasHeight+edgeWidth-(tempsingYAxisMarkLabel.yPos)/yValuePerPix;//Y轴相对于画布的像素位置
		    	//需求不需要X轴的刻度，刻度值
    			//canvas.drawLine(x,xStartPointY+xAxisWidth,x,y+3, listsingXAxisMarkLabel.get(i).xScalePaint);
    			canvas.drawText(tempsingYAxisMarkLabel.label,pointX,pointY, tempsingYAxisMarkLabel.labelPaint);	
    		}
    	}
    }
    	
	
	

	/**
	 *  文本类
	 */
	private class TextOutString {
		public float xTop; // 文本左上角的横坐标
		public float yTop; // 文本左上角的纵坐标
		public float width; // 文本的宽度
		public float height; // 文本的高度
		public String text; // 文本的内容
		public boolean bold; // 是否有边框
		public boolean verticalAlgin; // 是否竖排
		public Paint textOutPaint;

		// 构造函数
		public TextOutString() {}
		public TextOutString(float xTop, float yTop, float width, float height,
				String text, int font_size, int font_color, boolean bold,
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
    
	private List<TextOutString> listsingText = new ArrayList<TextOutString>();
    //增加文本
    public void addDrawText(float xTop, float yTop, float width, float height,
			String text, int font_size, int font_color, boolean bold,
			boolean verticalAlgin){
		
	    TextOutString tempsingYAxisMarkLabel = new TextOutString(xTop, yTop, width, height,
				text, font_size, font_color, bold,verticalAlgin);

	    listsingText.add(tempsingYAxisMarkLabel);  
    }
    
    //绘制文本
    private void drawText(Canvas canvas)
    {
    	/*  如果IsAddYAxisMarkLabel为false直接返回    */
    	if(listsingText!=null){
    		int drawCanvasHeight = canvasHeight-2*edgeWidth;//可绘制曲线的屏幕高度
    		int drawCanvasWidth = canvasWidth-2*edgeWidth;//可绘制曲线的屏幕宽度

			//最大值必须大于最小值
			if(xMaxValue>xMinValue){//计算每像素标识X轴长度 默认为1
				xValuePerPix = Math.abs((xMaxValue-xMinValue)/drawCanvasWidth);
			}else{
				xMaxValue = drawCanvasWidth;
				xMinValue = 0;
			}
			//最大值必须大于最小值
			if(yMaxValue>yMinValue){//计算每像素标识Y轴长度 默认为1
				yValuePerPix = Math.abs((yMaxValue-yMinValue)/drawCanvasHeight);
			}else{
				yMaxValue = drawCanvasHeight;
				yMinValue = 0;
			}
			
		    
    		for(TextOutString textOutString:listsingText){
        		float pointY = canvasHeight-edgeWidth;
        		float pointX = canvasWidth-edgeWidth+2;

        		pointX = edgeWidth+(textOutString.xTop)/xValuePerPix;//Y轴相对于画布的像素位置
    			pointY = drawCanvasHeight+edgeWidth-(textOutString.yTop)/yValuePerPix;//Y轴相对于画布的像素位置
    			canvas.drawText(textOutString.text,pointX,pointY, textOutString.textOutPaint);	
    		}
    	}
    }    
    
}

