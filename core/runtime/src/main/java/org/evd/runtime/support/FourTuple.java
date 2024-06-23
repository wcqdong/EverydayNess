package org.evd.runtime.support;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 *
 * 4元组
 * @param <A>
 * @param <B>
 * @param <C>
 * @param <D>
 */
public class FourTuple<A, B, C, D> extends ThreeTuple<A, B, C> {
	public final D fourth;

	public FourTuple(A first, B second, C third, D fourth) {
		super(first, second, third);
		this.fourth = fourth;
	}

	public D getFourth() {
		return fourth;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof FourTuple)) {
			return false;
		}
		
		@SuppressWarnings("rawtypes")
		FourTuple castOther = (FourTuple) other;
		return new EqualsBuilder()
				.append(this.first, castOther.first)
				.append(this.second, castOther.second)
				.append(this.third, castOther.third)
				.append(this.fourth, castOther.fourth)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(first).append(second).append(third).append(fourth).toHashCode();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("(")
				.append(first)
				.append(", ")
				.append(second)
				.append(", ")
				.append(third)
				.append(", ")
				.append(fourth)
				.append(")")
				.toString();
	}
	
}
