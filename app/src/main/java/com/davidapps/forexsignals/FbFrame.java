package com.davidapps.forexsignals;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;

public class FbFrame extends Fragment implements MenuFragment {

	public class LikeWebviewClient  extends WebViewClient {
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
	//	WebView foll_fb = (WebView) findViewById(R.id.button3);    
	//	String url = "http://www.facebook.com/plugins/like.php?layout=standard&show_faces=true&width=80&height=50&action=like&colorscheme=light&href=YOUR_URL_TO_LIKE";              
	//	foll_fb.loadUrl(url);
	//	foll_fb.getSettings().setJavaScriptEnabled(true);
		//foll_fb.requestFocus(View.FOCUS_DOWN);
		//foll_fb.setWebViewClient(new LikeWebviewClient());
				
		View viewHierarchy = inflater.inflate(R.layout.activity_main_fbframe, container, false);
		return viewHierarchy;
	}

}