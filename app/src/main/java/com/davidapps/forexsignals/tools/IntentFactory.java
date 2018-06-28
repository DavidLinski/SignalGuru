package com.davidapps.forexsignals.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by shabat on 11/14/2017.
 */

public class IntentFactory {

    public static void openExternalBrowser(Context context, String webPage){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(webPage));
        context.startActivity(i);
    }
}
