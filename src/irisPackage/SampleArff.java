package irisPackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class SampleArff {
	String savePath = "C:/Users/Paul/eclipse-workspace/Iris/SampleArffs";
	String saveFileName;
	ArrayList<File> fileLst;
	ArrayList<String> fileNames;
	ArrayList<String> filePaths;
	ArrayList<Double> frequencies;
	ArrayList<ArrayList<Double>> intensities;
	
	public SampleArff(ArrayList<File> inputFileLst) {
		fileLst = inputFileLst;
		saveFileName = saveFileName();
		fileNames = fileNameLst();
		filePaths = selectedFilePaths();
		intensities = storeIntensities();
		frequencies = storeFrequencies();
	}
	
	private String saveFileName() {
		StringBuffer fileLstNames = new StringBuffer();
		for (int i = 0; i < fileLst.size(); i++) {
			File theFile = fileLst.get(i);
			String fullFileName = theFile.getName();
			String[] baseNdExt = fullFileName.split("\\.(?=[^\\.]+$)");
			if(i < fileLst.size() - 1) {
				fileLstNames.append(baseNdExt[0]);
				fileLstNames.append("__");
			}else {
				fileLstNames.append(baseNdExt[0]);
			}	
		}
		return fileLstNames.toString();
	}
	
	public ArrayList<String> fileNameLst(){
		ArrayList<String> fileNames = new ArrayList<>();
		for(File theFile : fileLst) {
			String fileName = theFile.getName();
			fileNames.add(fileName);
		}
		return fileNames;
	}
	
	public ArrayList<String> selectedFilePaths(){
		ArrayList<String> pathLst = new ArrayList<>();
		for(File theFile : fileLst) {
			String eachPath = theFile.getPath();
			String eachFileName = theFile.getName();
			String nativePath = eachPath.replace(eachFileName, "");
			pathLst.add(nativePath);
		}
		return pathLst;
	}
	
	public ArrayList<String> selectedFileFullPaths(){
		ArrayList<String> fullPathLst = new ArrayList<>();
		for(File theFile : fileLst) {
			String eachPath = theFile.getPath();
			fullPathLst.add(eachPath);
		}
		return fullPathLst;
	}
	
	public ArrayList<String> saveFileEntriesAsStringArray(File toSave){
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
	
	private ArrayList<Double> storeFrequencies() {
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
		File storeFile = new File(savePath + "/" + saveFileName + ".arff");
//		ArrayList<ArrayList<Double>> addAllIntensities = new ArrayList<>();
		try {
			storeFile.createNewFile();
		}catch(IOException e) {
			System.out.println("Cannot create empty .arff file: " + e);
		}
		try {
			FileWriter writeArff = new FileWriter(storeFile);
			BufferedWriter buffWriteArff = new BufferedWriter(writeArff);
			//setting up structure and attributes
			buffWriteArff.append("@relation 'biomarkerSample " + saveFileName + "'");
			buffWriteArff.append("\n\n");
			buffWriteArff.append("@attribute ID string");
			buffWriteArff.append("\n");
			for (int i = 0; i < fileLst.size(); i++) {
				ArrayList<String> fileRows = saveFileEntriesAsStringArray(fileLst.get(i));// gets each file as String Array
//				ArrayList<Double> fileIntensities = new ArrayList<>();
				if(i == 0) {
					for(int j = 0; j < fileRows.size(); j++) {
						String[] rowParts = fileRows.get(j).split(",");
						double freqDbl = Double.parseDouble(rowParts[0]);//takes first element and places as attribute
//						if((freqDbl<3017 && freqDbl >2801) || (freqDbl<1772)) {
//							double intenDbl = Double.parseDouble(rowParts[1]);
//							fileIntensities.add(intenDbl);
//							frequencies.add(freqDbl);
//						}
						int freqInt = (int) freqDbl;
						if(j < fileRows.size() - 1) {
							buffWriteArff.append("@attribute f" + freqInt + " numeric");
							buffWriteArff.append("\n");
						}else {
							buffWriteArff.append("@attribute f" + freqInt + " numeric");
							buffWriteArff.append("\n");
							buffWriteArff.append("@attribute Pathologie {HC,Ne,NM,OF}"); //{HC,NEUR,SEP,NMO}
							buffWriteArff.append("\n\n");
							buffWriteArff.append("@data");
							buffWriteArff.append("\n");
							buffWriteArff.append(fileNames.get(i)+ ",");
						}
					}
					
					for(int h = 0; h < fileRows.size(); h++) {
						String[] rowParts = fileRows.get(h).split(",");
						if(h < fileRows.size()-1) {
							buffWriteArff.append(rowParts[1]);
							buffWriteArff.append(",");
						}else {
							buffWriteArff.append(rowParts[1] + ",?\n");
						}
					}
					
					
				}else {
					buffWriteArff.append(fileNames.get(i)+ ",");
					for(int j = 0; j < fileRows.size(); j++) {
						String[] rowParts = fileRows.get(j).split(",");
//						double freqDbl = Double.parseDouble(rowParts[0]);
//						if((freqDbl<3017 && freqDbl >2801) || (freqDbl<1772)) {
//							double intenDbl = Double.parseDouble(rowParts[1]);
//							fileIntensities.add(intenDbl);
//						}
						if(j < fileRows.size()-1) {
							buffWriteArff.append(rowParts[1]);
							buffWriteArff.append(",");
						}else {
							buffWriteArff.append(rowParts[1] + ",?\n");
						}
					}
				}
//				addAllIntensities.add(fileIntensities);
			}
//			intensities = addAllIntensities;
			buffWriteArff.close();
		}catch(IOException e) {
			System.out.println("Could not write into .arff file: " + e);
		}
		return storeFile;
	}
}
