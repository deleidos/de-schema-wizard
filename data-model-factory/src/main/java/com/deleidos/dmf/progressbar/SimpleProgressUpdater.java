package com.deleidos.dmf.progressbar;

import java.util.Optional;

import org.apache.log4j.Logger;

import com.deleidos.dmf.web.SchemaWizardSessionUtility;
import com.deleidos.dp.profiler.api.ProfilingProgressUpdateHandler;

/**
 * Give this class the session, the progress bar manager, and the total number of expected records, and this class
 * will handle the rest of the progress updates.  The actual numeric update for the progress bar is calculated by
 * dividing the progress update by the total number of expected records
 * multiplying that by the current {@link ProgressState}'s range,
 * and reporting that value to the {@link ProgressBarManager} to see if an update is appropriate.
 * @author leegc
 *
 */
public class SimpleProgressUpdater implements ProfilingProgressUpdateHandler {
	private static final Logger logger = Logger.getLogger(SimpleProgressUpdater.class);
	private final ProgressBarManager progressBar;
	private final long totalRecords;
	private final String sessionId;
	private Optional<DescriptionCallback> descriptionCallback = Optional.empty();
	
	public SimpleProgressUpdater(String sessionId, ProgressBarManager progressBar, long recordCount) {
		this(sessionId, progressBar, recordCount, true);
	}
	
	public SimpleProgressUpdater(String sessionId, ProgressBarManager progressBar, long recordCount, boolean smoothUpdates) {
		this.progressBar = progressBar;
		this.totalRecords = recordCount;
		this.sessionId = sessionId;
	}

	@Override
	public void handleProgressUpdate(long progress) {
		float percentCompleted = (float) progress / (float) totalRecords;
		int numeratorUpdate = 
				(int)progressBar.getCurrentState().getStartValue() + 
				(int)((percentCompleted * progressBar.getCurrentState().rangeLength()));
		if(numeratorUpdate != 0) {
			if(progressBar.updateNumerator(numeratorUpdate)) {
				descriptionCallback.ifPresent(x->progressBar.getCurrentState().setDescription(x.determineDescription(this, progress)));
			}
		}
		SchemaWizardSessionUtility.getInstance().updateProgress(progressBar, sessionId);
		
	}

	public ProgressBarManager getProgressBar() {
		return progressBar;
	}

	public long getTotalRecords() {
		return totalRecords;
	}

	public interface DescriptionCallback {
		public String determineDescription(SimpleProgressUpdater progressUpdater, long progress);
	}

	public Optional<DescriptionCallback> getDescriptionCallback() {
		return descriptionCallback;
	}

	public void setDescriptionCallback(DescriptionCallback descriptionCallback) {
		this.descriptionCallback = Optional.of(descriptionCallback);
	}
	
	public void removeDescriptionCallback() {
		this.descriptionCallback = Optional.empty();
	}
}
