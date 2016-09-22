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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author Yuhi Ishikura
 */
@RestController
@RequestMapping("/rest")
public class ServerMonitorController {

  @Autowired
  private EventService eventService;

  @RequestMapping(value = "/events", method = RequestMethod.GET)
  public List<Event> events(@RequestParam(defaultValue = "0") long from) {
    if (from <= 0) {
      return this.eventService.findAllEvents();
    }
    return this.eventService.findEvents(from);
  }

}
