package org.ahp.sqtrlengine.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;

/**
 * Query manipulation using Jena libs
 * @author lasolle1
 *
 */
public class QueryUtils {

	/**
	 * Extract triples patterns from a query
	 * @return
	 */
	public static List<Triple>  extractTriplesPath(String queryString) {
		final List<Triple> triples = new ArrayList<Triple>();

		Query query = QueryFactory.create(queryString);
		System.out.println("Prologue " + query.getQueryPattern());
		ElementWalker.walk(query.getQueryPattern(),
				new ElementVisitorBase() {

			@Override
			public void visit(ElementPathBlock el) {
				ListIterator<TriplePath> triplesIterator = el.getPattern().iterator();

				for( ;  triplesIterator.hasNext() ;) {
					TriplePath path = triplesIterator.next();
					System.out.println(path);
					triples.add(new Triple(path.getSubject(), 
							path.getPredicate(), 
							path.getObject()));
				}
			}});
	
		return triples;
	}
}
