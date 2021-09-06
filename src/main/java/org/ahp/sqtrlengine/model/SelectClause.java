package org.ahp.sqtrlengine.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

/**
 * Represent a SPARQL query select expression
 * @author Nicolas Lasolle
 *
 */
public class SelectClause {
	private List<String> vars; //A variable list (prefixed with $ or ? character).
	private List<String> expressions; //An expression list with can involve several where clause variables
	private boolean isDistinct; 
	private boolean isReduced;
	
	public SelectClause() {
		vars = new ArrayList<String>();
		expressions = new ArrayList<String>();
		isDistinct = false;
		isReduced = false;
	}
	
	public SelectClause(List<String> vars, List<String> expressions) {
		this.vars = vars;
		this.expressions = expressions;
		isDistinct = false;
		isReduced = false;
	}
	
	public List<String> getVars() {
		return vars;
	}
	public void setVars(List<String> vars) {
		this.vars = vars;
	}
	public List<String> getExpressions() {
		return expressions;
	}
	public void setExpressions(List<String> expressions) {
		this.expressions = expressions;
	}
	public boolean isDistinct() {
		return isDistinct;
	}
	public void setDistinct(boolean isDistinct) {
		this.isDistinct = isDistinct;
	}
	public boolean isReduced() {
		return isReduced;
	}
	public void setReduced(boolean isReduced) {
		this.isReduced = isReduced;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expressions == null) ? 0 : expressions.hashCode());
		result = prime * result + (isDistinct ? 1231 : 1237);
		result = prime * result + (isReduced ? 1231 : 1237);
		result = prime * result + ((vars == null) ? 0 : vars.hashCode());
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
		SelectClause other = (SelectClause) obj;
		if (expressions == null) {
			if (other.expressions != null)
				return false;
		} else if (!CollectionUtils.isEqualCollection(expressions,other.expressions))
			return false;
		if (isDistinct != other.isDistinct)
			return false;
		if (isReduced != other.isReduced)
			return false;
		if (vars == null) {
			if (other.vars != null)
				return false;
		} else if (!vars.equals(other.vars))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String result = "SELECT";
		
		if(isDistinct)
			result += " DISTINCT";
		
		if(isReduced)
			result += " REDUCED";
		
		for(String var : vars) {
			result += " " + var;
		}
		
		for(String expression : expressions) {
			result += " " + expression;
		}
		
		return result + "\n";
	}
	
	
	
}
