import acsse.csc2b.recieve.Seeder;
import acsse.csc2b.send.Leecher;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application
{
	int portNumber;
	VBox rootBox;
	Label choiceLabel;
	Button seederButton;
	Button leecherButton;
	Scene scene;
	
	public static void main(String[] args)
	{
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		portNumber = 2002;
		rootBox = new VBox();
		
		choiceLabel = new Label("Select a Mode:");
		
		seederButton = new Button("Seeder");
		
		leecherButton = new Button("Leecher");
		
		rootBox.getChildren().addAll(choiceLabel, seederButton, leecherButton);
		
		scene = new Scene(rootBox, 500, 500);
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		leecherButton.setOnAction(e -> makeLeecher(primaryStage));
		
		seederButton.setOnAction(e -> makeSeeder(primaryStage));
	}
	
	public void makeLeecher(Stage primaryStage)
	{
		rootBox.getChildren().clear();
		Leecher pane = new Leecher(portNumber);
		scene.setRoot(pane);
		primaryStage.setTitle("LEECHER");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public void makeSeeder(Stage primaryStage)
	{
		rootBox.getChildren().clear();
		Seeder pane = new Seeder(portNumber);
		scene.setRoot(pane);
		primaryStage.setTitle("SEEDER");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
