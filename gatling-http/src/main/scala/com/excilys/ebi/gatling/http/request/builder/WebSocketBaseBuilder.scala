/**
 * Copyright 2011-2012 eBusiness Information, Groupe Excilys (www.excilys.com)
 * Copyright 2012 Gilt Groupe, Inc. (www.gilt.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.excilys.ebi.gatling.http.request.builder

import com.excilys.ebi.gatling.core.session._
import com.excilys.ebi.gatling.http.action._
import com.excilys.ebi.gatling.http.util.{RequestLogger, WebSocketClient}

class WebSocketBaseBuilder(val attributeName: String) {
  /**
   * Opens a web socket and stores it in the session.
   *
   * @param fUrl The socket URL
   * @param actionName The action name in the log
   */
  def open(fUrl: EvaluatableString, actionName: EvaluatableString = (_ => attributeName))(implicit webSocketClient: WebSocketClient, requestLogger: RequestLogger) = new OpenWebSocketActionBuilder(attributeName, actionName, fUrl, webSocketClient, requestLogger)

  /**
   * Sends a message on the given socket.
   *
   * @param fMessage The message
   * @param actionName The action name in the log
   */
  def sendMessage(fMessage: EvaluatableString, actionName: EvaluatableString = (_ => attributeName)) = new SendWebSocketMessageActionBuilder(attributeName, actionName, fMessage)

  /**
   * Closes a web socket.
   *
   * @param actionName The action name in the log
   */
  def close(actionName: EvaluatableString = (_ => attributeName)) = new CloseWebSocketActionBuilder(attributeName, actionName)
}

object WebSocketBaseBuilder {
  def websocket(attributeName: String) = new WebSocketBaseBuilder(attributeName)
}