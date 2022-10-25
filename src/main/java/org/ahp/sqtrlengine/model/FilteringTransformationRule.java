package org.ahp.sqtrlengine.model;

import java.util.List;

import org.apache.jena.sparql.expr.Expr;

/**
 * This is a special type of transformation rule that deals with modification in FILTER clause
 * This implemantion is dependent of Jena API
 * @author Nicolas Lasolle
 *
 */
public class FilteringTransformationRule extends TransformationRule {

	private List<Expr> leftFilter;
	private List<Expr> rightFilter;
	private int extension;

	public FilteringTransformationRule(String iri, String label) {
		super(iri, label);
	}

	
	public List<Expr> getRightFilter() {
		return rightFilter;
	}

	public void setRightFilter(List<Expr> rightFilter) {
		this.rightFilter = rightFilter;
	}


	public List<Expr> getLeftFilter() {
		return leftFilter;
	}


	public void setLeftFilter(List<Expr> leftFilter) {
		this.leftFilter = leftFilter;
	}


	public int getExtension() {
		return extension;
	}


	public void setExtension(int extension) {
		this.extension = extension;
	}


	@Override
	public String toString() {
		return "FilteringTransformationRule [leftFilter=" + leftFilter + ", rightFilter=" + rightFilter + ", extension="
				+ extension + ", iri=" + iri + ", label=" + label + ", context=" + context + ", left=" + left
				+ ", right=" + right + ", cost=" + cost + ", exceptions=" + exceptions + ", explanation=" + explanation
				+ "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + extension;
		result = prime * result + ((leftFilter == null) ? 0 : leftFilter.hashCode());
		result = prime * result + ((rightFilter == null) ? 0 : rightFilter.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FilteringTransformationRule other = (FilteringTransformationRule) obj;
		if (extension != other.extension)
			return false;
		if (leftFilter == null) {
			if (other.leftFilter != null)
				return false;
		} else if (!leftFilter.equals(other.leftFilter))
			return false;
		if (rightFilter == null) {
			if (other.rightFilter != null)
				return false;
		} else if (!rightFilter.equals(other.rightFilter))
			return false;
		return true;
	}

	
}
