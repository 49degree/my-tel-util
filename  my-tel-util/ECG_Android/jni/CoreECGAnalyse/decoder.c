    #define    FSKBUF 4
    byte    g_cADCResult;//A/D�Ĳ���ֵ
    int    currentx,currenty,lastx,last_sample;
    int    g_iFSKBuf[FSKBUF];
    int    g_iFSKAvg;
    int    g_iFSKBuf1[FSKBUF];
    int    g_iFSKAvg1;
    int    g_iFSKBuf2[FSKBUF];
    int    g_iFSKAvg2;
    byte    g_cFSKBufPoint;
    //���˲�֮ǰ����������Ϊ0
    ����ʵ��:(ÿ�β���Ҫ�����¹�����ע�����Ƶ�ʺ�CID�Ĳ����ʲ��Ǳ�����ϵ
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
        //���յ�bit 1
    }
    else
    {
        //���յ�bit 0
    }
