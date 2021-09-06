package org.ahp.sqtrlengine.model;

/**
 * SPARQL query Java representation.
 * This representation relies on the use of SPARQL 1.0 and 1.1 elements
 * It does not include property paths
 * @author Nicolas Lasolle
 *
 */
public class Query {
	private Prologue prologue;
	
	private SelectClause selectClause;
	
	private WhereClause whereClause;
	
	private SolutionModifier solutionModifier;

	public Prologue getPrologue() {
		return prologue;
	}

	public void setPrologue(Prologue prologue) {
		this.prologue = prologue;
	}

	public SelectClause getSelectClause() {
		return selectClause;
	}

	public void setSelectClause(SelectClause selectClause) {
		this.selectClause = selectClause;
	}

	public WhereClause getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(WhereClause whereClause) {
		this.whereClause = whereClause;
	}

	public SolutionModifier getSolutionModifier() {
		return solutionModifier;
	}

	public void setSolutionModifier(SolutionModifier solutionModifier) {
		this.solutionModifier = solutionModifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((prologue == null) ? 0 : prologue.hashCode());
		result = prime * result + ((selectClause == null) ? 0 : selectClause.hashCode());
		result = prime * result + ((solutionModifier == null) ? 0 : solutionModifier.hashCode());
		result = prime * result + ((whereClause == null) ? 0 : whereClause.hashCode());
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
		Query other = (Query) obj;
		if (prologue == null) {
			if (other.prologue != null)
				return false;
		} else if (!prologue.equals(other.prologue))
			return false;
		if (selectClause == null) {
			if (other.selectClause != null)
				return false;
		} else if (!selectClause.equals(other.selectClause))
			return false;
		if (solutionModifier == null) {
			if (other.solutionModifier != null)
				return false;
		} else if (!solutionModifier.equals(other.solutionModifier))
			return false;
		if (whereClause == null) {
			if (other.whereClause != null)
				return false;
		} else if (!whereClause.equals(other.whereClause))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		String result = "";
		
		if(prologue != null) {
			result += prologue + "\n";
		}
		
		result += selectClause + "\n"
			    +  whereClause + "\n";
		
		if(solutionModifier != null) {
			result += solutionModifier;
		}
		
		return result;
	}
	
}
