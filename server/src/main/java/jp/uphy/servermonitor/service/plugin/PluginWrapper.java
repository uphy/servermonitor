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
import jp.uphy.servermonitor.plugin.api.Plugin;
import jp.uphy.servermonitor.plugin.api.PluginContext;
import jp.uphy.servermonitor.plugin.api.Scheduler;
import jp.uphy.servermonitor.plugin.api.Status;
import jp.uphy.servermonitor.plugin.api.WritableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Yuhi Ishikura
 */
public class PluginWrapper {

  private static final Logger logger = LoggerFactory.getLogger(PluginWrapper.class);
  private static final String STATUS_KEY = "state";
  private final Plugin plugin;
  private final PluginContext pluginContext;
  private Status status = Status.DEFAULT;

  static PluginWrapper wrapForTest(final Plugin plugin) {
    return new PluginWrapper(new PluginContext(plugin.getId(), new Scheduler()), plugin);
  }

  PluginWrapper(final PluginContext pluginContext, final Plugin plugin) {
    this.pluginContext = pluginContext;
    this.plugin = plugin;
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

  private void setStatus(Status status) {
    this.status = status;
    this.pluginContext.getState().setProperty(STATUS_KEY, status);
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
      setStatus(Status.INITIALIZED);
    } catch (Throwable ex) {
      logger.error(String.format("Failed to initialize plugin. (pluginId=%s)", this.plugin.getId()), ex);
      this.status = Status.INITIALIZE_FAILED;
    }
  }

  public void restart() {
    stop();
    start();
  }

  void restoreState() {
    final Status previousStatus = Status.valueOf(pluginContext.getState().getPropertyString(STATUS_KEY, Status.DEFAULT.name()));
    switch (previousStatus) {
      case DEFAULT:
        start();
        break;
      case INITIALIZE_FAILED:
        initialize();
        break;
      case INITIALIZED:
        setStatus(Status.INITIALIZED);
        break;
      case START_FAILED:
        setStatus(Status.INITIALIZED);
        start();
        break;
      case STARTED:
        setStatus(Status.INITIALIZED);
        start();
        break;
      case STOP_FAILED:
        setStatus(Status.INITIALIZED);
        break;
      case STOPPED:
        setStatus(Status.INITIALIZED);
        break;
    }
  }

  public void start() {
    if (this.status == Status.DEFAULT) {
      if (this.pluginContext.getState().getPropertyBoolean(STATUS_KEY, false) == false) {
        initialize();
      }
    }

    if (this.status.isStartable() == false) {
      return;
    }
    try {
      this.plugin.start(this.pluginContext);
      setStatus(Status.STARTED);
    } catch (Throwable ex) {
      logger.error(String.format("Failed to start plugin. (pluginId=%s)", this.plugin.getId()), ex);
      setStatus(Status.START_FAILED);
    }
  }

  public void stop() {
    stop(false);
  }

  void stopWithoutUpdatingState() {
    stop(true);
  }

  private void stop(boolean ignoreStateUpdate) {
    if (this.status.isStoppable() == false) {
      return;
    }
    try {
      this.plugin.stop(this.pluginContext);
      if (ignoreStateUpdate) {
        this.status = Status.STOPPED;
      } else {
        setStatus(Status.STOPPED);
      }
    } catch (Throwable ex) {
      logger.error(String.format("Failed to stop plugin. (pluginId=%s)", this.plugin.getId()), ex);
      if (ignoreStateUpdate) {
        this.status = Status.STOP_FAILED;
      } else {
        setStatus(Status.STOP_FAILED);
      }
    }
  }

}
