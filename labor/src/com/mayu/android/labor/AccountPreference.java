package com.mayu.android.labor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class AccountPreference extends DialogPreference {

	
	private Context mContext;
	private TextView mTextID;
	private TextView mTextPwd;

	public AccountPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		// ダイアログのレイアウトリソース指定
		setDialogLayoutResource(R.layout.account_pref);
		mContext = context;
	}

	@Override
	protected void onBindDialogView(View view) {
		// TODO Auto-generated method stub
		super.onBindDialogView(view);

		mTextID = (TextView) view.findViewById(R.id.text_pref_id);
		mTextPwd = (TextView) view.findViewById(R.id.text_pref_pwd);

		String prefId = LaborAppPreferences.getLoginId(mContext);
		String prefPwd = LaborAppPreferences.getPassword(mContext);
		if (!prefId.equals("default")) {
			mTextID.setText(prefId);
		}
		if (!prefPwd.equals("default")) {
			mTextPwd.setText(prefPwd);
		}

	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		// TODO Auto-generated method stub
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			persistString(String.valueOf(mTextID.getText()));

			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(getContext());
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(LaborAppPreferences.KEY_PASSWORD,
					String.valueOf(mTextPwd.getText()));

			// Commit the edits!
			editor.commit();

			this.setSummary(mTextID.getText() + "/" + mTextPwd.getText());

		}
	}

	public String getValue() {
		// TODO Auto-generated method stub
		String userId = String.valueOf(this.getPersistedString("--none--"));
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());	
		String userPwd = preferences.getString(LaborAppPreferences.KEY_PASSWORD, "******");

		return userId + "/" + userPwd;
	}
}
