package org.ahp.sqtrl_engine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.ahp.sqtrlengine.service.QueryParser;
import org.ahp.sqtrlengine.utils.QueryUtils;
import org.apache.jena.ext.com.google.common.io.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the class dedicated to the parsing of a SPARQL query
 * Various typical forms of SPARQL queries need to be tested (SELECT only)
 * @author Nicolas Lasolle
 *
 */
class QueryParserTest {
	
	/*private static final Logger logger = LogManager.getLogger(QueryParserTest.class);
	
	@ParameterizedTest
	@ValueSource(strings = {"queries/SelectTestQuery1.rq", 
			"queries/SelectTestQuery2.rq",
			"queries/WhereTestQuery1.rq",
	"queries/WhereTestQuery2.rq"})
	void testBasicValidQuery(String fileName) {
		String queryString;
		try {
			queryString = Resources.toString(getClass().getClassLoader().getResource(fileName),
					StandardCharsets.UTF_8);
			Query query = QueryParser.parseQuery(queryString);
			logger.debug(query);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@ParameterizedTest
	@ValueSource(strings = {"queries/WhereTestQuery2.rq"})
	void testMultipleFilterQuery(String fileName) {
		String queryString;
		try {
			queryString = Resources.toString(getClass().getClassLoader().getResource(fileName),
					StandardCharsets.UTF_8);
			Query query = QueryParser.parseQuery(queryString);
			logger.debug(query);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@ParameterizedTest
	@ValueSource(strings = {"queries/WhereTestQuery2.rq",
			"queries/WhereTestQuery3.rq"
	})
	void testSelectExpExtraction(String fileName) {
		String queryString;
		try {
			queryString = Resources.toString(getClass().getClassLoader().getResource(fileName),
					StandardCharsets.UTF_8);
			QueryUtils.extractSelectVariables(QueryUtils.parseQuery(queryString));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
*/
}
