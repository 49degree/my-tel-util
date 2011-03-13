package com.newElement.remp.sdk.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Paint;

public class DataGridViewPanel extends LinearLayout{

	private Paint	innerPaint, borderPaint ;
	public DataGridViewPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DataGridViewPanel(Context context ) {
		super(context);
		init(context);
	}
	
	private void init(Context context) {
		innerPaint = new Paint();
		innerPaint.setARGB(225, 75, 75, 75); //gray
		innerPaint.setAntiAlias(true);

		borderPaint = new Paint();
		borderPaint.setARGB(255, 120, 120, 120);
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(2);
	}
	
	public void setInnerPaint(Paint innerPaint) {
		this.innerPaint = innerPaint;
	}

	public void setBorderPaint(Paint borderPaint) {
		this.borderPaint = borderPaint;
	}

    @Override
    protected void dispatchDraw(Canvas canvas) {
    	
    	RectF drawRect = new RectF();
    	drawRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());
    	
    	canvas.drawRoundRect(drawRect, 5, 5, innerPaint);
		canvas.drawRoundRect(drawRect, 5, 5, borderPaint);
		
		super.dispatchDraw(canvas);
    }
}
