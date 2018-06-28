package com.davidapps.forexsignals;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


//signals and performances
public class MainFrame extends Fragment {

    private String FOREX_SIGNAL_ADDRESS = "http://signal-guru.com/signalguru/v2/index.php?method=page&type=app&page=last_signal_page&spec_page=forex";


    static Controller cController;

    MainActivity owner;
    View viewHierarchy;
    ProgressDialog progressDialog;
    //public TextView BodyMessage, TitleMessage;
    Button LogOut;
    public WebView webview;
    Integer StopTimer;
    Handler postDelayedHandler, mainLooper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        viewHierarchy = inflater.inflate(R.layout.activity_main1,container, false);
        createController();
        postDelayedHandler = new Handler();
        mainLooper = new Handler(Looper.getMainLooper());
        return viewHierarchy;
    }
    public MainFrame(){
        this.owner = MainActivity.instance;
    }
    @Override
    public void onPause(){
        cController.reload_page=1;
        StopTimer = 1;
        Log.d("PAGE_URL_USER", "main frame paused");
        super.onPause();
    }

    @Override
    public void onResume(){
        cController.reload_page=1;
        StopTimer = 0;
        super.onResume();
    }

    public void setOwner(MainActivity owner) {
        this.owner = owner;
    }

    public void createController() {
        StopTimer = 0;
        //TitleMessage = (TextView) viewHierarchy.findViewById(R.id.massageTitle);
        //BodyMessage = (TextView) viewHierarchy.findViewById(R.id.massageBody);

        OnClickListener listener = new MainFrameViewListener(owner);

        cController = (Controller)  getActivity().getApplicationContext();
        cController.loadMainFrame=0;
        Intent i = getActivity().getIntent();
        String msg;
        //if ((msg = i.getStringExtra(Config.EXTRA_MESSAGE)) != null) {
        //BodyMessage.setText(msg);
        //}

        //if ((msg = i.getStringExtra(Config.EXTRA_MESSAGE_TITLE)) != null) {
        //TitleMessage.setText(msg);
        //}

        //if ((msg = i.getStringExtra("UserId")) != null) {
        //cController.UserId = msg;
        //}
        cController.UserId = cController.GetCustomParameters(viewHierarchy.getContext(),Config.APP_PREFERENCES_USER_ID);
        cController.UserToken = cController.GetCustomParameters(viewHierarchy.getContext(),Config.APP_PREFERENCES_USER_TOKEN);
        webview = (WebView) viewHierarchy.findViewById(R.id.webView1);

        final Timer myTimer = new Timer(); // ������� ������
        final Handler uiHandler = new Handler();
        myTimer.schedule(new TimerTask() { // ���������� ������
            @Override
            public void run() {
                if(StopTimer==0){
                    if (!cController.UserId.equals("")){
                        if (cController.reload_page==1){
                            StopTimer=1;
                            cController.reload_page=0;
                            myTimer.cancel();
                            uiHandler.postDelayed(new Runnable() {
                                public int timesLogged;
                                public int counter;

                                @Override
                                public void run() {
                                    TimeZone tz = TimeZone.getDefault();
                                    StopTimer = 1;
                                    //Log.e("TIMEZONE","TimeZone   "	+ tz.getDisplayName(false,TimeZone.SHORT) + " Timezon id :: " + tz.getID());
                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String strDate = sdf.format(c.getTime());
                                    String page = Config.REGISTER_AUTH_SERVER_URL + "?method=page&type=app&page=last_signal_page"+"&id="+ cController.UserId+"&token="+cController.UserToken;
                                    Log.d("PAGE_URL_USER", page);
                                    webview.clearCache(true);
                                    WebSettings webSettings = webview.getSettings();

                                    webSettings.setJavaScriptEnabled(true);
                                    webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                                    //progressDialog = ProgressDialog.show(getActivity(),	"Loading...","Loading application View, please wait...", false,false);
                                    cController.showLoader(owner);
                                    webview.setWebViewClient(new WebViewClient() {
                                        @Override
                                        public void onPageFinished(WebView view, String url) {

                                            removeBinarySignalBtn();
                                            webview.loadUrl("javascript: window.CallToAnAndroidFunction.setVisible()");

                                            /*if(timesLogged ==2) {
                                                removeBinarySignalBtn();
                                                webview.loadUrl("javascript: window.CallToAnAndroidFunction.setVisible()");
                                                timesLogged = 1;
                                            }
                                            else
                                                timesLogged++;
*/
                                            cController.cancelLoader();
                                            //if(progressDial,oog.isShowing()&&progressDialog!=null){
                                            //progressDialog.dismiss();
                                            //}
                                        }
                                        @Override
                                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                            if(url.contains(Config.REGISTER_AUTH_SERVER_URL)) {
                                                view.loadUrl(url);
                                            } else {
                                                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                startActivity(i);
                                            }
                                            return true;
                                        }
                                    });


                                    //js script
                                    webview.addJavascriptInterface(new myJavaScriptInterface(), "CallToAnAndroidFunction");

                                    timesLogged = 1;
                                    webview.setVisibility(View.INVISIBLE);
                                    webview.loadUrl(page +"&spec_page=forex");

                                   /* postDelayedHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                        }
                                    },1500);

*/
                                    // Toast.makeText(getApplicationContext(), page,
                                    // Toast.LENGTH_LONG).show();

                                    //Log.v(Config.TAG, "befor register Broadcast receiver");
                                    // Register custom Broadcast receiver to show messages on activity
                                    //getActivity().registerReceiver(mHandleMessageReceiver, new IntentFilter(Config.DISPLAY_MESSAGE_ACTION));
                                    //Log.v(Config.TAG, "after register Broadcast receiver");
                                }
                            },2000);
                            //cancel();
                        }
                    }
                }
            }
        }, 0L, 20L*25); // �������� - 300 �����������, 0 ����������� �� ������� �������.


    }

    private void removeBinarySignalBtn() {

        webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {


            webview.evaluateJavascript("(function(){document.getElementsByClassName('show_more_news6 greenBtn11')[0].parentNode.removeChild(document.getElementsByClassName('show_more_news6 greenBtn11')[0])})()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    System.out.println("RESULT iof mine!: s"+s);
                }
            });
        } else
            webview.loadUrl("javascript:(function(){document.getElementsByClassName('show_more_news6 greenBtn11')[0].parentNode.removeChild(document.getElementsByClassName('show_more_news6 greenBtn11')[0])})()");
    }

    // Create a broadcast receiver to get message and show on screen
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String newMessage = intent.getExtras().getString(Config.EXTRA_MESSAGE);
            //String newMessageTitle = intent.getExtras().getString(Config.EXTRA_MESSAGE_TITLE);
            //cController.UserId = intent.getExtras().getString("UserId");

            // Waking up mobile if it is sleeping
            cController.acquireWakeLock( getActivity().getApplicationContext());

            // Display message on the screen
            //BodyMessage.setText(newMessage);
            //TitleMessage.setText(newMessageTitle);

            Toast.makeText(getActivity().getApplicationContext(), "Got Message: " +	 newMessage, Toast.LENGTH_LONG).show();

            // Releasing wake lock
            cController.releaseWakeLock();
        }
    };

    public WebView getWebview() {
        return webview;
    }

    public class myJavaScriptInterface {
        @JavascriptInterface
        public void setVisible(){
            mainLooper.post(new Runnable() {
                @Override
                public void run() {
                    webview.setVisibility(View.VISIBLE);
                }
            });
        }

    }

    class MainFrameViewListener implements
            OnClickListener {

        MainActivity owner;

        public MainFrameViewListener(MainActivity owner) {
            this.owner = owner;
        }

        @Override
        public void onClick(View arg0) {
            MainFrame.cController.UserId = "";
            //MainFrame.cController.WriteUserId( getActivity().getApplicationContext());
            //getActivity().StartCustomActivity(RegisterListActivity.class, 1);
        }


    }
}



