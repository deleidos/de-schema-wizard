package com.deleidos.dp.accumulator;

import com.deleidos.dp.exceptions.MainTypeException;

/**
 * Interface for accumulators that take in fields with the ultimate goal of calculating metrics 
 * @author leegc
 *
 * @param <T> The type that will be affected by the accumulation.
 * @param <V> The values that will affect T
 */
public interface Accumulator<T, V> {

	/**
	 * Accumulate data into the Accumulator's metrics.  This default method accumulates the presence without any special
	 * handling.
	 * @param value an instance of type V
	 * @param <V> The type that will be accumulated.
	 * @throws MainTypeException thrown if the given value is not able to be accumulated
	 */
	public void accumulate(V value) throws MainTypeException;
	
	/**
	 * Optional method.  Get a copy of the current state of T.
	 * There is no guarantee that fields will be non-null or accurate.
	 * @return A copy of the metric and all of its calculated fields.
	 * @param <T> The type that will be affected by the accumulation.
	 * @throws UnsupportedOperationException if the state cannot be retrieved interactively
	 */
	public T getState();
	
	/**
	 * Clean up and perform all final calculations of fields.
	 * 
	 * @param <T> The type that will be affected by the accumulation.
	 */
	public T finish();
	
	/**
	 * Interface with a method that allows a call to accumulate presence.  A presence aware
	 * accumulator should internally track the presence of a given field.  
	 * @author leegc
	 *
	 * @param <T> The type that will be affected by the accumulation.
	 * @param <V> The type of values that will affect T
	 */
	public interface PresenceAwareAccumulator<T, V> extends Accumulator<T, V> {
		public void accumulate(V value, Boolean accumulatePresence) throws MainTypeException;
		
		@Override
		default void accumulate(V value) throws MainTypeException {
			accumulate(value, true);
		}
	}
	
	/**
	 * Interface that specifically accumulates objects.  This is another layer of abstraction
	 * that allows an implementation to handle any and all type checking internally.
	 * @author leegc
	 *
	 * @param <T> The type that will be affected by the accumulation.
	 */
	public interface TypeInsensitiveAccumulator<T> extends Accumulator<T, Object> {
		
	}
	
	/**
	 * Interface that specifically accumulates objects and is presence aware.  Identifying 
	 * an implementation of this type is useful because we can call the presence aware
	 * accumulate method and know that the implementation will handle the type.
	 * @author leegc
	 *
	 * @param <T> The type that will be affected by the accumulation.
	 */
	public interface TypeInsensitivePresenceAwareAccumulator<T> extends PresenceAwareAccumulator<T, Object> {	
	
	}
}
