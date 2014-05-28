package com.skyeyes.base.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyeyes.storemonitor.R;

public class CustomProgressDialog extends Dialog {
    public CustomProgressDialog(Context context){
        super(context);
    }
    
    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }
    
    public static CustomProgressDialog createDialog(Context context){
    	CustomProgressDialog customProgressDialog = new CustomProgressDialog(context,R.style.custom_progress_dialog);
        customProgressDialog.setContentView(R.layout.progress_dialog_view);
        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        return customProgressDialog;
    }
  
    public void onWindowFocusChanged(boolean hasFocus){
        ImageView imageView = (ImageView)findViewById(R.id.nurse_common_progress_dialog_loading_iv);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }
  
    /**
     *
     * [Summary]
     *       setTitile 标题
     * @param strTitle
     * @return
     *
     */
    public CustomProgressDialog setTitile(String strTitle){
        return this;
        
    }
    /**
     * [Summary]
     *       setMessage 提示内容
     * @param strMessage
     * @return
     *
     */
    public CustomProgressDialog setMessage(String strMessage){
        TextView tvMsg = (TextView)findViewById(R.id.nurse_common_progress_dialog_msg_tv);
        if (tvMsg != null){
            tvMsg.setText(strMessage);
        }
        return this;
    }
}
