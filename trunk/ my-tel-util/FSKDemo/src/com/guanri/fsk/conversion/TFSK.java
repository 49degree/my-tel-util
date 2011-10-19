package com.guanri.fsk.conversion;


public class TFSK {
	static final int cSampFreq = 11025;	//采样频率
	static final int cSig0Freq = 2200;		//信号0频率
	static final int cSig1Freq = 1200;		//信号1频率
	static final int cBaudRate = 1200;		//波特率
	
	static final long cAmplitude = 16384;	//振幅
	static final long cOrigin = 0;			//基准值
	
	static final byte s_Start = 0;
	static final byte s_b0 = 1;
	static final byte s_b1 = 2;
	static final byte s_b2 = 3;
	static final byte s_b3 = 4;
	static final byte s_b4 = 5;
	static final byte s_b5 = 6;
	static final byte s_b6 = 7;
	static final byte s_b7 = 8;
	static final byte s_Stop = 9;
	
	
	
	int SampFreq = 11025;  	//采样频率
	double Limit = 0.18;		//判断阀值, 相对于振幅的百分比. 当计算后的采样值小于此值时, 则认为是信号0
	boolean Started = false;	//开始标志, 当识别到信号0时, 该标志为真
	
	int Position = 0;
	int SigCount = 0;
	int NextSigPos = 0;
	double Radian = 0;
	
	byte State = s_Start;
	byte ByteValue = 0;
	
	double[] BPF_Sig0 = null; 
	int Len_Sig0 = 0;
	int Index_Sig0 = 0;
	double Total_Sig0 = 0;
	
	double[] BPF_Baud = null; 
	int Len_Baud = 0;
	int Index_Baud = 0;
	double Total_Baud = 0;
	
	double LastValue = 0, LastValue1 = 0, LastValue2 = 0;//最近采样的3个点
	
	double MaxValue = 0, MinValue = 0;
	double[] ArrMax = null, ArrMin = null;
	boolean HaveMax = false, HaveMin = false;
	int Len_MaxMin = 0;
	int Index_MaxMin = 0;
	double Total_Max = 0, Total_Min = 0;
	boolean HaveLen_Max = false;
	
	double Origin = 0;//计算出的基准值
	double Amplitude = 0;//计算出的振幅
	
	double LPF = 0;
	
	boolean Reverse = false;
	
	int Data;
	boolean Sig0;
	

	void CalcNextSigPos(){
	    NextSigPos = NextSigPos +  SampFreq / cBaudRate;
	    SigCount++;
	    if (SigCount > cBaudRate){
	    	SigCount = SigCount - cBaudRate;
	    	NextSigPos = NextSigPos - Position;
	    	Position = 0;
	    	
	    }
	    
	}
	
    void ReviseNextSigPos(){
    	SigCount = 0;
    	NextSigPos = 0;
    	Position = 0;
    	Started = false;
    	CalcNextSigPos();
    }	
	
	
	public void Init(int ASampFreq, int AOrigin){
		SampFreq = ASampFreq;
		
		Limit = 0.68;
		
		Len_Sig0 = Math.round(SampFreq / cSig0Freq);
		BPF_Sig0 = new double [Len_Sig0];
		
		Len_Baud = Math.round(SampFreq / cBaudRate);
		BPF_Baud = new double [Len_Baud];
		
		Len_MaxMin = 20;		
		ArrMax = new double [Len_MaxMin];
		ArrMin = new double [Len_MaxMin];
		
		Origin = AOrigin;
		Amplitude = 3000;
		
		ReviseNextSigPos();
	}
	
	public void Init(int ASampFreq){
		Init(ASampFreq, 0);
	}
	
	public boolean Encode(){
		boolean Result = false;
		double R;
		
		R = Math.sin(Radian) * cAmplitude + Origin;
	    if (Sig0)
	      Radian = Radian +  (2 * Math.PI) / ((double)SampFreq / cSig0Freq);
	    else
	      Radian = Radian +  (2 * Math.PI) / ((double)SampFreq / cSig1Freq);
	    
	    Data = (int) Math.round(R);
	    
	    Position ++;
	    
	    Result = Position == NextSigPos;
	    if (Result) 
	    	CalcNextSigPos();
	    
		return Result;
	}
	
	public boolean EncodeByte() {
		boolean Result = false;
		switch (State) {
		case s_Start:
			Sig0 = true;
			if (Encode()) {
				//ByteValue = B;
				State ++;
			}
			break;
		case s_b0:
		case s_b1:
		case s_b2:
		case s_b3:
		case s_b4:
		case s_b5:
		case s_b6:
		case s_b7:
			Sig0 = (ByteValue & 1) == 0;
			if (Encode()) {
				ByteValue >>= 1;
				State ++;
			}
			break;
		case s_Stop:
			Sig0 = false;
			Result = Encode();
			if (Result) {
				State = s_Start;
			}
			break;
		default:
			State = s_Start;						
		}
		return Result;
	}
	
	void CalcAm(double d) {
		 LastValue2 = LastValue1;
		 LastValue1 = LastValue;
		 LastValue = d;
		 if ((LastValue <= LastValue1) & (LastValue2 <= LastValue1)) {    //判断最大值
//		 	if (LastValue1 < 1000) return;
//		 	if (MaxValue > 0) {
//		 	
//		 		if (LastValue1 < (MaxValue * 0.4)) return;
//		 		if (LastValue1 > (MaxValue * 1.8)) return;
//		 	}

		 	MaxValue = LastValue1;

		 	Index_MaxMin = Index_MaxMin % Len_MaxMin;
		 	Total_Max = Total_Max - ArrMax[Index_MaxMin] + MaxValue;
		 	ArrMax[Index_MaxMin] = MaxValue;
		 	Index_MaxMin ++;
		 	if (HaveLen_Max) {
		 		MaxValue = Total_Max / Len_MaxMin;
		 	} else {
    
		 		HaveLen_Max = Index_MaxMin >= Len_MaxMin;
		 		MaxValue = Total_Max / Index_MaxMin;
		 	}	 	


		 	Amplitude = MaxValue;
      
		 }	
	}
	
	public boolean Decode() {
		boolean Result = false;
		double d;
		
		d = Data;
	/*	
	    LastValue2 = LastValue1;
	    LastValue1 = LastValue;
	    LastValue = d;
	    
	    if ((LastValue <= LastValue1) & (LastValue2 <= LastValue1)) {     //判断最大值
	    	MaxValue = LastValue1 + Math.abs(LastValue - LastValue2) / 5;
	    	HaveMax = true;
	    }
	    
	    if ((LastValue >= LastValue1) & (LastValue2 >= LastValue1)) {     //判断最小值
	    	MinValue = LastValue1 - Math.abs(LastValue - LastValue2) / 5;
	    	HaveMin = true;
	    }
	    if (HaveMax & HaveMin) {
	    	Index_MaxMin = Index_MaxMin % Len_MaxMin;
	    	Total_Max = Total_Max - ArrMax[Index_MaxMin] + MaxValue;
	    	Total_Min = Total_Min - ArrMin[Index_MaxMin] + MinValue;
	    	ArrMax[Index_MaxMin] = MaxValue;
	    	ArrMin[Index_MaxMin] = MinValue;
	    	Index_MaxMin ++;


	    	MaxValue = Total_Max / Len_MaxMin;
	    	MinValue = Total_Min / Len_MaxMin;

	    	Origin = (MaxValue + MinValue) / 2;                              //计算基准点
	    	Amplitude = (MaxValue - MinValue) / 2;                           //计算振幅

	      
	    	HaveMax = false;
	    	HaveMin = false;
	    }

	    d = LastValue;
	    //*/
	    
	    Index_Sig0 = Index_Sig0 % Len_Sig0;
	    Total_Sig0 = Total_Sig0 - BPF_Sig0[Index_Sig0] + d;
	    BPF_Sig0[Index_Sig0] = d;
	    Index_Sig0 ++;
	    d = Total_Sig0 / Len_Sig0;

	    d = d - Origin;
	    d = Math.abs(d);

	    Index_Baud = Index_Baud % Len_Baud;
	    Total_Baud = Total_Baud - BPF_Baud[Index_Baud] + d;
	    BPF_Baud[Index_Baud] = d;
	    Index_Baud ++;
	    d = Total_Baud / Len_Baud;
	    
	    CalcAm(d);

	    LPF = d;

	    if (Amplitude > 0 ) d = d / Amplitude;

	    if (! Started) {
	    	if (d < Limit)
	    		Position ++;
	    	else
	    		Position = 0;

	      Reverse = Position >= (Len_Baud - Len_Sig0 + 1);
	      if (Reverse) {
	    	  ReviseNextSigPos();
	    	  Started = true;
	    	  Sig0 = true;
	    	  Result = true;
	      }
	    } else {
	    	Position ++;
	    	Reverse = Position >= NextSigPos;
	    	if (Reverse) {
	    		CalcNextSigPos();
	    		Sig0 = d < Limit;
	    		Result = true;
	    	}
	    }
		
		return Result;
	}
	
	public boolean DecodeByte(){
		boolean Result = false;
		
		if (Decode()) {
			switch (State) {
			case s_Start:
				if (Sig0) {
					State ++;
					ByteValue = 0;
				} else
					ReviseNextSigPos();
				break;
			case s_b0:
			case s_b1:
			case s_b2:
			case s_b3:
			case s_b4:
			case s_b5:
			case s_b6:
			case s_b7:
				State ++;
				ByteValue >>= 1;
	    		if (! Sig0) ByteValue |= 0x80; else ByteValue &= (0x7F);
				break;
			case s_Stop:
		         Result = ! Sig0;
		         //B = ByteValue;
		         State = s_Start;
		         ReviseNextSigPos();
				break;
			default:
				State = s_Start;						
			}
		}
		return Result;
	}
	
}