package com.mayu.android.labor;

import org.apache.http.auth.AuthenticationException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ContactActivity extends Activity {

	private Button mCallHaspital;
	private Button mCallTaxi;
	private Button mCallFamily;
	private Button mMailFamily;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.contact);

		mCallFamily = (Button) findViewById(R.id.button_call_family);
		mCallHaspital = (Button) findViewById(R.id.button_call_hospital);
		mCallTaxi = (Button) findViewById(R.id.button_call_taxi);
		mMailFamily = (Button) findViewById(R.id.button_mail_family);

		mCallFamily.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String tellnumber = LaborAppPreferences
						.getCallFamily(getApplicationContext());

				callPhone(tellnumber);

			}
		});

		mCallHaspital.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String tellnumber = LaborAppPreferences
						.getContactHospital(getApplicationContext());

				callPhone(tellnumber);

			}
		});

		mCallTaxi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String tellnumber = LaborAppPreferences
						.getContactTaxi(getApplicationContext());

				callPhone(tellnumber);

			}
		});

		mMailFamily.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String email = getEmail(getApplicationContext());
				String pwd = getPassword(getApplicationContext());
				try {
					GMailSender gmailSender = new GMailSender(
							getApplicationContext());
					gmailSender.setSenderAccount(email, pwd);
					gmailSender.sendMail("LaborTest", "Test", 
							LaborAppPreferences
									.getMailFamily(getApplicationContext()));

					Toast.makeText(ContactActivity.this,
							"[" + LaborAppPreferences
							.getMailFamily(getApplicationContext()) + "]　あてにメールを送信しました。",
							Toast.LENGTH_LONG).show();
					
				} catch (AuthenticationException e) {
					// TODO Auto-generated catch block
					Toast.makeText(ContactActivity.this,
							"[" + LaborAppPreferences
							.getMailFamily(getApplicationContext()) + "]　あて	のメール送信に失敗しました。\n 登録したIDとパスワードを確認してください。", 
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
					
				} catch (Exception e) {
					
					// TODO Auto-generated catch block
					Toast.makeText(ContactActivity.this,
							"[" + LaborAppPreferences
							.getMailFamily(getApplicationContext()) + "]　あて	のメール送信に失敗しました。",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}



			}
		});

		setContactEnabled();

	}

	private void setContactEnabled() {
		if (LaborAppPreferences.getCallFamily(getApplicationContext()).equals(
				"default")) {
			// 未設定
			mCallFamily.setEnabled(false);
		}

		if (LaborAppPreferences.getContactHospital(getApplicationContext())
				.equals("default")) {
			// 未設定
			mCallHaspital.setEnabled(false);
		}

		if (LaborAppPreferences.getContactTaxi(getApplicationContext()).equals(
				"default")) {
			// 未設定
			mCallTaxi.setEnabled(false);
		}

		if (LaborAppPreferences.getMailFamily(getApplicationContext()).equals(
				"default")) {
			// 未設定
			mMailFamily.setEnabled(false);
		}

	}

	private void callPhone(String tellnumber) {
		// 直接電話をかける
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ tellnumber));

		startActivity(intent);

//		// 電話をかける画面を表示する
//		 intent = new Intent(Intent.ACTION_DIAL,
//		 Uri.parse("tel:0123456789"));
//		
//		 startActivity(intent);
	}

	/**
	 * メール本文を生成する
	 * 
	 * @return
	 */
	private String getMailBody() {
		String mailBody = "Labor test mail! ";

		return mailBody;
	}

	private String getEmail(Context context) {

		return LaborAppPreferences.getLoginId(context);
		//
		// AccountManager accountManager = AccountManager.get(context);
		// Account account = getAccount(accountManager);
		// if (account == null) {
		// return null;
		// } else {
		// return account.name;
		// }
	}

	private String getPassword(Context context) {

		return LaborAppPreferences.getPassword(context);
		// AccountManager accountManager = AccountManager.get(context);
		// Account account = getAccount(accountManager);
		// if (account == null) {
		// return null;
		// } else {
		// return accountManager.getPassword(account);
		// }
	}

	// not working
	private Account getAccount(AccountManager accountManager) {
		Account[] accounts = accountManager.getAccountsByType("com.google");
		Account account;
		if (accounts.length > 0) {
			account = accounts[0];
		} else {
			account = null;
		}
		return account;
	}

}
