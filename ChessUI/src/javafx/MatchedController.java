package javafx;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.*;

public class MatchedController {
	private static final Logger log = Logger.getLogger(MatchedController.class.getName());
	@FXML Button beginButton;
	@FXML Button leaveButton;
	
	public void handleBegin(){
		// Transitions the UI to the GameBoard and loads the GameBoard FXML and CSS file
		Stage getstage = (Stage) beginButton.getScene().getWindow();
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("GameBoard.fxml"));
			Scene scene = new Scene(root,800,600);
			scene.getStylesheets().add(getClass().getResource("GameBoard.css").toExternalForm());
			getstage.setScene(scene);
			getstage.sizeToScene();
			getstage.show();
			// TODO Add background task to send to server and wait for other person to press begin
		} catch (IOException e) {
			log.log(Level.FINE, "Error in begin", e);
		}	
	}
	public void handleLeave(){
		try{
			Stage getstage = (Stage) leaveButton.getScene().getWindow();
			Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
			Scene scene = new Scene(root,600,400);
			scene.getStylesheets().add(getClass().getResource("MainMenu.css").toExternalForm());
			getstage.setScene(scene);
			getstage.show();
			//TODO Remove player from queue and notify other user that they left
		} catch (Exception e){
			log.log(Level.FINE, "Error in leave", e);
		}
	}
}
