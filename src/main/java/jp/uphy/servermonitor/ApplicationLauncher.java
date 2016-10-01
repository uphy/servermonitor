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

import java.io.IOException;


/**
 * @author Yuhi Ishikura
 */
public class ApplicationLauncher {

  public static void main(String[] args) throws IOException, InterruptedException {
    final String javaPath = System.getProperty("java.home") + "/bin/java";
    final String classpath = System.getProperty("java.class.path");
    final String restartableProperty = String.format("-D%s=true", Application.RESTARTABLE_KEY);

    while (true) {
      final ProcessBuilder pb = new ProcessBuilder(javaPath, "-cp", classpath, restartableProperty, Application.class.getName());
      pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
      pb.redirectError(ProcessBuilder.Redirect.INHERIT);
      final Process p = pb.start();
      final int exitCode = p.waitFor();
      if (exitCode != Application.RESTART_EXITCODE) {
        break;
      }
    }
  }

}
