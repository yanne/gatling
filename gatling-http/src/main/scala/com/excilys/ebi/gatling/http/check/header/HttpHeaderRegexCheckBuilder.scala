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
package com.excilys.ebi.gatling.http.check.header

import java.net.URLDecoder

import scala.collection.JavaConversions.asScalaBuffer

import com.excilys.ebi.gatling.core.check.ExtractorFactory
import com.excilys.ebi.gatling.core.check.extractor.Extractor
import com.excilys.ebi.gatling.core.check.extractor.regex.RegexExtractor
import com.excilys.ebi.gatling.core.config.GatlingConfiguration.configuration
import com.excilys.ebi.gatling.core.session.{ Session, Expression }
import com.excilys.ebi.gatling.http.Headers
import com.excilys.ebi.gatling.http.check.HttpMultipleCheckBuilder
import com.excilys.ebi.gatling.http.request.HttpPhase.HeadersReceived
import com.excilys.ebi.gatling.http.response.ExtendedResponse

object HttpHeaderRegexCheckBuilder extends Extractor {

	private def findExtractorFactory(occurrence: Int): ExtractorFactory[ExtendedResponse, (String, String), String] =
		(response: ExtendedResponse) =>
			(headerAndPattern: (String, String)) => {
				findAllExtractorFactory(response)(headerAndPattern) match {
					case Some(results) if results.isDefinedAt(occurrence) => results(occurrence)
					case _ => None
				}
			}

	private val findAllExtractorFactory: ExtractorFactory[ExtendedResponse, (String, String), Seq[String]] = (response: ExtendedResponse) =>
		(headerAndPattern: (String, String)) => {
			val (headerName, pattern) = headerAndPattern

			val decodedHeaderValues = Option(response.getHeaders(headerName))
				.map { headerValues =>
					if (headerName == Headers.Names.LOCATION)
						headerValues.map(URLDecoder.decode(_, configuration.simulation.encoding))
					else
						headerValues.toSeq
				}.getOrElse(Nil)

			decodedHeaderValues.foldLeft(Seq.empty[String]) { (matches, header) =>
				new RegexExtractor(header).extractMultiple(pattern).map(_ ++ matches).getOrElse(matches)
			}
		}

	private val countExtractorFactory: ExtractorFactory[ExtendedResponse, (String, String), Int] =
		(response: ExtendedResponse) => (headerAndPattern: (String, String)) => findAllExtractorFactory(response)(headerAndPattern).map(_.length).orElse(0)

	def headerRegex(headerName: Expression[String], pattern: Expression[String]) = {

		val expression = (s: Session) => for {
			h <- headerName(s)
			p <- pattern(s)
		} yield (h, p)

		new HttpMultipleCheckBuilder(findExtractorFactory, findAllExtractorFactory, countExtractorFactory, expression, HeadersReceived)
	}
}
