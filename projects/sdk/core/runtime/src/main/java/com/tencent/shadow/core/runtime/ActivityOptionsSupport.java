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

package com.tencent.shadow.core.runtime;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.util.Pair;
import android.view.View;

import tshadow.app.Activity;

/**
 * @see com.tencent.shadow.core.transform.specific.ActivityOptionsSupportTransform
 */
@SuppressLint("NewApi")
public class ActivityOptionsSupport {

    public static ActivityOptions makeSceneTransitionAnimation(
            Activity shadowActivity,
            View sharedElement,
            String sharedElementName) {
        android.app.Activity activity = shadowActivity.hostActivityDelegator
                .getHostActivity().getImplementActivity();
        return ActivityOptions.makeSceneTransitionAnimation(
                activity,
                sharedElement,
                sharedElementName
        );
    }

    @SafeVarargs
    public static ActivityOptions makeSceneTransitionAnimation(
            Activity shadowActivity,
            Pair<View, String>... sharedElements) {
        android.app.Activity activity = shadowActivity.hostActivityDelegator
                .getHostActivity().getImplementActivity();
        return ActivityOptions.makeSceneTransitionAnimation(
                activity,
                sharedElements
        );
    }
}
