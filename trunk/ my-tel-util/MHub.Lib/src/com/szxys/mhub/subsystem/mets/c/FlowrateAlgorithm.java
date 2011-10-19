package com.szxys.mhub.subsystem.mets.c;

import java.util.Date;

/**
 * Created by shiwen.chai.
 * User: Administrator
 * Date: 11-3-21
 * Time: 下午5:00
 */

public class FlowrateAlgorithm {
    public static int FWCFLAG_SMOOTH = 1;           //执行平滑
    public static int FWCFLAG_FILTER = 2;           //执行过滤

    public final static class FlowrateParam {
        public float[] volumeData;         // 最终的尿量数组，调用方用这个数据来绘制尿量图
        public float[] flowrateData;       // 最终的尿流率数组，调用方用这个数据来绘制尿流率图
        public int[] intermission;         // 排尿间歇的起始点和结束点位置，保存数据的格式：间歇开始1,间歇结束1,间歇开始2,间歇结束2...
        public float f2SecFlowrate;		// 2秒尿流率
        public float fMaxFlowrate;			// 最大尿流率
        public float fMaxFlowrateTime;		// 达峰时间
        public int nMaxFlowratePos;		// 达峰位置（尿流率最大时在数组中的位置）
        public float fAvgFlowrate;			// 平均尿流率
        public float fMicturateDuration;	// 排尿时间
        public float fFlowDuration;		// 尿流时间
        public float fMicturateVolume;		// 总尿量
        public int nMicturateStartPos;		// 排尿开始点
        public int nMicturateEndPos;		// 排尿结束点
    }

    private FlowrateAlgorithm() {}

    /**
     * 尿流率算法
     * @param orgVolume     从采集器收到 的原始数据
     * @param fwcFlag       算法标记
     * @param smoothDegree  若 fwcFlag 包含 FWCFLAG_SMOOTH, 则指定平滑度数，越大平滑得越厉害，默认用1
     * @param param         返回计算后的结果
     * @return
     */
    public static native boolean FlowRateCalc(float[] orgVolume, int fwcFlag, int smoothDegree, FlowrateParam param);

    static {
        System.loadLibrary("FlowrateAlgorithm");
    }
}
