package org.evd.game.runtime.support;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 *
 * 3元组
 * @param <A>
 * @param <B>
 * @param <C>
 */
public class ThreeTuple<A, B, C> extends TwoTuple<A, B> {
	public final C third;

	public ThreeTuple(A first, B second, C third) {
		super(first, second);
		this.third = third;
	}
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof ThreeTuple)) {
			return false;
		}
		
		@SuppressWarnings("rawtypes")
		ThreeTuple castOther = (ThreeTuple) other;
		return new EqualsBuilder()
				.append(this.first, castOther.first)
				.append(this.second, castOther.second)
				.append(this.third, castOther.third)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(first).append(second).append(third).toHashCode();
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
				.append(")")
				.toString();
	}

}
