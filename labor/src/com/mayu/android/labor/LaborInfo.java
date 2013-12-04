/*
 * Copyright © 2011 Infotec Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.mayu.android.labor;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.InputType;
import android.util.Log;

/**
 * @author 0a6055
 *
 *         1レコード分のデータを保持するクラス
 */
/**
 * @author 0a6055
 *
 */
/**
 * @author 0a6055
 *
 */
/**
 * @author 0a6055
 * 
 */
public class LaborInfo implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = LaborInfo.class.getSimpleName();
	private final LaborInfo self = this;

	// TableName
	public static final String TABLE_NAME = "labor_info";

	// カラム名
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TIME = "input_time";
	public static final String COLUMN_START = "start_time";
	public static final String COLUMN_END = "end_time";
	public static final String COLUMN_DURATION = "duration";
	public static final String COLUMN_TYPE = "input_type";
	public static final String COLUMN_MEMO = "memo";

	// 状態コード
	public static final int LABOR = 0; // 陣痛
	public static final int RUPTURE = 1; // 破水
	public static final int MARK = 2; // その他
	public static final int BARTH = 3; // その他
	public static final int OTHER = 4; // その他

	// 状態名
	public static final String[] typeName = { "陣痛", "破水", "おしるし", "産まれた", "その他" };

	// 　警告レベル
	public static final int[] warning_level = { 0, 1, 2, 3, 4, 5, 6 };
	public static final long[] warning_limit = { 60 * 60 * 1000, // 1H
			30 * 60 * 1000, // 30
			15 * 60 * 1000, // 15
			10 * 60 * 1000, // 10分 潜伏期
			5 * 60 * 1000,  // 5分 活動期
			2 * 60 * 1000,  // 2分 移行期
			1 * 60 * 1000}; // 1分 娩出期

	public static final String[] warning_state = { "前駆陣痛",
		"うん。始まったみたいだね！お風呂でも入って、準備しておこう！", 
		"入院準備はOKかな？", 
		"準備期",
		"進行期", 
		"移行期", 
		"娩出期",
		"前駆陣痛" };
	
	public static final String[] warning_message = { "始まったかな？",
			"うん。始まったみたいだね！お風呂でも入って、準備しておこう！", 
			"入院準備はOKかな？", 
			"さあ、病院に電話だ！",
			"まだ、いきんじゃだめだよ～！ りらく～す♪", 
			"もう生まれるよ！", 
			"前駆陣痛だったのかも。落ち着いて。" };

	// private static final String DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";

	// データなし時
	public static final String NO_DATA = "-";

	private Long id = null;
	private String inputTime = null;
	private String strStartTime = null;
	private String strEndTime = null;
	private Date startTime = null;
	private Date endTime = null;
	private int inputType = -1;
	private String memo = null;
	private Date inputDate = null;
	private long interval = 0;
	private String StrInterval = null;
	private Date prevDate = null;
	private int warningLevel = 0;
	private long duration = 0;

	private DateUtil dateUtil;

	public LaborInfo() {

	}

	public LaborInfo(Date date, int type, String memo, Date prevStat) {

		setInputType(type);
		setMemo(memo);
		setStartTime(date);
		dateUtil = new DateUtil();
		setInterval(date, prevStat);
	}

	public LaborInfo(Date date, int type, String memo) {
		// setInputDate(date);
		setInputType(type);
		setMemo(memo);
		setStartTime(date);
		dateUtil = new DateUtil();

	}

	/**
	 * @return id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            セットする id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public String getInputTime() {
		return inputTime;
	}

	public void setInputTime(String inputTime) {
		this.inputTime = inputTime;
	}

	public int getInputType() {
		return inputType;
	}

	public void setInputType(int inputType) {
		this.inputType = inputType;
	}

	public String getTypeName() {
		return typeName[inputType];

	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Date getInputDate() {
		return inputDate;
	}

	public void setInputDate(Date inputDate) {
		this.inputDate = inputDate;
	}

	public void setInputDate(long inputDateMSec) {
		if (inputDateMSec <= 0)
			return;
		if (this.inputDate == null) {
			this.inputDate = new Date(inputDateMSec);
		} else {
			this.inputDate.setTime(inputDateMSec);
		}
		setInputTime(this.inputDate.toLocaleString());
	}

	public String getStrInterval() {
		return StrInterval;
	}

	public void setStrInterval(String interval) {
		this.StrInterval = interval;
	}

	public Date getPrevDate() {
		return prevDate;
	}

	public void setPrevDate(Date prevDate) {
		this.prevDate = prevDate;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
		setWarningLevel();
	}

	public void setInterval(Date start, Date prev) {
		// Start － End から、経過時間を設定
		DiffCalendar diffCal = new DiffCalendar(prev, start);
		diffCal.getDiffTimemSec();

		setInterval( diffCal.getDiffTimemSec() );
	}

	public int getWarningLevel() {
		return warningLevel;
	}

	public void setWarningLevel() {

		this.warningLevel = warning_level.length - 1;

		for (int i = 0; i < warning_limit.length - 1; i++) {
			if ( warning_limit[i + 1] < interval  && interval <= warning_limit[i]) {
				this.warningLevel = warning_level[i];
				continue;
			}
		}
	}

	public String getWarningMessage(){
		return warning_message[this.warningLevel];
	}
	
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		if (startTime != null) {
			this.startTime = startTime;
		} else {
			// 現在時刻を設定
			this.startTime = Calendar.getInstance().getTime();
		}
	}

	public void setStartTime(long startTimeMSec) {

		if (startTimeMSec <= 0)
			return;

		if (this.startTime == null) {
			this.startTime = new Date(startTimeMSec);
		} else {
			this.startTime.setTime(startTimeMSec);
		}
		setStrStartTime(this.startTime.toLocaleString());
	}

	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime
	 */
	public void setEndTime(Date endTime) {
		if (endTime != null) {
			this.endTime = endTime;
		} else {
			// 現在時刻を設定
			this.endTime = Calendar.getInstance().getTime();
		}
		setDuration();
	}

	/**
	 * @param endTime
	 */
	public void setEndTime(long endTimeMsec) {
		if (endTimeMsec <= 0)
			return;

		if (this.endTime == null) {
			this.endTime = new Date(endTimeMsec);
		} else {
			this.endTime.setTime(endTimeMsec);
		}

		setStrStartTime(this.endTime.toLocaleString());

		setDuration();
	}

	/**
	 * @return
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * 持続時間算出→設定
	 */
	public void setDuration() {
		if (this.duration > 0)
			return;

		// Start － End から、経過時間を設定
		DiffCalendar diffCal = new DiffCalendar(getStartTime(), getEndTime());
		diffCal.getDiffTimemSec();

		this.duration = diffCal.getDiffTimemSec();
	}

	/**
	 * 持続時間算出→設定
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getStrStartTime() {
		return strStartTime;
	}

	public void setStrStartTime(String strStartTime) {
		this.strStartTime = strStartTime;
	}

	public String getStrEndTime() {
		return strEndTime;
	}

	public void setStrEndTime(String strEndTime) {
		this.strEndTime = strEndTime;
	}

}
