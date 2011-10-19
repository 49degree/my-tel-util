package com.guanri.fsk.utils;

public class Demo {
	public static final int FSKBUF = 4;
	public static int    currentx,currenty,lastx,last_sample =0;
	public static int    g_iFSKBuf[]  = new int[FSKBUF];
	public static int    g_iFSKAvg =0;
	public static int    g_iFSKBuf1[]  = new int[FSKBUF];
	public static int    g_iFSKAvg1=0;
	public static int    g_iFSKBuf2[]  = new int[FSKBUF];
	public static int    g_iFSKAvg2=0;
	public static byte    g_cFSKBufPoint=0;
	public static boolean decode(int g_cADCResult){
		    //byte    g_cADCResult = 0;//A/D的采样值

		    System.out.println("接收:"+g_cADCResult);
		    //在滤波之前将变量初化为0
		    //程序实现:(每次采样要做以下工作，注意采样频率和CID的波特率不是倍数关系
		    currentx = g_cADCResult;
		    currenty = last_sample;
		    last_sample = currentx;
		    //last sample in currenty,now sample in currenx;
		    currenty  *= currentx;//cos(t)*cos(t-T) = -/+sin(delta*T);
		    //------avg--lowpass filter;
		    g_iFSKAvg -= g_iFSKBuf[g_cFSKBufPoint];
		    g_iFSKBuf[g_cFSKBufPoint] = currenty;
		    g_iFSKAvg += currenty;
		    currenty = g_iFSKAvg;
		    //---------end filter;
		    g_iFSKAvg1 -= g_iFSKBuf1[g_cFSKBufPoint];
		    g_iFSKBuf1[g_cFSKBufPoint] = currenty;
		    g_iFSKAvg1 += currenty;
		    currenty = g_iFSKAvg1;
		    //second filter over
		    g_iFSKAvg2 -= g_iFSKBuf2[g_cFSKBufPoint];
		    g_iFSKBuf2[g_cFSKBufPoint] = currenty;
		    g_iFSKAvg2 += currenty;
		    currenty = g_iFSKAvg2;
		    //third filter over
		    g_cFSKBufPoint++;
		    g_cFSKBufPoint %= FSKBUF;
		    if(currenty>0)
		    {
		        //接收到bit 1
		    	System.out.println("接收到bit 1");
		    	return false;
		    }
		    else
		    {
		        //接收到bit 0\
		    	System.out.println("接收到bit 0");
		    	return true;
		    } 

	}
}
