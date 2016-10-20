package com.deleidos.dp.profiler.api;

import com.deleidos.dp.accumulator.Accumulator;
import com.deleidos.dp.exceptions.MainTypeException;

/**
 * A Profiler implementation is an Accumulator that specifically accumulates ProfilerRecords.
 * The ProfilerRecord is the abstraction of a "record" that brings all parsers to a common
 *  {@link com.deleidos.dp.profiler.api.ProfilerRecord} data type.
 * 
 * @author leegc
 *
 */
public interface Profiler<T> extends Accumulator<T, ProfilerRecord> {
	
	/**
	 * By default, throw an UnsupportedOperationException for Profiler classes.
	 */
	@Override
	default T getState() {
		throw new UnsupportedOperationException("getState() method not supported -- profiler must be finished to get state.");
	}
	
	/**
	 * For profilers, the main type exception should be handled rather than thrown.
	 */
	@Override
	public void accumulate(ProfilerRecord value);
	
}
