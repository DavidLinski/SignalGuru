package com.davidapps.forexsignals;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gcm.GCMRegistrar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller extends Application {

	private final int MAX_ATTEMPTS = 5;
	private final int BACKOFF_MILLI_SECONDS = 2000;
	private final Random random = new Random();
	public static Context RegisterContext;
	public static String reg_error;
	public static int reg_status;
	public static int reload_page;
	public static int loadMainFrame;
	public static String User_pass;
	public static String User_name;
	public static String User_mail;
	public Dialog commentDialog;
	public static String Reg_type;
	public static String FB_Email;
	public static String UserId;
	
	public static String UserToken;
	public static String UserAuth;
	public static Boolean Auth;
	
	public static String TimeZone;
	public static String Country;
	public static String FB_id;
	public static Boolean Need_email;
	public static Boolean First_register_launch;

	public static String First_launch;
	public static SharedPreferences mSettings;
	public static CustomAdapter mAdapter;
	public static Dialog dialog;

	void CheckAuth(final Context context, String id, String token,
			final String regId) {
		//method=check_auth&id=564&token=vvacCDofVlwNDSQY8ZOmNVskA2gmphb0&app_id=321321
		RegisterContext = context;
		//Log.i(Config.TAG, "auth device (regId = " + regId + ")");

		String serverUrl = Config.REGISTER_AUTH_SERVER_URL;
		
		Map<String, String> params = new HashMap<String, String>();

		params.put("id", id);
		params.put("token", token);
		params.put("app_id", regId);
		params.put("method", "check_auth");
		
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

		for (int i = 1; i <= MAX_ATTEMPTS; i++) {

			Log.d(Config.TAG, "Attempt #" + i + " to auth");

			try {
				// Send Broadcast to Show message on screen
				/*displayMessageOnScreen(context, context.getString(
						R.string.server_registering, i, MAX_ATTEMPTS));
*/
				// Post registration values to web server
				JSONObject returned_value = post(serverUrl, params);
				//Log.d(Config.TAG, returned_value.getString("result"));

				UserAuth = returned_value.getString("result");
				if( returned_value.has("to_setting_page")){
					First_launch = returned_value.getString("to_setting_page");
				}
				GCMRegistrar.setRegisteredOnServer(context, true);

				// Send Broadcast to Show message on screen
				//String message = context.getString(R.string.server_registered);
				//displayMessageOnScreen(context, message);
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {

				// Here we are simplifying and retrying on any error; in a real
				// application, it should retry only on unrecoverable errors
				// (like HTTP error code 503).

				Log.e(Config.TAG, "Failed to register on attempt " + i + ":"
						+ e);

				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {

					Log.d(Config.TAG, "Sleeping for " + backoff
							+ " ms before retry");
					Thread.sleep(backoff);

				} catch (InterruptedException e1) {
					// Activity finished before we complete - exit.
					Log.d(Config.TAG,
							"Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return;
				}

				// increase backoff exponentially
				backoff *= 2;
			}
		}

		String message = context.getString(R.string.auth_fail,
				MAX_ATTEMPTS);

		// Send Broadcast to Show message on screen
		displayMessageOnScreen(context, message);
	}
	// Auth
	void auth(final Context context, String name, String pass,
			final String regId) {
		RegisterContext = context;
		Log.i(Config.TAG, "auth device (regId = " + regId + ")");

		String serverUrl = Config.REGISTER_AUTH_SERVER_URL;
		
		Map<String, String> params = new HashMap<String, String>();
		if (pass != null) {
			User_pass = md5(pass);
			params.put("pass", User_pass);
		}
		User_pass = pass;
		User_name = name;
		Auth = false;
		params.put("app_id", regId);
		params.put("email", name);
		params.put("user", name);
		params.put("format", "json");
		params.put("type", "app");
		params.put("method", "auth");

		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

		// Once GCM returns a registration id, we need to register on our server
		// As the server might be down, we will retry it a couple
		// times.
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {

			Log.d(Config.TAG, "Attempt #" + i + " to auth");

			try {
				// Send Broadcast to Show message on screen
				displayMessageOnScreen(context, context.getString(
						R.string.server_registering, i, MAX_ATTEMPTS));
				
				// Post registration values to web server
				JSONObject returned_value = post(serverUrl, params);
				
				if( !returned_value.has("error")){
					SetCustomParameters(getApplicationContext(), Config.APP_PREFERENCES_USER_ID, returned_value.getString("id"));//GetCustomParameters(getApplicationContext(), Config.APP_PREFERENCES_USER_ID);
					SetCustomParameters(getApplicationContext(), Config.APP_PREFERENCES_USER_TOKEN, returned_value.getString("token"));
					if( returned_value.has("to_setting_page")){
						First_launch = returned_value.getString("to_setting_page");
					}
					UserId = returned_value.getString("id");
					UserToken = returned_value.getString("token");
					Auth = true;
				}else{
					JSONArray error = returned_value.getJSONArray("error");
					//Log.d("error array", error.toString());
					reg_error = "";
					int arraySize = error.length();
					for(int i1 = 0; i1 < arraySize; i1++) {
						//Log.d("error array",error.get(i1).toString());
						try{
						    int id = context.getResources().getIdentifier(error.get(i1).toString(),  "string", getPackageName());
						    String value = (String) getResources().getText(id);
						    Log.d("ERROR STRING",value);
						}catch (Exception e) {
							//Log.d("ERROR STRING",e);
							e.printStackTrace();
							//reg_error=reg_error+" "+error.get(i1).toString();
						}
						
						if(error.get(i1).toString().equals("user_miss")){
							reg_error=reg_error+" "+context.getString(R.string.user_miss);
						}else if(error.get(i1).toString().equals("user_wrong_pass")){
							reg_error=reg_error+" "+context.getString(R.string.user_wrong_pass);
						}else{
							reg_error=reg_error+" "+error.get(i1).toString();
						}
						
					}
				}
				GCMRegistrar.setRegisteredOnServer(context, true);

				// Send Broadcast to Show message on screen
				String message = context.getString(R.string.server_registered);
				displayMessageOnScreen(context, message);
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {

				// Here we are simplifying and retrying on any error; in a real
				// application, it should retry only on unrecoverable errors
				// (like HTTP error code 503).

				Log.e(Config.TAG, "Failed to register on attempt " + i + ":"
						+ e);

				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {

					Log.d(Config.TAG, "Sleeping for " + backoff
							+ " ms before retry");
					Thread.sleep(backoff);

				} catch (InterruptedException e1) {
					// Activity finished before we complete - exit.
					Log.d(Config.TAG,
							"Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return;
				}

				// increase backoff exponentially
				backoff *= 2;
			}
		}

		String message = context.getString(R.string.server_register_error,
				MAX_ATTEMPTS);

		// Send Broadcast to Show message on screen
		displayMessageOnScreen(context, message);
	}

	public static final String md5(final String s) {
		final String MD5 = "MD5";
		try {
			// Create MD5 Hash
			MessageDigest digest = MessageDigest.getInstance(MD5);
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuilder hexString = new StringBuilder();
			for (byte aMessageDigest : messageDigest) {
				String h = Integer.toHexString(0xFF & aMessageDigest);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	// Register this account with the server.
	void register(final Context context, String name, String email,
			String phone, String lname, final String regId){
		Need_email = false;
		RegisterContext = context;
		Log.i(Config.TAG, "registering device (regId = " + regId + ")");

		String serverUrl = Config.REGISTER_AUTH_SERVER_URL;

		Map<String, String> params = new HashMap<String, String>();
		params.put("type", Reg_type);//type fb or app
		params.put("timezone", TimeZone);
		
		params.put("f_name", name);//user name
		params.put("l_name", lname);//last name
		
		if(!regId.equals("")){
			params.put("app_id", regId);//notification id
		}

		params.put("method", "register");//method register
		
		if (User_pass != null) {
			User_pass = md5(User_pass);
			params.put("pass", User_pass);//user pass if isset
		}
		if(!email.equals("")){
			params.put("email", email);//user mail if isset
		}
		
		if(FB_id!=null && !FB_id.equals("")){
			params.put("fb_id", FB_id);//user mail if isset
		}
		if(Country!= null && !Country.equals("")){
			params.put("country", Country);//user mail if isset
		}


		params.put("phone", phone);//user phone

		//params.put("task", "new");
		//params.put("format", "json");
		User_name = name;
		User_mail = email;
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

		// Once GCM returns a registration id, we need to register on our server
		// As the server might be down, we will retry it a couple
		// times.
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {

			Log.d(Config.TAG, "Attempt #" + i + " to register");

			try {
				// Send Broadcast to Show message on screen
				displayMessageOnScreen(context, context.getString(
						R.string.server_registering, i, MAX_ATTEMPTS));

				// Post registration values to web server
				JSONObject returned_value = post(serverUrl, params);
				
				if( returned_value.has("need_fields")){
					Need_email = true;
				}
				if( returned_value.has("to_setting_page")){
					First_launch = returned_value.getString("to_setting_page");
				}
				//UserAuth = returned_value.getString("result");
				if( !returned_value.has("error")){
					SetCustomParameters(getApplicationContext(), Config.APP_PREFERENCES_USER_ID, returned_value.getString("id"));//GetCustomParameters(getApplicationContext(), Config.APP_PREFERENCES_USER_ID);
					SetCustomParameters(getApplicationContext(), Config.APP_PREFERENCES_USER_TOKEN, returned_value.getString("token"));
					UserId = returned_value.getString("id");
					UserToken = returned_value.getString("token");
				}
				
				GCMRegistrar.setRegisteredOnServer(context, true);
				
				// Send Broadcast to Show message on screen
				String message = context.getString(R.string.server_registered);
				displayMessageOnScreen(context, message);

				First_register_launch = false;
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}catch (IOException e) {

				// Here we are simplifying and retrying on any error; in a real
				// application, it should retry only on unrecoverable errors
				// (like HTTP error code 503).

				Log.e(Config.TAG, "Failed to register on attempt " + i + ":"
						+ e);
				e.printStackTrace();
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {

					Log.d(Config.TAG, "Sleeping for " + backoff
							+ " ms before retry");
					Thread.sleep(backoff);

				} catch (InterruptedException e1) {
					// Activity finished before we complete - exit.
					Log.d(Config.TAG,
							"Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return;
				}

				// increase backoff exponentially
				backoff *= 2;
			}
		}

		String message = context.getString(R.string.server_register_error,
				MAX_ATTEMPTS);

		// Send Broadcast to Show message on screen
		displayMessageOnScreen(context, message);
	}

	// Unregister this account/device pair within the server.
	void unregister(final Context context, final String regId) {

		Log.i(Config.TAG, "unregistering device (regId = " + regId + ")");

		String serverUrl = Config.YOUR_SERVER_URL + "/unregister";
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);

		try {
			post(serverUrl, params);
			GCMRegistrar.setRegisteredOnServer(context, false);
			String message = context.getString(R.string.server_unregistered);
			displayMessageOnScreen(context, message);
		} catch (IOException e) {

			// At this point the device is unregistered from GCM, but still
			// registered in the our server.
			// We could try to unregister again, but it is not necessary:
			// if the server tries to send a message to the device, it will get
			// a "NotRegistered" error message and should unregister the device.

			String message = context.getString(
					R.string.server_unregister_error, e.getMessage());
			displayMessageOnScreen(context, message);
		}
	}

	// validating email id
	public boolean isValidEmail(String email) {
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	// Issue a POST request to the server.
	private static JSONObject post(String endpoint, Map<String, String> params)
			throws IOException {

		URL url;
		JSONObject register_answer=new JSONObject();
		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}

		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();

		// constructs the POST body using the parameters
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=')
					.append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}

		String body = bodyBuilder.toString();

		Log.v(Config.TAG, "Posting '" + body + "' to " + url);

		byte[] bytes = body.getBytes();

		HttpURLConnection conn = null;
		try {

			Log.d("URL", "> " + url);

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			// post the request
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			Log.d("Debug URL",new String(bytes));
			InputStream is = conn.getInputStream();
			byte[] b = new byte[900];
			String reader = null;
			while (is.read(b) != -1) {
				// Log.d("Debug URL","abra"+(new String(b)));
				reader = new String(b);
			}

			out.close();
			try {
				Log.d("Returned JSON",reader);
				
				register_answer = new JSONObject(reader);
				/*Integer status1 = register_answer.getInt("status");
				reg_status = status1;
				if (status1 == 1) {
					reg_error = register_answer.getString("error");
				} else if (status1 == 0) {
					if (Reg_type=="fb"){
						User_pass = register_answer.getString("password");
					}
					UserId = register_answer.getString("user");
					WriteUserInfo(RegisterContext);
				}*/

				// Log.i("JSON",status1.toString());
			} catch(JSONException e) {
				e.printStackTrace();
			}catch(Exception e) {
				e.printStackTrace();
			}
			// Log.i(Config.TAG, "bytes:" + bytes );
			// handle the response
			int status = conn.getResponseCode();

			// If response is not success
			if (status != 200) {

				throw new IOException("Post failed with error code " + status);
			}
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return register_answer;
	}

	
	public static String WriteUserId(Context contex) {

		mSettings = contex.getSharedPreferences(Config.APP_PREFERENCES, contex.MODE_PRIVATE);
		Editor editor = mSettings.edit();

    	editor.putString(Config.APP_PREFERENCES_USER_ID, UserId);
    	editor.apply();
    	return UserId;
	}
	public static String WriteFirstLaunch(Context contex) {

		mSettings = contex.getSharedPreferences(Config.APP_PREFERENCES, contex.MODE_PRIVATE);
		Editor editor = mSettings.edit();

    	editor.putString(Config.APP_PREFERENCES_FIRST_LAUNCH, First_launch);
    	editor.apply();
    	return First_launch;
	}
	
	public static String WriteUserInfo(Context contex) {

		mSettings = contex.getSharedPreferences(Config.APP_PREFERENCES, contex.MODE_PRIVATE);
		Editor editor = mSettings.edit();

    	editor.putString(Config.APP_PREFERENCES_USER_ID, UserId);
    	editor.putString(Config.APP_PREFERENCES_USER_NAME, User_name);
    	editor.putString(Config.APP_PREFERENCES_USER_PASS, User_pass);
    	editor.putString(Config.APP_PREFERENCES_USER_AUTH_TYPE, Reg_type);
    	editor.putString(Config.APP_PREFERENCES_USER_MAIL, User_mail);
    	editor.apply();
    	return UserId;
	}
	public static String GetLastAuthType(Context contex) {
		mSettings = contex.getSharedPreferences(Config.APP_PREFERENCES, contex.MODE_PRIVATE);
		String AuthType = "";
	    if (mSettings.contains(Config.APP_PREFERENCES_USER_AUTH_TYPE)) {
	    	AuthType = mSettings.getString(Config.APP_PREFERENCES_USER_AUTH_TYPE, "");
		}
	    return AuthType;
	}
	public static String GetUserId(Context contex) {
		mSettings = contex.getSharedPreferences(Config.APP_PREFERENCES, contex.MODE_PRIVATE);
		UserId = "";
	    if (mSettings.contains(Config.APP_PREFERENCES_USER_ID)) {
			UserId = mSettings.getString(Config.APP_PREFERENCES_USER_ID, "");
		}
	    return UserId;
	}
	public static String GetUserName(Context contex) {
		mSettings = contex.getSharedPreferences(Config.APP_PREFERENCES, contex.MODE_PRIVATE);
		String UserName = "";
	    if (mSettings.contains(Config.APP_PREFERENCES_USER_NAME)) {
	    	UserName = mSettings.getString(Config.APP_PREFERENCES_USER_NAME, "");
		}
	    return UserName;
	}
	public static String GetUserMail(Context contex) {
		mSettings = contex.getSharedPreferences(Config.APP_PREFERENCES, contex.MODE_PRIVATE);
		String UserMail = "";
	    if (mSettings.contains(Config.APP_PREFERENCES_USER_MAIL)) {
	    	UserMail = mSettings.getString(Config.APP_PREFERENCES_USER_MAIL, "");
		}
	    return UserMail;
	}

	public static String GetFirstLaunch(Context contex) {
		mSettings = contex.getSharedPreferences(Config.APP_PREFERENCES, contex.MODE_PRIVATE);
		String FirstLaunch = "";
	    if (mSettings.contains(Config.APP_PREFERENCES_FIRST_LAUNCH)) {
	    	FirstLaunch = mSettings.getString(Config.APP_PREFERENCES_FIRST_LAUNCH, "");
		}
	    return FirstLaunch;
	}
	public static String GetUserPass(Context contex) {
		mSettings = contex.getSharedPreferences(Config.APP_PREFERENCES, contex.MODE_PRIVATE);
		String UserPass = "";
	    if (mSettings.contains(Config.APP_PREFERENCES_USER_PASS)) {
	    	UserPass = mSettings.getString(Config.APP_PREFERENCES_USER_PASS, "");
		}
	    return UserPass;
	}
	
	public static String GetSettingNotifocation(Context contex) {
		mSettings = contex.getSharedPreferences(Config.APP_PREFERENCES, contex.MODE_PRIVATE);
		String Notification = "1";
	    if (mSettings.contains(Config.APP_PREFERENCES_SETTING_NOTIFICATION_ENABLED)) {
	    	Notification = mSettings.getString(Config.APP_PREFERENCES_SETTING_NOTIFICATION_ENABLED, "1");
		}
	    return Notification;
	}
	public static String WriteSettingNotifocation(Context contex,String value) {

		mSettings = contex.getSharedPreferences(Config.APP_PREFERENCES, contex.MODE_PRIVATE);
		Editor editor = mSettings.edit();
    	editor.putString(Config.APP_PREFERENCES_SETTING_NOTIFICATION_ENABLED, value);
    	editor.apply();
    	return value;
	}
	
	// Checking for all possible internet providers
	public boolean isConnectingToInternet() {

		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

	void displayMessageOnScreen(Context context, String message) {
		displayMessageOnScreen(context, message,"","");
	}
	
	void displayMessageOnScreen(Context context, String message,String title) {
		displayMessageOnScreen(context, message,title,"");
	}
	
	// Notifies UI to display a message.
	void displayMessageOnScreen(Context context, String message,String title,String LocalUserId) {
		//String title1 = title.length > 0 ? title[0] : "";
		Intent intent = new Intent(Config.DISPLAY_MESSAGE_ACTION);
		intent.putExtra(Config.EXTRA_MESSAGE, message);
		intent.putExtra(Config.EXTRA_MESSAGE_TITLE, title);
		//intent.putExtra("UserId", UserId);

		// Send Broadcast to Broadcast receiver with message
		context.sendBroadcast(intent);

	}

	// Function to display simple Alert Dialog
	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Set Dialog Title
		alertDialog.setTitle(title);

		// Set Dialog Message
		alertDialog.setMessage(message);

		if (status != null)
			// Set alert dialog icon
			alertDialog	.setIcon((status) ? R.drawable.success : R.drawable.fail);

		// Set OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		// Show Alert Message
		alertDialog.show();
	}
	
	public void creatCommentDialog(Context context) {

		commentDialog = new Dialog(context);
		commentDialog.setContentView(R.layout.reply);
		Button okBtn = (Button) commentDialog.findViewById(R.id.ok);
		okBtn.setOnClickListener(new View.OnClickListener() {

		                            @Override
		                            public void onClick(View v) {
		                                    //do anything you want here before close the dialog
		                            		EditText FB_Email_Edit = (EditText) commentDialog.findViewById(R.id.body);
		                            		FB_Email = FB_Email_Edit.getText().toString();
		                                    commentDialog.dismiss();
		                            }
		 });
		Button cancelBtn = (Button) commentDialog.findViewById(R.id.cancel);
		cancelBtn.setOnClickListener(new View.OnClickListener() {

		                            @Override
		                            public void onClick(View v) {

		                                    commentDialog.dismiss();
		                            }
		 });
		
	}
	public void showCommentDialog() {
		commentDialog.show();
	}
	// Function to display simple Alert Dialog
	public void showLoader(Activity activity) {
		
		AlertDialog.Builder  adb = new AlertDialog.Builder(activity,R.style.MyTheme);
	    //adb.setTitle("Custom dialog");
	    // ������� view �� dialog.xml
	    RelativeLayout view = (RelativeLayout) (activity).getLayoutInflater().inflate(R.layout.activity_dialog, null);
	    // ������������� ��, ��� ���������� ���� �������
	    //adb.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	    adb.setView(view);
	    // ������� TexView ��� ����������� ���-��
	    //tvCount = (TextView) view.findViewById(R.id.tvCount);
	    ImageView favicon = (ImageView) view.findViewById(R.id.imageView1);
	    
	    //LayoutForTrasparent.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	    dialog = adb.create();
	    //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

	    dialog.show();
	    //RelativeLayout LayoutForTrasparent = (RelativeLayout) view.findViewById(R.id.transparent);
	    //LayoutForTrasparent.setBackgroundColor(android.graphics.Color.TRANSPARENT);
	    RotateAnimation r;

	    final float ROTATE_FROM = 0.0f;
	    final float ROTATE_TO = 2*360.0f;// 3.141592654f * 32.0f;


		Long t1 = 5500L;
		Long t2 = 3L;
		Long t3 = 1700L;
		r = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		r.setInterpolator(new LinearInterpolator());
		r.setDuration((long) t2*t3);
		r.setRepeatCount(Animation.INFINITE);
		favicon.startAnimation(r);

	}
	
	public void cancelLoader() {
		if(dialog!=null) dialog.cancel();
	}

	private PowerManager.WakeLock wakeLock;

	@SuppressWarnings("deprecation")
	@SuppressLint("Wakelock")
	public void acquireWakeLock(Context context) {
		if (wakeLock != null)
			wakeLock.release();

		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);

		wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, "WakeLock");

		wakeLock.acquire();
	}

	public void releaseWakeLock() {
		if (wakeLock != null)
			wakeLock.release();
		wakeLock = null;
	}
	
	public static String GetCustomParameters(Context contex,String paramName) {
		mSettings = contex.getSharedPreferences(Config.APP_PREFERENCES, contex.MODE_PRIVATE);
		String Param = "";
	    if (mSettings.contains(paramName)) {
	    	Param = mSettings.getString(paramName, "");
		}
	    Log.d("Params Get",paramName+"\\|/"+Param);
	    return Param;
	}
	
	public static  void SetCustomParameters(Context contex,String paramName,String ParamValue) {
		mSettings = contex.getSharedPreferences(Config.APP_PREFERENCES, contex.MODE_PRIVATE);
		Editor editor = mSettings.edit();
    	editor.putString(paramName, ParamValue);
    	editor.apply();
    	Log.d("Params Write",paramName+"\\|/"+ParamValue);
	}
	

}
