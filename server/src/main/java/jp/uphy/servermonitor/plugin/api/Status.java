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

/**
 * @author Yuhi Ishikura
 */
public enum Status {

  DEFAULT(true, false, false),
  INITIALIZE_FAILED(true, false, false),
  INITIALIZED(true, true, false),
  START_FAILED(true, true, false),
  STARTED(true, false, true),
  STOP_FAILED(true, false, true),
  STOPPED(true, true, false);

  private boolean initializable;
  private boolean startable;
  private boolean stoppable;

  Status(final boolean initializable, final boolean startable, final boolean stoppable) {
    this.initializable = initializable;
    this.startable = startable;
    this.stoppable = stoppable;
  }

  public boolean isInitializable() {
    return initializable;
  }

  public boolean isStartable() {
    return startable;
  }

  public boolean isStoppable() {
    return stoppable;
  }
}
