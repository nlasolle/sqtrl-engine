package org.ahp.sqtrlengine;

import java.util.ArrayList;
import java.util.List;

import org.ahp.sqtrlengine.exception.QueryException;
import org.ahp.sqtrlengine.utils.QueryUtils;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.expr.E_IsNumeric;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprFunction;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.nodevalue.NodeValueInteger;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class FilteringRuleApplicationTest {
	private static final Logger logger = LogManager.getLogger(FilteringRuleApplicationTest.class);
	
	@Test
	void testNumericalExtension() throws QueryException {
		String queryString = "PREFIX ahpo: <http://e-hp.ahp-numerique.fr/ahpo#> \nSELECT ?l WHERE {?l ahpo:writingDate ?date . FILTER(YEAR(?d) >= 1885 && YEAR (?d) <= 1990)}";
		
		String leftFilter = "FILTER(?a >= YEAR(?d) && YEAR(?d) <= ?b)";
		String rightFilter = "FILTER(YEAR(?d) >= (?a - 2) && YEAR(?d) <= (?b + 2))";
		int extensionBound = 2;
		Query query = QueryUtils.parseQuery(queryString);
		
		List<ElementFilter> filters = QueryUtils.extractFilters(query);
		
		for( ElementFilter filter : filters ) {
			logger.info(filter); //FILTER ( ( year(?d) >= 1885 ) && ( year(?d) <= 1990 ) )
			logger.info(filter.getExpr().getFunction().getArgs().get(0).getFunction().getArgs().get(1));
			NodeValue value = new NodeValueInteger(1885 - extensionBound);
			
			List<Expr> exprs = new ArrayList<>(filter.getExpr().getFunction().getArgs().get(0).getFunction().getArgs());
			
			exprs.set(1, value);
			logger.info(exprs);
			
			//Update first bound
			//filter.getExpr().getFunction().getArgs().set;
			
			logger.info(filter);
		}
		

	}
}
