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
package jp.uphy.servermonitor.plugin.api;

/**
 * @author Yuhi Ishikura
 */
public interface Plugin {

  String getName();

  String getId();

  void initialize(PluginContext pluginContext) throws PluginException;

  void start(PluginContext pluginContext) throws PluginException;

  void stop(final PluginContext value) throws PluginException;

}