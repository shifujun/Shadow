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

package com.tencent.shadow.sample.manager;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.dynamic.host.PluginManagerImpl;
import com.tencent.shadow.sample.constant.Constant;


public class SamplePluginManager implements PluginManagerImpl {

    final private PluginManager1 mPM1;
    final private PluginManager2 mPM2;

    public SamplePluginManager(Context context) {
        mPM1 = new PluginManager1(context);
        mPM2 = new PluginManager2(context);
    }

    @Override
    public void enter(final Context context, long fromId, Bundle bundle, final EnterCallback callback) {
        final FastPluginManager pm;
        if (bundle != null) {
            String process = bundle.getString(Constant.KEY_PLUGIN_PROCESS);
            if (!TextUtils.isEmpty(process) && "plugin_2".equals(process)) {
                pm = mPM2;
            } else {
                pm = mPM1;
            }
        } else {
            pm = mPM1;
        }

        if (fromId == Constant.FROM_ID_NOOP) {
            //do nothing.
        } else if (fromId == Constant.FROM_ID_START_ACTIVITY) {
            pm.onStartActivity(context, bundle, callback);
        } else if (fromId == Constant.FROM_ID_CALL_SERVICE) {
            pm.callPluginService(context, bundle);
        } else {
            throw new IllegalArgumentException("不认识的fromId==" + fromId);
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        mPM1.onCreate(bundle);
        mPM2.onCreate(bundle);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        mPM1.onSaveInstanceState(bundle);
        mPM2.onSaveInstanceState(bundle);
    }

    @Override
    public void onDestroy() {
        mPM1.onDestroy();
        mPM2.onDestroy();
    }
}
