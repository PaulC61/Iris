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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ModelMedicalInstruction extends Application {

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage stage) throws Exception {
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
		
		
		Text instructionTxt = new Text("Place patient sample on ATR Crystal\nand then click 'Continue' to start the\nmeasurement");
		
		final GridPane instructionPane = new GridPane();
		instructionPane.getChildren().add(instructionTxt);
		
		final Button continueBtn = new Button("Continue");
		continueBtn.setPrefWidth(180);
		
		final VBox vbox = new VBox();
		vbox.setSpacing(20);
		vbox.setPadding(new Insets(20,0,0,20));
		vbox.getChildren().addAll(logoPane, instructionPane, continueBtn);
		
		((Group) scene.getRoot()).getChildren().addAll(vbox);
		
		stage.setScene(scene);
		stage.show();
		
	}

}
