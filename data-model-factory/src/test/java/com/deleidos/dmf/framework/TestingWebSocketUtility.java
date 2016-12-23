package com.deleidos.dmf.framework;

import java.util.Map;
import java.util.PriorityQueue;

import org.apache.log4j.Logger;

import com.deleidos.dmf.exception.AnalyticsRuntimeException;
import com.deleidos.dmf.exception.JobQueueException;
import com.deleidos.dmf.progressbar.ProgressBarManager.ProgressBar;
import com.deleidos.dmf.web.SchemaWizardSessionUtility;
import com.deleidos.dmf.web.SessionData;

public class TestingWebSocketUtility extends SchemaWizardSessionUtility {
	private static final Logger logger = Logger.getLogger(TestingWebSocketUtility.class);
	private boolean hasOutputTestInfo;
	private int lastValue;
	private int numCalls;
	private boolean error;
	private Long totalMemory;
	private Long totalOverhead;

	public TestingWebSocketUtility() {
		this(-1L, -1L);
	}
	
	public TestingWebSocketUtility(Long totalMemory, Long totalOverhead) {
		hasOutputTestInfo = false;
		error = false;
		lastValue = 0;
		numCalls = 0;
		this.totalMemory = totalMemory;
		this.totalOverhead = totalOverhead;
		super.setPerformFakeUpdates(false);
	}
	
	@Override
	protected void updateSession(ProgressBar progressBar, SessionData sessionData) throws Exception {
		if (sessionData.getIsWebSocketOpen()) {
			super.updateSession(progressBar, sessionData);
		}
		numCalls++;
		if(!hasOutputTestInfo) {
			logger.debug("This output shows that a test call was made to update the progress bar.");
			hasOutputTestInfo = true;
		}
		int currentValue = progressBar.getNumerator();
		if(currentValue < lastValue) {
			error = true;
			logger.error("Progress bar did not monotonically increase!");
		} else if(currentValue > 1) {
			logger.error("Progress over 1!");
		}
		if (numCalls % 100 == 0) {
			logger.debug("After " + numCalls + " calls to the fake progress bar, update sent as "
					+ progressBar.getNumerator() + "/" + progressBar.getDenominator());
		}
	}
	
	@Override
	public Boolean isCancelled(String sessionId) {
		return false;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}
	
	@Override
	public boolean requestAnalysis(String sessionId, Long sampleFileLength, Long checkFrequency) {
		if (this.totalMemory < 0 || this.totalOverhead < 0) {
			return true;
		}
		return super.requestAnalysis(sessionId, sampleFileLength, checkFrequency);
	}
	
	@Override
	protected PriorityQueue<JobQueueEntry> waitForSessionToReachNextInQueue(PriorityQueue<JobQueueEntry> jobQueue,
			Map<String, SessionData> sessionDataMapping, String sessionId, Long minimalMemoryRequirement,
			Long insertionTime, Long checkFrequency) throws JobQueueException {
		boolean sendUpdates = true;
		int previousActiveJobs = -1;
		while (!jobQueue.peek().getSessionId().equals(sessionId)) {
			if (sendUpdates) {
				int activeJobs = sessionDataMapping.size();
				if (activeJobs != previousActiveJobs) {
					// there's a change -- need to update the user on status
					StringBuilder progressUpdate = new StringBuilder("Your analysis has been queued.");
					if (activeJobs == 1) {
						progressUpdate.append("There is 1 job ahead of yours.");
					} else if (activeJobs > 1) {
						progressUpdate.append("There are ");
						progressUpdate.append(activeJobs);
						progressUpdate.append(" ahead of yours.");
					} else {
						throw new AnalyticsRuntimeException("Reached unexpected branch of code waiting for job queue.");
					}
					// don't change progress until analysis actually starts
					SessionData sessionData = sessionDataMapping.get(sessionId);
					int num = 0;
					ProgressBar queuedProgressBarUpdate = new ProgressBar(num, progressUpdate.toString());
					try {
						updateSession(queuedProgressBarUpdate, sessionData);
					} catch (Exception e) {
						logger.debug("Web socket communications failed.  Attempting to continue analysis.", e);
						sendUpdates = false;
					}
				}
			}
			if (System.currentTimeMillis() - insertionTime > MAX_WAIT_TIME_SECONDS*1000) {
				throw new JobQueueException("Analysis timed out while waiting in queue (waited for "
						+MAX_WAIT_TIME_SECONDS+" seconds).");
			}
			try {
				Thread.sleep(checkFrequency);
			} catch (InterruptedException e) {
				logger.error("Unexpected thread interrupt.", e);
			}
		}

		if(totalMemory < minimalMemoryRequirement) {
			throw new JobQueueException("File is too large to be processed with current JVM settings.");
		} else {
			Long memoryEstimate = waitForEnoughMemory(
					totalMemory, totalOverhead, insertionTime, minimalMemoryRequirement, checkFrequency);
			sessionDataMapping.get(sessionId).setMemoryEstimate(memoryEstimate);
			return jobQueue;
		}
	}
	
	@Override
	protected Long waitForEnoughMemory(Long totalAvailableMemory, Long overheadMemory, Long insertionTime, 
			Long minimalMemoryRequirement, Long checkFrequency) throws JobQueueException {
		long memoryUsageOverEstimate = (long)(minimalMemoryRequirement*OVER_ESTIMATE_MULTIPLIER);
		long freeMemory = totalAvailableMemory - overheadMemory - currentlyRequiredMemory();
		while(memoryUsageOverEstimate > freeMemory) {
			if(System.currentTimeMillis() - insertionTime > MAX_WAIT_TIME_SECONDS*1000) {
				throw new JobQueueException("Job timed out while waiting for resources.");
			} else if(sessionDataMapping.size() == 0) {
				// throw new FileUploadException("There is not enough space in the JVM for this file.");
				// let front end handle size limit - always attempt to process it
				logger.info("There is not enough space in the JVM to handle the estimate of this file.");
				logger.info("If this error message has started showing after long term use, "
						+ "there may be a resource leak!");
				logger.warn("Attempting to analyze despite excessive memory estimate.");
				break;
			}
			try {
				Thread.sleep(checkFrequency);
			} catch (InterruptedException e) {
				logger.error(e);
			}

			freeMemory = totalAvailableMemory - overheadMemory - currentlyRequiredMemory();
		}
		return memoryUsageOverEstimate;
	}
	
	public boolean isInQueue(String sessionId) {
		JobQueueEntry[] jobs = jobQueue.toArray(new JobQueueEntry[jobQueue.size()]);
		for (JobQueueEntry job : jobs) {
			if (job.getSessionId().equals(sessionId)) {
				return true;
			}
		}
		return false;
	}

}
