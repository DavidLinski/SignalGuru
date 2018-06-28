package com.davidapps.forexsignals;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


public class MenuSlider {
	

	private static Controller aController = null;
	
	public MainActivity owner;
	ImageButton mButton;
	public SlidingMenu value;

	public MenuSlider(MainActivity owner) {
		this.setOwner(owner);
		this.createMenu();
	}

	public MenuSlider() {

	}

	public void setOwner(MainActivity owner) {
		this.owner = owner;
	}

	private CustomAdapter mAdapter;
	private ListView listView1;

	private void createMenu() {

		final SlidingMenu menu = new SlidingMenu(this.owner);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//TOUCHMODE_MARGIN
		menu.setFadeDegree(1.0f);
		menu.attachToActivity(this.owner, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.sidemenu);
		float scale  = owner.getResources().getDisplayMetrics().density;
		menu.setBehindWidth((int)(200*scale));
		menu.setBackgroundColor(0x151515);
		owner.getSupportActionBar().setHomeButtonEnabled(true);
		value = menu;

/*		// ������ ������� ������ �� ��������
		DisplayMetrics displaymetrics = owner.getResources().getDisplayMetrics();

		// ������ ������� ������ �� ������ Display
		Display display = owner.getWindowManager().getDefaultDisplay();
		DisplayMetrics metricsB = new DisplayMetrics();
		display.getMetrics(metricsB);
		
		Log.d("RESOLUTION",
				"[��������� �������] \n" +//mydp+
		    	"������: " + displaymetrics.widthPixels + "\n" +
		    	"������: " + displaymetrics.heightPixels + "\n"
		    	+ "\n" +
		    	"[��������� Display] \n" +
		    	"������: " + metricsB.widthPixels + "\n" +
		    	"������: " + metricsB.heightPixels + "\n"
		    );*/
		
		// this.setMenuItems();
		if(aController == null)
            aController = (Controller) owner.getApplicationContext();
		String SettingNotification = aController.GetSettingNotifocation(owner.getApplicationContext());
		String NotificationSettingText = "Disable notifications";
    	if (SettingNotification.equals("1")){
    		NotificationSettingText = "Disable notifications";
    	}else{
    		NotificationSettingText = "Enable notifications";
    	}

		mAdapter = new CustomAdapter(this.owner);
		Item[] items = {
				new Item("Menu", true), //0
				new Item("Signal & Performances", false), //1
				//new Item("Refer a friend", false),
				//new Item("Performances", false),
				new Item("Trading Blog", false), //2
				new Item("Trade on a free demo", false), //3
				new Item("AutoTrade the signals", false), //4
				new Item("Pricing", false), //5
				new Item("Social", true),//6
				new Item("Contact us", true),//7
				new Item("Rate us on Google play", false), //8
				new Item("SignalGuru Facebook Group", false), //9
				//new Item("Contact us", false),

				new Item("Account details", true),//10
				new Item("Personal settings", false), //11
				new Item("Version", false), //12
				//new Item("Pricing", false),
				new Item("Log out", false)//13
		};

		for (int i = 0; i < items.length; i++) {
			if (items[i].type)
				mAdapter.addSectionHeaderItem(items[i].text);
			else
				mAdapter.addItem(items[i].text);
		}
		
		listView1 = (ListView) this.owner.findViewById(R.id.sidemenu);

		View header = (View) this.owner.getLayoutInflater().inflate(R.layout.sidemenu_item, null);
		//listView1.addHeaderView(header);

		listView1.setAdapter(mAdapter);

		mButton = (ImageButton) owner.findViewById(R.id.btmenu);
		mButton.setOnClickListener(new MenuListener(this.owner, menu));
		aController.mAdapter = mAdapter;
	}

}


class MenuListener implements OnClickListener {

	private SlidingMenu menu;
	ImageButton mButton;
	private MainActivity owner;
	Controller cController;
	
	public MenuListener(MainActivity owner, SlidingMenu menu) {
		cController = (Controller) owner.getActivity().getApplicationContext();
		this.owner = owner;
		this.menu = menu;
		mButton = (ImageButton) owner.findViewById(R.id.btmenu);
		
		menu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
			
			@Override
			public void onOpened() {
				// TODO Auto-generated method stub
				mButton.setImageResource(R.drawable.cross_min); 
				mButton.setBackgroundResource(R.drawable.cross_min); 
			}
		});
		menu.setOnClosedListener(new SlidingMenu.OnClosedListener() {
			
			@Override
			public void onClosed() {
				// TODO Auto-generated method stub
				mButton.setImageResource(R.drawable.menu); 
				mButton.setBackgroundResource(R.drawable.menu); 
			}
		});
		MainActivity.getSideMenuList().setOnItemClickListener(new MenuItemListener(this.owner));

	}
	
	@Override
	public void onClick(View arg0) {
		menu.toggle();
		if (menu.isMenuShowing()){
			mButton.setImageResource(R.drawable.cross_min); 
			mButton.setBackgroundResource(R.drawable.cross_min); 
		}else{
			mButton.setImageResource(R.drawable.menu); 
			mButton.setBackgroundResource(R.drawable.menu); 
		}

	}
}

class MenuItemListener implements AdapterView.OnItemClickListener {
	
	private MainActivity owner;
	private static Controller aController = null;
	
	public MenuItemListener(MainActivity owner) {
		this.owner = owner;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(aController == null)
            aController = (Controller) owner.getApplicationContext();
		Long item = MainActivity.getSideMenuList().getItemIdAtPosition(position);
		/*if (position==10){
			String SettingNotification = aController.GetSettingNotifocation(owner.getApplicationContext());
			String NotificationSettingText = "Disable notifications";
	    	if (SettingNotification.equals("0")){
	    		NotificationSettingText = "Disable notifications";
	    	}else{
	    		NotificationSettingText = "Enable notifications";
	    	}
			
			aController.mAdapter.mData.set(position, NotificationSettingText);
			aController.mAdapter.getView(position, view, parent);
		}*/

		this.owner.selectMenuItem(item.intValue());

		//Toast.makeText(MainActivity.instance,"You selected : " + item,Toast.LENGTH_SHORT).show();
	}
}
