package com.davidapps.forexsignals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class Frame_Cabinet extends android.app.Fragment {

	static Controller cController;
	ProgressDialog progressDialog;
	View viewHierarchy;
	RelativeLayout view;
	Integer StopTimer;
	ImageView favicon;

	public TextView BodyMessage, TitleMessage;
	Button LogOut;
	WebView webview;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		viewHierarchy = inflater.inflate(R.layout.activity_cabinet,container, false);
		createController();
		return viewHierarchy;
	}


	public void createController() {
		String msg;
		Intent i = getActivity().getIntent();
		if ((msg = i.getStringExtra("UserId")) != null) {
			 cController.UserId = msg;
		}
		cController.reload_page = 1;
		cController = (Controller)  getActivity().getApplicationContext();
		cController.loadMainFrame=1;
		webview = (WebView) viewHierarchy.findViewById(R.id.webView1);
		StopTimer = 0;
		
		Timer myTimer = new Timer(); // ������� ������
		final Handler uiHandler = new Handler();
		myTimer.schedule(new TimerTask() { // ���������� ������
		    @Override
		    public void run() {
		    	if(StopTimer==0){
			    	if (!cController.UserId.equals("")){
			    		if (cController.reload_page==1){
			    			StopTimer=1;
			    			cController.reload_page=0;
					        uiHandler.post(new Runnable() {
					            @Override
					            public void run() {
					            	
					            	StopTimer=1; 
									
					            	String page = Config.REGISTER_AUTH_SERVER_URL + "?method=page&type=app&page=personal_settings_page"+"&id="+ cController.UserId+"&token="+cController.UserToken;
					        		Log.d("PAGE_URL_USER", page);
					        		
					        		cController.showLoader(getActivity());

					        		WebSettings webSettings = webview.getSettings();
					        		webSettings.setJavaScriptEnabled(true);
					        		webview.setWebViewClient(new WebViewClient() {
					                    @Override
					                    public void onPageFinished(WebView view, String url) {
					                    	cController.cancelLoader();

					                    }
					                });
					        		
					        		
					        		webview.loadUrl(page);

					            }
					        });
					        //cancel();
			    		}
			        }
		    	}
		    }
		}, 0L, 20L*25); // �������� - 300 �����������, 0 ����������� �� ������� �������.
		
		
	}
	

}
