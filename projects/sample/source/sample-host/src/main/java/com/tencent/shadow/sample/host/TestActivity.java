package com.tencent.shadow.sample.host;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences preferences = getApplication().getBaseContext().getSharedPreferences("user", 0);
        String name = preferences.getString("name", "defaultname1");
        Toast.makeText(this, name, Toast.LENGTH_LONG).show();
    }
}
