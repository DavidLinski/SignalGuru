package com.davidapps.forexsignals;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
//http://www.androidwarriors.com/2015/10/push-notification-using-gcm-in-android.html

public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";

    private static Controller aController = null;

    public GCMIntentService() {
        // Call extended class Constructor GCMBaseIntentService
        super(Config.GOOGLE_SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {

        //Get Global Controller Class object (see application tag in AndroidManifest.xml)
        if(aController == null)
            aController = (Controller) getApplicationContext();

        Log.i(TAG, "Device registered: regId = " + registrationId);
        //aController.displayMessageOnScreen(context, "Your device registred with GCM");
        //Log.d("NAME", MainActivity.name);
        /*try {
			aController.register(context, RegisterActivity.name, RegisterActivity.email,RegisterActivity.cphone,RegisterActivity.lname, registrationId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }

    /**
     * Method called on device unregistred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        if(aController == null)
            aController = (Controller) getApplicationContext();
        Log.i(TAG, "Device unregistered");
        //aController.displayMessageOnScreen(context, getString(R.string.gcm_unregistered));
        aController.unregister(context, registrationId);

    }
    /**
     * Method called on Receiving a new message from GCM server
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {

        if(aController == null)
            aController = (Controller) getApplicationContext();
        String SettingNotification = aController.GetSettingNotifocation(context);
        if (SettingNotification.equals("1")){
            aController.acquireWakeLock( context.getApplicationContext());
            //Log.i(TAG, "Received message");
            String message = intent.getExtras().getString("title");//intent.getExtras().getString("body");
            String title = intent.getExtras().getString("body");//intent.getExtras().getString("title");
            //Log.d("MESSAGE NOTIF1",message);
            //Log.d("TITLE NOTIF1",title);
            aController.UserId = aController.GetCustomParameters(context.getApplicationContext(),Config.APP_PREFERENCES_USER_ID);
            aController.UserToken = aController.GetCustomParameters(context.getApplicationContext(),Config.APP_PREFERENCES_USER_TOKEN);
            aController.reload_page = 1;
            aController.displayMessageOnScreen(context, message, title,aController.UserId);
            // notifies user
            generateNotification(context, message,title);
            aController.releaseWakeLock();
        }

    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {

        if(aController == null)
            aController = (Controller) getApplicationContext();

        //Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        String title = context.getString(R.string.app_name);
        aController.displayMessageOnScreen(context, message);
        // notifies user
        generateNotification(context, message,title);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {

        if(aController == null)
            aController = (Controller) getApplicationContext();

        Log.i(TAG, "Received error: " + errorId);
        aController.displayMessageOnScreen(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {

        if(aController == null)
            aController = (Controller) getApplicationContext();

        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        aController.displayMessageOnScreen(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Create a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message,String title) {

        if(aController == null)
            aController = (Controller) context;

        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);





        //String title = context.getString(R.string.app_name);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);


        notificationIntent.putExtra(Config.EXTRA_MESSAGE, message);
        notificationIntent.putExtra(Config.EXTRA_MESSAGE_TITLE, title);

        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //added my custom notification here!
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentIntent(intent)
                        .setTicker(message)
                        .setWhen(when)
                        .setSmallIcon(icon)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                        .setAutoCancel(true);


        //todo: these guys added
        //notification.setLatestEventInfo(context, title, message, intent);
        //notification.flags |= Notification.FLAG_AUTO_CANCEL;
        //notification.defaults |= Notification.DEFAULT_SOUND;
        //notification.defaults |= Notification.DEFAULT_VIBRATE;
        
        
        notificationManager.notify(0, mBuilder.build());

    }

}
