package com.davidapps.forexsignals;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.PluginState;
import android.webkit.*;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FrameTradingBlog extends android.app.Fragment {

	static Controller cController;
	ProgressDialog progressDialog;
	MainActivity owner;
	View viewHierarchy;
	RelativeLayout view;
	

	
	
	ImageView favicon;

	public TextView BodyMessage, TitleMessage;
	Button LogOut;
	WebView webview;
	
	public FrameTradingBlog(){
		this.owner = MainActivity.instance;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		viewHierarchy = inflater.inflate(R.layout.activity_video,container, false);

		createController();
		
		return viewHierarchy;
	}

	public void setOwner(MainActivity owner) {
		this.owner = owner;
	}

	public void createController() {

		cController = (Controller)  getActivity().getApplicationContext();
		cController.loadMainFrame=1;
		webview = (WebView) viewHierarchy.findViewById(R.id.webView1);
		cController.showLoader(owner);
		//progressDialog = ProgressDialog.show(getActivity(),	"","");
		WebSettings webSettings = webview.getSettings();
		
		this.owner.mWebChromeClient.mContentView = (FrameLayout)viewHierarchy.findViewById(R.id.main_content);
		this.owner.mWebChromeClient.mCustomViewContainer = (FrameLayout)viewHierarchy.findViewById(R.id.target_view);
		this.owner.mWebChromeClient.ParentLayout = (RelativeLayout)viewHierarchy.findViewById(R.id.activity_main_relative);
		//this.owner.onBackPressed() = onBackPressed();
        

        //Log.e("APPDebug",this.owner.toString());
        //mWebChromeClient.owner = this.owner;
        webview.setWebChromeClient(this.owner.mWebChromeClient);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setPluginState(PluginState.ON);
		//webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		/*webview.setWebChromeClient(new WebChromeClient() {
		});
		*/
		webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
            	cController.cancelLoader();
                //if(progressDial,oog.isShowing()&&progressDialog!=null){
                	//progressDialog.dismiss();
                //}
            }
        });


		Log.d("link", LinksBank.VIDEOS);
		webview.loadUrl(LinksBank.VIDEOS);
		
	}

	



}
