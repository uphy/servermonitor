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
import jp.uphy.servermonitor.plugin.api.PluginException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @author Yuhi Ishikura
 */
public class JsPlugin implements Plugin {

  private final Path jsFile;
  private Plugin plugin;
  private long lastModified;

  JsPlugin(Path jsFile) throws IOException {
    this.jsFile = jsFile;
    reloadPluginIfNeeded();
  }

  private void reloadPluginIfNeeded() throws IOException {
    final long lastModified = Files.getLastModifiedTime(jsFile).toMillis();
    if (this.lastModified == lastModified) {
      return;
    }
    this.lastModified = lastModified;
    final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByExtension("js");
    try (Reader reader = new InputStreamReader(JsPlugin.class.getResourceAsStream("base.js"), "UTF-8")) {
      scriptEngine.eval(reader);
    } catch (Throwable e) {
      throw new IOException("Failed to load base script.");
    }
    try (Reader reader = Files.newBufferedReader(jsFile, StandardCharsets.UTF_8)) {
      scriptEngine.eval(reader);
      this.plugin = (Plugin)scriptEngine.get("plugin");
    } catch (Throwable e) {
      throw new IOException("Failed to load plugin: " + jsFile, e);
    }
  }

  @Override
  public void initialize(final PluginContext pluginContext) throws PluginException {
    try {
      reloadPluginIfNeeded();
    } catch (IOException e) {
      throw new PluginException("Failed to reload plugin: " + jsFile, e);
    }
    this.plugin.initialize(pluginContext);
  }

  @Override
  public void start(final PluginContext pluginContext) throws PluginException {
    try {
      reloadPluginIfNeeded();
    } catch (IOException e) {
      throw new PluginException("Failed to reload plugin: " + jsFile, e);
    }
    this.plugin.start(pluginContext);
  }

  @Override
  public void stop(final PluginContext pluginContext) throws PluginException {
    this.plugin.stop(pluginContext);
  }

  @Override
  public String getName() {
    return this.plugin.getName();
  }

  @Override
  public String getId() {
    return this.plugin.getId();
  }

  public static void main(String[] args) throws IOException, PluginException {
    final PluginWrapper plugin = PluginWrapper.wrapForTest(new JsPlugin(Paths.get("server/plugins/test.js")));
    plugin.start();
  }

}
