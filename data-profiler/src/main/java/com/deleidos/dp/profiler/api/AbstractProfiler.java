package com.deleidos.dp.profiler.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.deleidos.dp.accumulator.Accumulator;
import com.deleidos.dp.exceptions.MainTypeException;
import com.deleidos.dp.profiler.BinaryProfilerRecord;

public abstract class AbstractProfiler<T> implements Profiler<T> {
	private static final Logger logger = Logger.getLogger(AbstractProfiler.class);
	protected int recordsLoaded;
	protected Map<String, Long> droppedValueMapping;

	public AbstractProfiler() {
		recordsLoaded = 0;
		droppedValueMapping = new HashMap<String, Long>();
	}
	
	/**
	 * Overload and handle BinaryRecord accumulation as a special case.
	 * @param binaryRecord
	 */
	public abstract void accumulateBinaryRecord(BinaryProfilerRecord binaryRecord);
	
	public abstract void accumulateRecord(ProfilerRecord record);
	
	@Override
	public final void accumulate(ProfilerRecord value) {
		if (value instanceof BinaryProfilerRecord) {
			accumulateBinaryRecord((BinaryProfilerRecord)value);
		} else {
			accumulateRecord(value);
		}
	}

	/**
	 * Convenience method to accumulate the normalized record.
	 * @param accumulator
	 * @param key
	 * @param values
	 */
	protected final void accumulateNormalizedValues(
			Accumulator.TypeInsensitivePresenceAwareAccumulator<?> accumulator,
			String key, List<Object> values) {
		if(values != null) {
			int i = 0;
			for(; i < values.size(); i++) {
				try {
					accumulator.accumulate(values.get(i), true);
					i++;
					break;
				} catch (MainTypeException e) {
					addDroppedValue(key);
				}
			}
			for(; i < values.size(); i++) {
				try {
					accumulator.accumulate(values.get(i), false);
				} catch (MainTypeException e) {
					addDroppedValue(key);
				}
			}
		}
	}

	public int getRecordsLoaded() {
		return recordsLoaded;
	}

	public void setRecordsLoaded(int recordsLoaded) {
		this.recordsLoaded = recordsLoaded;
	}

	public Long getDroppedValue(String key) {
		return droppedValueMapping.get(key);
	}

	public void addDroppedValue(String key) {
		Long val = droppedValueMapping.getOrDefault(key, 0L) + 1;
		droppedValueMapping.put(key, val);
	}

}
