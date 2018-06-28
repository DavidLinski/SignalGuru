package com.davidapps.forexsignals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class Frame_Setting_notification extends android.app.Fragment {

	static Controller cController;

	MainActivity owner;
	View viewHierarchy;

	public TextView BodyMessage, TitleMessage;
	Button LogOut;
	WebView webview;
	private Switch SwitchNotification;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		viewHierarchy = inflater.inflate(R.layout.activity_setting_notification, container, false);
		createController();
		return viewHierarchy;
	}

	public void setOwner(MainActivity owner) {
		this.owner = owner;
	}

	public void createController() {

		cController = (Controller) getActivity().getApplicationContext();
		SwitchNotification = (Switch) viewHierarchy.findViewById(R.id.enable_notification);

		// set the switch to ON
		//SwitchNotification.setChecked(true);
		// attach a listener to check for changes in state
		SwitchNotification.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {

				if (isChecked) {

				} else {

				}

			}
		});

		// check the current state before we display the screen
		/*if (SwitchNotification.isChecked()) {

		} else {

		}*/

	}

}
