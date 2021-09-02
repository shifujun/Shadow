package skin.support.mobile.demo.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.tencent.shadow.sample.host.R;

import skin.support.mobile.demo.activity.base.BaseActivity;

public class ColorPickerActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);
        initToolbar(true);
    }
}
