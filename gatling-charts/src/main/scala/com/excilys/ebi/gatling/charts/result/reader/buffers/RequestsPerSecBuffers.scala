/**
 * Copyright 2011-2012 eBusiness Information, Groupe Excilys (www.excilys.com)
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
package com.excilys.ebi.gatling.charts.result.reader.buffers

import scala.collection.mutable

import com.excilys.ebi.gatling.charts.result.reader.ActionRecord
import com.excilys.ebi.gatling.core.result.Group
import com.excilys.ebi.gatling.core.result.message.RequestStatus

trait RequestsPerSecBuffers {

	val requestsPerSecBuffers: mutable.Map[BufferKey, CountBuffer] = mutable.HashMap.empty

	def getRequestsPerSecBuffer(requestName: Option[String], group: Option[Group], status: Option[RequestStatus]): CountBuffer = requestsPerSecBuffers.getOrElseUpdate(computeKey(requestName, group, status), new CountBuffer)

	def updateRequestsPerSecBuffers(record: ActionRecord, group: Option[Group]) {
		getRequestsPerSecBuffer(Some(record.request), group, None).update(record.executionStartBucket)
		getRequestsPerSecBuffer(Some(record.request), group, Some(record.status)).update(record.executionStartBucket)

		getRequestsPerSecBuffer(None, None, None).update(record.executionStartBucket)
		getRequestsPerSecBuffer(None, None, Some(record.status)).update(record.executionStartBucket)
	}
}