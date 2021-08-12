package org.ahp.sqtrlengine.model;

/**
 * SPARQL query solution modifier clause reprentation. 
 * All items are optional in the SPARQL grammar
 * @author Nicolas Lasolle
 *
 */
public class SolutionModifier {
	private GroupClause groupClause;
	private HavingClause havingClause;
	private OrderClause orderClause;
	private LimitClause limitClause;
	
	public GroupClause getGroupClause() {
		return groupClause;
	}
	public void setGroupClause(GroupClause groupClause) {
		this.groupClause = groupClause;
	}
	public HavingClause getHavingClause() {
		return havingClause;
	}
	public void setHavingClause(HavingClause havingClause) {
		this.havingClause = havingClause;
	}
	public OrderClause getOrderClause() {
		return orderClause;
	}
	public void setOrderClause(OrderClause orderClause) {
		this.orderClause = orderClause;
	}
	public LimitClause getLimitClause() {
		return limitClause;
	}
	public void setLimitClause(LimitClause limitClause) {
		this.limitClause = limitClause;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupClause == null) ? 0 : groupClause.hashCode());
		result = prime * result + ((havingClause == null) ? 0 : havingClause.hashCode());
		result = prime * result + ((limitClause == null) ? 0 : limitClause.hashCode());
		result = prime * result + ((orderClause == null) ? 0 : orderClause.hashCode());
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
		SolutionModifier other = (SolutionModifier) obj;
		if (groupClause == null) {
			if (other.groupClause != null)
				return false;
		} else if (!groupClause.equals(other.groupClause))
			return false;
		if (havingClause == null) {
			if (other.havingClause != null)
				return false;
		} else if (!havingClause.equals(other.havingClause))
			return false;
		if (limitClause == null) {
			if (other.limitClause != null)
				return false;
		} else if (!limitClause.equals(other.limitClause))
			return false;
		if (orderClause == null) {
			if (other.orderClause != null)
				return false;
		} else if (!orderClause.equals(other.orderClause))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return groupClause + "\n"
			 + havingClause + "\n"
			 + orderClause + "\n"
			 + limitClause;
	}
	
	
}
