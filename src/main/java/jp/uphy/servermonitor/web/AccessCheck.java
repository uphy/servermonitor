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
package jp.uphy.servermonitor.web;

import jp.uphy.servermonitor.domain.Event;
import jp.uphy.servermonitor.web.websocket.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;


/**
 * @author Yuhi Ishikura
 */
@Component
public class AccessCheck {

  @Autowired
  private EventHandler eventHandler;

  @PostConstruct
  public void postConstruct() {
    new Thread(() -> {
      while (true) {
        // eventHandler.publish(new Event(new Date().toString()));
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }).start();
  }


}
