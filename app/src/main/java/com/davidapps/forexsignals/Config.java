package com.davidapps.forexsignals;

public interface Config {


    // CONSTANTS
    static final String YOUR_SERVER_URL =  "http://signal-guru-fx.com/register.php";
    //static final String REGISTER_AUTH_SERVER_URL =  "http://signal-guru.artem.unidep.ru/signalguru/v2/";
    static final String REGISTER_AUTH_SERVER_URL =  "http://signal-guru-fx.com/signalguru/v2/";
    //static final String YOUR_SERVER_URL =  "http://wordpress.devel.unidep.ru/register.php";
    // YOUR_SERVER_URL : Server url where you have placed your server files
    // Google project id
    static final String GOOGLE_SENDER_ID = "559091230930";  // Place here your Google project id

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCM Android Example";

    static final String DISPLAY_MESSAGE_ACTION =
            "com.davidapps.signalguru.MainActivity";

    static final String EXTRA_MESSAGE = "message";
    static final String EXTRA_MESSAGE_TITLE = "message_title";

    //save user profile
    static final String APP_PREFERENCES = "signal_guru_settings";
    static final String APP_PREFERENCES_USER_NAME = "user_name";
    static final String APP_PREFERENCES_USER_PASS = "user_pass";
    static final String APP_PREFERENCES_USER_MAIL = "user_mail";
    static final String APP_PREFERENCES_FIRST_LAUNCH = "first_launch";
    static final String APP_PREFERENCES_SETTING_NOTIFICATION_ENABLED = "notification_enabled";
    static final String APP_PREFERENCES_USER_ID = "user_id";
    static final String APP_PREFERENCES_USER_TOKEN = "user_token";
    static final String APP_PREFERENCES_USER_AUTH_TYPE = "user_auth_type";
}



