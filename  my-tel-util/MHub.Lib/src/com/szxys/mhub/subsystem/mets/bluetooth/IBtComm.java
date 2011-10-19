package com.szxys.mhub.subsystem.mets.bluetooth;

import com.szxys.mhub.subsystem.mets.c.FlowrateAlgorithm;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by shiwen.chai.
 * User: Administrator
 * Date: 11-5-10
 * Time: 下午3:27
 */
public interface IBtComm {

    final class Param {
        public Date timerTime = null;
        public short waitTime = 600;
        public byte mode = 0;
        public byte intervalUnit = 0;
        public byte timerInterval = 6;
        public short maxDuration = 300;
        public byte voiceSwitch = 0;
        public short voiceType = 2052;
        public short cupWeight = 0;
    }

    final class VoidingDiary {
        public Date date;
        public float quantity;
    }

    final class UfoFlow {
        public Date date;
        public float[] orgVolume;
        public FlowrateAlgorithm.FlowrateParam param = new FlowrateAlgorithm.FlowrateParam();
    }

    public Param param = new Param();

    // when the time to communicate with the collector, this method is invoke to obtain the data
    public boolean load();

    // when the connunication is completed, this method is invode to save the data
    public boolean save();

    // save voiding diary data
    public void resultVoidingDiary(ArrayList<VoidingDiary> diarys);

    // save ufoflow data
    public void resultUfoFlow(ArrayList<UfoFlow> ufoFlows);

    public void communicateResult(boolean succeed);
}


