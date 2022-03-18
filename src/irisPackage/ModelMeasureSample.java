package irisPackage;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ModelMeasureSample extends Application {

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
		stage.setTitle("");
		stage.getIcons().add(new Image("blank-space.png"));
		Scene scene = new Scene(new Group(), 370, 250);
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
		
		Text measureSampleTxt = new Text("Measuring sample. Please wait...");
		
		final GridPane measureSamplePne = new GridPane();
		measureSamplePne.getChildren().add(measureSampleTxt);
		measureSamplePne.setPadding(new Insets(10,0,0,45));
		
		final ProgressBar progressBar = new ProgressBar(0);
		progressBar.setProgress(1.0);
		progressBar.setPrefWidth(200);
		progressBar.setPadding(new Insets(0,0,0,77));
		
		final VBox vbox = new VBox();
		vbox.setSpacing(20);
		vbox.setPadding(new Insets(20,0,0,20));
		vbox.getChildren().addAll(logoPane, measureSamplePne, progressBar);
		
		((Group) scene.getRoot()).getChildren().addAll(vbox);
		
		stage.setScene(scene);
		stage.show();
	}

}
