package com.davidapps.forexsignals.tools;

import android.app.Dialog;
import android.content.Context;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

/**
 * Created by shabat on 10/7/2017.
 */

public class DialogPopa extends Dialog {

    private static ArrayList<Dialog> openDialogsList;

    protected DialogPopa(Context context, int themeResId) {
        super(context, themeResId);
    }

    public DialogPopa(Context context) {
        super(context);
    }


    public void show(Dialog dialog) {
        super.show();
        setDialogsList();
        if(!openDialogsList.contains(dialog))
            openDialogsList.add(dialog);
    }

    public void dismiss(Dialog dialog){
        super.dismiss();
        if(openDialogsList==null)return;
        if(openDialogsList.contains(dialog))
            openDialogsList.remove(dialog);
    }

    public static void dismissAllDialogs(){

        try{

            if(openDialogsList==null) return;
            for (Dialog dialog : openDialogsList) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
            openDialogsList.clear();
        } catch (ConcurrentModificationException e){
            e.printStackTrace();
        }

    }

    private void setDialogsList() {if(openDialogsList == null) openDialogsList = new ArrayList<>();}


}

