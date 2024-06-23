package org.evd.runtime.support;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 
 *
 * 2元组
 * @param <A>
 * @param <B>
 */
public class TwoTuple<A, B> {
	public final A first;
	public final B second;
	
	public TwoTuple(A first, B second) {
		super();
		this.first = first;
		this.second = second;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof TwoTuple)) {
			return false;
		}
		
		@SuppressWarnings("rawtypes")
		TwoTuple castOther = (TwoTuple) other;
		return new EqualsBuilder()
				.append(this.first, castOther.first)
				.append(this.second, castOther.second)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(first).append(second).toHashCode();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("(")
				.append(first)
				.append(", ")
				.append(second)
				.append(")")
				.toString();
	}

}
