package com.davidapps.forexsignals;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gcm.GCMRegistrar;

import java.util.ArrayList;
import java.util.Locale;

public class RegisterActivity extends Activity {

	// UI elements
	EditText txtName, txtEmail, LName, phone, EDpass, EDpassConfirm;

	Controller aController;

	public static Context curLayout;
	// Register button
	Button btnRegister, btnLogin;
	AsyncTask<Void, Void, Void> mRegisterTask;

	ArrayList<String> CountryCodelist = new ArrayList<String>();
	ArrayList<String> CountryNameList = new ArrayList<String>();

	public static String name;
	public static String country;
	public static String email;
	public static String lname;
	public static String pass;
	public static String cphone;
	public static String error;
	public static int status;
	ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		error = "";
		status = 0;
		final Context curLayout = this;

// http://www.mkyong.com/java/display-a-list-of-countries-in-java/
		String[] locales = Locale.getISOCountries();
		CountryCodelist.add("ind1");
		CountryNameList.add("Choose a country");
		for (String countryCode : locales) {
	 
			Locale obj = new Locale("", countryCode);
			//Log.d("COUNTRY_getDisplayCountry", obj.getDisplayCountry());
			CountryCodelist.add(obj.getCountry());
			CountryNameList.add(obj.getDisplayCountry());
			obj = new Locale("", obj.getDisplayCountry());
			//Log.d("COUNTRY_getCountry", obj.getCountry());
			//System.out.println("Country Code = " + obj.getCountry() + ", Country Name = " + obj.getDisplayCountry());
	 
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.my_spinner_item, CountryNameList);
        adapter.setDropDownViewResource(R.layout.spinner_my_dropdown_item);
        
        Spinner spinner = (Spinner) findViewById(R.id.country_list);
        spinner.setAdapter(adapter);
        
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                int position, long id) {
              // ���������� ������� �������� ��������
            	//Log.d("SELECTED COUNTRY",CountryCodelist.get(position));
            	country = CountryCodelist.get(position);
              //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
          });
        
		final String regId = GCMRegistrar.getRegistrationId(this);

		// Check if regid already presents
		if (!regId.equals("")) {

			// Device is already registered on GCM Server
			if (GCMRegistrar.isRegisteredOnServer(this)) {

				// Skips registration.
				// Toast.makeText(getApplicationContext(),
				// "Already registered with GCM Server",
				// Toast.LENGTH_LONG).show();

				// Intent i = new Intent(getApplicationContext(),
				// MainActivity.class);

				// Registering user on our server
				// Sending registraiton details to MainActivity
				// String name = "";
				// String email = "";
				// i.putExtra("name", name);
				// i.putExtra("email", email);
				// startActivity(i);
				// finish();
			}
		}

		// Get Global Controller Class object (see application tag in
		// AndroidManifest.xml)
		final Controller aController = (Controller) getApplicationContext();

		// Check if Internet Connection present
		if (!aController.isConnectingToInternet()) {

			// Internet Connection is not present
			aController.showAlertDialog(RegisterActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);

			// stop executing code by return
			return;
		}

		// Check if GCM configuration is set
		if (Config.YOUR_SERVER_URL == null || Config.GOOGLE_SENDER_ID == null
				|| Config.YOUR_SERVER_URL.length() == 0
				|| Config.GOOGLE_SENDER_ID.length() == 0) {

			// GCM sernder id / server url is missing
			aController.showAlertDialog(RegisterActivity.this,
					"Configuration Error!",
					"Please set your Server URL and GCM Sender ID", false);

			// stop executing code by return
			return;
		}

		txtName = (EditText) findViewById(R.id.txtName);
		LName = (EditText) findViewById(R.id.lName);
		phone = (EditText) findViewById(R.id.phone);
		phone.addTextChangedListener(new TextWatcher() {
            private boolean mFormatting; // this is a flag which prevents the  stack overflow.
            private int mAfter;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // nothing to do here.. 
            }

            //called before the text is changed...
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               //nothing to do here...
                mAfter  =   after; // flag to detect backspace..

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Make sure to ignore calls to afterTextChanged caused by the work done below
                if (!mFormatting) {
                    mFormatting = true;
                   // using US formatting...
                    if(mAfter!=0) // in case back space ain't clicked...
            PhoneNumberUtils.formatNumber(s,PhoneNumberUtils.getFormatTypeForLocale(Locale.getDefault()));                             
                     mFormatting = false;
                }
            }
        });
		EDpass = (EditText) findViewById(R.id.pass);
		EDpassConfirm = (EditText) findViewById(R.id.confirm_pass);
		txtEmail = (EditText) findViewById(R.id.txtEmail);

		btnLogin = (Button) findViewById(R.id.login);
		// Click event on Register button
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				StartCustomActivity(RegisterListActivity.class,1);
			}
		});
		btnRegister = (Button) findViewById(R.id.btnRegister);

		// Click event on Register button
		btnRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String ConfirmPass1 = EDpass.getText().toString();
				String ConfirmPass2 = EDpassConfirm.getText().toString();
				email = txtEmail.getText().toString();
				if(ConfirmPass1.equals("") || ConfirmPass2.equals("")){
					aController.showAlertDialog(RegisterActivity.this,
							"Registration", "Please, fill password and confirm password field", false);
					return;
				}
				if (!ConfirmPass1.equals(ConfirmPass2)) {
					aController.showAlertDialog(RegisterActivity.this,
							"Registration", "Passwords are not equal", false);
					// Boolean Validate = aController.isValidEmail(email);
					// Log.e("VALID_EMAIL",Validate.toString());
					return;
				}
				if (!aController.isValidEmail(email)) {
					aController.showAlertDialog(RegisterActivity.this,
							"Registration", "E-mail not valid", false);
					return;
				}
				if (country.equals("ind1")) {
					aController.showAlertDialog(RegisterActivity.this,
							"Registration", "Please, select country", false);
					return;
				}
				aController.Country = country;
				// Get data from EditText
				name = txtName.getText().toString();
				if (name.equals("")) {
					aController.showAlertDialog(RegisterActivity.this,
							"Registration", "Please, enter name field", false);
					return;
				}
				lname = LName.getText().toString();
				if (lname.equals("")) {
					aController.showAlertDialog(RegisterActivity.this,
							"Registration", "Please, enter lastname field", false);
					return;
				}
				
				cphone = phone.getText().toString();
				if (cphone.equals("")) {
					aController.showAlertDialog(RegisterActivity.this,
							"Registration", "Please, enter phone", false);
					return;
				}
				aController.User_pass = EDpass.getText().toString();

				// Check if user filled the form
				if (name.trim().length() > 0 && email.trim().length() > 0) {
					progressDialog = ProgressDialog.show(RegisterActivity.this,"Loading...","Loading application View, please wait...", false,false);
					// Make sure the device has the proper dependencies.
					GCMRegistrar.checkDevice(curLayout);

					// Make sure the manifest permissions was properly set
					GCMRegistrar.checkManifest(curLayout);

					// Get GCM registration id
					final String regId = GCMRegistrar.getRegistrationId(curLayout);
					// Check if regid already presents
					//if (regId.equals("")) {
						// Register with GCM
						GCMRegistrar.register(curLayout,Config.GOOGLE_SENDER_ID);
						String regId1 = GCMRegistrar.getRegistrationId(curLayout);
						Log.i(Config.TAG, "regId1" + regId1);
					//} else {
						// Device is already registered on GCM Server
						// if (GCMRegistrar.isRegisteredOnServer(curLayout)) {
						// Skips registration.
						// Toast.makeText(getApplicationContext(),
						// "Already registered with GCM Server",
						// Toast.LENGTH_LONG).show();
						// } else {
						// Try to register again, but not in the UI thread.
						// It's also necessary to cancel the thread onDestroy(),
						// hence the use of AsyncTask instead of a raw thread.
						Log.i(Config.TAG, "start register");
						final Context context = curLayout;
						mRegisterTask = new AsyncTask<Void, Void, Void>() {
							@Override
							protected Void doInBackground(Void... params) {
								// Register on our server
								// On server creates a new user
								
								Log.i(Config.TAG, "register started +" + error);
								aController.Reg_type = "app";
								aController.register(context, name, email,cphone, lname, regId);
								
								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								mRegisterTask = null;
								progressDialog.dismiss();
								if (aController.reg_status == 1) {
									aController.showAlertDialog(RegisterActivity.this,"Registration",aController.reg_error, false);
								} else if (aController.reg_status == 0) {
									// Toast.makeText(getApplicationContext(),
									// "Register success",
									// Toast.LENGTH_LONG).show();
									StartCustomActivity(MainActivity.class, 1);
								}
							}

						};
						// execute AsyncTask
						mRegisterTask.execute(null, null, null);
						// }
					//}

				} else {
					progressDialog.dismiss();
					// user doen't filled that data
					aController.showAlertDialog(RegisterActivity.this,
							"Registration Error!", "Please enter your details",
							false);
				}
			}
		});
	}

	private void StartCustomActivity(Class<?> cls, int finish) {
		Intent i = new Intent(getApplicationContext(), cls);

		// Registering user on our server
		// Sending registraiton details to MainActivity

		startActivity(i);
		if (finish == 1) {
			finish();
		}
		overridePendingTransition(R.anim.fade_out_splash, R.anim.fade_in_splash);

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		StartCustomActivity(RegisterListActivity.class,0);
		return;
	}

	@Override
	protected void onDestroy() {
		// Cancel AsyncTask
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		super.onDestroy();
	}

}
