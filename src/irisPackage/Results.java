package irisPackage;

import java.util.ArrayList;

public class Results {
	private RForestFTIR modelForResults;
	private RForestFTIR modelForResultWekaScore;
	private ApplicabilityDomain domain;
	private String fileName;
	private String diseaseClassification;
	private double diseaseClassificationDouble;
	private String wekaScore;
	private double wekaScoreDouble;
	private String domainScore;
	private double domainScoreDouble;
	private String comment = "None";
	boolean isFaulty = false;
	
	public Results(RForestFTIR inputModel, ApplicabilityDomain inputDomain, int sampleIndex) throws Exception {
		fileName = inputDomain.sample_s.fileNames.get(sampleIndex);
		modelForResults = inputModel;
		modelForResultWekaScore = inputModel;
		domain = inputDomain;
		ArrayList<Double> allClassifications = modelForResults.applyModel();
		ArrayList<double[]> allClassificationScores = modelForResultWekaScore.instanceScore();
		ArrayList<Double> applicabilityScores = domain.sampleScores;
		double sampleClassification = allClassifications.get(sampleIndex);
		diseaseClassification = String.valueOf(sampleClassification);
		diseaseClassificationDouble =  sampleClassification;
		int resultProbIndex = (int) sampleClassification;
		wekaScore = String.valueOf(allClassificationScores.get(sampleIndex)[resultProbIndex]);
		wekaScoreDouble = allClassificationScores.get(sampleIndex)[resultProbIndex];
		domainScore = String.valueOf(applicabilityScores.get(sampleIndex));
		domainScoreDouble = applicabilityScores.get(sampleIndex);
		convertDiagToChar();
		checkResults();
	}
	
	private void convertDiagToChar() {
		if(diseaseClassificationDouble == 0.0 ) {
			diseaseClassification = "Healthy";
		} else if(diseaseClassificationDouble == 1.0) {
			diseaseClassification = "Auto-immune Periphal Neuropathies";
		} else if (diseaseClassificationDouble == 2.0) {
			diseaseClassification = "Neuromyelitis Optica Spectrum Disorder";
		} else if (diseaseClassificationDouble == 3.0) {
			diseaseClassification = "Relapsing Remitting Multiple Sclerosis";
		}
	}
	
	private void checkResults() {
		if(wekaScoreDouble < 0.95 && domainScoreDouble < 0.90) {
			isFaulty = true;
			comment = "Both the diagnosis score and the spectrum score were too low, re-measure sample.";
			diseaseClassification = "Unknown";
		}else if(wekaScoreDouble < 0.95) {
			isFaulty = true;
			comment = "The diagnosis score was too low, results are inconclusive.";
			diseaseClassification = "Unknown";
		} else if(domainScoreDouble < 0.90) {
			isFaulty = true;
			comment = "The spectrum score was too low, re-measure the sample."; // inconclusive
			diseaseClassification = "Unknown";
		}
		// what should I do now?
		// re-record spectra?
		// take another blood sample?
	}
	
	
	public String getFileName() {
		return fileName;
	}
	public String getDiseaseClassification() {
		return diseaseClassification;
	}
	public String getWekaScore() {
		return wekaScore;
	}
	public String getDomainScore() {
		return domainScore;
	}
	
	public String getComment() {
		return comment;
	}
	
}
