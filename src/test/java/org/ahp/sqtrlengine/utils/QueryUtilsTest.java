package org.ahp.sqtrlengine.utils;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.ahp.sqtrlengine.exception.QueryException;
import org.ahp.sqtrlengine.model.Prefix;
import org.apache.jena.sparql.core.Var;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test the class dedicated to the manipulation of SPARQL queries as String and as objects
 * @author Nicolas Lasolle
 *
 */
class QueryUtilsTest {

	/**
	 * List of prefixes which are used for testing purposes
	 * @return a list of prefixes
	 */
	private static List<Prefix> getPrefixes(){
		List<Prefix> prefixes = new ArrayList<Prefix>();

		prefixes.add(new Prefix("http://www.w3.org/2000/01/rdf-schema#", "rdfs"));

		return prefixes;
	}

	private static Stream<Arguments> getQueries() {
		return Stream.of(Arguments.of(null, true),
				Arguments.of("", true),
				Arguments.of(" ", true),
				Arguments.of("Some text which is not SPARQL syntax friendly", true),
				Arguments.of("SELECT ?p WHERE {?s ?p ?o}", false), 
				Arguments.of("SELECT ?pWHERE{}", false)
				);
	}

	private static Stream<Arguments> getQueriesForVariablesExtraction() {
		return Stream.of(
				Arguments.of(null, 0, null, null, true),
				Arguments.of("", 0, null, null, true),
				Arguments.of(" ", 0, null, null, true),
				Arguments.of("SELECT ?p WHERE {?s ?p ?o}", 1, "?p", null, false), 
				Arguments.of("SELECT ?s ?p ?o WHERE{}", 3, "?s", "?p", false),
				Arguments.of("SELECT * WHERE{}", 0, null, null, false)
				);
	}

	private static Stream<Arguments> getGraphPatterns() {
		return Stream.of(
				Arguments.of(null, false),
				Arguments.of("", false),
				Arguments.of("  ", false),
				Arguments.of("\n", false),
				Arguments.of("Some text which is not SPARQL syntax friendly", false),
				Arguments.of("?s ?p ?o", true),
				Arguments.of("?s ?p <http://someiri>", true),
				Arguments.of("?s ?p \"some string\"", true),
				Arguments.of("?s ?p ?o . FILTER(?o = \"some string\")", true),
				Arguments.of("?s ?p ?o1 . ?s ?p ?o2", true),
				Arguments.of("?s?p?o1 . ?s ?p ?o2", true)
				);
	}

	@ParameterizedTest
	@MethodSource("getQueries")
	void testParseQuery(String query, boolean exception) {
		
		if(exception) {
			assertThrows(QueryException.class, () -> {
				QueryUtils.parseQuery(query);
			});
		} else {
			assertDoesNotThrow(() -> {
				assertNotNull(QueryUtils.parseQuery(query));
			});
		}

	}

	@ParameterizedTest
	@MethodSource("getQueriesForVariablesExtraction")
	void testExtractSelectVariables(String query, int size, String var1, String var2, boolean exception) {
		
		if(exception) {
			assertThrows(QueryException.class, () -> {
				QueryUtils.extractSelectVariables(QueryUtils.parseQuery(query));
			});
		} else {
			assertDoesNotThrow(() -> {
				List<Var> variables; variables = QueryUtils.extractSelectVariables(QueryUtils.parseQuery(query));
				assertEquals(size, variables.size());

				if(variables.size() >= 2) {
					assertEquals(var1, variables.get(0).toString());
					assertEquals(var2, variables.get(1).toString());
				} else if (variables.size() == 1) {
					assertEquals(var1, variables.get(0).toString());
				}
				
			});
		}
		
	}

	@ParameterizedTest
	@MethodSource("getGraphPatterns")
	void testIsGraphPatternValid(String pattern, boolean expected) {
		assertEquals(expected, QueryUtils.isGraphPatternValid(pattern, getPrefixes()));
	}

}
