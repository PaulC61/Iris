package irisPackage;

import java.io.File;
import java.util.ArrayList;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

public class Stage {

	public static void main(String[] args) {
		ArrayList<File> presSampleFile = new ArrayList<>();
		presSampleFile.add(new File("Training\\NMO-16-AQ.dpt"));
		
		
		SampleArff presentSampleArff = new SampleArff(presSampleFile);
		TrainingSetArff trainingSet = new TrainingSetArff(new File("Training"));
		
		ApplicabilityDomain presDomain = new ApplicabilityDomain(presentSampleArff, trainingSet, 3);
		presDomain.domainChart();
		
		ArrayList<JFreeChart> chartLst = presDomain.sampleCharts();
		
		try {
			ChartUtilities.saveChartAsPNG(new File("SampleCharts\\reportSampleChart.png") , chartLst.get(0), 910, 697);
		}catch(Exception e) {
			System.out.println("Could not save chart to JPEG: " + e);
		}
		
	}

}
