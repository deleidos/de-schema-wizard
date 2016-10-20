package com.deleidos.dmf.web;

/**
 * This class maintains some session information that will help report messages to the user.
 * @author leegc
 *
 */
public class SessionData {
	public static final int ERROR_CUTOFF = 10;
	private boolean isDone;
	private boolean isCancelled;
	private float lastUpdateNumerator;
	private long lastUpdateTime;
	private int sendingErrors;
	
	public SessionData() {
		isDone = false;
		isCancelled = false;
		lastUpdateTime = System.currentTimeMillis();
		sendingErrors = 0;
	}
	
	public boolean isDone() {
		return isDone;
	}
	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}
	public boolean isCancelled() {
		return isCancelled;
	}
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	public long getLastUpdate() {
		return lastUpdateTime;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdateTime = lastUpdate;
	}
	
	public boolean shouldUpdate() {
		if(System.currentTimeMillis() - lastUpdateTime > 
			SchemaWizardSessionUtility.getInstance().getUpdateFrequencyMillis()
				&& this.sendingErrors < ERROR_CUTOFF) {
			lastUpdateTime = System.currentTimeMillis();
			return true; 
		}
		return false;
	}

	public int getSendingErrors() {
		return sendingErrors;
	}

	public void setSendingErrors(int sendingErrors) {
		this.sendingErrors = sendingErrors;
	}

	public float getLastUpdateNumerator() {
		return lastUpdateNumerator;
	}

	public void setLastUpdateNumerator(float lastUpdateNumerator) {
		this.lastUpdateNumerator = lastUpdateNumerator;
	}
}
