package com.mayu.android.labor;

import java.util.Calendar;
import java.util.Date;

import com.mayu.android.labor.LaporTabActivity.OnClearData;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LaborpainsActivity extends Activity implements OnClearData {

	private Button mBtnLabor = null;
	private Button mBtnLaborStop = null;
	private Button mBtmMemo = null;
	private TextView mTextStatus = null;
	// private ListView mListView = null;
	private TextView mTextRemainingDays = null;
	private Chronometer mChronometer = null;
	private Date mStartDate = null;

	private ListLaborInfos mLaborInfos = null;
	private LaborInfo mLastLaborInfo = null;
	private boolean mLaborStartFlg = false;

	private boolean mIntervalStartParam = false; // false:Startからの間隔を計測。true:Endからの間隔を計測

	private long mRemainingDays = -1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// loadList();
		//
		// initIconDrawable();
		//
		// setRemainingDays();

		mBtnLabor = (Button) findViewById(R.id.btn_labor);
		mBtnLaborStop = (Button) findViewById(R.id.btn_labor_stop);
		mBtmMemo = (Button) findViewById(R.id.btn_memo);

		mTextStatus = (TextView) findViewById(R.id.text_status);
		mChronometer = (Chronometer) findViewById(R.id.chronometer);

		mTextRemainingDays = (TextView) findViewById(R.id.message);

		// mChronometer
		// .setOnChronometerTickListener(new OnChronometerTickListener() {
		// public void onChronometerTick(Chronometer cArg) {
		// long t = SystemClock.elapsedRealtime() - cArg.getBase();
		// cArg.setText(DateFormat.format("hh:mm:ss", t));
		// }
		// });

		mChronometer.setFormat("%s");

		setInitialaizeView();

		mBtnLabor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mLaborStartFlg = true;
				// じんつうちゅう
				mTextStatus.setText(R.string.start_labor);
				mChronometer.setTextColor(Color.RED);
				// mBtnLabor.setText(R.string.end_labor);

				mBtnLaborStop.setClickable(true);
				setStopButtonEnable(true);
				// RecordAdd
				saveStartLabor();

				mTextRemainingDays.setText(mLastLaborInfo.getWarningMessage());
				// カウントクリア
				startTimmer(null);

			}
		});

		mBtnLaborStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mLaborStartFlg = false;

				mTextStatus.setText(R.string.end_labor);
				mChronometer.setTextColor(Color.BLUE);
				mBtnLaborStop.setClickable(false);
				setStopButtonEnable(false);
				// SetEndTime
				saveEndLabor();
				mStartDate = null;

				if (mIntervalStartParam)
					// カウントクリア
					startTimmer(null);

			}
		});

		mBtmMemo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(LaborpainsActivity.this)
						.setTitle("Pick a type")
						.setItems(R.array.input_type_name,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int item) {
										// その他の場合。。。
										if (item == 3) {
											// テキスト入力を受け付けるビューを作成します。
											final EditText editView = new EditText(
													LaborpainsActivity.this);
											new AlertDialog.Builder(
													LaborpainsActivity.this)
													.setIcon(
															android.R.drawable.ic_dialog_info)
													.setTitle("コメント入力")
													// setViewにてビューを設定します。
													.setView(editView)
													.setPositiveButton(
															"OK",
															new DialogInterface.OnClickListener() {
																public void onClick(
																		DialogInterface dialog,
																		int whichButton) {
																	// 選択したタイプを登録
																	saveCondition(newCondition(
																			null,
																			3,
																			editView.getText()
																					.toString()));

																	// 入力した文字をトースト出力する
																	Toast.makeText(
																			LaborpainsActivity.this,
																			editView.getText()
																					.toString(),
																			Toast.LENGTH_LONG)
																			.show();
																}
															})
													.setNegativeButton(
															"キャンセル",
															new DialogInterface.OnClickListener() {
																public void onClick(
																		DialogInterface dialog,
																		int whichButton) {
																}
															}).show();
										} else {
											// 選択したタイプを登録
											saveCondition(newCondition(null,
													item + 1, null));
											
											if(item + 1 == LaborInfo.BARTH){
												stopTimmer();
											}
										}

										String[] intary = getResources()
												.getStringArray(
														R.array.input_type_name);
										mTextStatus.setText(String.format(
												"%sが選択されました。", intary[item]));
									}
								}).show();
			}
		});

	}

	private void loadList() {
		if (mLaborInfos != null)
			mLaborInfos.clear();

		LaborInfoDAO dao = new LaborInfoDAO(getApplicationContext());

		mLastLaborInfo = dao.loadLastLabor();
		if (mLastLaborInfo != null) {
			mStartDate = mLastLaborInfo.getStartTime();
			if (mLastLaborInfo.getEndTime() == null) { // 痛みが終わってない
				mLaborStartFlg = true;
			} else { // 痛みが終わってる
				mLaborStartFlg = false;
			}

		}

	}
	
	private void stopTimmer(){
		mChronometer.stop();
		mTextStatus.setText("出産 おめでとう！");
	}

	private void startTimmer(Date start) {
		if (start != null) {
			long passageTime = 0;
			Calendar nowCal = Calendar.getInstance();
			DiffCalendar diffCal = new DiffCalendar(start, nowCal.getTime());
			passageTime = diffCal.getDiffTimemSec();

			mChronometer.setBase(SystemClock.elapsedRealtime() - passageTime);

		} else {
			mChronometer.setBase(SystemClock.elapsedRealtime());
		}
		mChronometer.start();
	}

	// 開始レコード作成
	private void saveStartLabor() {
		mLastLaborInfo = setNewLaborInfo(Calendar.getInstance().getTime(),
				LaborInfo.LABOR, "");

		saveCondition();
	}

	// 終了時刻UPDATE
	private void saveEndLabor() {
		setEndLaborInfo();

		saveCondition();
	}

	// その他 登録
	private void saveMemo() {

	}

	// 開始レコード 新規作成
	private LaborInfo setNewLaborInfo(Date date, int type, String memo) {
		if (mLastLaborInfo != null) {
			LaborInfo info = new LaborInfo(date, type, memo,
					mLastLaborInfo.getStartTime());
			return info;

		} else {
			LaborInfo info = new LaborInfo(date, type, memo);
			return info;

		}

	}

	// 終了時刻　設定
	private void setEndLaborInfo() {
		mLastLaborInfo.setEndTime(Calendar.getInstance().getTime());
	}

	// レコード作成
	private LaborInfo newCondition(Date date, int type, String memo) {
		LaborInfo info = new LaborInfo(date, type, memo);
		// mLaborInfos.add(info);
		return info;
	}

	private LaborInfo writeCondition(LaborInfo info, Date start, int type,
			String memo, Date end, int index) {
		info.setStartTime(start);
		info.setInputType(type);
		info.setMemo(memo);
		info.setEndTime(end);

		return info;

	}

	private void saveCondition() {
		LaborInfo info;
		info = saveCondition(mLastLaborInfo);
		mLastLaborInfo.setId(info.getId());
	}

	// DB登録
	private LaborInfo saveCondition(LaborInfo info) {
		if (info == null) {
			return null;
		}
		LaborInfoDAO dao = new LaborInfoDAO(getApplicationContext());
		info = dao.save(info);

		return info;

	}

	private void setRemainingDays() {
		Long expectedDateMinits = LaborAppPreferences
				.getExpectedDate(getApplicationContext());

		if (expectedDateMinits <= 0) {
			mRemainingDays = DatePickerPreference.INITIAL_VALUE;
			return;
		}

		Date expectedDate = new Date(expectedDateMinits);

		Calendar cal = Calendar.getInstance();

		DiffCalendar diffCal = new DiffCalendar(cal.getTime(), expectedDate);

		mRemainingDays = diffCal.getDiffDays();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void setStopButtonEnable(boolean enable) {
		mBtnLaborStop.setEnabled(enable);
		if (enable) {
			mBtnLaborStop
					.setCompoundDrawables(null, iconStopEnable, null, null);

		} else {
			mBtnLaborStop.setCompoundDrawables(null, iconStopDisable, null,
					null);

		}
	}

	Drawable iconStopEnable;
	Drawable iconStopDisable;

	private void initIconDrawable() {
		iconStopEnable = getResources().getDrawable(R.drawable.ic_labor_stop);
		iconStopEnable.setBounds(0, 0, iconStopEnable.getIntrinsicWidth(),
				iconStopEnable.getIntrinsicHeight());

		iconStopDisable = getResources().getDrawable(
				R.drawable.ic_labor_stop_disable);
		iconStopDisable.setBounds(0, 0, iconStopDisable.getIntrinsicWidth(),
				iconStopDisable.getIntrinsicHeight());

	}

	public void setInitialaizeView() {
		loadList();

		initIconDrawable();

		setRemainingDays();

		if (mRemainingDays == DatePickerPreference.INITIAL_VALUE) {
			mTextRemainingDays.setText("-- 予定日が未設定です --");
		} else if (mRemainingDays > 0) {
			mTextRemainingDays.setText("予定日まであと　"
					+ String.valueOf(mRemainingDays) + "日　です");
		} else {
			mTextRemainingDays.setText("予定日から　"
					+ String.valueOf(mRemainingDays * -1) + "日 過ぎました");
		}

		// 陣痛は始まっているか？
		if (mLastLaborInfo != null) {
			startTimmer(mStartDate);

			// 痛みの途中か？
			if (mLastLaborInfo.getEndTime() == null) {
				mTextStatus.setText(R.string.start_labor);
				mChronometer.setTextColor(Color.RED);
				mBtnLaborStop.setClickable(true);
			} else {
				mTextStatus.setText(R.string.end_labor);
				mChronometer.setTextColor(Color.BLUE);
				mBtnLaborStop.setClickable(false);
			}
		} else {
			mTextStatus.setText(R.string.wait_labor);
			mChronometer.setTextColor(Color.BLUE);
			mBtnLaborStop.setClickable(false);
		}
	}

	@Override
	public void onClearData() {
		// TODO Auto-generated method stub
		setInitialaizeView();
	}
}