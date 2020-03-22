package com.sop.cacapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

public class LoaderDialog extends Dialog {

    private boolean isActive;

    public LoaderDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.loader_dialog);
        isActive = false;
        ProgressBar progressBar = findViewById(R.id.pbLoaderDialog);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Log.d("LoaderDialog", "Loader Dialog Created!");
    }

    public boolean isLoaderActive() {
        return isActive;
    }

    public void startLoading() {
        this.isActive = true;
        this.show();
        Log.d("LoaderDialog", "Loader Dialog has started!");
    }

    public void stopLoading() {
        this.isActive = false;
        this.dismiss();
        Log.d("LoaderDialog", "Loader Dialog has stopped!");
    }


}
