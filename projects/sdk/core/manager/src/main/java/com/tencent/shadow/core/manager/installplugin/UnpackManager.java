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

package com.tencent.shadow.core.manager.installplugin;

import static com.tencent.shadow.core.utils.Md5.md5File;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class UnpackManager {

    private static final Logger mLogger = LoggerFactory.getLogger(UnpackManager.class);

    public static final String CONFIG_FILENAME = "config.json";//todo #28 json的格式需要沉淀文档。
    private static final String DEFAULT_STORE_DIR_NAME = "ShadowPluginManager";

    private final File mPluginUnpackedDir;

    private final String mAppName;

    public UnpackManager(File root, String appName) {
        File parent = new File(root, DEFAULT_STORE_DIR_NAME);
        mPluginUnpackedDir = new File(parent, "UnpackedPlugin");
        mPluginUnpackedDir.mkdirs();
        mAppName = appName;
    }


    File getVersionDir(String appHash) {
        return AppCacheFolderManager.getVersionDir(mPluginUnpackedDir, mAppName, appHash);
    }

    public File getAppDir() {
        return AppCacheFolderManager.getAppDir(mPluginUnpackedDir, mAppName);
    }

    /**
     * 获取插件解包的目标目录。根据target的文件名决定。
     *
     * @param target Target
     * @return 插件解包的目标目录
     */
    public File getPluginUnpackDir(String appHash, File target) {
        return new File(getVersionDir(appHash), target.getName());
    }

    public String zipHash(File zip) {
        return md5File(zip);
    }

    public JSONObject getConfigJson(File zip) {
        ZipFile zipFile = null;
        try {
            zipFile = new SafeZipFile(zip);
            ZipEntry entry = zipFile.getEntry(CONFIG_FILENAME);
            InputStream inputStream = zipFile.getInputStream(entry);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            for (String line; (line = br.readLine()) != null; ) {
                sb.append(line);
            }

            return new JSONObject(sb.toString());
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (IOException e) {
                mLogger.warn("zip关闭时出错忽略", e);
            }
        }
    }

    public JSONObject getConfigJsonInDir(File dir) throws IOException, JSONException {
        File configJsonFile = new File(dir, UnpackManager.CONFIG_FILENAME);
        if (!configJsonFile.exists()) {
            throw new FileNotFoundException(UnpackManager.CONFIG_FILENAME + "文件不存在："
                    + configJsonFile.getAbsolutePath());
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(configJsonFile)
                )
        )) {
            for (String line; (line = br.readLine()) != null; ) {
                sb.append(line);
            }
        }
        return new JSONObject(sb.toString());
    }

    /**
     * 创建解压目录，清空解压目录，然后解压文件到目录中。
     *
     * @param zip       zip压缩包
     * @param outputDir 解压目录
     */
    public static void unzip(File zip, File outputDir) throws IOException {
        outputDir.mkdirs();
        MinFileUtils.cleanDirectory(outputDir);

        ZipFile zipFile = null;
        try {
            zipFile = new SafeZipFile(zip);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    MinFileUtils.writeOutZipEntry(zipFile, entry, outputDir, entry.getName());
                }
            }
        } finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (IOException e) {
                mLogger.warn("zip关闭时出错忽略", e);
            }
        }
    }

}
