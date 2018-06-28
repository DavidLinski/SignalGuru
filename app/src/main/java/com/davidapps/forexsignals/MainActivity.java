package com.davidapps.forexsignals;

//import com.actionbarsherlock.view.Window;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gcm.GCMRegistrar;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.davidapps.forexsignals.tools.ExitDialog;


public class MainActivity extends SherlockFragmentActivity implements ExitDialog.ExitDialogCallback {
    public Controller cController;
    //public TextView BodyMessage,TitleMessage;
    Button LogOut;

    LinearLayout ll;
    protected static MainActivity instance;
    protected static MenuSlider menu;
    MyWebChromeClient mWebChromeClient = null;
    private ExitDialog exitDialog;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //	getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5d1026")));
        // super.onCreate(savedInstanceState);


        Integer First_launch=2;

        //set custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        //ini webchrome client
        mWebChromeClient = new MyWebChromeClient();

        cController = (Controller)  getApplicationContext();
        cController.UserId = cController.GetCustomParameters(getApplicationContext(),Config.APP_PREFERENCES_USER_ID);
        cController.UserToken = cController.GetCustomParameters(getApplicationContext(),Config.APP_PREFERENCES_USER_TOKEN);
        if (cController.First_launch != null && cController.First_launch.equals("need")){
            First_launch=1;
            cController.First_launch = "not_need";
            cController.WriteFirstLaunch(this);
        }else{
            First_launch=1;
        }
        //Log.d("token",cController.GetCustomParameters(getApplicationContext(),  Config.APP_PREFERENCES_USER_TOKEN));
        //First_launch=10;
        // set the content view
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ll = (LinearLayout) findViewById(R.id.mainfraim);
        //ll.setVisibility(View.GONE);
        // - See more at:
        // http://www.survivingwithandroid.com/2012/11/android-sliding-menu-animation.html#sthash.xeWst61o.dpuf

        //setContentView(R.layout.activity_main);
        MainActivity.instance = this;
        this.menu = new MenuSlider(this);

        //Log.v(Config.TAG, "befor register Broadcast receiver");
        // Register custom Broadcast receiver to show messages on activity
        //registerReceiver(mHandleMessageReceiver, new IntentFilter(Config.DISPLAY_MESSAGE_ACTION));
        //Log.v(Config.TAG, "after register Broadcast receiver");
        if(mWebChromeClient.mCustomView==null){
            FragmentFactory factory = new FragmentFactory(this, First_launch);
        }



    }


    /* METHODS */
    public void menuToggle() {
        if (getSideMenu().isMenuShowing())
            menu.value.showContent();
        else
            menu.value.showMenu();
    }

    public void HideMenu() {
        if (getSideMenu().isMenuShowing())
            menu.value.showContent();
    }

    public static MainActivity getActivity() {
        return MainActivity.instance;
    }


    public static SlidingMenu getSideMenu() {
        return menu.value;
    }

    public static ListView getSideMenuList() {
        return (ListView) getActivity().findViewById(R.id.sidemenu);
    }


    // Create a broadcast receiver to get message and show on screen
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String newMessage = intent.getExtras().getString(Config.EXTRA_MESSAGE);
            //String newMessageTitle = intent.getExtras().getString(Config.EXTRA_MESSAGE_TITLE);
            //cController.UserId = intent.getExtras().getString("UserId");

            // Waking up mobile if it is sleeping
            cController.acquireWakeLock(getApplicationContext());

            // Display message on the screen
            //BodyMessage.setText(newMessage);
            //TitleMessage.setText(newMessageTitle);

            //Toast.makeText(getApplicationContext(), "Got Message: " + newMessage, Toast.LENGTH_LONG).show();

            // Releasing wake lock
            cController.releaseWakeLock();
        }
    };
    @Override
    public void onBackPressed() {
        if(mWebChromeClient.mCustomView==null){
            if(cController.loadMainFrame==1){
                new FragmentFactory(this, 1);
            }else{
                if(exitDialog == null)
                    exitDialog = new ExitDialog(this,this);
                exitDialog.show();
            }
        }else{
            mWebChromeClient.onHideCustomView();
        }
		/*        if (mCustomViewContainer != null)
            mWebChromeClient.onHideCustomView();
        else if (webview.canGoBack())
        	webview.goBack();
        else
            this.owner.onBackPressed();
            */
    }

    @Override
    public void onCloseAppRequested() {
        super.onBackPressed();
    }

    public class MyWebChromeClient extends WebChromeClient {

        FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams ParentLayoutParams = new RelativeLayout.LayoutParams(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        public RelativeLayout ParentLayout;
        public MainActivity owner;
        public FrameLayout mCustomViewContainer;
        public FrameLayout mContentView;
        public CustomViewCallback mCustomViewCallback;
        public View mCustomView;

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            // if a view already exists then immediately terminate the new one

            mCustomViewCallback = callback;
            mCustomViewContainer.addView(view);
            mCustomView = view;
            mContentView.setVisibility(View.GONE);
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();

            mCustomViewContainer.setVisibility(View.VISIBLE);

            mCustomViewContainer.bringToFront();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null)
                return;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            //WindowManager.LayoutParams attrs = getWindow().getAttributes();
            //attrs.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            //getWindow().setAttributes(attrs);
            ActionBar actionBar = getSupportActionBar();
            actionBar.show();
            mCustomView.setVisibility(View.GONE);
            mCustomViewContainer.removeView(mCustomView);
            mCustomView = null;
            mCustomViewContainer.setVisibility(View.GONE);
            mCustomViewCallback.onCustomViewHidden();
            mContentView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onDestroy() {
        try {
            // Unregister Broadcast Receiver
            unregisterReceiver(mHandleMessageReceiver);

            //Clear internal resources.
            GCMRegistrar.onDestroy(this);

        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main1, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	*/

    public void StartCustomActivity( Class<?> cls,int finish) {
        Intent i = new Intent(getApplicationContext(), cls);

        // Registering user on our server
        // Sending registraiton details to MainActivity

        startActivity(i);
        if (finish==1){
            finish();
        }
        overridePendingTransition(R.anim.fade_out_splash,R.anim.fade_in_splash);

    }

    public void selectMenuItem(int index) {
        FragmentFactory factory = new FragmentFactory(this, index);
    }
}
