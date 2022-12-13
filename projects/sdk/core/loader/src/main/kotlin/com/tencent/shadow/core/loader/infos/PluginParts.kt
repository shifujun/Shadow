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

package com.tencent.shadow.core.loader.infos

import android.content.res.Resources
import com.tencent.shadow.core.common.InstalledApk
import com.tencent.shadow.core.load_parameters.LoadParameters
import com.tencent.shadow.core.loader.classloaders.PluginClassLoader
import com.tencent.shadow.core.runtime.PluginPackageManager
import com.tencent.shadow.core.runtime.ShadowAppComponentFactory
import com.tencent.shadow.core.runtime.ShadowApplication

class PluginParts(
    pluginPartsMap: Map<String, PluginParts>,
    val appComponentFactory: ShadowAppComponentFactory,
    val application: ShadowApplication,
    val classLoader: PluginClassLoader,
    val resources: Resources,
    val pluginPackageManager: PluginPackageManager,
    val installedApk: InstalledApk,
    val loadParameters: LoadParameters
) {
    val dependsOnResourcesApks: Array<String>

    init {
        val dependsOn = loadParameters.dependsOn
        val l = mutableListOf<String>()
        if (dependsOn != null && dependsOn.isNotEmpty()) {
            for (partKey in dependsOn) {
                val pluginParts = pluginPartsMap[partKey]
                if (pluginParts != null) {
                    l.addAll(pluginParts.dependsOnResourcesApks)
                    l.add(pluginParts.installedApk.apkFilePath)
                } else {
                    throw Error("partKey：${partKey} 没有先加载")
                }
            }
        }
        this.dependsOnResourcesApks = l.toTypedArray()
    }

}