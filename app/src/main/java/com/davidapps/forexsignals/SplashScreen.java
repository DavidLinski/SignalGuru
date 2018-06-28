package com.davidapps.forexsignals;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.google.android.gcm.GCMRegistrar;

public class SplashScreen extends Activity {
	// Splash screen timer
    private static int SPLASH_TIME_OUT = 6000;
    private static final float ROTATE_FROM = 0.0f;
    private static final float ROTATE_TO = 2*360.0f;// 3.141592654f * 32.0f;
    public Controller cController;
    SharedPreferences mSettings;
    AsyncTask<Void, Void, Void> mRegisterTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
        //int version = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
		cController = (Controller) getApplicationContext();
		cController.reload_page = 1;
		//cController.UserId="";
		//cController.WriteUserId(this);
		String regId = GCMRegistrar.getRegistrationId(this);
		Log.d("ID",regId);
        if (regId.equals("")) {
			// Register with GCM			
			GCMRegistrar.register(this, Config.GOOGLE_SENDER_ID);
        }
        //cController.auth(this, , pass, regId)
        cController.First_launch= cController.GetFirstLaunch(this);
        
        
        
        
        
        mRegisterTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// Get data from EditText
				
					String regId = GCMRegistrar.getRegistrationId(getApplicationContext());
					cController.UserId = cController.GetCustomParameters(getApplicationContext(), Config.APP_PREFERENCES_USER_ID);
					cController.UserToken =cController.GetCustomParameters(getApplicationContext(), Config.APP_PREFERENCES_USER_TOKEN);

					cController.CheckAuth(getApplicationContext(), cController.UserId, cController.UserToken, regId);


				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				Intent i = getIntent();

				Long t1 = i.getLongExtra("t1",5500);
				new Handler().postDelayed(new Runnable() {
					 
		            /*
		             * Showing splash screen with a timer. This will be useful when you
		             * want to show case your app logo / company
		             */
		 
		            @Override
		            public void run() {
		            	String UserAuth = cController.UserAuth;
		            	if (UserAuth.equals("no")){
			                // This method will be executed once the timer is over
			                // Start your app main activity

			                Intent i = new Intent(SplashScreen.this, RegisterListActivity.class);
			                startActivity(i);
			 
			                // close this activity
			                finish();
		            	}else if (UserAuth.equals("yes")){
		            		// This method will be executed once the timer is over
			                // Start your app main activity
		            		
			                Intent i = new Intent(SplashScreen.this, MainActivity.class);
			                startActivity(i);
			 
			                // close this activity
			                finish();
		            	}
		                //overridePendingTransition(R.anim.fade_in_splash, R.anim.fade_out_splash);
		            }
		        }, t1);
			}

		};
		// execute AsyncTask
		mRegisterTask.execute(null, null, null);
        

		
		Log.d("Status auth",mRegisterTask.getStatus().toString());

        
        
		ImageView favicon = (ImageView) findViewById(R.id.imageView1);

		Intent i = getIntent();

		Long t1 = i.getLongExtra("t1",5500);
		Long t2 = i.getLongExtra("t2",3);
		Long t3 = i.getLongExtra("t3",1700);
		
		RotateAnimation r;
		r = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		r.setInterpolator(new LinearInterpolator());
		r.setDuration((long) t2*t3);
		r.setRepeatCount(0);
		favicon.startAnimation(r);
		
		
		
	}

}
