package com.deleidos.dp.profiler.api;

/**
 * Callback interface for handling profiling progress.
 * @author leegc
 *
 */
public interface ProfilingProgressUpdateHandler {
	public void handleProgressUpdate(long progress);
}
