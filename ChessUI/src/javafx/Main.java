package javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
			VBox root = loader.load();
			MainMenuController mainMenu = loader.getController();
			Parameters p = getParameters();
			mainMenu.SetIP(p.getRaw().get(1));
			mainMenu.SetPort(Integer.parseInt(p.getRaw().get(0)));
			Scene scene = new Scene(root,600,400);
			scene.getStylesheets().add(getClass().getResource("MainMenu.css").toExternalForm());
			
			Image image = new Image("/images/Chancellor_Piece.png");
			
			primaryStage.getIcons().add(image);
			primaryStage.setTitle("Play Chess");
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
