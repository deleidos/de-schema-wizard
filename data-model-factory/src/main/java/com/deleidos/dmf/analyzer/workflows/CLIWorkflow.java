package com.deleidos.dmf.analyzer.workflows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.deleidos.dp.beans.Schema;

public class CLIWorkflow extends AbstractHeadlessWorkflow {
	List<String> files;
	Scanner scanner;
	
	public CLIWorkflow(String uploadDir, String domain, String tolerance, List<String> files, Scanner scanner) {
		super(uploadDir, domain, tolerance);
		this.files = files;
		this.scanner = scanner;
	}


	@Override
	public void addNecessaryTestFiles() {
		for (String file : files) {
			addLocalTestFile(file);
		}
	}

	@Override
	public String[] performMockVerificationStep(String[] generatedSampleGuids) {
		List<String> guids = new ArrayList<String>(Arrays.asList(generatedSampleGuids));
		boolean cont;
		do {
			for (int i = 0; i < generatedSampleGuids.length; i++) {
				System.out.println("Sample with guid: " + generatedSampleGuids[i] + " at index: " + i);
			}
			System.out.print(
					"Enter the index of the sample you would like to drop or \"Enter\" to continue: ");
			String s = scanner.nextLine();
			try {
				if (s.isEmpty()) {
					cont = false;
				} else {
					int sampleIndex = Integer.valueOf(s);
					cont = true;
					if (sampleIndex > guids.size()) {
						System.out.println("Index out of bounds.");
						continue;
					}
					guids.remove(sampleIndex);
				}
			} catch (NumberFormatException e) {
				cont = false;
			}
		} while (cont);
		String[] modifiedSampleGuids = new String[guids.size()];
		for (int i = 0; i < guids.size(); i++) {
			modifiedSampleGuids[i] = guids.get(i);
			System.out.println(modifiedSampleGuids[i]);
		}
		return modifiedSampleGuids;
	}

	@Override
	public JSONArray performMockMergeSamplesStep(Schema existingSchema,
			JSONArray retrieveSourceAnalysisResult) {
		System.out.println(retrieveSourceAnalysisResult);
		boolean cont;
		do {
			for (int i = 0; i < retrieveSourceAnalysisResult.length(); i++) {
				System.out.println(
						"Fields of " + retrieveSourceAnalysisResult.getJSONObject(i).getString("dsName")
						+ " at index " + i);
				JSONObject dsProfile = retrieveSourceAnalysisResult.getJSONObject(i)
						.getJSONObject("dsProfile");
				for (String key : dsProfile.keySet()) {
					System.out.print(key + "\t");
				}
				System.out.println();
			}
			System.out.print(
					"Enter the index of the sample in which you would like to perform merges, or press \"Enter\" to continue: ");
			String s = scanner.nextLine();
			try {
				if (s.isEmpty()) {
					cont = false;
				} else {
					int sampleIndex = Integer.valueOf(s);
					cont = true;
					if (sampleIndex > retrieveSourceAnalysisResult.length()) {
						System.out.println("Index out of bounds.");
						continue;
					}

					JSONObject dataSample = retrieveSourceAnalysisResult.getJSONObject(sampleIndex);
					String nonMergedKey;
					boolean exists;
					String mergedFieldKey;
					do {
						System.out.print("Enter the name of the unmerged field: ");
						nonMergedKey = scanner.nextLine();
						exists = (dataSample.getJSONObject("dsProfile").keySet().contains(nonMergedKey))
								? true : false;
					} while (!exists);

					System.out.print("Enter the merged name of field \"" + nonMergedKey + "\": ");
					mergedFieldKey = scanner.nextLine();

					simulateMerge(dataSample, nonMergedKey, mergedFieldKey);
					retrieveSourceAnalysisResult.put(sampleIndex, dataSample);

				}
			} catch (NumberFormatException e) {
				cont = false;
			}
		} while (cont);
		return retrieveSourceAnalysisResult;
	}

	@Override
	public JSONObject performMockSchemaInlineEdittingStep(JSONObject schemaAnalysis) {
		return schemaAnalysis;
	}

}
