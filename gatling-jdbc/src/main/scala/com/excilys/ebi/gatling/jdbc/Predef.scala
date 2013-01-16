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
package com.excilys.ebi.gatling.jdbc

import com.excilys.ebi.gatling.core.session.Expression
import com.excilys.ebi.gatling.jdbc.config.JdbcProtocolConfigurationBuilder
import com.excilys.ebi.gatling.jdbc.feeder.database.JdbcFeederSource
import com.excilys.ebi.gatling.jdbc.statement.builder.{ AbstractJdbcStatementBuilder, JdbcStatementBaseBuilder }
import com.excilys.ebi.gatling.jdbc.statement.action.builder.JdbcTransactionActionBuilder

object Predef {

	implicit def jdbcProtocolConfigurationBuilder2JdbcProtocolConfiguration(builder: JdbcProtocolConfigurationBuilder) = builder.build
	implicit def statementBuilder2ActionBuilder(statementBuilder: AbstractJdbcStatementBuilder[_]) = statementBuilder.toActionBuilder

	def jdbcConfig = JdbcProtocolConfigurationBuilder.jdbcConfig
	def sql(statementName: Expression[String]) = JdbcStatementBaseBuilder.sql(statementName)
	def transaction(queries: AbstractJdbcStatementBuilder[_]*) = JdbcTransactionActionBuilder(queries)

	def jdbcFeeder(url: String, username: String, password: String, sql: String) = JdbcFeederSource(url, username, password, sql)
}