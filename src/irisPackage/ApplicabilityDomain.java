package irisPackage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.io.File;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.RectangleInsets;


public class ApplicabilityDomain {
	int stdDevInterval;
	double domainMax;
	double domainMin;
	SampleArff sample_s;
	TrainingSetArff trainingSet;
	ArrayList<Double> sampleScores;
	ArrayList<ArrayList<Integer>> samplesBinaryIndex;
	//need to make these
	ArrayList<Double> domainAverage;
	ArrayList<Double> domainUpperBound;
	ArrayList<Double> domainLowerBound;
	
	public ApplicabilityDomain(SampleArff inputSamples, TrainingSetArff inputTrainingSet, int stdDev) {
		stdDevInterval = stdDev;
		sample_s = inputSamples;
		trainingSet = inputTrainingSet;
		ArrayList<Double> emptyDomainUpperBound = new ArrayList<>();
		ArrayList<Double> emptyDomainLowerBound = new ArrayList<>();
		domainUpperBound = emptyDomainUpperBound;
		domainLowerBound = emptyDomainLowerBound;
		setDomain();
		sampleScores = applicabilityScores();
		domainMax = maxFunc();
		domainMin = minFunc();
		samplesBinaryIndex = allSampleBinaryIndex();
	}
	
	private void setDomain() {
		domainAverage = trainingSet.averageIntensities();
		ArrayList<Double> intensityDomainStdDev = trainingSet.stdDevIntensities();
		for(int i = 0; i < domainAverage.size(); i++) {
			domainUpperBound.add(domainAverage.get(i) + 2*intensityDomainStdDev.get(i));
			domainLowerBound.add(domainAverage.get(i) - 2*intensityDomainStdDev.get(i));
		}
	}
	
	private double maxFunc() {
		double max = 0;
		for(double eachDouble : domainUpperBound) {
			if(eachDouble > max) {
				max = eachDouble;
			}
		}
		return max;
	}
	
	private double minFunc() {
		double min = 0;
		for(double eachDouble : domainLowerBound) {
			if(eachDouble < min) {
				min = eachDouble;
			}
		}
		return min;
	}
	
	private ArrayList<Integer> singleSampleBinaryIndex(int index){
		ArrayList<Integer> singleSampBinInd = new ArrayList<>();
		ArrayList<Double> intensityDomainAvgs = trainingSet.averageIntensities();
		ArrayList<Double> intensityDomainStdDev = trainingSet.stdDevIntensities();
		ArrayList<Double> indexSampleIntensities = sample_s.intensities.get(index);
		// for 68.3% of intensities at a given frequency
		for(int i = 0; i < indexSampleIntensities.size(); i++) { //sample_s.frequencies.size()
			double freqAvg = intensityDomainAvgs.get(i);
			double freqStdDev = intensityDomainStdDev.get(i);
			double upperLimit = freqAvg + (stdDevInterval*freqStdDev);
			double lowerLimit = freqAvg - (stdDevInterval*freqStdDev);
			if (indexSampleIntensities.get(i) < lowerLimit || indexSampleIntensities.get(i) > upperLimit) {
				singleSampBinInd.add(1);
			}else {
				singleSampBinInd.add(0);
			}
		}
		return singleSampBinInd;
	}
	
	private ArrayList<ArrayList<Integer>> allSampleBinaryIndex() {
		ArrayList<ArrayList<Integer>> allSamplesBinaryIndexLst = new ArrayList<>();
		for(int j = 0; j < sample_s.intensities.size(); j++) {
			ArrayList<Integer> eachSample = singleSampleBinaryIndex(j);
			allSamplesBinaryIndexLst.add(eachSample);
		}
		return allSamplesBinaryIndexLst;
	} 
	
	private double applicabilityScoreSingleSample(int sampleIndex) {
		int sumOutsideDomain = 0;
		ArrayList<Double> intensityDomainAvgs = trainingSet.averageIntensities();
		ArrayList<Double> intensityDomainStdDev = trainingSet.stdDevIntensities();
		ArrayList<Double> indexSampleIntensities = sample_s.intensities.get(sampleIndex);
		// for 68.3% of intensities at a given frequency
		for(int i = 0; i < intensityDomainAvgs.size(); i++) { //sample_s.frequencies.size()
			double freqAvg = intensityDomainAvgs.get(i);
			double freqStdDev = intensityDomainStdDev.get(i);
			double upperLimit = freqAvg + (stdDevInterval*freqStdDev);
			double lowerLimit = freqAvg - (stdDevInterval*freqStdDev);
			if (indexSampleIntensities.get(i) < lowerLimit || indexSampleIntensities.get(i) > upperLimit) {
				sumOutsideDomain = sumOutsideDomain + 1;
			}	
		}
		int totalFrequencies = sample_s.frequencies.size();
		double difference = totalFrequencies - sumOutsideDomain;
		double score = difference/totalFrequencies;
		double roundedScore = Math.round(score * 100.0)/100.0;
//		double scorePercent = score*100;
		return roundedScore;
	}
	
	
	private ArrayList<Double> applicabilityScores(){
		ArrayList<Double> scoreLst = new ArrayList<>();
		for(int j = 0; j < sample_s.intensities.size(); j++) {
			double singleSampleScore = applicabilityScoreSingleSample(j);
			scoreLst.add(singleSampleScore);
		}
		return scoreLst;
	}
	
	
	
	public JFreeChart domainChart() {
		// my new attempt, plot intensities on the same graph, label each with file name, 
		ArrayList<ArrayList<Double>> sampleSpectra = sample_s.intensities;
		
		ArrayList<Double> trainingAvgs = trainingSet.averageIntensities();
		ArrayList<Double> trainingStdDevs = trainingSet.stdDevIntensities();

		
		YIntervalSeriesCollection stdDevAvgStdDev1 = new YIntervalSeriesCollection();
		YIntervalSeriesCollection stdDevAvgStdDev2 = new YIntervalSeriesCollection();
		YIntervalSeries trainingDomain1 = new YIntervalSeries("");
		YIntervalSeries trainingDomain2 = new YIntervalSeries("");
		for(int i = 0; i < sample_s.frequencies.size(); i++) {
			double frequency = sample_s.frequencies.get(i);
			double stdDevX3 = 3*trainingStdDevs.get(i);
			double avg = trainingAvgs.get(i);
			double upper = avg + stdDevX3;
			double lower = avg - stdDevX3;
			if(frequency < 1772) {
				trainingDomain1.add(frequency, trainingAvgs.get(i), lower,upper);
			}else {
				trainingDomain2.add(frequency, trainingAvgs.get(i), lower, upper);
			}
			
		}
		
		stdDevAvgStdDev1.addSeries(trainingDomain1);
		stdDevAvgStdDev2.addSeries(trainingDomain2);
		
		DeviationRenderer renderer = new DeviationRenderer(true, false);
		renderer.setSeriesStroke(0, new BasicStroke(
				2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesFillPaint(0, new Color(255,96, 200));
		
		
		
		
		XYPlot firstRange = new XYPlot(stdDevAvgStdDev1, new NumberAxis("        Wavenumber / cm\u207B\u00B9"), null, renderer);
		XYPlot secondRange = new XYPlot(stdDevAvgStdDev2, new NumberAxis(""), null, renderer);
		
		Font labelFont = new Font("Calibri", Font.BOLD, 42);
		Font tickFont = new Font("Claibri", Font.PLAIN, 24);
		
		DecimalFormat format = new DecimalFormat("####");
		
		firstRange.setDomainPannable(true);
		firstRange.setRangePannable(false);
		firstRange.getDomainAxis().setInverted(true);
		firstRange.getDomainAxis().setRange(698, 1772);
		firstRange.getDomainAxis().setLabelLocation(AxisLabelLocation.LOW_END);
		firstRange.getDomainAxis().setLabelFont(labelFont);
		firstRange.getDomainAxis().setTickLabelFont(tickFont);
		firstRange.getDomainAxis().setTickLabelsVisible(true); //hide axes labels
		firstRange.getDomainAxis().setLabelInsets(new RectangleInsets(15,0,15,0));
		NumberAxis domainAxisFirst = (NumberAxis)firstRange.getDomainAxis();
		domainAxisFirst.setNumberFormatOverride(format);
	
		secondRange.setDomainPannable(true);
		secondRange.setRangePannable(false);
		secondRange.getDomainAxis().setInverted(true);
		secondRange.getDomainAxis().setRange(2801, 3017);
		secondRange.getDomainAxis().setLabel("");
		secondRange.getDomainAxis().setTickLabelFont(tickFont);
		secondRange.getDomainAxis().setTickLabelsVisible(true); // hide axes labels
		secondRange.getDomainAxis().setLabelInsets(new RectangleInsets(15,0,15,0));
		NumberAxis domainAxisSecond = (NumberAxis)secondRange.getDomainAxis();
		domainAxisSecond.setNumberFormatOverride(format);
		
		//Parent plot
		NumberAxis sharedAxis = new NumberAxis("Intensity (a.u.)");
		sharedAxis.setTickMarkInsideLength(10.0f);
		sharedAxis.setTickUnit(new NumberTickUnit(0.001));
		sharedAxis.setTickLabelsVisible(true);
		CombinedRangeXYPlot plot = new CombinedRangeXYPlot(sharedAxis);
		
		//add subplots...
		plot.add(secondRange, 1);
		plot.add(firstRange, 4);
		plot.getRangeAxis().setRange(domainMin*1.1, domainMax*1.1);
		plot.getRangeAxis().setLabelFont(labelFont);
		plot.getRangeAxis().setTickLabelFont(tickFont);
		

		JFreeChart chart = new JFreeChart(
				"", 
				plot);
		chart.setBackgroundPaint(Color.WHITE);
		chart.setPadding(new RectangleInsets(10,10,10,10));
		try {
			ChartUtilities.saveChartAsPNG(new File("C:\\Users\\Paul\\eclipse-workspace\\Iris\\DomainCharts\\presentChart.png"), chart, 895, 697);
		}catch(Exception e) {
			System.out.println("Could not save chart to JPEG: " + e);
		}
		
		return chart;
	}
	
	
	
	@SuppressWarnings("deprecation")
	public ArrayList<JFreeChart> sampleCharts(){
		ArrayList<JFreeChart> sampleChartList = new ArrayList<>();
		// for each sample
		for(int i = 0; i < sample_s.intensities.size(); i++) {
			
			XYSeries firstRangeDataSet = new XYSeries("");
			XYSeries secondRangeDataSet = new XYSeries("");
			
			for(int j = 0; j < sample_s.intensities.get(i).size(); j++) {
				double frequency = sample_s.frequencies.get(j);
				double intensity = sample_s.intensities.get(i).get(j);
				if(frequency < 1772) {
					firstRangeDataSet.add(frequency, intensity);
				}else {
					secondRangeDataSet.add(frequency, intensity);
				}	
			}
			
			XYSeriesCollection firstRangeCollection = new XYSeriesCollection();
			firstRangeCollection.addSeries(firstRangeDataSet);
			XYSeriesCollection secondRangeCollection = new XYSeriesCollection();
			secondRangeCollection.addSeries(secondRangeDataSet);
			
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			renderer.setSeriesFillPaint(0, new Color(255, 200, 255));
			renderer.setShapesVisible(false);
			renderer.setStroke(new BasicStroke(2.0f));
			renderer.setOutlineStroke(new BasicStroke(8.0f));
			
			
			XYPlot firstRange = new XYPlot(firstRangeCollection, new NumberAxis("        Wavenumber / cm\u207B\u00B9"), null, renderer);
			XYPlot secondRange = new XYPlot(secondRangeCollection, new NumberAxis("Wavenumber /cm"), null, renderer);
			
			Font labelFont = new Font("Calibri", Font.BOLD, 42);
			Font tickFont = new Font("Claibri", Font.PLAIN, 24);
			
			DecimalFormat format = new DecimalFormat("####");
			
			firstRange.setDomainPannable(true);
			firstRange.setRangePannable(false);
			firstRange.getDomainAxis().setInverted(true);
			firstRange.getDomainAxis().setRange(698, 1772);
			firstRange.getDomainAxis().setLabelLocation(AxisLabelLocation.LOW_END);
			firstRange.getDomainAxis().setLabelFont(labelFont);
			firstRange.getDomainAxis().setTickLabelFont(tickFont);
			firstRange.getDomainAxis().setTickLabelsVisible(true);
			firstRange.getDomainAxis().setLabelInsets(new RectangleInsets(15,0,15,0));
			NumberAxis domainAxisFirst = (NumberAxis)firstRange.getDomainAxis();
			domainAxisFirst.setNumberFormatOverride(format);
			
			
			secondRange.setDomainPannable(true);
			secondRange.setRangePannable(false);
			secondRange.getDomainAxis().setInverted(true);
			secondRange.getDomainAxis().setRange(2801, 3017);
			secondRange.getDomainAxis().setLabel("");
			secondRange.getDomainAxis().setTickLabelFont(tickFont);
			secondRange.getDomainAxis().setTickLabelsVisible(true);
			NumberAxis domainAxisSecond = (NumberAxis)secondRange.getDomainAxis();
			domainAxisSecond.setNumberFormatOverride(format);
			
			
			//Parent plot
			NumberAxis sharedAxis = new NumberAxis("Intensity (a.u.)");
			sharedAxis.setLabelInsets(new RectangleInsets(0,15,0,15));
			sharedAxis.setTickMarkInsideLength(10.0f);
			sharedAxis.setTickUnit(new NumberTickUnit(0.001));
			sharedAxis.setTickLabelsVisible(true); // hiding values for presentation
			CombinedRangeXYPlot plot = new CombinedRangeXYPlot(sharedAxis);
			
			
			
			//add subplots...
			plot.add(secondRange, 1);
			plot.add(firstRange, 4);
			plot.getRangeAxis().setRange(domainMin*1.4, domainMax*1.2);
			plot.getRangeAxis().setLabelFont(labelFont);
			plot.setOutlineStroke(new BasicStroke(10.0f));
			plot.getRangeAxis().setTickLabelFont(tickFont);
			
			JFreeChart chart = new JFreeChart(
					"", 
					plot);
			chart.setBackgroundPaint(Color.WHITE);
			chart.setPadding(new RectangleInsets(10,10,10,10));
			sampleChartList.add(chart);
			
		}
		
		
		return sampleChartList;
	}
	
//	public ChartPanel createDomainPanel() {
//		JFreeChart chart = domainChart();
//		ChartPanel chartFrame = new ChartPanel(chart);
//		return chartFrame;
//	}
	
}
