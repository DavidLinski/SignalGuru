package com.davidapps.forexsignals.tools;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.davidapps.forexsignals.R;


public class ExitDialog extends DialogPopa{

    //instances
    private ExitDialogCallback exitDialogCallback;

    //views
    private TextView stayRV, exitRV;
    //private LottieAnimationView fatWhaleLav;

    public ExitDialog(Activity activity, ExitDialogCallback exitDialogCallback) {
        super(activity, R.style.FullHeightDialog);
        setContentView(R.layout.dialog_exit);
        this.exitDialogCallback = exitDialogCallback;
        setCanceledOnTouchOutside(true);
        setViews();
    }


    private void setViews() {
        stayRV = (TextView) findViewById(R.id.stay_tv);
        exitRV = (TextView) findViewById(R.id.exit_tv);

        stayRV.setOnClickListener(onStayClicked());
        exitRV.setOnClickListener(onExitClicked());
    }

    @Override
    public void show() {
        super.show(this);
    }

    private View.OnClickListener onExitClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                exitDialogCallback.onCloseAppRequested();
            }
        };
    }

    private View.OnClickListener onStayClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        };
    }

    public void dismiss() {
        if(getWindow()==null)return;
        stayRV = null;
        exitRV = null;
        //fatWhaleLav.clearAnimation();
        //fatWhaleLav = null;
        dismiss(this);
    }

    public interface ExitDialogCallback {
        void onCloseAppRequested();
    }

}
