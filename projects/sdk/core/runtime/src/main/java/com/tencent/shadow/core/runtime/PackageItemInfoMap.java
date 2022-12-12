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

import java.util.HashMap;
import java.util.Map;

/**
 * 用静态域保存已加载的插件组建name和partKey关系
 */
public class PackageItemInfoMap {
    /**
     * key: android:name
     * value: partKey
     * 由于插件加载后是不能反加载的，所以这个map不用清理
     */
    private final static Map<String, String> map = new HashMap<>();

    private static void loadComponentInfo(String partKey, PluginManifest.ComponentInfo[] infos) {
        if (infos != null) {
            for (PluginManifest.ComponentInfo info : infos) {
                map.put(info.className, partKey);
            }
        }
    }

    public static void load(String partKey, PluginManifest pluginManifest) {
        map.put(pluginManifest.getApplicationClassName(), partKey);
        loadComponentInfo(partKey, pluginManifest.getActivities());
        loadComponentInfo(partKey, pluginManifest.getServices());
        loadComponentInfo(partKey, pluginManifest.getProviders());
        loadComponentInfo(partKey, pluginManifest.getReceivers());
    }

    public static String findPartKeyByName(String name) {
        return map.get(name);
    }
}
