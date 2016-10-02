/**
 * Copyright (C) 2015 uphy.jp
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.uphy.servermonitor.service.plugin.api;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;


/**
 * @author Yuhi Ishikura
 */
public class ReadOnlyProperties {

  protected final String pluginId;
  protected final Path file;
  protected final Properties properties = new Properties();

  public ReadOnlyProperties(String pluginId, Path file) {
    this.pluginId = pluginId;
    this.file = file;
  }

  public boolean getPropertyBoolean(String key, boolean defaultValue) {
    String s = getPropertyString(key, null);
    if (s == null) {
      return defaultValue;
    }
    return Boolean.parseBoolean(s);
  }

  public long getPropertyLong(String key, long defaultValue) {
    String s = getPropertyString(key, null);
    if (s == null) {
      return defaultValue;
    }
    return Long.parseLong(s);
  }

  public int getPropertyInt(String key, int defaultValue) {
    String s = getPropertyString(key, null);
    if (s == null) {
      return defaultValue;
    }
    return Integer.parseInt(s);
  }

  public String getPropertyString(String key, String defaultValue) {
    return this.properties.getProperty(key, defaultValue);
  }

  public void load() {
    this.properties.clear();
    if (Files.exists(this.file) == false) {
      return;
    }
    try (BufferedReader br = Files.newBufferedReader(this.file, StandardCharsets.UTF_8)) {
      this.properties.load(br);
    } catch (Throwable e) {
      throw new IllegalStateException("Failed to load property file: " + this.file, e);
    }
  }

  public Properties getProperties() {
    return properties;
  }
}
