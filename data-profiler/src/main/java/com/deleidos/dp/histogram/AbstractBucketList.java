package com.deleidos.dp.histogram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.deleidos.dp.accumulator.Accumulator;
import com.deleidos.dp.beans.Histogram;
import com.deleidos.dp.exceptions.MainTypeException;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.netty.handler.codec.UnsupportedMessageTypeException;

/**
 * Abstraction of a bucket list.  Subclasses must define how to add a value to the list and the ordered list.
 * @author leegc
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class AbstractBucketList implements Accumulator<Histogram, Object> {
	public static final int STRING_BUCKET_LENGTH_CUTOFF = 15;
	public static final int NUMBER_BUCKET_LENGTH_CUTOFF = 6;
	protected Histogram histogram;
	
	public Histogram finish() {
		Histogram histogram = new Histogram();
		List<String> longLabels =  new ArrayList<String>();
		List<String> labels = new ArrayList<String>();
		List<Integer> data = new ArrayList<Integer>();
		
		for(AbstractBucket bucket : getOrderedBuckets()) {
			String label = bucket.getLabel();
			if(bucket instanceof NumberBucket) {
				labels.add(NumberBucket.trimRawLabel(label, NUMBER_BUCKET_LENGTH_CUTOFF));
			} else if(bucket instanceof TermBucket) {
				labels.add(TermBucket.trimRawLabel(label, STRING_BUCKET_LENGTH_CUTOFF));
			} else {
				labels.add("".equals(bucket.getLabel()) ? AbstractBucket.EMPTY_STRING_INDICATOR : bucket.getLabel());
			}
			longLabels.add("".equals(label) ? AbstractBucket.EMPTY_STRING_INDICATOR : label);
			data.add(bucket.getCount().intValue());
		}
		
		histogram.setLabels(labels);
		histogram.setLongLabels(longLabels);
		histogram.setData(data);
		return histogram;
	}
	
	@Override
	public Histogram getState() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void accumulate(Object value) throws MainTypeException {
		putValue(value);
	}
	
	public abstract List<AbstractBucket> getOrderedBuckets();
	
	public abstract boolean putValue(Object object);

}
