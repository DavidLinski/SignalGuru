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
import android.widget.EditText;

public class FrameContactUsBackup extends android.app.Fragment {

	static Controller cController;
	ProgressDialog progressDialog;
	MainActivity owner;
	View viewHierarchy;

	public EditText txtEmail, txtName,txtLastName,txtPhone,txtComment;
	Button btnSend;
	WebView webview;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		viewHierarchy = inflater.inflate(R.layout.activity_contact_us1,container, false);
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
		//progressDialog = ProgressDialog.show(getActivity(),	"Loading...","Loading application View, please wait...", false,false);
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
		
		
		webview.loadUrl(LinksBank.CONTACT_US);
		/*cController = (Controller)  getActivity().getApplicationContext();
		txtName = (EditText) viewHierarchy.findViewById(R.id.name);
		txtLastName = (EditText) viewHierarchy.findViewById(R.id.last_name);
		txtPhone = (EditText) viewHierarchy.findViewById(R.id.phone);
		txtEmail = (EditText) viewHierarchy.findViewById(R.id.email);
		txtComment = (EditText) viewHierarchy.findViewById(R.id.comment);
		btnSend = (Button) viewHierarchy.findViewById(R.id.send_button);
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","abc@gmail.com", null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
				startActivity(Intent.createChooser(emailIntent, "Send email..."));
			}
		});*/
		
	}


}
