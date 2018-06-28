package com.davidapps.forexsignals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VersionFrame extends android.app.Fragment  { 
	static Controller cController;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
		View viewHierarchy = inflater.inflate(R.layout.activity_main_versionframe, container, false);
		cController = (Controller)  getActivity().getApplicationContext();
		cController.loadMainFrame=1;
		return viewHierarchy;
    }
}