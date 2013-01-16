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

import java.sql.Connection

import com.excilys.ebi.gatling.core.action.builder.ActionBuilder
import com.excilys.ebi.gatling.core.action.system
import com.excilys.ebi.gatling.core.config.ProtocolConfigurationRegistry
import com.excilys.ebi.gatling.jdbc.statement.action.JdbcTransactionAction
import com.excilys.ebi.gatling.jdbc.statement.builder.AbstractJdbcStatementBuilder

import akka.actor.{ ActorRef, Props }

object JdbcTransactionActionBuilder {

	def apply(builders: Seq[AbstractJdbcStatementBuilder[_]]) = new JdbcTransactionActionBuilder(builders.toList,None)
}

class JdbcTransactionActionBuilder(builders: List[AbstractJdbcStatementBuilder[_]],isolationLevel: Option[Int]) extends ActionBuilder {

	/**
	 * @param protocolConfigurationRegistry
	 * @return the built Action
	 */
	private[gatling] def build(next: ActorRef,protocolConfigurationRegistry: ProtocolConfigurationRegistry) = system.actorOf(Props(JdbcTransactionAction(builders,isolationLevel,next)))

	def readCommitted = new JdbcTransactionActionBuilder(builders,Some(Connection.TRANSACTION_READ_COMMITTED))

	def readUncommitted = new JdbcTransactionActionBuilder(builders,Some(Connection.TRANSACTION_READ_UNCOMMITTED))

	def repeatableRead = new JdbcTransactionActionBuilder(builders,Some(Connection.TRANSACTION_REPEATABLE_READ))

	def serializable = new JdbcTransactionActionBuilder(builders,Some(Connection.TRANSACTION_SERIALIZABLE))
}
