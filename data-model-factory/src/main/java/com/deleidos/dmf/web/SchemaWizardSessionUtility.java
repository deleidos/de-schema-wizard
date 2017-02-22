package com.deleidos.dmf.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;

import com.deleidos.analytics.websocket.WebSocketServer;
import com.deleidos.analytics.websocket.api.WebSocketApiPlugin;
import com.deleidos.analytics.websocket.api.WebSocketEventListener;
import com.deleidos.analytics.websocket.api.WebSocketMessage;
import com.deleidos.analytics.websocket.api.WebSocketMessageFactory;
import com.deleidos.dmf.exception.AnalyticsCancelledWorkflowException;
import com.deleidos.dmf.exception.AnalyticsRuntimeException;
import com.deleidos.dmf.exception.JobQueueException;
import com.deleidos.dmf.progressbar.ProgressBarManager;
import com.deleidos.dmf.progressbar.ProgressBarManager.ProgressBar;
import com.deleidos.dp.deserializors.SerializationUtility;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Manage backend resources by keeping track of session data.  Session data is currently based on active 
 * websocket connections.  So, this class is only active/important during sample/schema analysis time.
 * @author leegc
 *
 */
public class SchemaWizardSessionUtility implements WebSocketApiPlugin, WebSocketMessageFactory, WebSocketEventListener {
	private static SchemaWizardSessionUtility INSTANCE = null;
	private static final Logger logger = Logger.getLogger(SchemaWizardSessionUtility.class);
	protected static final int MAX_QUEUE_SIZE = 10;
	protected static final long MAX_WAIT_TIME_SECONDS = 1200; // wait ten minutes max 
	private static final Long DEFAULT_CHECK_FREQUENCY = 1000L;
	protected final Map<String, String> socketToSessionMapping;
	protected final Map<String, SessionData> sessionDataMapping;
	protected final PriorityQueue<JobQueueEntry> jobQueue;
	protected final ExecutorService executorService;
	protected long overheadEstimate;
	protected long updateFrequencyMillis = 100;
	protected long fakeUpdateDelay = 105;
	protected int noticeableProgressJump = 5;
	protected int minFakeUpdates = 10;
	private boolean performFakeUpdates = true;
	public final String OVER_ESTIMATE_ENV_VAR = "OVER_ESTIMATE_MULTIPLIER";
	public static double OVER_ESTIMATE_MULTIPLIER;

	protected SchemaWizardSessionUtility() {
		try {
			OVER_ESTIMATE_MULTIPLIER = (System.getenv(OVER_ESTIMATE_ENV_VAR)) != null ? Double.valueOf(System.getenv(OVER_ESTIMATE_ENV_VAR)) : 3;
		} catch (Exception e) {
			OVER_ESTIMATE_MULTIPLIER = 3;
		}
		executorService = Executors.newCachedThreadPool();
		socketToSessionMapping = new HashMap<String, String>();
		sessionDataMapping = new ConcurrentHashMap<String, SessionData>();
		overheadEstimate = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		jobQueue = new PriorityQueue<JobQueueEntry>(MAX_QUEUE_SIZE);
	}

	public static SchemaWizardSessionUtility getInstance(SchemaWizardSessionUtility testUtility) {
		if(INSTANCE == null) {
			INSTANCE = testUtility;
			logger.info("Registering plugin with web socket server.");
		}
		return INSTANCE;
	}

	public static SchemaWizardSessionUtility getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new SchemaWizardSessionUtility();
			logger.info("Registering plugin with web socket server.");
			WebSocketServer.getInstance().registerPlugin(INSTANCE);
		}
		return INSTANCE;
	}

	/**
	 * Update progress if the socket/session mapping contains the session id.
	 * @param updateBean
	 * @param sessionId
	 */
	public synchronized void updateProgress(ProgressBarManager updater, String sessionId) {
		if (sessionDataMapping.containsKey(sessionId)) {
			SessionData sessionData = sessionDataMapping.get(sessionId);
			try {
				if(sessionData.isWebSocketOpen() && sessionData.shouldUpdate()) {
					updateSession(updater, sessionData);
				} 
			} catch (Exception e) {
				logger.debug("Progress update failed to send to session " + sessionId + ".", e);
				sessionData.setSendingErrors(sessionData.getSendingErrors()+1);
				if(sessionData.getSendingErrors() >= SessionData.ERROR_CUTOFF) {
					logger.error(sessionData.getSendingErrors() + " errors have been caught for session " 
							+ sessionId + ".  Attempting to send on last message "
							+ "and then disabling progress updates.", e);
					try {
						updater.getCurrentState().setDescription("Sorry, there was a problem gauging the progress of your analysis.");
						updateSession(updater, sessionData);
					} catch (Exception e1) {
						logger.error("Exception sending message over websocket.", e);
					}
				}
			}
		}
	}

	private void updateSession(ProgressBarManager progressBarManager, SessionData sessionData) throws Exception {
		ProgressBar updateBean = progressBarManager.asBean();
		// if the progress jump is large, send a few intermediary updates to smooth it out
		smoothUpdate(progressBarManager, sessionData);
		updateSession(updateBean, sessionData);
		sessionData.setLastUpdateNumerator(progressBarManager.getNumerator());
		sessionData.setLastUpdateTime(System.currentTimeMillis());
	}

	protected void updateSession(ProgressBar progressBar, SessionData sessionData) throws Exception {
		WebSocketServer.getInstance().send(progressBar, sessionData.getWebSocketId());
	}
	
	private void smoothUpdate(ProgressBarManager progressUpdater, SessionData sessionData) throws Exception {
		ProgressBar progressBar = progressUpdater.asBean();
		float previousUpdate = sessionData.getLastUpdateNumerator();
		float updateDif = progressUpdater.getNumerator() - previousUpdate;
		// need to lock the progress bar so the websocket doesnt get closed while these updates are happening
		synchronized(progressBar) {
			if(updateDif >= noticeableProgressJump && performFakeUpdates) {
				int fakeUpdates = (int)(updateDif / minFakeUpdates);
				fakeUpdates = (fakeUpdates < minFakeUpdates) ? minFakeUpdates : fakeUpdates;
				final float fakeUpdateProgressInterval = updateDif/fakeUpdates;
				boolean interrupted = false;
				for (int i = 0; i < fakeUpdates && !interrupted; i++) {
					float numerator = previousUpdate + (fakeUpdateProgressInterval * i);
					String description = progressUpdater.getStateByNumerator(numerator).getDescription();
					ProgressBar ithUpdateProgressBar = new ProgressBar((int)numerator, description);
					updateSession(ithUpdateProgressBar, sessionData);
					try {
						Thread.sleep(fakeUpdateDelay);
					} catch (InterruptedException e) {
						logger.error("Smooth updater interrupted.  Sending raw update");
						interrupted = true;
					}
				}
			}
		}
	}

	@Override
	public List<WebSocketEventListener> getWebSocketEventListeners() {
		return Arrays.asList(this);
	}

	@Override
	public List<WebSocketMessageFactory> getWebSocketMessageFactories() {
		return Arrays.asList(this);
	}

	@Override
	public List<String> getResourcePackages() {
		// unnecessary for now
		// add for service layer accessor
		return null;
	}

	@Override
	public WebSocketMessage buildMessage(String message, String webSocketId) {
		try {
			JsonNode j = SerializationUtility.getObjectMapper().readTree(message);
			JsonNode sessionId = j.path("sessionId");
			if(sessionId != null) {
				String sessionIdString = sessionId.asText(null);
				if(sessionIdString == null) {
					logger.error("Session Id received from client as null.  Progress bar not successfully initialized.");
				} else {
					socketToSessionMapping.put(webSocketId, sessionIdString);
					if (!sessionDataMapping.containsKey(sessionIdString)) {
						logger.debug("Added session " + sessionIdString + " to sessiond data mapping.");
						SessionData sessionData = new SessionData();
						sessionData.setIsWebSocketOpen(true);
						sessionData.setWebSocketId(webSocketId);
						sessionDataMapping.put(sessionIdString, sessionData);
					} else {
						logger.debug("Associated session " + sessionIdString + " with websocket " + webSocketId + ".");
						sessionDataMapping.get(sessionIdString).setIsWebSocketOpen(true);
						sessionDataMapping.get(sessionIdString).setWebSocketId(webSocketId);
					}
				}

			} else {
				logger.warn("Received unexpected message: " + message);
			}
		} catch (IOException e) {
			logger.error("Received non parseable message: " + message, e);
		}

		// returning null is fine
		return null; 
	}

	@Override
	public void onWebSocketClose(String webSocketId) {
		if (socketToSessionMapping.containsKey(webSocketId)) {
			String associatedSessionId = socketToSessionMapping.get(webSocketId);
			sessionDataMapping.get(associatedSessionId).setIsWebSocketOpen(false);
			logger.debug("Socket " + webSocketId + " with associated session " + associatedSessionId + " closed.");
		} else {
			logger.warn("SockerId " + webSocketId + " was not associated as a session.");
		}
	}

	@Override
	public void onWebSocketConnect(String webSocketId) {
		logger.debug("Socket " + webSocketId + " opened.  Awaiting session ID.");
	}

	public Boolean isCancelled(String sessionId) {
		if (!sessionDataMapping.containsKey(sessionId)) {
			throw new AnalyticsRuntimeException("Session should not be removed from data mapping until it is"
					+ " recognized as cancelled or completed.");
		}
		return sessionDataMapping.get(sessionId).isCancelled();
	}

	/**
	 * Debugging code
	public long userSetAvailableMemory = -1;
	public long userSetOverhead = -1;
	public boolean requireGoAhead = false;
	public boolean goAhead = false;
	
	public void setAvailableMemory(long available) {
		userSetAvailableMemory = available;
	}
	
	public long getAvailableMemory() {
		return userSetAvailableMemory;
	}
	
	public long getUserSetOverhead() {
		return userSetOverhead;
	}

	public void setUserSetOverhead(long userSetOverhead) {
		this.userSetOverhead = userSetOverhead;
	}

	public boolean isRequireGoAhead() {
		return requireGoAhead;
	}

	public void setRequireGoAhead(boolean requireGoAhead) {
		this.requireGoAhead = requireGoAhead;
	}

	public void setGoAhead() {
		this.goAhead = true;
	}
	 */

	protected PriorityQueue<JobQueueEntry> waitForSessionToReachNextInQueue(PriorityQueue<JobQueueEntry> jobQueue,
			Map<String, SessionData> sessionDataMapping, String sessionId, Long minimalMemoryRequirement,
			Long insertionTime, Long checkFrequency) throws JobQueueException {
		boolean sendUpdates = true;
		int previousActiveJobs = -1;
		//boolean requireGoAhead = this.requireGoAhead;
		while (!jobQueue.peek().getSessionId().equals(sessionId)/* || requireGoAhead*/) {
			/*if (requireGoAhead) synchronized (this) {
				if (goAhead && jobQueue.peek().getSessionId().equals(sessionId)) {
					goAhead = false;
					requireGoAhead = false;
				}
			}*/
			
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
					int num = sessionData.getLastUpdateNumerator().intValue();
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

		/* debug version
		 * Long totalMemory = userSetAvailableMemory > -1 ? userSetAvailableMemory : Runtime.getRuntime().totalMemory();
		Long overHead = userSetOverhead > -1 ? userSetOverhead : overheadEstimate; 
		
		 */
		
		// now that this job is next, wait for appropriate resources to be available before taking
		// it off the queue
		// note docs say this total memory may vary over time
		Long totalMemory = Runtime.getRuntime().totalMemory();
		if(totalMemory < minimalMemoryRequirement) {
			throw new JobQueueException("File is too large to be processed with current JVM settings.");
		} else {
			Long memoryEstimate = waitForEnoughMemory(
					totalMemory, overheadEstimate, insertionTime, minimalMemoryRequirement, checkFrequency);
			sessionDataMapping.get(sessionId).setMemoryEstimate(memoryEstimate);
			return jobQueue;
		}
	}

	protected Long waitForEnoughMemory(Long totalAvailableMemory, Long overheadMemory, Long insertionTime, 
			Long minimalMemoryRequirement, Long checkFrequency) throws JobQueueException {
		long memoryUsageOverEstimate = (long)(minimalMemoryRequirement*OVER_ESTIMATE_MULTIPLIER);
		long freeMemory = totalAvailableMemory - overheadMemory - currentlyRequiredMemory();
		while(memoryUsageOverEstimate > freeMemory) {
			logger.debug("Free memory - " + freeMemory + " < " + memoryUsageOverEstimate);

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
	
	/**
	 * Should be called when a batch of samples or a schema analysis is started.
	 * @param sessionId
	 * @param sampleFile
	 * @return
	 * @throws FileUploadException
	 */
	public boolean requestAnalysis(String sessionId, File sampleFile) {
		return requestAnalysis(sessionId, sampleFile.length(), DEFAULT_CHECK_FREQUENCY);
	}

	protected boolean requestAnalysis(String sessionId, Long sampleFileLength, Long checkFrequency) {
		if (!sessionDataMapping.containsKey(sessionId)) {
			SessionData sessionData = new SessionData();
			sessionData.setIsWebSocketOpen(false);
			sessionDataMapping.put(sessionId, sessionData);
			logger.info("Session " + sessionId + " is not in the session data mapping.  Creating without a websocket.");
		} else {
			logger.debug("Session " + sessionId + " is in the session data mapping.");
		}
		if (jobQueue.size() > MAX_QUEUE_SIZE) {
			return false;
		} 
		Long insertTime = System.currentTimeMillis();
		jobQueue.offer(new JobQueueEntry(sessionId, insertTime));

		try {
			// hold the queue until this particular session is next
			JobQueueEntry jobQueueEntry = waitForSessionToReachNextInQueue(
					jobQueue, sessionDataMapping, sessionId, sampleFileLength, insertTime, checkFrequency).poll();
			logger.debug("Ready to analyze for session " + jobQueueEntry.getSessionId() + ".");

			return true;
		} catch (JobQueueException e) {
			logger.error("Error in the queue.", e);
			return false;
		}
	}

	/**
	 * Should be called at the completion (regardless of success) of any analysis.
	 * @param sessionId
	 * @throws AnalyticsCancelledWorkflowException if the session is determined to be cancelled. This should
	 * prevent a response from being sent to the client.
	 */
	public synchronized void registerCompleteAnalysis(String sessionId) throws AnalyticsCancelledWorkflowException {
		boolean isCancelled = isCancelled(sessionId); // need to check cancel status before removing from mappings
		sessionDataMapping.remove(sessionId);
		logger.info("Session " + sessionId + " registered as completed.");
		System.gc();
		if (sessionDataMapping.size() == 0) {
			overheadEstimate = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		}
		if (isCancelled) {
			throw new AnalyticsCancelledWorkflowException("Session " + sessionId + " detected as cancelled");
		}
	}

	protected synchronized long currentlyRequiredMemory() {
		long sum = 0;
		for(String key : sessionDataMapping.keySet()) {
			sum += sessionDataMapping.get(key).getMemoryEstimate();
		}
		return sum;
	}

	public long getUpdateFrequencyMillis() {
		return updateFrequencyMillis;
	}

	public void setUpdateFrequencyMillis(long updateFrequencyMillis) {
		this.updateFrequencyMillis = updateFrequencyMillis;
	}

	public long getFakeUpdateDelay() {
		return fakeUpdateDelay;
	}

	public void setFakeUpdateDelay(long fakeUpdateDelay) {
		this.fakeUpdateDelay = fakeUpdateDelay;
	}

	public int getNoticeableProgressJump() {
		return noticeableProgressJump;
	}

	public void setNoticeableProgressJump(int noticeableProgressJump) {
		this.noticeableProgressJump = noticeableProgressJump;
	}

	public int getMinFakeUpdates() {
		return minFakeUpdates;
	}

	public void setMinFakeUpdates(int minFakeUpdates) {
		this.minFakeUpdates = minFakeUpdates;
	}

	public boolean isPerformFakeUpdates() {
		return performFakeUpdates;
	}

	public void setPerformFakeUpdates(boolean performFakeUpdates) {
		this.performFakeUpdates = performFakeUpdates;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}
	
	public synchronized StatusReport getStatusReport() {
		int totalJobs = sessionDataMapping.size();
		int queuedJobs = jobQueue.size();
		int activeJobs = totalJobs - queuedJobs;
		long availableMem = Runtime.getRuntime().freeMemory();
		return new StatusReport(activeJobs, queuedJobs, availableMem, overheadEstimate, currentlyRequiredMemory());
	}
	
	public static class StatusReport {
		private final int numActiveJobs;
		private final int numJobsInQueue;
		private final long memoryAvailable;
		private final long overheadEstimate;
		private final long memoryHeld;
		
		public StatusReport(int numActiveJobs, int numJobsInQueue, 
				long memoryAvailable, long overheadEstimate, long memoryHeld) {
			this.numActiveJobs = numActiveJobs;
			this.numJobsInQueue = numJobsInQueue;
			this.memoryAvailable = memoryAvailable;
			this.overheadEstimate = overheadEstimate;
			this.memoryHeld = memoryHeld;
		}

		public int getNumActiveJobs() {
			return numActiveJobs;
		}

		public int getNumJobsInQueue() {
			return numJobsInQueue;
		}

		public long getMemoryAvailable() {
			return memoryAvailable;
		}

		public long getOverheadEstimate() {
			return overheadEstimate;
		}

		public long getMemoryHeld() {
			return memoryHeld;
		}
		
	}

	/**
	 * Ordering for jobs in the priority queue.
	 * @author leegc
	 *
	 */
	protected static class JobQueueEntry implements Comparable<JobQueueEntry> {
		private final String sessionId;
		private final Long insertionTime;

		public JobQueueEntry(String sessionId, Long insertionTime) {
			this.sessionId = sessionId;
			this.insertionTime = insertionTime;
		}

		public String getSessionId() {
			return sessionId;
		}

		public Long getInsertionTime() {
			return insertionTime;
		}

		@Override
		public int compareTo(JobQueueEntry o) {
			return (int)(getInsertionTime() - o.getInsertionTime());
		}
	}

}
