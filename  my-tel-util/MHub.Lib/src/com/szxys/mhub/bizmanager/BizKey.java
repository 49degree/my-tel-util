package com.szxys.mhub.bizmanager;


public class BizKey {
	private static final int KEYBASE = 100;
	public int curUserID;
	public int curSubSystemID;

	public BizKey(int inUserID, int ssid) {
		curUserID = inUserID;
		curSubSystemID = ssid;
	}

	@Override
	public int hashCode() {
		return curUserID * KEYBASE + curSubSystemID;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof BizKey)
				&& (curUserID == ((BizKey) obj).curUserID)
				&& (curSubSystemID == ((BizKey) obj).curSubSystemID);
	}
}
