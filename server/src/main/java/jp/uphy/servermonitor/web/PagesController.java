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

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * @author Yuhi Ishikura
 */
@Controller
@RequestMapping("/")
public class PagesController {

  @RequestMapping("/")
  public String index(Model model, HttpServletRequest request) {
    final String wsurl;
    try {
      final URL url = new URL(request.getRequestURL().toString());
      final String host = url.getHost();
      final int port = url.getPort();
      wsurl = String.format("ws://%s:%d/ws/events", host, port);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    model.addAttribute("wsurl", wsurl);
    return "index";
  }

  @RequestMapping("admin")
  public String plugins() {
    return "admin";
  }

}
