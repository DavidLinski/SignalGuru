package com.davidapps.forexsignals;

import android.app.ProgressDialog;
import android.os.Bundle;
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

public class FramePricing extends android.app.Fragment {

	static Controller cController;
	ProgressDialog progressDialog;
	View viewHierarchy;
	RelativeLayout view;
	
	ImageView favicon;

	public TextView BodyMessage, TitleMessage;
	Button LogOut;
	WebView webview;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		viewHierarchy = inflater.inflate(R.layout.activity_pricing,container, false);
		createController();
		return viewHierarchy;
	}

	public void createController() {

		cController = (Controller)  getActivity().getApplicationContext();
		cController.loadMainFrame=1;
		webview = (WebView) viewHierarchy.findViewById(R.id.webView1);
		//String page = "http://signal-guru.com/pricing/";
		//String page = Config.REGISTER_AUTH_SERVER_URL + "?method=page&type=app&page=pricing_page"+"&id="+ cController.UserId+"&token="+cController.UserToken;

		cController.showLoader(getActivity());
		//progressDialog = ProgressDialog.show(getActivity(),	"","");
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
            	cController.cancelLoader();
                //if(progressDial,oog.isShowing()&&progressDialog!=null){
                	//progressDialog.dismiss();
                //}
            }
        });
		
		
		webview.loadUrl(LinksBank.PRICING);

//		IntentFactory.openExternalBrowser(getActivity(), "http://signal-guru.com/pricing/");
		
	}
	

}
