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
package jp.uphy.servermonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * @author Yuhi Ishikura
 */
@SpringBootApplication
public class Application {

  static final String RESTARTABLE_KEY = "restartable";
  static final int RESTART_EXITCODE = 100;
  private static ConfigurableApplicationContext applicationContext;

  public static void main(String[] args) throws Exception {
    applicationContext = SpringApplication.run(Application.class, args);
  }

  public static void exit() {
    exit(0);
  }

  public static void restart() {
    if (isRestartable() == false) {
      throw new IllegalStateException("Not restartable.");
    }
    exit(RESTART_EXITCODE);
  }

  private static void exit(int exitCode) {
    System.exit(exitCode);
  }

  public static boolean isRestartable() {
    return System.getProperty(RESTARTABLE_KEY, null) != null;
  }

}
