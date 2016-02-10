package javafx;

import java.io.IOException;
import java.util.Optional;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenuController {
	@FXML private Button playGameButton;
	@FXML private Button quitGameButton;
	private Service<Void> backgroundTask;
	/**
	 * Initiate pop-up to show searching and send request to server to place player in queue 
	 */
	public void handleClick(){
		try{
			// Create cancel button
			ButtonType cancelButtonType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
			
			// Create label and progress indicator
			Label label = new Label("Searching for opponent");
			ProgressIndicator p = new ProgressIndicator();
			
			// Create and add VBox to hold progress indicator and searching text
			VBox vbox = new VBox(); 
			vbox.getChildren().addAll(p,label);
			
			Alert a = new Alert(AlertType.NONE);
			
			//change default icon of window to chess piece
			Image image = new Image("/images/Chancellor_Piece.png");
			Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
			stage.getIcons().add(image);
			
			// Add vbox and cancel button
			a.getButtonTypes().add(cancelButtonType);
			a.getDialogPane().setContent(vbox);
			a.setTitle("Play Chess");
			
			
			//Create background task to communicate with server
			backgroundTask = new Service<Void>() {
				@Override
				protected Task<Void> createTask() {
					
					return new Task<Void>(){
						
						@Override
						protected Void call() throws Exception 
						{
							//TODO implement code  to send packet to server	
							
							int i =0;
							while(i<6){
								if(isCancelled()){
									break;
								}
								System.out.println("Doing something");
								i++;
							 try {
					                Thread.sleep(1000);
					            } catch (InterruptedException interrupted) {
					                if (isCancelled()) {
					                    updateMessage("Cancelled");
					                    break;
					                }
					            }							
							}
							return null;
						}	
					};
				}
			};
			
			//Remove player from queue when Cancel button is pressed while searching
			backgroundTask.setOnCancelled(new EventHandler<WorkerStateEvent>(){

				@Override
				public void handle(WorkerStateEvent event) {
					System.out.println("Handeled Cancel");
				}
			});
			
			//Close the dialog box and transition to the game board
			backgroundTask.setOnSucceeded(new EventHandler<WorkerStateEvent>(){

				@Override
				public void handle(WorkerStateEvent event) {
					
					a.close();
					
					// Transitions the UI to the GameBoard and loads the GameBoard FXML and CSS file
					Stage getstage = (Stage) playGameButton.getScene().getWindow();
					Parent root;
					try {
						root = FXMLLoader.load(getClass().getResource("GameBoard.fxml"));
						Scene scene = new Scene(root,800,600);
						scene.getStylesheets().add(getClass().getResource("GameBoard.css").toExternalForm());
						
						getstage.setScene(scene);
						getstage.show();
					} catch (IOException e) {
						e.printStackTrace();
					}	
				}
				
			});
			
			backgroundTask.start();
			
			//show dialog box
			Optional<ButtonType> result = a.showAndWait();
			 if (result.isPresent()) {
				 System.out.println("You clicked Cancel");
				 backgroundTask.cancel();
			 }
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Terminates the game
	 */
	public void handleQuit(){
		System.exit(0);
	}
}