/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.test.dynamic.host;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.test.espresso.idling.CountingIdlingResource;

import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.test.lib.constant.Constant;
import com.tencent.shadow.test.lib.test_manager.TestManager;

public class BindPluginServiceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        button.setTag("jump");
        button.setText("jump");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump(v);
            }
        });
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(button);

        setContentView(linearLayout);
        TestManager.sBindPluginServiceActivityContentView = linearLayout;
    }

    public void jump(View view) {
        HostApplication.getApp().loadPluginManager(PluginHelper.getInstance().pluginManagerFile);

        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_PLUGIN_ZIP_PATH, PluginHelper.getInstance().pluginZipFile.getAbsolutePath());
        bundle.putString(Constant.KEY_PLUGIN_PART_KEY, getIntent().getStringExtra(Constant.KEY_PLUGIN_PART_KEY));
        bundle.putString(Constant.KEY_ACTIVITY_CLASSNAME, getIntent().getStringExtra(Constant.KEY_ACTIVITY_CLASSNAME));
        bundle.putBundle(Constant.KEY_EXTRAS, getIntent().getBundleExtra(Constant.KEY_EXTRAS));

        final CountingIdlingResource idlingResource = HostApplication.getApp().mIdlingResource;
        idlingResource.increment();
        HostApplication.getApp().getPluginManager()
                .enter(this, Constant.FROM_ID_BIND_SERVICE, bundle, new EnterCallback() {
                    @Override
                    public void onShowLoadingView(View view) {

                    }

                    @Override
                    public void onCloseLoadingView() {
                        idlingResource.decrement();
                    }

                    @Override
                    public void onEnterComplete() {

                    }
                });

    }
}
