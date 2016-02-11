package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ChessFXMLController {
	@FXML private Button playGame;
	@FXML private Button quit;
	
	/**
	 * Initiate pop-up to show searching and send request to Server to place player in queue
	 */
	public void handleClick(){
		System.out.println("You clicked me");
		
	}

	/**
	 * Terminates the Game
	 */
	public void handleQuit(){
		System.exit(0);
	}
}
