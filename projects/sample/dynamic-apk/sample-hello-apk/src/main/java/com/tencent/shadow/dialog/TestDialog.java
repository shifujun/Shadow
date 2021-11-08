package com.tencent.shadow.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * @author 程良明
 * @date 2021/11/8
 * * 说明:
 **/
public class TestDialog extends Dialog {
    public static Context sContetx;

    public TestDialog(Context context) {
        super(context);

        View rootView = getView(sContetx);
        setContentView(rootView);
        int id = sContetx.getResources().getIdentifier("clm_test", "id", sContetx.getPackageName());
        Log.e("clm ", "view id " + id);
        EditText view = findViewById(id);
        Log.e("clm ", "view " + view);
        Log.e("clm ", "view Text" + view.getText());

//        设置这个后,如果输入框没有文本，正常，有文本闪退
        view.setFocusableInTouchMode(true);
//        view.setText("");


    }

    private View getView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        int layoutId = context.getResources().getIdentifier("temp_view", "layout", context.getPackageName());
        Log.e("clm ", layoutId + "");
        return inflater.inflate(layoutId, null);
    }
}
