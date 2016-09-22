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

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.uphy.servermonitor.domain.Event;
import jp.uphy.servermonitor.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Yuhi Ishikura
 */
@Component
public class EventHandler extends TextWebSocketHandler {

  private final Map<String, WebSocketSession> sessions = new HashMap<>();
  private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private EventService eventService;

  public synchronized void publish(Event event) {
    this.eventService.save(event);
    for (WebSocketSession session : this.sessions.values()) {
      try {
        session.sendMessage(new TextMessage(this.objectMapper.writeValueAsString(event)));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public synchronized void afterConnectionEstablished(final WebSocketSession session) throws Exception {
    super.afterConnectionEstablished(session);
    this.sessions.put(session.getId(), session);
    logger.info(String.format("New event endpoint user. (address=%s)", session.getRemoteAddress()));
  }

  @Override
  public synchronized void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) throws Exception {
    super.afterConnectionClosed(session, status);
    this.sessions.remove(session.getId());
    logger.info(String.format("Event endpoint user exited. (address=%s, status=%s)", session.getRemoteAddress(), status));
  }

  @Override
  protected void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception {
    super.handleTextMessage(session, message);
    logger.trace(String.format("Received command. (address=%s, command=%s)", session.getRemoteAddress(), message.getPayload()));
    try {
      final Command command = this.objectMapper.readValue(message.getPayload(), Command.class);
      switch (command.getName()) {
        case "receive":
          final List<Event> events;
          if (command.hasArgument("from")) {
            events = this.eventService.findEvents((long)command.getArgumentLong("from"));
          } else {
            events = this.eventService.findAllEvents();
          }
          session.sendMessage(new TextMessage(this.objectMapper.writeValueAsString(events)));
          break;
        default:
          throw new IllegalArgumentException(String.format("Unsupported command.  (command=%s)", command));
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

}
