package com.davidapps.forexsignals;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;


public class FragmentFactory {

    private android.app.Fragment value;

    private FragmentActivity videoOwner;
    private MainActivity owner;

    private android.app.FragmentTransaction transaction;

    public FragmentFactory(MainActivity owner, int type) {
        this.owner = owner;
        Integer CreatFragment;
        switch (type) {
            case -1:
                this.value = new MainFrame();
                this.createFragment();
                owner.HideMenu();
                break;



            //signals and performances
            case 1:
                this.value = new MainFrame();
                this.createFragment();
                owner.HideMenu();
                break;

                /*case 2:
				this.value = new Frame_Referal_Partners();
				this.createFragment();
				owner.HideMenu();
				break;
			case 3:
				this.value = new Frame_Performances();
				this.createFragment();
				owner.HideMenu();
				break;*/

            //Trading Blog
            case 2:
                this.value = new FrameTradingBlog();
                //this.value = new FrameTradingBlog(owner);
                this.createFragment();
                owner.HideMenu();
                break;

            //trade on a free demo
            case 3:
                this.value = new FrameFreeDemo();
                this.createFragment();
                owner.HideMenu();
                break;

            //AutoTrade the signals
            case 4:
                this.value = new FrameAutoTradeSignal();
                this.createFragment();
                owner.HideMenu();
                break;

            //pricing
            case 5:
                this.value = new FramePricing();
                this.createFragment();
                owner.HideMenu();
                break;

            //contact us
            case 7:
                this.value = new FrameContactUs();
                this.createFragment();
                owner.HideMenu();
                break;
           /* case 7:
                //this.value = new SharesFrame();
                //this.createFragment();
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Here is the share content body";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                owner.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                owner.HideMenu();
                break;
*/


            //rate us
            case 8:
                Uri uri = Uri.parse("market://details?id=" + this.owner.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    this.owner.startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    this.owner.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + this.owner.getPackageName())));
                }
                break;


            //Social, share us on facebook
            case 6:
            case 9:
                this.value = new Frame_Share_On_Facebook();
                this.createFragment();
                owner.HideMenu();
                break;
			/*case 10:
				this.value = new FrameContactUsBackup();
				this.createFragment();
				owner.HideMenu();
				break;*/

			//account details
            case 10:
				/*String SettingNotification = owner.cController.GetSettingNotifocation(owner.getApplicationContext());
		    	if (SettingNotification.equals("1")){
		    		SettingNotification = "0";
		    	}else{
		    		SettingNotification = "1";
		    	}
				owner.cController.WriteSettingNotifocation(owner.getApplicationContext(), SettingNotification);*/
                this.value = new Frame_Cabinet();
                this.createFragment();
                owner.HideMenu();
                break;

			//Personal settings
            case 11:
				/*String SettingNotification = owner.cController.GetSettingNotifocation(owner.getApplicationContext());
		    	if (SettingNotification.equals("1")){
		    		SettingNotification = "0";
		    	}else{
		    		SettingNotification = "1";
		    	}
				owner.cController.WriteSettingNotifocation(owner.getApplicationContext(), SettingNotification);*/
                this.value = new Frame_Cabinet();
                this.createFragment();
                owner.HideMenu();
                break;


            //version
            case 12:
                this.value = new VersionFrame();
                this.createFragment();
                owner.HideMenu();
                break;
			/*case 13:
				this.value = new FramePricing();
				this.createFragment();
				owner.HideMenu();
				break;*/

			//log out
            case 13:
                owner.cController.UserId = "";
                owner.cController.WriteUserId(owner.getApplicationContext());
                owner.StartCustomActivity(RegisterListActivity.class, 1);
                owner.cController.Reg_type="logout";
                owner.HideMenu();
                break;

        }

    }

    public android.app.Fragment getValue() {
        return value;
    }

    private void createFragment() {
        transaction = this.owner.getFragmentManager().beginTransaction();

        // transaction.setCustomAnimations(R.animator.slide_in_left,
        // R.animator.slide_in_right);

        transaction.replace(R.id.mainfraim, value);
        transaction.addToBackStack(null);

        transaction.commit();
    }
}
