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
package com.excilys.ebi.gatling.http.util

import com.excilys.ebi.gatling.http.ahc.GatlingHttpClient
import com.excilys.ebi.gatling.core.result.message.RequestStatus._
import com.excilys.ebi.gatling.core.result.writer.DataWriter
import com.excilys.ebi.gatling.core.session.Session
import com.ning.http.client.websocket.{WebSocketListener, WebSocketUpgradeHandler}
import grizzled.slf4j.Logging
import java.io.IOException
import java.net.URI

trait WebSocketClient {
  @throws(classOf[IOException])
  def open(uri: URI, listener: WebSocketListener)
}

trait RequestLogger {
  def logRequest(session: Session, actionName: String, requestStatus: RequestStatus, started: Long, ended: Long, errorMessage: Option[String] = None)
}

/** The default AsyncHttpClient WebSocket client. */
object DefaultWebSocketClient extends WebSocketClient with Logging {
  def open(uri: URI, listener: WebSocketListener) {
    GatlingHttpClient.client.prepareGet(uri.toString).execute(
      new WebSocketUpgradeHandler.Builder().addWebSocketListener(listener).build()
    )
  }
}

/** The default Gatling request logger. */
object DefaultRequestLogger extends RequestLogger {
  def logRequest(session: Session, actionName: String, requestStatus: RequestStatus, started: Long, ended: Long, errorMessage: Option[String]) {
    DataWriter.logRequest(session.scenarioName, session.userId, actionName,
      started, ended, ended, ended,
      requestStatus, errorMessage)
  }
}
