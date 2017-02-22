package com.deleidos.dmf.web;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.deleidos.dmf.exception.AnalyticsCancelledWorkflowException;
import com.deleidos.dmf.framework.TestingWebSocketUtility;

public class SchemaWizardSessionUtilityTest {
	private static final Logger logger = Logger.getLogger(SchemaWizardSessionUtilityTest.class);
	private static TestingWebSocketUtility testSessionUtil;
	private static final Long mockTotalMemory = 1000L;
	private static final Long mockOverhead = 0L;
	private static final Long stepDelay = 1L;
	private static List<SimpleMockupJobRunnable> jobs;
	private static List<Exception> exceptions = new ArrayList<Exception>(10);
	private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
	private static final Long TIMEOUT_SECONDS = 10L;

	@Before
	public void initSessionUtil() {
		testSessionUtil = new TestingWebSocketUtility(mockTotalMemory, mockOverhead);
	}

	public void stopJobs(List<SimpleMockupJobRunnable> jobs) {
		if (jobs != null) {
			jobs.forEach(job->job.end());
		}
	}

	@Test
	public void testPauseAndResume() throws Exception {
		List<SimpleMockupJobRunnable> jobs = Arrays.asList(new SimpleMockupJobRunnable("test", 100L, 10, true));
		try {
			callJobs(jobs);
			step(jobs, 5);
			// after 5 "units" of progress, the job should still be running
			assertFalse(jobs.get(0).isDone);
			step(jobs, 5);
			// after 10 units of progress, the job should be done
			assertTrue(jobs.get(0).isDone);
		} finally {
			stopJobs(jobs);
		}
	}

	@Test
	public void testQueueing() throws Exception {
		List<String> sessionIds = Arrays.asList("session1", "session2");
		Long largeFirstEstimate = 300L;
		Integer duration1 = 2;
		Long mediumSecondEstimate = 100L;
		Integer duration2 = 2;
		// should take 4 steps to finish all because they cannot be processed at the same time
		List<SimpleMockupJobRunnable> jobs = Arrays.asList(
				new SimpleMockupJobRunnable(sessionIds.get(0), largeFirstEstimate, duration1, true),
				new SimpleMockupJobRunnable(sessionIds.get(1), mediumSecondEstimate, duration2, false));
		try {
			callJobs(jobs);
			try {
				assertFalse(jobs.get(0).isWaiting);
				assertTrue(jobs.get(1).isWaiting);
			} catch (AssertionError e) {
				logger.error("After being started, the first job should be progressing, and the second should be queued",e);
				fail();
			}

			step(jobs);
			try {
				assertFalse(jobs.get(0).isWaiting);
				assertTrue(jobs.get(1).isWaiting);
			} catch (AssertionError e) {
				logger.error("After one unit of progress, the first job should be progressing, and the second should be queued",e);
				fail();
			}
			step(jobs, 2);
			try {
				assertTrue(jobs.get(0).isDone);
				assertFalse(jobs.get(1).isWaiting);
			} catch (AssertionError e) {
				logger.error("After two more units of progress, the first job should be done, and the second should be started", e);
				fail();
			}
			step(jobs);

			try {
				assertTrue(exceptions.size() == 0);
				jobs.forEach(job->assertTrue(job.isDone));
			} catch (AssertionError e2) {
				logger.error("After 4 units of progress, they should both be done", e2);
				if (exceptions.size() != 0) {
					logger.error("Also had exceptions:");
					exceptions.forEach(x->logger.error(x));
				}
				fail();
			} 
		} finally {
			stopJobs(jobs);
		}

	}

	private void callJobs(final List<SimpleMockupJobRunnable> jobs) {
		logger.info("Starting jobs.");
		for (SimpleMockupJobRunnable job : jobs) {
			executorService.submit(job);
			// wait until the job has been queued or is ready
			if (job.shouldStartImmediately) {
				waitFor(()->!job.isWaiting);
				logger.info("Job " + job.sessionId + " is no longer waiting to process.");
			} else {
				waitFor(()->testSessionUtil.isInQueue(job.sessionId));
				logger.info("Job " + job.sessionId + " is in queue.");
			}
		}
	}

	private static class SimpleMockupJobRunnable implements Runnable {
		private String sessionId;
		private Long memoryEstimate;
		private Integer jobsInQueueAtFinish = -1;
		private Integer stepsRemaining;
		private Boolean isWaiting;
		private Boolean isDone;

		private final Boolean shouldStartImmediately;

		private volatile Boolean interrupt;
		private volatile Boolean takeStep;

		public SimpleMockupJobRunnable(String fakeSessionId, Long fakeMemoryEstimate, Integer stepsToFinish, Boolean shouldStartImmediately) {
			this.shouldStartImmediately = shouldStartImmediately;
			sessionId = fakeSessionId;
			memoryEstimate = fakeMemoryEstimate;
			stepsRemaining = stepsToFinish;
			isWaiting = true;
			isDone = false;
			takeStep = false;
			interrupt = false;
		}

		@Override
		public void run() {
			logger.info("Queueing session " + this.sessionId);
			isWaiting = !testSessionUtil.requestAnalysis(this.sessionId, this.memoryEstimate, 1L);
			logger.info("Session " + sessionId + " taken off queue.");
			while (stepsRemaining > 0) {
				waitFor(()->takeStep);
				synchronized (this) {
					logger.info("Session " + this.sessionId + " has " + this.stepsRemaining + " steps remaining.");
					stepsRemaining--;
					takeStep = false;
				}
			}
			jobsInQueueAtFinish = testSessionUtil.jobQueue.size();
			try {
				testSessionUtil.registerCompleteAnalysis(sessionId);
			} catch (AnalyticsCancelledWorkflowException e) {
				logger.error("Session cancelled.", e);
			}
			isDone = true;
		}

		public void end() {
			// mark the interrupt to end the thread
			interrupt = true;
		}

	}

	private static void step(List<SimpleMockupJobRunnable> jobs) throws InterruptedException {
		for (SimpleMockupJobRunnable job : jobs) {
			if (!job.isWaiting && !job.isDone) {
				job.takeStep = true;

				// let the run loop execute until it recognizes the step
				// the other thread will set this to false, signifying we can return from this method
				// takeStep == false means that the thread has recognized the step and taken it
				waitFor(()->!job.takeStep);

				if (job.stepsRemaining == 0) {
					// the job is done, let it finish the rest of the run() method before returning true
					waitFor(()->job.isDone);
					logger.info("Job " + job.sessionId + " finished ("+job.isDone+") "
							+ "with jobs in queue " + job.jobsInQueueAtFinish);
				}
			}
		}
	}

	private static void step(List<SimpleMockupJobRunnable> jobs, int numSteps) throws InterruptedException {
		for (int i = 0; i < numSteps; i++) step(jobs);
	}

	@AfterClass
	public static void shutdown() {
		executorService.shutdown();
	}

	private interface WaitForCondition {
		public boolean waitFor();
	}

	private static void waitFor(WaitForCondition condition) {
		try {
			executorService.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					while (!condition.waitFor()) {
						Thread.sleep(stepDelay);
					} 
					return null;
				}
			}).get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new RuntimeException("Programming threading error.", e);
		}
	}


}
