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

import java.nio.file.Paths;


/**
 * @author Yuhi Ishikura
 */
public class PluginContext {

  private Scheduler scheduler;
  private WritableProperties state;
  private WritableProperties settings;

  public PluginContext(final String id, final Scheduler scheduler) {
    this.scheduler = scheduler;
    this.state = new WritableProperties(id, Paths.get(String.format("state/%s.properties", id)));
    this.settings = new WritableProperties(id, Paths.get(String.format("settings/%s.properties", id)));

    this.state.load();
    this.settings.load();
  }

  public WritableProperties getState() {
    return state;
  }

  public WritableProperties getSettings() {
    return settings;
  }

  public Scheduler getScheduler() {
    return scheduler;
  }

}
