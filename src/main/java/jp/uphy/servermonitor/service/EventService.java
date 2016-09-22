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
package jp.uphy.servermonitor.service;

import jp.uphy.servermonitor.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author Yuhi Ishikura
 */
@Service
public class EventService {

  @Autowired
  private EventRepository eventRepository;

  @Transactional(readOnly = true)
  public List<Event> findAllEvents() {
    return this.eventRepository.findAll();
  }

  @Transactional
  public void save(final Event event) {
    this.eventRepository.save(event);
  }

  public List<Event> findEvents(long from) {
    return this.eventRepository.findByTimeAfter(from);
  }
}
