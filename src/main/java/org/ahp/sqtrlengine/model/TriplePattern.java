package org.ahp.sqtrlengine.model;

/**
 * SPARQL triple pattern
 * @author Nicolas Lasolle
 *
 */
public class TriplePattern {

	private Term subject;
	private Term predicate;
	private Term object;
	
	public TriplePattern(Term subject, Term predicate, Term object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}
	
	public TriplePattern() {
		
	}

	public Term getSubject() {
		return subject;
	}
	public void setSubject(Term subject) {
		this.subject = subject;
	}
	public Term getPredicate() {
		return predicate;
	}
	public void setPredicate(Term predicate) {
		this.predicate = predicate;
	}
	public Term getObject() {
		return object;
	}
	public void setObject(Term object) {
		this.object = object;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
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
		TriplePattern other = (TriplePattern) obj;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return subject + " " + predicate + " " + object;
	}
	
	
}
