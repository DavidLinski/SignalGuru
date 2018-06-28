package com.davidapps.forexsignals;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Arrays;

//import android.provider.Settings.Global;

public class RegisterListActivity extends Activity implements OnClickListener {

    // FB start
    private LoginButton FBloginButton;
    private TextView txtName;
    private final String PENDING_ACTION_BUNDLE_KEY = "com.signal.guru.MainActivity:PendingAction";
    private PendingAction pendingAction = PendingAction.NONE;

    private enum PendingAction {
        NONE, POST_PHOTO, POST_STATUS_UPDATE
    }

    // FB end
    SharedPreferences mSettings;
    AsyncTask<Void, Void, Void> mRegisterTask;

    // UI elements
    EditText txtPass;
    EditText txtEmail;
    EditText FB_Email_Edit;
    public String pass, email;
    // Auth button
    Button btnAuth;

    // G+ start
    private static final int RC_SIGN_IN = 0;
    private static final int REQUEST_CODE_RESOLVE_ERR = 2;

    // Logcat tag
    private static final String TAG = "MainActivity";
    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;

    private boolean mSignInClicked;
    public static Context curLayout;

    private ConnectionResult mConnectionResult;
    public Controller cController;

    private SignInButton btnSignIn;

    private Button btnRegister, btnSignUp;
    private ImageView imgProfilePic;
    ProgressDialog progressDialog;

    // private TextView txtName, txtEmail;
    // private LinearLayout llProfileLayout;
    // G+ end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.register_main);
        curLayout = this;
        cController = (Controller) getApplicationContext();
		/*
		 * progressDialog =
		 * ProgressDialog.show(RegisterListActivity.this,"Loading...",
		 * "Loading application View, please wait...", false, false);
		 */
        mSettings = getSharedPreferences(Config.APP_PREFERENCES,
                Context.MODE_PRIVATE);

        // FB start
        txtName = (TextView) findViewById(R.id.txt1);
        FBloginButton = (LoginButton) findViewById(R.id.fb_login_button);
        FBloginButton.setReadPermissions(Arrays.asList("public_profile","email", "user_friends"));

        FBloginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

            }
        });
        // FB end

        btnRegister = (Button) findViewById(R.id.register);
        // Button click listeners
        btnRegister.setOnClickListener(this);

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
        cController.creatCommentDialog(RegisterListActivity.this);
        // Check if Internet Connection present
        if (!aController.isConnectingToInternet()) {

            // Internet Connection is not present
            aController.showAlertDialog(RegisterListActivity.this,
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
            aController.showAlertDialog(RegisterListActivity.this,
                    "Configuration Error!",
                    "Please set your Server URL and GCM Sender ID", false);

            // stop executing code by return
            return;
        }

        txtPass = (EditText) findViewById(R.id.pass);
        txtEmail = (EditText) findViewById(R.id.Email);
        btnAuth = (Button) findViewById(R.id.btnAuth);

        // Click event on Register button
        btnAuth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                progressDialog = ProgressDialog.show(RegisterListActivity.this,
                        "Loading...",
                        "Loading application View, please wait...", false,
                        false);
                pass = txtPass.getText().toString();
                email = txtEmail.getText().toString();
                if (pass.trim().length() > 0 && email.trim().length() > 0) {
                    pass = txtPass.getText().toString();
                    email = txtEmail.getText().toString();
                    mRegisterTask = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            // Get data from EditText

                            // Check if user filled the form
                            if (pass.trim().length() > 0&& email.trim().length() > 0) {
                                String regId = GCMRegistrar.getRegistrationId(RegisterListActivity.this);

                                aController.Reg_type = "app";
                                aController.auth(RegisterListActivity.this,	email, pass, regId);


                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            progressDialog.dismiss();
                            if (aController.Auth) {
                                StartCustomActivity(MainActivity.class, 1);
                            } else {
                                aController.showAlertDialog(RegisterListActivity.this,"Sign in", aController.reg_error,false);
                            }
                        }

                    };
                    // execute AsyncTask
                    mRegisterTask.execute(null, null, null);
                } else {
                    progressDialog.dismiss();
                    // user doen't filled that data
                    aController.showAlertDialog(RegisterListActivity.this,
                            "Registration Error!", "Please enter your details",
                            false);
                }

            }

        });

        // btnSignUp = (Button) findViewById(R.id.sign_up);
        // btnSignUp.setOnClickListener(this);

        // G+ start
        // btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        // Button click listeners
        // btnSignIn.setOnClickListener(this);

		/*
		 * Plus.PlusOptions options = new Plus.PlusOptions.Builder()
		 * .addActivityTypes("http://schemas.google.com/AddActivity",
		 * "http://schemas.google.com/ReviewActivity") .build();
		 */

		/*
		 * mGoogleApiClient = new GoogleApiClient.Builder(this)
		 * .addConnectionCallbacks(this) .addOnConnectionFailedListener(this)
		 * .addApi(Plus.API) .addScope(Plus.SCOPE_PLUS_PROFILE) .build();
		 */
        // G+ end

        // Add code to print out the key hash
		/*
		 * try { PackageInfo info = getPackageManager().getPackageInfo(
		 * "com.signal.guru", PackageManager.GET_SIGNATURES); for (Signature
		 * signature : info.signatures) { MessageDigest md =
		 * MessageDigest.getInstance("SHA"); md.update(signature.toByteArray());
		 * Log.d("KeyHash:", Base64.encodeToString(md.digest(),
		 * Base64.DEFAULT)); } } catch (NameNotFoundException e) {
		 *
		 * } catch (NoSuchAlgorithmException e) {
		 *
		 * }
		 */
        // UP9r/rVUzIFLYmP5B3BD0nWt7qw=

		/*
		 * FBloginButton.setUserInfoChangedCallback(new
		 * LoginButton.UserInfoChangedCallback() {
		 *
		 * @Override public void onUserInfoFetched(GraphUser user) {
		 * MainActivity.this.user = user;
		 *
		 * //Log.e(Config.TAG, MainActivity.this.user.getUsername());
		 * updateUI(); // It's possible that we were waiting for this.user to be
		 * populated in order to post a // status update.
		 * //handlePendingAction(); } });
		 */
		/*
		 * Button g_plus_sign = (Button)
		 * findViewById(R.id.g_plus_sign_in_button);
		 * g_plus_sign.setOnClickListener(new View.OnClickListener() {
		 *
		 * @Override public void onClick(View arg0) { Intent i = new
		 * Intent(MainActivity.this, SplashScreen.class); startActivity(i); }
		 * });
		 */

        // findViewById(R.id.sign_in_button).setOnClickListener((OnClickListener)
        // this);
		/*
		 * Button btnSplash = (Button) findViewById(R.id.button1);
		 *
		 * // Click event on Register button btnSplash.setOnClickListener(new
		 * View.OnClickListener() {
		 *
		 * @Override public void onClick(View arg0) { Intent i = new
		 * Intent(MainActivity.this, SplashScreen.class); startActivity(i); }
		 * });
		 */

    }

    @SuppressWarnings("deprecation")
	/*private void onSessionStateChange(LoginManager session, SessionState state, Exception exception) {
    	if (session != null && session.isOpened()) {
    		Log.d("DEBUG", "facebook session is open ");

    			// make request to the /me API
    			LoginClient.Request.executeMeRequestAsync(session, new LoginClient.Request.GraphUserCallback() {
                // callback after Graph API response with user object
                @Override
                public void onCompleted(GraphUser user, Response response) {

                	if (user != null) {
                		RegisterListActivity.this.user = user;
                		progressDialog = ProgressDialog.show(RegisterListActivity.this,"Loading...","Loading application View, please wait...", false,false);
                        //Log.e(Config.TAG, RegisterListActivity.curLayout.toString());
                        //updateUI();
                		cController.FB_Email="";
                		if(user.asMap().get("email")!=null){
                			Log.d("DEBUG", "email: " + user.asMap().get("email").toString());
                			cController.FB_Email = user.asMap().get("email").toString();
                		}else{
                			//commentDialog.show();
                		}


                		class PostRegister extends AsyncTask<Void, Void, Void>{
 							@Override
 							protected Void doInBackground(Void... params) {
 								// Register on our server
 								// On server creates a new user
 								while(cController.commentDialog.isShowing()){
 		 							try {
 										Thread.currentThread().sleep(1000);
 									} catch (InterruptedException e) {
 										// TODO Auto-generated catch block
 										e.printStackTrace();
 									}
 		 						}

 								//Log.i(Config.TAG, "register started +"+error );
 								String LastAuthType = cController.GetLastAuthType(getApplicationContext());
 								if (cController.Need_email && cController.FB_Email.equals("")){
 									return null;
 								}

 								cController.FB_id = RegisterListActivity.this.user.getId();
 								String FB_Fname = RegisterListActivity.this.user.getFirstName();
 					            String FB_Lname = RegisterListActivity.this.user.getLastName();
 					            String phone = "";

 					            String regId = GCMRegistrar.getRegistrationId(RegisterListActivity.this);
 					            if (regId.equals("")) {
 									// Register with GCM
 									GCMRegistrar.register(curLayout, Config.GOOGLE_SENDER_ID);
 									regId = GCMRegistrar.getRegistrationId(RegisterListActivity.this);
 					            }

 					        	cController.User_pass = null;
 					        	cController.Reg_type = "fb";
 					        	TimeZone tz = TimeZone.getDefault();
 					        	cController.TimeZone = tz.getID();
 					        	cController.RegisterContext = curLayout;
 					        	//String userId = cController.GetUserId(getApplicationContext());
 					        	//if (userId==null||userId.equals("")){
 					        		cController.register(RegisterListActivity.this, FB_Fname, cController.FB_Email, phone, FB_Lname, regId);
 					        	//}

 								//onPostExecute(null);
 					        	mRegisterTask.cancel(false);
 								return null;
 							}
 							@Override
 							protected void onPostExecute(Void result) {
 								progressDialog.dismiss();
 								//if(!cController.Need_email){
 									StartCustomActivity(MainActivity.class,0);
 									finish();
 								//}
 								//mRegisterTask.cancel(false);
 								//Log.d("Task", "onPostExecute");

 								super.onPostExecute(result);
 								//Thread.currentThread().stop();
 								//mRegisterTask.cancel(false);

	                			//mRegisterTask = null;

 								*//*
 								if (cController.reg_status==1){
 									cController.showAlertDialog(RegisterListActivity.this, "Registration", cController.reg_error, false);
 									StartCustomActivity(MainActivity.class,0);
 								}else if (cController.reg_status==0){
 									StartCustomActivity(MainActivity.class,0);
 								}*//*
 							}
 							@Override
 						    protected void onCancelled() {
 						      super.onCancelled();

 						      //Log.d("Task", "Cancel");
 						     //if(!cController.Need_email){
									StartCustomActivity(MainActivity.class,0);
									finish();
								//}
 						    }
 							// progressDialog.dismiss();

 						};
 						cController.Need_email = false;
 						cController.First_register_launch = true;
 						mRegisterTask = new PostRegister();
 						mRegisterTask.execute(null, null, null);

                		//commentDialog.show();
 						*//*while(cController.First_register_launch || !(mRegisterTask.isCancelled())){
							try {
								Thread.currentThread().sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						if(cController.Need_email){
								cController.showCommentDialog();
								mRegisterTask = new PostRegister();
		 						mRegisterTask.execute(null, null, null);
	                	}*//*

						*//*while( !(mRegisterTask.isCancelled())){
							try {
								//Log.d("Task",Boolean.toString(mRegisterTask.isCancelled()));
								//Log.d("Task",mRegisterTask.getStatus().toString());
								Thread.currentThread().sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}*//*

						// execute AsyncTask


                	}
                	//progressDialog.dismiss();
                }
            });

    	}

        updateUI();
    }

    private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
        @Override
        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
        }

        @Override
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            Log.d("HelloFacebook", "Success!");
        }
    };
    */

/*
	private void updateUI() {
		LoginManager session = LoginManager.getInstance();
		boolean enableButtons = (session != null && session.isOpened());

		// postStatusUpdateButton.setEnabled(enableButtons ||
		// canPresentShareDialog);
		// postPhotoButton.setEnabled(enableButtons ||
		// canPresentShareDialogWithPhotos);
		// pickFriendsButton.setEnabled(enableButtons);
		// pickPlaceButton.setEnabled(enableButtons);

		if (enableButtons && user != null) {
			txtName.setText(user.getFirstName());
			// String email = user.getProperty("email").toString();

			// Log.i("email", user.asMap().get("email").toString());
			// profilePictureView.setProfileId(user.getId());
			// Log.e(Config.TAG, MainActivity.this.user.getUsername());
			// greeting.setText(getString(R.string.hello_user,
			// user.getFirstName()));
		} else {
			// profilePictureView.setProfileId(null);
			// greeting.setText(null);
		}
	}
*/

/*	// FB end
	@Override
	protected void onResume() {
		super.onResume();
		// FB start
		uiHelper.onResume();
		// Call the 'activateApp' method to log an app event for use in
		// analytics and advertising reporting. Do so in
		// the onResume methods of the primary Activities that an app may be
		// launched into.
		AppEventsLogger.activateApp(this);
		updateUI();

		LoginManager  openSession = LoginManager .getActiveSession();

        if (openSession != null) {
        	if (cController.Reg_type=="logout"){
        		openSession.closeAndClearTokenInformation();
        		cController.Reg_type="";
        	}
        }
		// FB end
	}*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // FB start
        //uiHelper.onSaveInstanceState(outState);
        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
        // FB end
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // FB start
        //uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
        // FB end
    }

    @Override
    public void onPause() {
        super.onPause();
        // FB start
        //uiHelper.onPause();
        // Call the 'deactivateApp' method to log an app event for use in
        // analytics and advertising
        // reporting. Do so in the onPause methods of the primary Activities
        // that an app may be launched into.
        AppEventsLogger.deactivateApp(this);
        // FB end
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // FB start
        //uiHelper.onDestroy();
        // FB end
    }

    /**
     * Button on click listener
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //case R.id.btn_sign_in:
            // Signin button clicked
            //signInWithGplus();
            //break;
            case R.id.register:
                // Register button clicked
                StartCustomActivity(RegisterActivity.class,1);
                break;
            //case R.id.sign_up:
            // Register button clicked
            //StartCustomActivity(AuthActivity.class,0);
            //break;
            //case R.id.btn_revoke_access:
            // Revoke access button clicked
            //revokeGplusAccess();
            //break;
        }
    }

    private void StartCustomActivity( Class<?> cls,int finish) {
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
        overridePendingTransition(R.anim.fade_bottom_in_splash,
                R.anim.fade_top_out_splash);
        return;

    }

}
