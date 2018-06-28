package com.davidapps.forexsignals;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class AuthActivity extends Activity {



	AsyncTask<Void, Void, Void> mRegisterTask;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		
	}

	private void StartCustomActivity( Class<?> cls,int finish) {
    	Intent i = new Intent(getApplicationContext(), cls);
		
		// Registering user on our server					
		// Sending registraiton details to MainActivity

		startActivity(i);
		if (finish==1){
			finish();
		}
		overridePendingTransition(R.anim.fade_out_splash,R.anim.fade_in_splash);
		
    }
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_bottom_in_splash,R.anim.fade_top_out_splash);
		return;

	}

}
