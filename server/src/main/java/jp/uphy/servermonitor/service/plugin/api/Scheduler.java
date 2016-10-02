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
package jp.uphy.servermonitor.service.plugin.api;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


/**
 * @author Yuhi Ishikura
 */
public final class Scheduler {

  private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
  private Consumer<ScheduleTaskException> taskExceptionHandler = Throwable::printStackTrace;

  public ScheduleTaskHandler scheduleAtFixedRate(ScheduleTask task, long period) {
    final Future<?> f = this.executorService.scheduleAtFixedRate(() -> {
      try {
        task.run();
      } catch (ScheduleTaskException e) {
        taskExceptionHandler.accept(e);
      }
    }, 0, period, TimeUnit.MILLISECONDS);
    return new ScheduleTaskHandler(f);
  }

  public static class ScheduleTaskHandler {

    private Future<?> task;

    ScheduleTaskHandler(final Future<?> task) {
      this.task = task;
    }

    public void stop() {
      this.task.cancel(true);
    }

  }

}
