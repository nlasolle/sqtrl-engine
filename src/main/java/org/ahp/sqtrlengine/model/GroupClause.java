package org.ahp.sqtrlengine.model;

/**
 * SPARQL query GROUP BY clause
 * @author Nicolas Lasolle
 *
 */
public class GroupClause {
	public String groupCondition;

	public String getGroupCondition() {
		return groupCondition;
	}

	public void setGroupCondition(String groupCondition) {
		this.groupCondition = groupCondition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupCondition == null) ? 0 : groupCondition.hashCode());
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
		GroupClause other = (GroupClause) obj;
		if (groupCondition == null) {
			if (other.groupCondition != null)
				return false;
		} else if (!groupCondition.equals(other.groupCondition))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GROUP BY " + groupCondition;
	}


}
