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

import jp.uphy.servermonitor.plugin.api.AbstractPlugin;
import jp.uphy.servermonitor.plugin.api.PluginContext;
import jp.uphy.servermonitor.plugin.api.PluginException;
import jp.uphy.servermonitor.plugin.api.Scheduler;

import java.util.concurrent.TimeUnit;


/**
 * @author Yuhi Ishikura
 */
public class AccessCheckerPlugin extends AbstractPlugin {

  public static final long DEFAULT_INTERVAL = TimeUnit.MINUTES.toSeconds(1);
  public static final long DEFAULT_TIMEOUT = 15;
  public static final String INTERVAL_KEY = "intervalInSeconds";
  public static final String CONNECT_TIMEOUT_KEY = "connectTimeoutInSeconds";
  public static final String READ_TIMEOUT_KEY = "readTimeoutInSeconds";
  private Scheduler.ScheduleTaskHandler handler;

  @Override
  public String getName() {
    return "Access Checker";
  }

  @Override
  public String getId() {
    return "access-checker";
  }

  @Override
  public void initialize(final PluginContext pluginContext) throws PluginException {
    pluginContext.getSettings().setProperty(INTERVAL_KEY, DEFAULT_INTERVAL);
    pluginContext.getSettings().setProperty(READ_TIMEOUT_KEY, DEFAULT_TIMEOUT);
    pluginContext.getSettings().setProperty(CONNECT_TIMEOUT_KEY, DEFAULT_TIMEOUT);
  }

  @Override
  public void start(final PluginContext pluginContext) throws PluginException {
    final long interval = pluginContext.getSettings().getPropertyLong(INTERVAL_KEY, DEFAULT_INTERVAL) * 1000;
    final long readTimeout = pluginContext.getSettings().getPropertyLong(READ_TIMEOUT_KEY, DEFAULT_TIMEOUT) * 1000;
    final long connectTimeout = pluginContext.getSettings().getPropertyLong(CONNECT_TIMEOUT_KEY, DEFAULT_TIMEOUT) * 1000;
    this.handler = pluginContext.getScheduler().scheduleAtRate(() -> {
      System.out.println(readTimeout);
      System.out.println(connectTimeout);
    }, interval);
  }

  @Override
  public void stop(final PluginContext value) throws PluginException {
    this.handler.stop();
  }
}
