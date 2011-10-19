    #define    FSKBUF 4
    byte    g_cADCResult;//A/D的采样值
    int    currentx,currenty,lastx,last_sample;
    int    g_iFSKBuf[FSKBUF];
    int    g_iFSKAvg;
    int    g_iFSKBuf1[FSKBUF];
    int    g_iFSKAvg1;
    int    g_iFSKBuf2[FSKBUF];
    int    g_iFSKAvg2;
    byte    g_cFSKBufPoint;
    //在滤波之前将变量初化为0
    程序实现:(每次采样要做以下工作，注意采样频率和CID的波特率不是倍数关系
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
    }
    else
    {
        //接收到bit 0
    }
