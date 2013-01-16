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
package com.excilys.ebi.gatling.jdbc.statement.action.actor

import java.lang.System.nanoTime

import java.sql.{ Connection, PreparedStatement, SQLException }

import scala.collection.mutable

import com.excilys.ebi.gatling.core.result.message.{ KO, OK }
import com.excilys.ebi.gatling.core.session.Session
import com.excilys.ebi.gatling.core.util.TimeHelper.{ computeTimeMillisFromNanos, nowMillis }
import com.excilys.ebi.gatling.jdbc.util.StatementBundle

import akka.actor.ActorRef


object JdbcTransactionActor {
	def apply(bundles: Seq[StatementBundle],isolationLevel: Option[Int],session: Session,next: ActorRef) = new JdbcTransactionActor(bundles,isolationLevel,session,next)
}
class JdbcTransactionActor(bundles: Seq[StatementBundle],isolationLevel: Option[Int],session: Session,next: ActorRef) extends JdbcActor(session,next) {

	var statementsProcessed = 0

	def onTimeout {
		logCurrentStatement(KO,Some("JdbcTransactionActor timed out"))
		failRemainingStatements
		executeNext(session.setFailed)
	}

	def onExecute {
		val statements = mutable.ListBuffer.empty[PreparedStatement]
		var connection: Connection = null

		def executeSingleStatement(bundle: StatementBundle) {
			currentStatementName = bundle.name
			val statement = bundle.buildStatement(connection)
			statements += statement
			statementExecutionStartDate = computeTimeMillisFromNanos(nanoTime)
			val hasResultSet = statement.execute
			statementExecutionEndDate = computeTimeMillisFromNanos(nanoTime)
			resetTimeout
			if (hasResultSet) processResultSet(statement)
			executionEndDate = computeTimeMillisFromNanos(nanoTime)
			resetTimeout
			logCurrentStatement(OK)
			statementsProcessed += 1
		}

		try {
			executionStartDate = nowMillis
			connection = setupConnection(isolationLevel)
			connection.setAutoCommit(false)
			resetTimeout
			bundles.foreach(executeSingleStatement(_))
			connection.commit
			next ! session
			context.stop(self)
		} catch {
			case e: SQLException =>
				try {
					connection.rollback
				} catch {
					case _ : Exception => // shut up
				}
				logCurrentStatement(KO,Some(e.getMessage))
				failRemainingStatements
				executeNext(session.setFailed)
		} finally {
			if(connection != null) connection.setAutoCommit(true)
			statements.foreach(closeStatement(_))
			closeConnection(connection)
		}

	}

	def failRemainingStatements = {
		val remainingStatements = bundles.drop(statementsProcessed - 1)
		val failingQueryName = bundles.head.name
		remainingStatements.tail.foreach(bundle => logStatement(bundle.name,KO,Some(s"Transaction failed because '$failingQueryName' failed ")))
	}
}
