package com.deleidos.dp.histogram;

import java.math.BigInteger;

import com.deleidos.dp.profiler.DefaultProfilerRecord;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstraction of buckets.  Initialize and increment a counter.
 * @author leegc
 * @param <T>
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class AbstractBucket implements Comparable<AbstractBucket> {
	public static final String EMPTY_STRING_INDICATOR = DefaultProfilerRecord.EMPTY_FIELD_VALUE_INDICATOR;
	protected BigInteger count;
	
	protected AbstractBucket() {
		count = BigInteger.ZERO;
	}
	
	public AbstractBucket(BigInteger count) {
		this.count = count;
	}

	public BigInteger getCount() {
		return count;
	}
	
	public void incrementCount() {
		count = count.add(BigInteger.ONE);
	}

	/**
	 * Determine where the object belongs.
	 * @param object The provided object
	 * @return -1 if the object should go before the bucket.
	 * 1 if the object should go after the bucket.
	 * 0 if the object belongs in the bucket.
	 */
	public abstract int belongs(Object object);
	
	/**
	 * Get the label that defines the column of the histogram.
	 * @return A string representation of the label.
	 */
	public abstract String getLabel();
}
