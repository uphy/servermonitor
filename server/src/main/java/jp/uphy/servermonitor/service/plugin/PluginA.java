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

import jp.uphy.servermonitor.service.plugin.api.Plugin;
import jp.uphy.servermonitor.service.plugin.api.PluginContext;
import jp.uphy.servermonitor.service.plugin.api.Scheduler;

import java.util.concurrent.TimeUnit;


/**
 * @author Yuhi Ishikura
 */
public class PluginA extends Plugin {

  private Scheduler.ScheduleTaskHandler handler;
  private int i = 0;

  @Override
  public void initialize(final PluginContext pluginContext) {
    this.i = 0;
    pluginContext.getSettings().setProperty("intervalInSeconds", 1000);
    pluginContext.getSettings().setProperty("message", "Hello");
  }

  @Override
  public void start(final PluginContext pluginContext) {
    final int id = i++;
    this.handler = pluginContext.getScheduler().scheduleAtFixedRate(() -> {
      final String message = pluginContext.getSettings().getPropertyString("message", null);
      if (message != null) {
        System.out.println(id + ":" + message);
      }
    }, TimeUnit.SECONDS.toMillis(5));
  }

  @Override
  public void stop(final PluginContext value) {
    super.stop(value);
    value.getState().setProperty("a", 100);
    handler.stop();
  }

  @Override
  public String getId() {
    return "pluginA";
  }

  @Override
  public String getName() {
    return "Plugin A";
  }

}
