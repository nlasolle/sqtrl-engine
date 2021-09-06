package org.ahp.sqtrlengine.model;

/*
**
* SPARQL query WHERE clause
* @author Nicolas Lasolle
*
*/
public class WhereClause {

	private GraphPattern graphPattern;
	
	
	public GraphPattern getGraphPattern() {
		return graphPattern;
	}

	public void setGraphPattern(GraphPattern graphPattern) {
		this.graphPattern = graphPattern;
	}

	@Override
	public String toString() {
		return "WHERE {\n" 
			   + graphPattern 
			   + "\n}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((graphPattern == null) ? 0 : graphPattern.hashCode());
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
		WhereClause other = (WhereClause) obj;
		if (graphPattern == null) {
			if (other.graphPattern != null)
				return false;
		} else if (!graphPattern.equals(other.graphPattern))
			return false;
		return true;
	}

	
	
}
