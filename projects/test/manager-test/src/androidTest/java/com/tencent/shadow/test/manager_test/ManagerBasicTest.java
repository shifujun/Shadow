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

public class ManagerBasicTest {

   @Before
   public void setUp() throws IOException {
      Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
      FileUtils.cleanDirectory(targetContext.getFilesDir().getParentFile());
   }

   @Test
   public void getInstalledPluginsIsEmpty() {
      Context applicationContext = ApplicationProvider.getApplicationContext();
      TestCoreManager testCoreManager = new TestCoreManager(applicationContext);
      List<InstalledPlugin> installedPlugins = testCoreManager.getInstalledPlugins(1);
      Assert.assertTrue(installedPlugins.isEmpty());
   }

   @Test
   public void installPlugin() throws JSONException, IOException {
      File pluginZip = mockAPluginZip();

      Context applicationContext = ApplicationProvider.getApplicationContext();
      TestCoreManager testCoreManager = new TestCoreManager(applicationContext);

      PluginConfig pluginConfig = testCoreManager.installPluginFromZip(pluginZip, null);
      testCoreManager.onInstallCompleted(pluginConfig, Collections.emptyMap());

      List<InstalledPlugin> installedPlugins = testCoreManager.getInstalledPlugins(1);
      InstalledPlugin installedPlugin = installedPlugins.get(0);
      Assert.assertEquals("ManagerBasicTest", installedPlugin.UUID_NickName);
   }

   static JSONObject mockConfigJson() throws JSONException {
      JSONObject config = new JSONObject();
      config.put("version", 4);
      config.put("UUID", UUID.randomUUID().toString());
      config.put("UUID_NickName", "ManagerBasicTest");
      return config;
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

      JSONObject configJson = mockConfigJson();
      File configJsonFile = new File(dir, "config.json");
      FileUtils.writeStringToFile(configJsonFile, configJson.toString(), StandardCharsets.UTF_8);

      return dir;
   }

}
