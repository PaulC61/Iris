package irisPackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class TrainingSetArff {
	File folder;
	String savePath = "C:/Users/Paul/eclipse-workspace/Iris/Validation";
	ArrayList<File> fileLst;
	String fileName;
	String folderPath;
	ArrayList<Double> frequencies;
	ArrayList<ArrayList<Double>> intensities;
	ArrayList<String> diseaseClassifiers;
	
	
	public TrainingSetArff(File inputFolder) {
		folder = inputFolder;
		folderPath = inputFolder.getPath();
		fileLst = storeFiles();
		frequencies = storeFrequencies();
		intensities = storeIntensities();
		fileName = trainingSetName();
		diseaseClassifiers = diseaseNames();
	}
	
	private String trainingSetName() {
		Calendar.getInstance();
		String saveFileName = "irisValidationSet" + Calendar.DAY_OF_MONTH + "_" + Calendar.MONTH + "_" + Calendar.YEAR;
		return saveFileName;
	}
	
	private ArrayList<File> storeFiles(){
		ArrayList<File> storedFiles = new ArrayList<>();
		if(this.folder.isDirectory()) {
			for (File singleFile : folder.listFiles()) {
				if(singleFile.getName().endsWith(".csv") || singleFile.getName().endsWith(".dpt") || singleFile.getName().endsWith(".DPT")) {
					storedFiles.add(singleFile);
				}
			}
		}else {
			System.out.println("Please choose a directory, not a file to build a training set");
		}
		
		return storedFiles;
	}
	
	
	private ArrayList<String> saveFileEntriesAsStringArray(File toSave){
		ArrayList<String> rows = new ArrayList<>();
		try(Scanner scanner = new Scanner(toSave)){
			while(scanner.hasNextLine()) {
				String singleRow = scanner.nextLine();
				rows.add(singleRow);
			}
		}catch(Exception e) {
			System.out.println("Could not scan file: " + e);
		}
		return rows;
	}
	
	private ArrayList<String> diseaseNames(){
		ArrayList<String> diseaseNamesLst = new ArrayList<String>();
		for (File eachFile : fileLst) {
			if(!diseaseNamesLst.contains(eachFile.getName().substring(0, 2))) {
				diseaseNamesLst.add(eachFile.getName().substring(0, 2));
			}
		}
		
		return diseaseNamesLst;
	}
	
	
	private String diseaseNamesToString() {
		StringBuffer addElements = new StringBuffer();
		addElements.append("{");
		for(int i = 0; i<diseaseClassifiers.size(); i++) {
			if (i < diseaseClassifiers.size() - 1) {
				addElements.append(diseaseClassifiers.get(i) + ",");
			}else {
				addElements.append(diseaseClassifiers.get(i) + "}");
			}
		}
		return addElements.toString();
	}
	
	private ArrayList<Double> storeFrequencies(){
		ArrayList<Double> frequencies = new ArrayList<>();
		ArrayList<String> firstFileRows = saveFileEntriesAsStringArray(fileLst.get(0));
		
		for(String eachString : firstFileRows) {
			String[] rowParts = eachString.split(",");
			double freqDbl = Double.parseDouble(rowParts[0]);
			if((freqDbl<3017 && freqDbl >2801) || (freqDbl<1772)) {
				frequencies.add(freqDbl);
			}
		}
		return frequencies;
	}
	
	private ArrayList<ArrayList<Double>> storeIntensities(){
		ArrayList<ArrayList<Double>> allFileIntensities = new ArrayList<>();
		for (int j = 0; j < fileLst.size(); j++) {
			ArrayList<Double> singleFileIntensity = storeSingleFileIntensities(j);
			allFileIntensities.add(singleFileIntensity);
		}
		return allFileIntensities;
	}
	
	
	private ArrayList<Double> storeSingleFileIntensities(int fileIndex){
		ArrayList<Double> singleFileIntensities = new ArrayList<>();
		ArrayList<String> fileRows = saveFileEntriesAsStringArray(fileLst.get(fileIndex));
		for(int i = 0; i< fileRows.size(); i++) {
			String[] rowParts = fileRows.get(i).split(",");
			double freqDbl = Double.parseDouble(rowParts[0]);//takes first element and places as attribute
			double intenDbl = Double.parseDouble(rowParts[1]);
			if((freqDbl<3017 && freqDbl >2801) || (freqDbl<1772)) {
				singleFileIntensities.add(intenDbl);
			}
		}
		return singleFileIntensities;
	}
	
	
	public File createArff() {
		File storeFile = new File(savePath + "/" + fileName + ".arff");
		try {
			storeFile.createNewFile();
		}catch(Exception e){
			System.out.println("Could not create file: " + e);
		}
		try {
			FileWriter writeArff = new FileWriter(storeFile);
			BufferedWriter buffWriteArff = new BufferedWriter(writeArff);
			buffWriteArff.append("@relation '" + fileName + "'");
			buffWriteArff.append("\n\n");
			buffWriteArff.append("@attribute ID string");
			buffWriteArff.append("\n");
			for(int i = 0; i < fileLst.size(); i++) {
				ArrayList<String> fileRows = saveFileEntriesAsStringArray(fileLst.get(i));
				if(i == 0) {
					for(int j = 0; j < fileRows.size(); j++) {
						String[] rowParts = fileRows.get(j).split(",");
						double freqDbl = Double.parseDouble(rowParts[0]);
						int freqInt = (int) freqDbl;
						if(j < fileRows.size() - 1) {
							buffWriteArff.append("@attribute f" + freqInt + " numeric");
							buffWriteArff.append("\n");
						}else {
							buffWriteArff.append("@attribute f" + freqInt + " numeric");
							buffWriteArff.append("\n");
							String classifiers = diseaseNamesToString();
							buffWriteArff.append("@attribute Pathologie " + classifiers);
							buffWriteArff.append("\n\n");
							buffWriteArff.append("@data");
							buffWriteArff.append("\n");
							buffWriteArff.append(fileLst.get(i).getName() + ",");
							
						}	
					}
					for (int h = 0; h < fileRows.size(); h++) {
						String[] rowParts = fileRows.get(h).split(",");
						if(h < fileRows.size() - 1) {
							buffWriteArff.append(rowParts[1]);
							buffWriteArff.append(",");
						}else {
							buffWriteArff.append(rowParts[1] + "," + fileLst.get(i).getName().substring(0,2) + "\n");
						}
					}
				}else {
					buffWriteArff.append(fileLst.get(i).getName() + ",");
					for(int j = 0; j < fileRows.size(); j++) {
						String[] rowParts = fileRows.get(j).split(",");
						if(j < fileRows.size() - 1) {
							buffWriteArff.append(rowParts[1]);
							buffWriteArff.append(",");
						} else {
							buffWriteArff.append(rowParts[1] + "," + fileLst.get(i).getName().substring(0,2) + "\n");
						}
					}
				}
			}
			buffWriteArff.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Could not write into created file: " + e);;
		}
		return storeFile;
	}
	
	private double averageIntensityAtOneFrequency(int freqIndex){
		double sum = 0;
		for (int i = 0; i < intensities.size(); i++) {
			double sampIntesnityAtFreqIndex = intensities.get(i).get(freqIndex);
			sum = sum + sampIntesnityAtFreqIndex;
		}
		int numberOfSamples = intensities.size();
		double averageAtFreqIndex = sum/numberOfSamples;
		return averageAtFreqIndex;
	}
	
	private double stdDevIntensityAtOneFrequency(int freqIndex) {
		double sumSDs = 0;
		double average = averageIntensityAtOneFrequency(freqIndex);
		for(int i = 0; i < intensities.size(); i++) {
			double sampIntensityAtFreqIndex = intensities.get(i).get(freqIndex);
			double distance = average - sampIntensityAtFreqIndex;
			double squaredDistance = distance*distance;
			sumSDs = sumSDs + squaredDistance;
		}
		double standardError = sumSDs/(intensities.size()-1);
		double stdDev = Math.sqrt(standardError);
		return stdDev;
	}
	
	public ArrayList<Double> averageIntensities(){
		ArrayList<Double> averageAtEachFreq = new ArrayList<>();
		for (int j = 0; j < intensities.get(0).size(); j ++) {
			double freqAv = averageIntensityAtOneFrequency(j);
			averageAtEachFreq.add(freqAv);
		}
		return averageAtEachFreq;
	}
	
	public ArrayList<Double> stdDevIntensities(){
		ArrayList<Double> allStdDevs = new ArrayList<>();
		for(int j = 0; j < intensities.get(0).size(); j++) {
			double intensityStdDev = stdDevIntensityAtOneFrequency(j);
			allStdDevs.add(intensityStdDev);
		}
		return allStdDevs;
	}
}
