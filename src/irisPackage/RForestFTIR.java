package irisPackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;


public class RForestFTIR {
	protected String pathFolder;
	protected String fileName;
	private Instances sampleData;
	
	public RForestFTIR(File inputArff, String saveFilePath, String saveFileName) throws Exception {
		pathFolder = saveFilePath;
		fileName = saveFileName;
		ArffLoader loader = new ArffLoader();
		try {
			loader.setSource(inputArff);
			Instances inputData = loader.getDataSet();
			if(inputData.classIndex() == -1) {
				inputData.setClassIndex(inputData.numAttributes() - 1);
			}
			sampleData = inputData;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not load .arff file, check file input: " + e);
		}
		filterFrequencies();
	}
	
	public void filterFrequencies() throws Exception {
		String[] filterOpts = new String[] {"-R", "1-510, 622-1155"};
		Remove remove = new Remove();
		remove.setOptions(filterOpts);
		remove.setInputFormat(sampleData);
		Instances filteredData = Filter.useFilter(sampleData, remove);
		sampleData = filteredData;
	}
	
	public void filterArff() throws Exception {
		ArffSaver saver = new ArffSaver();
		filterFrequencies();
		saver.setInstances(sampleData);
		String saveFile = pathFolder + "\\" + fileName + "filtered.arff";
		saver.setFile(new File(saveFile));
		saver.writeBatch();
	}
	
	//retrieve saved model
	public Classifier retrieveSavedModel() throws Exception {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Model\\myRF.model"));
		Classifier cls = (Classifier) ois.readObject();
		ois.close();
		return cls;
	}
	
	//apply saved model
	public ArrayList<Double> applyModel() throws Exception {
		ArrayList<Double> classResults = new ArrayList<>();
		Classifier rfModel = retrieveSavedModel();
		for(int i = 0; i <sampleData.size(); i ++) {
			classResults.add(rfModel.classifyInstance(sampleData.get(i)));
		}
		return classResults;
	}
	
	
	// get interval with distributionForInstance()
	// estimated membership probabilities
	public ArrayList<double[]> instanceScore() throws Exception{
		ArrayList<double[]> classScores = new ArrayList<>();
		Classifier rfModel = retrieveSavedModel();
//		filterFrequencies();
		for(int i = 0; i < sampleData.size(); i++) {
			classScores.add(rfModel.distributionForInstance(sampleData.get(i)));
		}
		return classScores;
	}
}
