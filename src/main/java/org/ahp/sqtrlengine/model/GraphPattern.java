package org.ahp.sqtrlengine.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

/**
 * SPARQL query graph pattern
 * @author Nicolas Lasolle
 *
 */
public class GraphPattern {
	private List<TriplePattern> triplePatterns = new ArrayList<TriplePattern>();
	private List<Filter> filters = new ArrayList<Filter>();
	private List<Optional> optionalBlocks = new ArrayList<Optional>();
	
	public List<TriplePattern> getTriplePatterns() {
		return triplePatterns;
	}
	
	public void setTriplePatterns(List<TriplePattern> triplePatterns) {
		this.triplePatterns = triplePatterns;
	}
	
	public List<Filter> getFilters() {
		return filters;
	}
	
	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}
	
	public List<Optional> getOptionalBlocks() {
		return optionalBlocks;
	}
	
	public void setOptionalBlocks(List<Optional> optionalBlocks) {
		this.optionalBlocks = optionalBlocks;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filters == null) ? 0 : filters.hashCode());
		result = prime * result + ((optionalBlocks == null) ? 0 : optionalBlocks.hashCode());
		result = prime * result + ((triplePatterns == null) ? 0 : triplePatterns.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphPattern other = (GraphPattern) obj;
		if (filters == null) {
			if (other.filters != null)
				return false;
		} else if (!CollectionUtils.isEqualCollection(filters, other.filters))
			return false;
		if (optionalBlocks == null) {
			if (other.optionalBlocks != null)
				return false;
		} else if (!CollectionUtils.isEqualCollection(optionalBlocks, other.optionalBlocks))
			return false;
		if (triplePatterns == null) {
			if (other.triplePatterns != null)
				return false;
		} else if (!CollectionUtils.isEqualCollection(triplePatterns, other.triplePatterns))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String result = "";
		
		for (TriplePattern triplePattern : triplePatterns) {
			result += "\t" + triplePattern + " .\n";
		}
		
		for (Optional optional : optionalBlocks) {
			result += optional + "\n";
		}
		
		for(Filter filter : filters) {
			result += filter + "\n";
		}
		
		return result;
	}
	
	
	
}
