package com.szxys.mhub.subsystem.mets.bean;

public class Urineflowrate {
	/**
	 * 尿流率数据(上传专用)
	 */
	private String StartTime; //	开始时间
	private float Duration; //	采集时长
	private String VaryVal; //	每秒的尿量
	private float MeanFlow; //	平均尿流率
	private float Q90; //	90％流量的平均尿流率
	private float PeakFlow; //	最大尿流率
	private float twoSecondFlow; //	2秒时的尿流率
	private float FlowTime; //	排尿时间
	private float StreamTime; //	尿流时间
	private float T90; //	90％流量时的时间
	private float TimeToPeak; //	达到最大尿流率的时间
	private float VoidVolume; //	总尿量
	private String AmountUnit; //	总尿量单位
	private String Proportion; //	尿液比重
	private int BeginPos; //	间歇开始位置
	private int EndPos; //	间歇结束位置
	public void setStartTime(String startTime) {
		StartTime = startTime;
	}
	public String getStartTime() {
		return StartTime;
	}
	public void setDuration(float duration) {
		Duration = duration;
	}
	public float getDuration() {
		return Duration;
	}
	public void setVaryVal(String varyVal) {
		VaryVal = varyVal;
	}
	public String getVaryVal() {
		return VaryVal;
	}
	public void setMeanFlow(float meanFlow) {
		MeanFlow = meanFlow;
	}
	public float getMeanFlow() {
		return MeanFlow;
	}
	public void setQ90(float q90) {
		Q90 = q90;
	}
	public float getQ90() {
		return Q90;
	}
	public void setPeakFlow(float peakFlow) {
		PeakFlow = peakFlow;
	}
	public float getPeakFlow() {
		return PeakFlow;
	}
	public void setTwoSecondFlow(float twoSecondFlow) {
		this.twoSecondFlow = twoSecondFlow;
	}
	public float getTwoSecondFlow() {
		return twoSecondFlow;
	}
	public void setFlowTime(float flowTime) {
		FlowTime = flowTime;
	}
	public float getFlowTime() {
		return FlowTime;
	}
	public void setStreamTime(float streamTime) {
		StreamTime = streamTime;
	}
	public float getStreamTime() {
		return StreamTime;
	}
	public void setT90(float t90) {
		T90 = t90;
	}
	public float getT90() {
		return T90;
	}
	public void setTimeToPeak(float timeToPeak) {
		TimeToPeak = timeToPeak;
	}
	public float getTimeToPeak() {
		return TimeToPeak;
	}
	public void setVoidVolume(float voidVolume) {
		VoidVolume = voidVolume;
	}
	public float getVoidVolume() {
		return VoidVolume;
	}
	public void setAmountUnit(String amountUnit) {
		AmountUnit = amountUnit;
	}
	public String getAmountUnit() {
		return AmountUnit;
	}
	public void setProportion(String proportion) {
		Proportion = proportion;
	}
	public String getProportion() {
		return Proportion;
	}
	public void setBeginPos(int beginPos) {
		BeginPos = beginPos;
	}
	public int getBeginPos() {
		return BeginPos;
	}
	public void setEndPos(int endPos) {
		EndPos = endPos;
	}
	public int getEndPos() {
		return EndPos;
	}
}
