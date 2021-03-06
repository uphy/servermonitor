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
package jp.uphy.servermonitor.web.websocket;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.Map;


/**
 * @author Yuhi Ishikura
 */
@Getter
@EqualsAndHashCode
@ToString
public class Command {

  private String name;
  private Map<String, Object> arguments = Collections.emptyMap();

  public Command() {

  }

  public Command(String name, Map<String, Object> arguments) {
    this.name = name;
    this.arguments = arguments;
  }

  public boolean hasArgument(String name) {
    return this.arguments.containsKey(name);
  }

  public Object getArgument(String name) {
    return this.arguments.get(name);
  }

  public long getArgumentLong(String name) {
    Object o = getArgument(name);
    if (o instanceof Number) {
      return ((Number)o).longValue();
    } else {
      return Long.parseLong(o.toString());
    }
  }

}
