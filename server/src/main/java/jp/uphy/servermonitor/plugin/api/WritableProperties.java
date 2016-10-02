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
package jp.uphy.servermonitor.plugin.api;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * @author Yuhi Ishikura
 */
public final class WritableProperties extends ReadOnlyProperties {

  public WritableProperties(final String pluginId, final Path file) {
    super(pluginId, file);
  }

  public void setProperty(String key, Object value) {
    setProperty(key, String.valueOf(value));
  }

  public void setProperty(String key, String value) {
    this.properties.setProperty(key, value);
    save();
  }

  public void clear() {
    this.properties.clear();
    save();
  }

  public void save() {
    if (Files.exists(this.file.getParent()) == false) {
      try {
        Files.createDirectories(this.file.getParent());
      } catch (IOException e) {
        throw new IllegalStateException(String.format("Failed to create directories for %s.", this.pluginId));
      }
    }
    try (BufferedWriter bw = Files.newBufferedWriter(this.file, StandardCharsets.UTF_8)) {
      this.properties.store(bw, String.format("PluginProperty for %s", this.pluginId));
    } catch (Throwable e) {
      throw new IllegalStateException("Failed to save property file: " + this.file, e);
    }
  }

}
