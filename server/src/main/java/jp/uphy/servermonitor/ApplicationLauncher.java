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
import java.util.ArrayList;
import java.util.List;


/**
 * @author Yuhi Ishikura
 */
public class ApplicationLauncher {

  public static void main(String[] args) throws IOException, InterruptedException {
    final String javaPath = System.getProperty("java.home") + "/bin/java";
    final String classpath = System.getProperty("java.class.path");
    final String restartableProperty = String.format("-D%s=true", Application.RESTARTABLE_KEY);
    String springloaded = null;
    for (String s : classpath.split(":")) {
      if (s.contains("springloaded")) {
        springloaded = s;
        break;
      }
    }
    final List<String> cmd = new ArrayList<>();
    cmd.add(javaPath);
    if (springloaded != null) {
      cmd.add("-javaagent:" + springloaded);
      cmd.add("-noverify");
    }
    cmd.add("-cp");
    cmd.add(classpath);
    cmd.add(restartableProperty);
    cmd.add(Application.class.getName());

    while (true) {
      final ProcessBuilder pb = new ProcessBuilder(cmd);
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
