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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 基本场景：单个zip插件包，单个zip插件包解压对应的dir，两种安装方式，
 * 安装之后能找到文件，删除插件后apk文件被删除了。
 */
public class ManagerBasicTest {
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
   public void getInstalledPluginsIsEmpty() {
      Context applicationContext = ApplicationProvider.getApplicationContext();
      TestCoreManager testCoreManager = new TestCoreManager(applicationContext);
      List<InstalledPlugin> installedPlugins = testCoreManager.getInstalledPlugins(1);
      Assert.assertTrue(installedPlugins.isEmpty());
   }

   private void sameInstallTest(PluginConfig pluginConfig) {
      testCoreManager.onInstallCompleted(pluginConfig, Collections.emptyMap());

      List<InstalledPlugin> installedPlugins = testCoreManager.getInstalledPlugins(1);
      InstalledPlugin installedPlugin = installedPlugins.get(0);
      Assert.assertEquals("ManagerBasicTest", installedPlugin.UUID_NickName);

      Assert.assertTrue(installedPlugin.runtimeFile.pluginFile.exists());
   }

   @Test
   public void installPluginFromZip() throws JSONException, IOException {
      File pluginZip = mockAPluginZip();
      PluginConfig pluginConfig = testCoreManager.installPluginFromZip(pluginZip, null);
      sameInstallTest(pluginConfig);
   }

   @Test
   public void installPluginFromDir() throws JSONException, IOException {
      File pluginDir = mockAPluginDir();
      PluginConfig pluginConfig = testCoreManager.installPluginFromDir(pluginDir);
      sameInstallTest(pluginConfig);
   }

   @Test
   public void deleteInstalledPlugin() throws JSONException, IOException {
      File pluginZip = mockAPluginZip();
      PluginConfig pluginConfig = testCoreManager.installPluginFromZip(pluginZip, null);
      testCoreManager.onInstallCompleted(pluginConfig, Collections.emptyMap());

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

   static File mockAPluginZip() throws JSONException, IOException {
      Context applicationContext = ApplicationProvider.getApplicationContext();
      File zipFile = File.createTempFile("mockAPluginZip", ".zip", applicationContext.getFilesDir());

      ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));

      File mockAPluginDir = mockAPluginDir();
      for (File file : mockAPluginDir.listFiles()) {
         zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
         FileUtils.copyFile(file, zipOutputStream);
      }
      zipOutputStream.close();

      return zipFile;
   }

   static File mockAPluginDir() throws IOException, JSONException {
      Context applicationContext = ApplicationProvider.getApplicationContext();
      File dir = Files.createTempDirectory(applicationContext.getFilesDir().toPath(), "mockAPluginDir").toFile();
      dir.deleteOnExit();

      File runtimeApkFile = new File(dir, "runtime.apk");
      runtimeApkFile.createNewFile();

      JSONObject configJson = mockConfigJson(runtimeApkFile);
      File configJsonFile = new File(dir, "config.json");
      FileUtils.writeStringToFile(configJsonFile, configJson.toString(), StandardCharsets.UTF_8);

      return dir;
   }

}
