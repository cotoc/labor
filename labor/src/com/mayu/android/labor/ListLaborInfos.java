package com.mayu.android.labor;

import java.util.ArrayList;
import java.util.Date;

public class ListLaborInfos extends ArrayList<LaborInfo> {
	
	private boolean startedFlg = false;
	private boolean subsidedFlg = true;
	private Date lastStartedTime = null;
	
	public boolean isStartedFlg() {
		return startedFlg;
	}

	public void setStartedFlg(boolean startedFlg) {
		this.startedFlg = startedFlg;
	}

	public Date getLastStartedTime() {
		return lastStartedTime;
	}

	public void setLastStartedTime(Date lastStartedTime) {
		this.lastStartedTime = lastStartedTime;
	}

	public boolean isSubsidedFlg() {
		return subsidedFlg;
	}

	public void setSubsidedFlg(boolean subsidedFlg) {
		this.subsidedFlg = subsidedFlg;
	}
	

}
