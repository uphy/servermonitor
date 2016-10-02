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

import com.fasterxml.jackson.annotation.JsonIgnore;
import jp.uphy.servermonitor.service.plugin.api.Plugin;
import jp.uphy.servermonitor.service.plugin.api.PluginContext;
import jp.uphy.servermonitor.service.plugin.api.Scheduler;
import jp.uphy.servermonitor.service.plugin.api.Status;
import jp.uphy.servermonitor.service.plugin.api.WritableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;


/**
 * @author Yuhi Ishikura
 */
@Service
public class PluginManager {

  private static final Logger logger = LoggerFactory.getLogger(PluginManager.class);
  private final Map<String, PluginWrapper> plugins = new HashMap<>();
  private final Path pluginDirectory = Paths.get("plugins");

  @PostConstruct
  public void load() throws IOException {
    if (Files.exists(pluginDirectory) == false) {
      return;
    }
    final List<Path> pluginFiles = new ArrayList<>();
    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(pluginDirectory)) {
      for (Path file : directoryStream) {
        if (file.getFileName().toString().endsWith(".jar")) {
          pluginFiles.add(file);
        }
      }
    }

    final URLClassLoader classLoader = new URLClassLoader(pluginFiles.stream().map(p -> {
      try {
        return p.toUri().toURL();
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    }).collect(Collectors.toList()).toArray(new URL[0]));

    final Scheduler scheduler = new Scheduler();
    for (Plugin plugin : ServiceLoader.load(Plugin.class, classLoader)) {
      final WritableProperties state = new WritableProperties(plugin.getId(), Paths.get(String.format("state/%s.properties", plugin.getId())));
      final WritableProperties settings = new WritableProperties(plugin.getId(), Paths.get(String.format("settings/%s.properties", plugin.getId())));
      final PluginContext context = new PluginContext(plugin.getId(), scheduler, state, settings);
      state.load();
      settings.load();
      plugins.put(plugin.getId(), new PluginWrapper(context, plugin));
    }

    plugins.values().forEach(PluginWrapper::start);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      plugins.values().forEach(PluginWrapper::stop);
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

  public static class PluginWrapper {

    private static final String INITIALIZED_KEY = "initialized";
    private final Plugin plugin;
    private final PluginContext pluginContext;
    private Status status = Status.DEFAULT;

    PluginWrapper(final PluginContext pluginContext, final Plugin plugin) {
      this.pluginContext = pluginContext;
      this.plugin = plugin;
      if (pluginContext.getState().getPropertyBoolean(INITIALIZED_KEY, false)) {
        this.status = Status.INITIALIZED;
      }
    }

    public String getId() {
      return this.plugin.getId();
    }

    public String getName() {
      return this.plugin.getName();
    }

    @JsonIgnore
    public PluginContext getPluginContext() {
      return pluginContext;
    }

    public Status getStatus() {
      return status;
    }

    public void initialize() {
      if (this.status.isInitializable() == false) {
        return;
      }
      if (this.status.isStoppable()) {
        stop();
      }
      try {
        final WritableProperties state = this.pluginContext.getState();
        state.clear();
        final WritableProperties settings = this.pluginContext.getSettings();
        settings.clear();
        this.plugin.initialize(this.pluginContext);
        state.setProperty(INITIALIZED_KEY, "true");
        this.status = Status.INITIALIZED;
      } catch (Throwable ex) {
        logger.error(String.format("Failed to initialize plugin. (pluginId=%s)", this.plugin.getId()), ex);
        this.status = Status.INITIALIZE_FAILED;
      }
    }

    public void restart() {
      stop();
      start();
    }

    public void start() {
      if (this.status == Status.DEFAULT) {
        if (this.pluginContext.getState().getPropertyBoolean(INITIALIZED_KEY, false) == false) {
          initialize();
        }
      }

      if (this.status.isStartable() == false) {
        return;
      }
      try {
        this.plugin.start(this.pluginContext);
        this.status = Status.STARTED;
      } catch (Throwable ex) {
        logger.error(String.format("Failed to start plugin. (pluginId=%s)", this.plugin.getId()), ex);
        this.status = Status.START_FAILED;
      }
    }

    public void stop() {
      if (this.status.isStoppable() == false) {
        return;
      }
      try {
        this.plugin.stop(this.pluginContext);
        this.status = Status.STOPPED;
      } catch (Throwable ex) {
        logger.error(String.format("Failed to stop plugin. (pluginId=%s)", this.plugin.getId()), ex);
        this.status = Status.STOP_FAILED;
      }
    }

  }

}
