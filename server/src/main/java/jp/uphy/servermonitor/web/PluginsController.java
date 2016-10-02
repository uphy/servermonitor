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
package jp.uphy.servermonitor.web;

import jp.uphy.servermonitor.service.plugin.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Properties;


/**
 * @author Yuhi Ishikura
 */
@RestController
@RequestMapping("rest/plugins")
public class PluginsController {

  @Autowired
  private PluginManager pluginManager;

  @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Iterable<PluginManager.PluginWrapper> list() {
    return this.pluginManager.getAllPlugins();
  }

  @RequestMapping(value = "{pluginId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public PluginManager.PluginWrapper get(@PathVariable String pluginId) {
    return getPlugin(pluginId);
  }

  @RequestMapping(value = "{pluginId}/restart", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public PluginManager.PluginWrapper restart(@PathVariable String pluginId) {
    final PluginManager.PluginWrapper plugin = getPlugin(pluginId);
    plugin.restart();
    return plugin;
  }

  @RequestMapping(value = "{pluginId}/stop", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public PluginManager.PluginWrapper stop(@PathVariable String pluginId) {
    final PluginManager.PluginWrapper plugin = getPlugin(pluginId);
    plugin.stop();
    return plugin;
  }

  @RequestMapping(value = "{pluginId}/start", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public PluginManager.PluginWrapper start(@PathVariable String pluginId) {
    final PluginManager.PluginWrapper plugin = getPlugin(pluginId);
    plugin.start();
    return plugin;
  }

  @RequestMapping(value = "{pluginId}/initialize", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public PluginManager.PluginWrapper initialize(@PathVariable String pluginId) {
    final PluginManager.PluginWrapper plugin = getPlugin(pluginId);
    plugin.initialize();
    return plugin;
  }

  @RequestMapping(value = "{pluginId}/settings", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Properties settings(@PathVariable("pluginId") String pluginId, @RequestParam(required = false) String name, @RequestParam(required = false) String value, @RequestParam(required = false, defaultValue = "false") boolean restart) {
    final PluginManager.PluginWrapper plugin = getPlugin(pluginId);
    if (name != null && value != null) {
      plugin.getPluginContext().getSettings().setProperty(name, value);
    }
    if (restart) {
      plugin.restart();
    }
    return plugin.getPluginContext().getSettings().getProperties();
  }

  @RequestMapping(value = "{pluginId}/settings", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Properties settingsAll(@PathVariable String pluginId, @RequestBody Map<String, String> settings, @RequestParam(required = false) String value, @RequestParam(required = false, defaultValue = "false") boolean restart) {
    final PluginManager.PluginWrapper plugin = getPlugin(pluginId);
    plugin.getPluginContext().getSettings().clear();
    for (Map.Entry<String, String> entry : settings.entrySet()) {
      plugin.getPluginContext().getSettings().setProperty(entry.getKey(), entry.getValue());
    }
    if (restart) {
      plugin.restart();
    }
    return plugin.getPluginContext().getSettings().getProperties();
  }

  @RequestMapping(method = RequestMethod.POST)
  public void upload(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
    if (file.getOriginalFilename().endsWith(".jar") == false) {
      response.getWriter().println("Unsupported plugin file: " + file.getOriginalFilename());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } else {
      final Path outFile = this.pluginManager.getPluginDirectory().resolve(file.getOriginalFilename());
      try (final InputStream in = file.getInputStream();
        final OutputStream out = Files.newOutputStream(outFile, StandardOpenOption.CREATE)) {
        final byte[] buf = new byte[0x1000];
        int size;
        while ((size = in.read(buf)) != -1) {
          out.write(buf, 0, size);
        }
      }
      response.sendRedirect("/plugins");
    }
  }

  private PluginManager.PluginWrapper getPlugin(String id) {
    PluginManager.PluginWrapper plugin = this.pluginManager.getPlugin(id);
    if (plugin == null) {
      throw new IllegalArgumentException("No such plugin: " + id);
    }
    return plugin;
  }

}
