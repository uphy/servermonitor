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
import jp.uphy.servermonitor.plugin.api.PluginContext;
import jp.uphy.servermonitor.plugin.api.Scheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Yuhi Ishikura
 */
@Service
public class PluginManager {

  private final Map<String, PluginWrapper> plugins = new HashMap<>();
  private final Path pluginDirectory = Paths.get("plugins");

  @PostConstruct
  public void load() throws IOException {
    if (Files.exists(pluginDirectory) == false) {
      return;
    }
    final Scheduler scheduler = new Scheduler();
    for (Plugin plugin : new PluginLoader(this.pluginDirectory).load()) {
      final PluginContext context = new PluginContext(plugin.getId(), scheduler);
      context.getState().load();
      context.getSettings().load();
      plugins.put(plugin.getId(), new PluginWrapper(context, plugin));
    }

    plugins.values().forEach(PluginWrapper::restoreState);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      plugins.values().forEach(PluginWrapper::stopWithoutUpdatingState);
    }));
  }

  public Path getPluginDirectory() {
    return pluginDirectory;
  }

  public PluginWrapper getPlugin(String id) {
    return this.plugins.get(id);
  }

  public Iterable<PluginWrapper> getAllPlugins() {
    return this.plugins.values();
  }

}
