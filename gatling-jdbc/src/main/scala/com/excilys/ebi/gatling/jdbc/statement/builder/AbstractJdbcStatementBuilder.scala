/**
 * Copyright 2011-2012 eBusiness Information, Groupe Excilys (www.excilys.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.excilys.ebi.gatling.jdbc.statement.builder

import java.sql.Connection

import com.excilys.ebi.gatling.core.session.{ Expression, Session }
import com.excilys.ebi.gatling.core.util.FlattenableValidations
import com.excilys.ebi.gatling.jdbc.statement.{ CALL, QUERY, StatementType }
import com.excilys.ebi.gatling.jdbc.statement.action.builder.JdbcStatementActionBuilder

import grizzled.slf4j.Logging

case class JdbcAttributes(
	statementName: Expression[String],
	statement: String,
	statementType: StatementType,
	params: List[Expression[Any]])

abstract class AbstractJdbcStatementBuilder[B <: AbstractJdbcStatementBuilder[B]](jdbcAttributes: JdbcAttributes) extends Logging {

	private[jdbc] def newInstance(jdbcAttributes: JdbcAttributes): B

	def bind(value: Expression[Any]) = newInstance(jdbcAttributes.copy(params = value :: jdbcAttributes.params))

	private[gatling] def toActionBuilder = JdbcStatementActionBuilder(this)

	private[jdbc] def statementName = jdbcAttributes.statementName

	private[jdbc] def build(connection: Connection) = createStatement(connection)

	private def createStatement(connection: Connection) = jdbcAttributes.statementType match {
		case CALL => connection.prepareCall(jdbcAttributes.statement)
		case QUERY => connection.prepareStatement(jdbcAttributes.statement)
	}

	private[jdbc] def resolveParams(session: Session) = jdbcAttributes.params.reverse.map(_(session)).flattenIt
}

