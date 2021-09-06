package org.ahp.sqtrlengine.model;

/*
**
* SPARQL query limit clause
* @author Nicolas Lasolle
*
*/
public class LimitClause {
	private int limit;
	
	public LimitClause(int limit) {
		this.setLimit(limit);
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	@Override
	public String toString() {
		return "LIMIT " + limit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + limit;
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
		
		LimitClause other = (LimitClause) obj;
		if (limit != other.limit)
			return false;
		
		return true;
	}
	
}
