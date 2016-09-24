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
import jp.uphy.servermonitor.service.EventService;
import jp.uphy.servermonitor.web.websocket.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author Yuhi Ishikura
 */
@RestController
@RequestMapping("rest/events")
public class EventController {

  @Autowired
  private EventService eventService;
  @Autowired
  private EventHandler eventHandler;

  @RequestMapping(method = RequestMethod.GET)
  public List<Event> events(@RequestParam(defaultValue = "0") long from) {
    if (from <= 0) {
      return this.eventService.findAllEvents();
    }
    return this.eventService.findEvents(from);
  }

  @RequestMapping(value = "clear", method = RequestMethod.GET)
  public void clear() {
    this.eventService.clear();
  }

  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public void post(@RequestBody Event event) {
    this.eventHandler.publish(event);
  }
}
