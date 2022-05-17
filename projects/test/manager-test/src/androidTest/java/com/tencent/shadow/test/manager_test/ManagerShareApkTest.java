package com.tencent.shadow.test.manager_test;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

import com.tencent.shadow.core.manager.installplugin.InstalledPlugin;
import com.tencent.shadow.core.manager.installplugin.PluginConfig;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * ShareApk: 两个UUID的插件集合共享了同一个apk文件的情况
 * 这种情况下，要保证删除一个UUID的插件时不会直接删除另一个UUID还在依赖的apk文件。
 */
public class ManagerShareApkTest {
   private TestCoreManager testCoreManager;

   @Before
   public void setUp() throws IOException {
      Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
      FileUtils.cleanDirectory(targetContext.getFilesDir().getParentFile());
   }

   @Before
   public void initTestCoreManager() {
      Context applicationContext = ApplicationProvider.getApplicationContext();
      testCoreManager = new TestCoreManager(applicationContext);
   }

   @Test
   public void deleteInstalledPlugin() throws JSONException, IOException {
      File[] dirs = mockAPluginDirs();
      for (File dir : dirs) {
         PluginConfig pluginConfig = testCoreManager.installPluginFromDir(dir);
         testCoreManager.onInstallCompleted(pluginConfig, Collections.emptyMap());
      }


      List<InstalledPlugin> installedPlugins = testCoreManager.getInstalledPlugins(1);
      InstalledPlugin installedPlugin = installedPlugins.get(0);
      Assert.assertEquals("ManagerBasicTest", installedPlugin.UUID_NickName);
      Assert.assertTrue(installedPlugin.runtimeFile.pluginFile.exists());

      testCoreManager.deleteInstalledPlugin(installedPlugin.UUID);
      List<InstalledPlugin> getAfterDelete = testCoreManager.getInstalledPlugins(1);
      Assert.assertTrue(getAfterDelete.isEmpty());
      Assert.assertFalse(installedPlugin.runtimeFile.pluginFile.exists());
   }

   static JSONObject mockConfigJson(File runtimeApkFile) throws JSONException {
      JSONObject config = new JSONObject();
      config.put("version", 4);
      config.put("UUID", UUID.randomUUID().toString());
      config.put("UUID_NickName", "ManagerBasicTest");

      JSONObject runtimeJson = mockApkFileJson(runtimeApkFile.getName(), "runtime-hash");
      config.put("runtime", runtimeJson);

      return config;
   }

   static JSONObject mockApkFileJson(String apkName, String hash) throws JSONException {
      JSONObject json = new JSONObject();
      json.put("apkName", apkName);
      json.put("hash", hash);
      return json;
   }

   static File[] mockAPluginDirs() throws IOException, JSONException {
      Context applicationContext = ApplicationProvider.getApplicationContext();

      int size = 2;
      File[] dirs = new File[size];
      for (int i = 0; i < dirs.length; i++) {
         File dir = Files.createTempDirectory(
                 applicationContext.getFilesDir().toPath(),
                 "mockAPluginDirs"
         ).toFile();
         dir.deleteOnExit();

         dirs[i] = dir;
      }

      File sharedDir = Files.createTempDirectory(
              applicationContext.getFilesDir().toPath(),
              "sharedDir").toFile();

      File sharedRuntimeApkFile = new File(sharedDir, "runtime.apk");
      sharedRuntimeApkFile.createNewFile();

      for (File dir : dirs) {
         JSONObject configJson = mockConfigJson(sharedRuntimeApkFile);
         File configJsonFile = new File(dir, "config.json");
         FileUtils.writeStringToFile(configJsonFile, configJson.toString(), StandardCharsets.UTF_8);
      }

      return dirs;
   }

}
