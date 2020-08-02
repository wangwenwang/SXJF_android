package com.kaidongyuan.app.kdydriver.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import com.kaidongyuan.app.kdydriver.R;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class MessageBox extends Dialog  {

	int dialogResult;  
	Handler mHandler ;  
	
	public MessageBox(Activity context) 
	{
		super(context);
		dialogResult=0;
        setOwnerActivity(context);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);       
        onCreate();  
	}
	
    public void onCreate() 
    {
	}  
 
    public int getDialogResult()  
    {  
        return dialogResult;  
    }  

    public void setDialogResult(int dialogResult)  
    {  
        this.dialogResult = dialogResult;  
    }  

    public void endDialog(int result)  
    {  
        dismiss();  
        setDialogResult(result);  
        Message m = mHandler.obtainMessage();  
        mHandler.sendMessage(m);  
    }
}
