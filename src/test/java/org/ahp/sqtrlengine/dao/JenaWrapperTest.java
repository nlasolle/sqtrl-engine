package org.ahp.sqtrlengine.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.ahp.sqtrlengine.exception.QueryException;
import org.apache.jena.query.ResultSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/*
 * Test the class dedicated to the execution of SPARQL queries over an RDF graph
 * @author Nicolas Lasolle
 *
 */
class JenaWrapperTest {

	private static final String SPARQL_ENDPOINT = "http://localhost:3030/full_ahp_corpus";
	private static JenaWrapper wrapper;

	@BeforeAll
	static void initializeConnection() {
		wrapper = new JenaWrapper(SPARQL_ENDPOINT);
	}


	private static Stream<Arguments> getQueriesForSelectExecution() {
		return Stream.of(
				Arguments.of(null, true, false),
				Arguments.of("", true, false),
				Arguments.of("SELECT ?p WHERE {?s ?p ?o} LIMIT 50", false, true),
				Arguments.of("SELECT ?p WHERE {?s ?p ?o} LIMIT 0", false, false)
				);
	}

	@Test
	void testGetCount() {
		int tripleCount;

		try {
			tripleCount = wrapper.getCount();
			assertNotNull(tripleCount);		
			assertTrue(tripleCount >= 0);
		} catch (QueryException e) {
			e.printStackTrace();
		}

	}

	@ParameterizedTest
	@MethodSource("getQueriesForSelectExecution")
	void executeSelectQuery(String query, boolean exception, boolean expected) {

		if(exception) {
			assertThrows(QueryException.class, () -> {
				wrapper.executeSelectQuery(query);
			});
		} else {
			assertDoesNotThrow(() ->{
				ResultSet results = wrapper.executeSelectQuery(query);
				assertEquals(results.hasNext(), expected);
			});
		}
	}
	
}