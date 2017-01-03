package com.varos.picmap.activities;

import android.app.ProgressDialog;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by David on 09.12.2016.
 */

public class BaseActivity extends AppCompatActivity
{
    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog(){
if(mProgressDialog==null)
    mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(true);
    }
    public void hideProgressDialog() {
        if(mProgressDialog!=null&&mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
