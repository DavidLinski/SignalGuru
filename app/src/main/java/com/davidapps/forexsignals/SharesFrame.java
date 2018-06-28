package com.davidapps.forexsignals;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;

public class SharesFrame extends android.app.Fragment  {

	private ShareActionProvider mShareActionProvider;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		String shareBody = "Proof is in the performance";
		sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Signal Guru");
		sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
		
		View viewHierarchy = inflater.inflate(R.layout.activity_main_sharesframe, container, false);
		return viewHierarchy;
	}



}
