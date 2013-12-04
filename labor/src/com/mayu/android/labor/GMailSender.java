package com.mayu.android.labor;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.oauth.OAuthConsumer;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.mayu.android.labor.xoauth.XoauthProtocol;
import com.mayu.android.labor.xoauth.XoauthSaslResponseBuilder;
import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.BASE64EncoderStream;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GMailSender {
	private String mailhost = "smtp.gmail.com";
	private Session session;
	private String sender_id = "default";
	private String password = "default";
	private String authToken = "default";

	// static {
	// Security.addProvider(new org.mortbay.ijetty.console.JSSEProvider());
	// }

	public GMailSender(Context context) throws Exception {
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", this.mailhost);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.quitwait", "false");

		boolean debug = false;

		// Authenticator auth = new SMTPAuthenticator(context);
		this.session = Session.getDefaultInstance(props);
		this.session.setDebug(debug);

//		this.sender_id = getEmail(context);
//		this.authToken = getAuthToken(context);
	}

	public synchronized void sendMail(String subject, String body,
			String recipients) throws Exception {

		
		try {
			MimeMessage message = new MimeMessage(session);
			message.setSender(new InternetAddress(this.sender_id));
			message.setSubject(subject);
			message.setText(body);
			if (recipients.indexOf(',') > 0)
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(recipients));
			else
				message.setRecipient(Message.RecipientType.TO,
						new InternetAddress(recipients));

			 Transport t = session.getTransport("smtp");
			 t.connect(this.sender_id, this.password); //Gmailアカウント設定
			 t.sendMessage(message, message.getAllRecipients());


			 //↓↓↓　OAuthでできないか試してみた傷あと ↓↓↓
//			final URLName unusedUrlName = null;
//			SMTPTransport transport = new SMTPTransport(session, unusedUrlName);
//			// If the password is non-null, SMTP tries to do AUTH LOGIN.
//			final String emptyPassword = null;
//			transport.connect(mailhost, 465, this.sender_id, emptyPassword);
//
//			/*
//			 * I couldn't get the SASL infrastructure to work with JavaMail
//			 * 1.4.3; I don't think it was ready yet in that release. So we'll
//			 * construct the AUTH command manually.
//			 */
//			
//			XoauthSaslResponseBuilder builder = new XoauthSaslResponseBuilder();
//			byte[] saslResponse = builder.buildResponse(this.sender_id,
//					XoauthProtocol.SMTP, this.authToken, "anonymous",
//					getAnonymousConsumer());
//			saslResponse = BASE64EncoderStream.encode(saslResponse);
//			transport.issueCommand("AUTH XOAUTH " + new String(saslResponse),
//					235);

			// Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
			throw e;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void setSenderAccount(String sender, String password) {
		this.sender_id = sender;
		this.password = password;
	}

	/**
	 * Generates a new OAuthConsumer with token and secret of
	 * "anonymous"/"anonymous". This can be used for testing.
	 */
	public static OAuthConsumer getAnonymousConsumer() {
		return new OAuthConsumer(null, "anonymous", "anonymous", null);
	}
	

	
	
	// not working no context to be had through ijetty
	private String getEmail(Context context) {
		AccountManager accountManager = AccountManager.get(context);
		Account account = getAccount(accountManager);
		if (account == null) {
			return null;
		} else {
			return account.name;
		}
	}

	private String getPassword(Context context) {
		AccountManager accountManager = AccountManager.get(context);
		Account account = getAccount(accountManager);
		if (account == null) {
			return null;
		} else {
			return accountManager.getPassword(account);
		}
	}

	private String getAuthToken(Context context) {
		AccountManager accountManager = AccountManager.get(context);
		Account account = getAccount(accountManager);
		String token = null;
		if (account == null) {
			return null;
		} else {
			AccountManagerFuture<Bundle> accountManagerFuture = accountManager
					.getAuthToken(account, "mail", false, null, null);
			Bundle authTokenBundle;
			try {
				authTokenBundle = accountManagerFuture.getResult();
				token = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN)
						.toString();
				// token = accountManager.blockingGetAuthToken(account,
				// "mail", false);
			} catch (OperationCanceledException e) {
				e.printStackTrace();
			} catch (AuthenticatorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return token;
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
	
	
	
	
	
	/**
	 * SimpleAuthenticator is used to do simple authentication when the SMTP
	 * server requires it.
	 */
	private class SMTPAuthenticator extends javax.mail.Authenticator {
		private String user;
		private String password;

		public SMTPAuthenticator(Context context) {
			this.user = getEmail(context);
			this.password = getPassword(context);
			Log.w("GMAILSENDER ", "username : " + this.user + " password: "
					+ this.password);
		}

		// not working no context to be had through ijetty
		private String getEmail(Context context) {
			AccountManager accountManager = AccountManager.get(context);
			Account account = getAccount(accountManager);
			if (account == null) {
				return null;
			} else {
				return account.name;
			}
		}

		private String getPassword(Context context) {
			AccountManager accountManager = AccountManager.get(context);
			Account account = getAccount(accountManager);
			if (account == null) {
				return null;
			} else {
				return accountManager.getPassword(account);
			}
		}

		private String getAuthToken(Context context) {
			AccountManager accountManager = AccountManager.get(context);
			Account account = getAccount(accountManager);
			String token = null;
			if (account == null) {
				return null;
			} else {
				AccountManagerFuture<Bundle> accountManagerFuture = accountManager
						.getAuthToken(account, "mail", false, null, null);
				Bundle authTokenBundle;
				try {
					authTokenBundle = accountManagerFuture.getResult();
					token = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN)
							.toString();
					// token = accountManager.blockingGetAuthToken(account,
					// "mail", false);
				} catch (OperationCanceledException e) {
					e.printStackTrace();
				} catch (AuthenticatorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return token;
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

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(this.user, this.password);
		}
	}

	private class GetAuthTokenCallback implements
			AccountManagerCallback<Bundle> {

		static final String TAG = "+++ GetAuthTokenCallback";

		@Override
		public void run(AccountManagerFuture<Bundle> arg0) {
			Bundle bundle;
			try {
				bundle = arg0.getResult();
				Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
				if (intent != null) {
					Log.d(TAG, "User Input required");
					// startActivity(intent);
				} else {
					Log.d(TAG,
							"Token = "
									+ bundle.getString(AccountManager.KEY_AUTHTOKEN));
					loginGoogle(bundle.getString(AccountManager.KEY_AUTHTOKEN));
				}
			} catch (OperationCanceledException e) {
				e.printStackTrace();
			} catch (AuthenticatorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void loginGoogle(String token) {
			// DefaultHttpClient http_client = new DefaultHttpClient();
			// HttpGet http_get = new HttpGet(
			// // TokenAuthの他にも Login、ServiceLgoin、ClientLoginがあるがどれもNG
			// "https://www.google.com/accounts/TokenAuth?auth=" + token
			// + "&continue=http://www.google.com/calendar/");
			// HttpResponse response = null;
			// try {
			// response = http_client.execute(http_get);
			// } catch (ClientProtocolException e) {
			// e.printStackTrace();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			// {
			// try {
			// String entity = EntityUtils.toString(response.getEntity());
			// Log.d(TAG, entity);
			// if (entity.contains("The page you requested is invalid")) {
			// Log.d(TAG, "The page you requested is invalid");
			// mAccountManager
			// .invalidateAuthToken("com.google", token);
			// }
			// } catch (IllegalStateException e) {
			// e.printStackTrace();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// } else
			// Log.d(TAG, "Login failure");
		}
	}

}