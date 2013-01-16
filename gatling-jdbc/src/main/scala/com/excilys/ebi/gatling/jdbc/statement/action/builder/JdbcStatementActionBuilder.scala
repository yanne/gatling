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
package com.excilys.ebi.gatling.jdbc.statement.action.builder

import com.excilys.ebi.gatling.core.action.builder.ActionBuilder
import com.excilys.ebi.gatling.core.action.system
import com.excilys.ebi.gatling.core.config.ProtocolConfigurationRegistry
import com.excilys.ebi.gatling.jdbc.statement.action.JdbcStatementAction
import com.excilys.ebi.gatling.jdbc.statement.builder.AbstractJdbcStatementBuilder

import akka.actor.{ ActorRef, Props }

object JdbcStatementActionBuilder {

	def apply(builder: AbstractJdbcStatementBuilder[_])  = new JdbcStatementActionBuilder(builder)
}
class JdbcStatementActionBuilder(builder: AbstractJdbcStatementBuilder[_]) extends ActionBuilder {

	/**
	 * @param protocolConfigurationRegistry
	 * @return the built Action
	 */
	private[gatling] def build(next: ActorRef,protocolConfigurationRegistry: ProtocolConfigurationRegistry) = system.actorOf(Props(JdbcStatementAction(builder,next)))
}
