package irisPackage;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ModelMedicalInputs extends Application {


	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("");
		stage.getIcons().add(new Image("blank-space.png"));
		Scene scene = new Scene(new Group(), 400, 370);
		scene.getStylesheets().add("IrisStyling.css");
		
		
		final Image logoIcon = new Image("Icon.png");
		final ImageView logoIconView = new ImageView(logoIcon);	
		final Text iris = new Text("Iris");
		iris.setId("iris-banner");
		final Label uniStra = new Label("Université de Strasbourg");
		uniStra.setId("unistra-banner");
		uniStra.setPadding(new Insets(7,0,0,0));
		uniStra.setAlignment(Pos.BOTTOM_CENTER);
		
		final GridPane logoPane = new GridPane();
		GridPane.setConstraints(logoIconView, 0, 0);
		GridPane.setConstraints(iris, 1, 0);
		GridPane.setConstraints(uniStra, 2, 0);
		logoPane.getChildren().addAll(logoIconView, iris,uniStra);
		logoPane.setHgap(10);
		
		
		Text measureSample = new Text("Measure Sample");
		measureSample.setUnderline(true);
		
		Label operatorName = new Label("Operator Name");
		operatorName.setPrefWidth(125);
		operatorName.setPadding(new Insets(4,0,0,0));
		TextField inputOperatorName = new TextField();
		HBox operatorHb = new HBox();
		operatorHb.setSpacing(10);
		operatorHb.getChildren().addAll(operatorName, inputOperatorName);
		
		Label patientID = new Label("Patient ID");
		patientID.setPrefWidth(125);
		patientID.setPadding(new Insets(4,0,0,0));
		TextField inputPatientID = new TextField();
		HBox patientHb = new HBox();
		patientHb.setSpacing(10);
	
		patientHb.getChildren().addAll(patientID, inputPatientID);
		
		Label diseaseID = new Label("Disease ID");
		diseaseID.setPrefWidth(125);
		diseaseID.setPadding(new Insets(4,0,0,0));
		TextField inputDiseaseID = new TextField();
		HBox diseaseHb = new HBox();
		diseaseHb.setSpacing(10);
		
		final Button continueBtn = new Button("Continue");
		continueBtn.setPrefWidth(180);
		
		
		diseaseHb.getChildren().addAll(diseaseID, inputDiseaseID);
		
		final VBox vbox = new VBox();
		vbox.setSpacing(20);
		vbox.setPadding(new Insets(20,0,0,20));
		vbox.getChildren().addAll(logoPane,measureSample,operatorHb, patientHb, diseaseHb, continueBtn);
		
		((Group) scene.getRoot()).getChildren().addAll(vbox);
		
		stage.setScene(scene);
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);

	}
}
