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
package jp.uphy.servermonitor.service.plugin;

import jp.uphy.servermonitor.plugin.api.Plugin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;


/**
 * @author Yuhi Ishikura
 */
public class PluginLoader {

  private Path pluginDirectory;

  PluginLoader(Path pluginDirectory) {
    this.pluginDirectory = pluginDirectory;
  }

  public Iterable<Plugin> load() throws IOException {
    final List<Path> jarPluginFiles = new ArrayList<>();
    final List<Path> jsPluginFiles = new ArrayList<>();
    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(pluginDirectory)) {
      for (Path file : directoryStream) {
        final String fileName = file.getFileName().toString();
        if (fileName.endsWith(".jar")) {
          jarPluginFiles.add(file);
        } else if (fileName.endsWith(".js")) {
          jsPluginFiles.add(file);
        }
      }
    }

    final URLClassLoader classLoader = new URLClassLoader(jarPluginFiles.stream().map(p -> {
      try {
        return p.toUri().toURL();
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    }).collect(Collectors.toList()).toArray(new URL[0]));

    final List<Plugin> plugins = new ArrayList<>();
    for (Plugin plugin : ServiceLoader.load(Plugin.class, classLoader)) {
      plugins.add(plugin);
    }
    for (Path jsPluginFile : jsPluginFiles) {
      plugins.add(new JsPlugin(jsPluginFile));
    }
    return plugins;
  }

}
