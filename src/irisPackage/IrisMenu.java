package irisPackage;

import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class IrisMenu extends Application {
	private Desktop desktop = Desktop.getDesktop();
	private ArrayList<String> loadedFileNames = new ArrayList<>();
	private SampleArff currentArff;
	private TrainingSetArff modelArff = new TrainingSetArff(new File("Training\\"));
	private TableView resultsTable = new TableView();

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("");
		Scene scene = new Scene(new Group(), 700, 650);
		
		final Image logoIcon = new Image("Icon.png");
		final ImageView logoIconView = new ImageView(logoIcon);
		
		// model buttons
		final Button modelInfo = new Button("Model Info");
//		modelInfo.setPadding(new Insets(15));
		final Button applyModel = new Button("Apply Model");
		applyModel.setId("apply-model");
		
		// file buttons
		final Button openFilesButton = new Button("Load Sample File");
		
		final Button setFolderButton = new Button("Default Folder");
		
		// initialise fileChooser
		final FileChooser fileChooser = new FileChooser();
		
		// initialise ListView
		final ObservableList<String> items = FXCollections.observableArrayList(loadedFileNames);
		final ListView<String> list = new ListView<String>(items);
		list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	
		
		// initialise TableView
		resultsTable.setEditable(true);
		TableColumn fileNameCol = new TableColumn("Patient File");
		fileNameCol.setResizable(false);
		fileNameCol.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.20));
		
		fileNameCol.setCellValueFactory(new PropertyValueFactory<Results, String>("fileName"));
		
		TableColumn appDomain = new TableColumn("Spectrum Score");
		appDomain.setCellValueFactory(new PropertyValueFactory<Results, String>("domainScore"));
		appDomain.setResizable(false);
		appDomain.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.20));
		
		TableColumn diseaseCol = new TableColumn("Diagnosis");
		diseaseCol.setCellValueFactory(new PropertyValueFactory<Results, String>("diseaseClassification"));
		diseaseCol.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.20));
		
		TableColumn disProbCol = new TableColumn("Diagnosis Score");
		disProbCol.setCellValueFactory(new PropertyValueFactory<Results, String>("wekaScore"));
		disProbCol.setResizable(false);
		disProbCol.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.20));
		
		TableColumn commentsCol = new TableColumn("Comments");
		commentsCol.setCellValueFactory(new PropertyValueFactory<Results, String>("comment"));
		commentsCol.prefWidthProperty().bind(resultsTable.widthProperty().multiply(0.20));
		
		resultsTable.getColumns().addAll(fileNameCol, appDomain, diseaseCol, disProbCol, commentsCol);
		
		
		
		// Label items
		final Label fileFieldLabel = new Label("Selected Files:");
		final Label resultFieldLabel = new Label("Results: ");
		
		final Text iris = new Text("Iris");
		iris.setId("iris-banner");
		final Label uniStra = new Label("Université de Strasbourg");
		uniStra.setId("unistra-banner");
		uniStra.setPadding(new Insets(7,0,0,0));
		uniStra.setAlignment(Pos.BOTTOM_CENTER);
		
		openFilesButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				configureFileChooser(fileChooser);
				List<File> fileLst = fileChooser.showOpenMultipleDialog(stage);
				if(fileLst != null) {
					ArrayList<File> parsedFiles = new ArrayList<File>(fileLst);
					SampleArff sampleArff = new SampleArff(parsedFiles);
					currentArff = sampleArff;
					ArrayList<String> sampleFileNames = new ArrayList<String>(sampleArff.fileNames);
					items.setAll(sampleFileNames);
					list.setItems(items);
				}
			}
		});
		
		setFolderButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				final DirectoryChooser directoryChooser = new DirectoryChooser();
				final File selectedDirectory = directoryChooser.showDialog(stage);
				if(selectedDirectory != null) {
					selectedDirectory.getAbsolutePath();
				}
				fileChooser.setInitialDirectory(selectedDirectory);
			}
		});
		
		applyModel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				resultsTable.getItems().clear();
				if(currentArff != null) {
					// for the model
					File sampleArff = currentArff.createArff();
					try {
						RForestFTIR model = new RForestFTIR(sampleArff, fileChooser.getInitialDirectory().toString(), currentArff.saveFileName);
						ApplicabilityDomain domain = new ApplicabilityDomain(currentArff, modelArff, 2);
						for(int i = 0; i < items.size(); i++) {
							Results result = new Results(model, domain, i);
							resultsTable.getItems().add(result);
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						System.out.println("Could not initialise model: " + e1);
					}
					
					resultsTable.autosize();
				}
			}
		});
		
		final GridPane logoPane = new GridPane();
		GridPane.setConstraints(logoIconView, 0, 0);
		GridPane.setConstraints(iris, 1, 0);
		GridPane.setConstraints(uniStra, 2, 0);
		logoPane.getChildren().addAll(logoIconView, iris,uniStra);
		logoPane.setHgap(10);
		
		final GridPane selectFilesPane = new GridPane();
		GridPane.setConstraints(fileFieldLabel, 0, 0);
		GridPane.setConstraints(list, 0, 1);
		selectFilesPane.getChildren().addAll(fileFieldLabel, list);
		
		final GridPane sampleSelectGrid = new GridPane();
		GridPane.setConstraints(openFilesButton, 0, 0);
		GridPane.setConstraints(setFolderButton, 1, 0);
		sampleSelectGrid.getChildren().addAll(openFilesButton, setFolderButton);
		
		final GridPane resultsPane = new GridPane();
		GridPane.setConstraints(resultFieldLabel, 0, 0);
		GridPane.setConstraints(resultsTable, 0, 1);
		resultsPane.getChildren().addAll(resultFieldLabel, resultsTable);
		
		// model buttons placed in vertical grid pane
		final GridPane modelGrid = new GridPane();
		modelInfo.setAlignment(Pos.CENTER);
		applyModel.setAlignment(Pos.CENTER);
		GridPane.setConstraints(applyModel, 0, 0);
		GridPane.setConstraints(modelInfo, 1, 0);
		modelGrid.getChildren().addAll(applyModel, modelInfo);

		
		
		final VBox vbox = new VBox();
		vbox.setSpacing(20);
		vbox.setPadding(new Insets(20,0,0,20));
		vbox.getChildren().addAll(logoPane,selectFilesPane, sampleSelectGrid, resultsPane, modelGrid);
		
		((Group) scene.getRoot()).getChildren().addAll(vbox);
//		scene.getRoot().getStyleClass().add(getClass().getResource("IrisStyling.css").toExternalForm());
		list.setPrefHeight(133);
		list.setPrefWidth(645);
		selectFilesPane.setVgap(15);
		resultsPane.setVgap(15);
		openFilesButton.setPrefWidth(180);
		applyModel.setPrefWidth(180);
		setFolderButton.setPrefWidth(180);
		modelInfo.setPrefWidth(180);
		sampleSelectGrid.setHgap(40);
		modelGrid.setHgap(40);
		resultsTable.setPrefHeight(146);
		resultsTable.setPrefWidth(645);

		
		scene.getStylesheets().add("IrisStyling.css");
		
		applyModel.setTextFill(Color.WHITE);
		stage.getIcons().add(new Image("blank-space.png"));
	//	stage.getIcons().add(new Image("C:\\Users\\Paul\\eclipse-workspace\\Iris\\Images\\Icon-1.png"));
		stage.setScene(scene);
		stage.show();
	
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.setTitle("Select Samples");
		fileChooser.setInitialDirectory(new File("C:\\Users\\Paul\\eclipse-workspace\\Iris\\Presentation"));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("dpt", "*.dpt"),
				new FileChooser.ExtensionFilter("CSV", "*.csv"));
	}

}
