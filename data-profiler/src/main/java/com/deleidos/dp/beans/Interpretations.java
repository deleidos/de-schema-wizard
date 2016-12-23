package com.deleidos.dp.beans;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Interpretations {
	private Set<Interpretation> availableOptions;
	private Interpretation selectedOption;
	
	public Interpretations() {
		availableOptions = new HashSet<Interpretation>();
		availableOptions.add(Interpretation.UNKNOWN);
		selectedOption = Interpretation.UNKNOWN;
	}
	
	public static Interpretations newInstance(Interpretation...interpretations) {
		Interpretations interpretations2 = new Interpretations();
		Arrays.asList(interpretations).forEach(interpretations2::add);
		return interpretations2;
	}
	
	public Set<Interpretation> getAvailableOptions() {
		return availableOptions;
	}
	public void setAvailableOptions(Set<Interpretation> availableOptions) {
		this.availableOptions = availableOptions;
	}

	public Interpretation getSelectedOption() {
		return selectedOption;
	}

	public void setSelectedOption(Interpretation selectedOption) {
		this.selectedOption = selectedOption;
	}
	
	public boolean add(Interpretation interpretation) {
		return availableOptions.add(interpretation);
	}
	
	public boolean containsInterpretation(Interpretation candidate) {
		return availableOptions.stream()
				.map(Interpretation::getiName)
				.anyMatch(candidate.getiName()::equals);
	}

}
