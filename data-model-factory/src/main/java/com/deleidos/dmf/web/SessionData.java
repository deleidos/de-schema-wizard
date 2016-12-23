package com.deleidos.dmf.web;

/**
 * This class maintains some session information that will help report messages to the user.
 * @author leegc
 *
 */
public class SessionData {
	public static final int ERROR_CUTOFF = 10;
	private String webSocketId;
	private Boolean isDone;
	private Boolean isWebSocketOpen;
	private Float lastUpdateNumerator;
	private Long lastUpdateTime;
	private Integer sendingErrors;
	private Long memoryEstimate;
	
	public SessionData() {
		webSocketId = null;
		isDone = false;
		isWebSocketOpen = false;
		lastUpdateTime = System.currentTimeMillis();
		lastUpdateNumerator = 1F;
		sendingErrors = 0;
		memoryEstimate = 0L;
	}
	
	public boolean isCancelled() {
		// if the websocket is not open, but there is no id, then it hasn't been established yet
		return !isWebSocketOpen && webSocketId != null;
	}
	
	public boolean shouldUpdate() {
		if(System.currentTimeMillis() - lastUpdateTime > 
			SchemaWizardSessionUtility.getInstance().getUpdateFrequencyMillis()
				&& this.sendingErrors < ERROR_CUTOFF
				&& this.isWebSocketOpen) {
			return true; 
		}
		return false;
	}

	public String getWebSocketId() {
		return webSocketId;
	}

	public void setWebSocketId(String webSocketId) {
		this.webSocketId = webSocketId;
	}

	public Boolean isDone() {
		return isDone;
	}

	public void setIsDone(Boolean isDone) {
		this.isDone = isDone;
	}

	public Boolean isWebSocketOpen() {
		return isWebSocketOpen;
	}

	public void setIsWebSocketOpen(Boolean isWebSocketOpen) {
		this.isWebSocketOpen = isWebSocketOpen;
	}

	public Long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Integer getSendingErrors() {
		return sendingErrors;
	}

	public void setSendingErrors(Integer sendingErrors) {
		this.sendingErrors = sendingErrors;
	}

	public Long getMemoryEstimate() {
		return memoryEstimate;
	}

	public void setMemoryEstimate(Long memoryEstimate) {
		this.memoryEstimate = memoryEstimate;
	}

	public Float getLastUpdateNumerator() {
		return lastUpdateNumerator;
	}

	public void setLastUpdateNumerator(Float lastUpdateNumerator) {
		this.lastUpdateNumerator = lastUpdateNumerator;
	}

	public Boolean getIsDone() {
		return isDone;
	}

	public Boolean getIsWebSocketOpen() {
		return isWebSocketOpen;
	}


}
