package com.deleidos.dmf.progressbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.deleidos.dmf.exception.AnalyticsRuntimeException;
import com.deleidos.dmf.progressbar.ProgressState.STAGE;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * ProgressBarManager handles the appropriate updating of the progress bar.  It will prevents progress updates from going
 * backwards, even with the recursive parsing functionality of the {@link com.deleidos.dmf.framework.AnalyticsDefaultParser}
 *
 */
public class ProgressBarManager {
	private static final Logger logger = Logger.getLogger(ProgressBarManager.class);
	private float numerator;
	private int currentStateIndex;
	private List<ProgressState> states;

	private ProgressBarManager(List<ProgressState> states) {
		this.currentStateIndex = 0;
		this.states = states;
	}

	@JsonIgnore
	public ProgressState getCurrentState() {
		return states.get(currentStateIndex);
	}

	public synchronized boolean updateNumerator(float updateValue) {
		if(updateValue < numerator || !getCurrentState().withinRange(updateValue)) {
			return false;
		} else if(updateValue > 100) {
			updateValue = 100;
		}
		numerator = updateValue;
		return true;
	}
	
	/**
	 * Split the current state into <i>numSplits</i> states.  This allows any subprogress updates to still report their progress
	 * on a scale that is unaware of any parent progress.
	 * @param numSplits the number of desired splits.
	 * @return the number of states in the progressbarmanager after splitting
	 */
	public int split(int numSplits) {
		List<ProgressState> newStates = new ArrayList<ProgressState>(states.subList(0, currentStateIndex));		
		List<ProgressState> splitStates = ProgressState.split(numerator, 
				getCurrentState().getEndValue(), getCurrentState().getDescription(), numSplits);
		List<ProgressState> afterSplits = new ArrayList<ProgressState>(states.subList(currentStateIndex+1, states.size()));
		getCurrentState().setEndValue(numerator);
		newStates.add(getCurrentState());
		newStates.addAll(splitStates);
		newStates.addAll(afterSplits);
		goToNextStateIfCurrentIs(getCurrentState().getStage());
		states = newStates;
		return states.size();
	}
	
	public void updateNumeratorInRequiredState(float updateValue, STAGE ... requiredStage) {
		for(STAGE stage : requiredStage) {
			if(stage.equals(getCurrentState())) {
				updateNumerator(updateValue);
			}
		}
	}

	public void goToNextStateIfCurrentIs(STAGE ... requiredStage) {
		for(STAGE stage : requiredStage) {
			if(getCurrentState().getStage().equals(stage)) {
				currentStateIndex++;
				currentStateIndex = (currentStateIndex >= states.size()) ? states.size() - 1 : currentStateIndex;
				updateNumerator(getCurrentState().getStartValue());
				return;
			}
		}
	}
	
	public void jumpToEndOfSplits() {
		int prev = this.currentStateIndex;
		while(getCurrentState().isSplit()) {
			goToNextStateIfCurrentIs(STAGE.SPLIT);
			updateNumerator(getCurrentState().getStartValue());
			if(prev == this.currentStateIndex) {
				throw new AnalyticsRuntimeException("Error with progress bar splitting.");
			}
		}
	}
	
	/**
	 * Temporary convenience method to put difference instances of "the same" progress bar in right place
	 * @param n
	 * @param state
	 */
	public void jumpToNthIndexStage(int n, STAGE state) {
		int numJumps = 0;
		int resultingIndex = 0;
		for(ProgressState progressState : states) {
			if(progressState.getStage().equals(state)) {
				if(numJumps == n) {
					updateNumerator(progressState.getStartValue());
					currentStateIndex = resultingIndex;
					return;
				}
				numJumps++;
			}
			resultingIndex++;
		}
	}

	public ProgressBar asBean() {
		return new ProgressBar((int)numerator, getCurrentState().getDescription());
	}
	
	public void setSampleNamesIfApplicable(List<String> names) {
		int nameIndex = 0;
		for(ProgressState state : states) {
			switch(state.getStage()) {
			case DETECT: state.setDescription(names.get(nameIndex)+": Detecting type."); break;
			case FIRST_PASS: state.setDescription(names.get(nameIndex)+": Parsing fields."); break;
			case INTERPRET: state.setDescription(names.get(nameIndex)+": Interpretting fields."); break;
			case SECOND_PASS: state.setDescription(names.get(nameIndex)+": Building data visualizations."); break;
			case SAMPLE_COMPLETE: {
				state.setDescription(names.get(nameIndex)+": Analysis completed.");
				nameIndex++;
				break;
			}
			default: break;
			}
		}
	}

	public static ProgressBarManager fileUploadProgressBar(List<String> names, long totalBytes) {
		ProgressBarManager progressBar = fullProgressBar(names, totalBytes);
		progressBar.jumpToNthIndexStage(0, STAGE.UPLOAD);
		return progressBar;
	}

	public static ProgressBarManager matchingProgressBar(List<String> names) {
		ProgressBarManager progressBar = fullProgressBar(names);
		progressBar.jumpToNthIndexStage(0, STAGE.MATCHING);
		return progressBar;
	}
	
	public static ProgressBarManager sampleProgressBar(List<String> names, int currentSample) {
		ProgressBarManager progressBar = fullProgressBar(names);
		progressBar.jumpToNthIndexStage(currentSample, STAGE.DETECT);
		return progressBar;
	}

	private final static float megabytesPerUpdatePercentage = 10;
	
	public static ProgressBarManager fullProgressBar(List<String> names) {
		return fullProgressBar(names, (long)(megabytesPerUpdatePercentage*1024*1024));
	}
	
	public static ProgressBarManager fullProgressBar(List<String> names, long totalUploadBytes) {
		if(names.size() < 1) {
			throw new AnalyticsRuntimeException("No sample provided.");
		}
		final float fileUploadMarkMax = 10;
		final float matchingMarkMin = 90;
		final float matchWeightPerSample = 1.5f;
		double fileUploadSizeMB = (double)totalUploadBytes / 1024 / 1024;
		float fileUploadMark = (float)(fileUploadSizeMB / megabytesPerUpdatePercentage);
		float matchingMark = 100 - (names.size() * matchWeightPerSample);
		if(names.size() == 1) {
			matchingMark = 100;
		}
		fileUploadMark = (fileUploadMark > fileUploadMarkMax) ? fileUploadMarkMax : fileUploadMark;
		matchingMark = (matchingMark < matchingMarkMin) ? matchingMarkMin : matchingMark;
		ProgressBarManager progressBar = fullProgressBar(names, fileUploadMark, matchingMark);
		return progressBar;
	}

	private static ProgressBarManager fullProgressBar(List<String> names, float allSamplesStart, float allSamplesEnd) {
		float delegatedSampleProgresWidth = allSamplesEnd - allSamplesStart;
		float width = delegatedSampleProgresWidth / (float) names.size();
		List<ProgressState> fullStateList = new ArrayList<ProgressState>();
		fullStateList.add(ProgressState.uploading(0, allSamplesStart));
		for(int i = 0; i < names.size(); i++) {
			String name = names.get(i);
			float globalStart = (width*i)+allSamplesStart;
			float globalEnd = (width*(i+1))+allSamplesStart;
			fullStateList.addAll(ProgressState.sampleFlow(name, globalStart, globalEnd));
		}
		fullStateList.add(ProgressState.matching(names.size(), allSamplesEnd, ProgressState.globalComplete.getStartValue()));
		fullStateList.add(ProgressState.globalComplete);
		return new ProgressBarManager(fullStateList);
	}

	public static ProgressBarManager schemaProgressBar(List<String> names) {
		final float range = 100f;
		final float width = range/(float)names.size();
		List<ProgressState> schemaState = new ArrayList<ProgressState>();
		for(int i = 0; i < names.size(); i++) {
			float start = (width*i);
			float end = (width*(i+1));
			schemaState.add(ProgressState.schemaProgress(names.get(i), start, end));
		}
		schemaState.add(ProgressState.globalComplete);
		return new ProgressBarManager(schemaState);
	}
	
	public float getNumerator() {
		return numerator;
	}

	public void setNumerator(float numerator) {
		this.numerator = numerator;
	}

	public ProgressState getStateByNumerator(float numerator) {
		for(ProgressState state : states) {
			if(state.withinRange(numerator)) {
				return state;
			}
		}
		return ProgressState.globalComplete;
	}
	
	public boolean isDuring(STAGE stage) {
		return getCurrentState().getStage().equals(stage);
	}
	
	public static class ProgressBar {
		private int numerator;
		private String description;
		
		public ProgressBar(int numerator, String description) {
			setNumerator(numerator);
			setDescription(description);
		}
		
		public int getNumerator() {
			return numerator;
		}
		public void setNumerator(int numerator) {
			this.numerator = numerator;
		}
		public int getDenominator() {
			return 100;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		
		
	}
}
