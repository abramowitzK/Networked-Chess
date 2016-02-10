package javafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GameBoardController {

	@FXML Button forfeitButton;
	@FXML Button submitMoveButton;
    @FXML GridPane gameBoard;
    
    public void handleClick(MouseEvent e){
    	Node source = (Node) e.getSource();
    	System.out.println( source.getId() );
    }
    
	public void handleForfeitClick(){	
		try{
			System.out.println("You clicked Forfeit");
			Stage getstage = (Stage) forfeitButton.getScene().getWindow();
			Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
			
			Scene scene = new Scene(root,600,400);
			scene.getStylesheets().add(getClass().getResource("MainMenu.css").toExternalForm());
			
			getstage.setScene(scene);
			getstage.show();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void handleSubmitMoveClick(){
		try{
			System.out.println("You clicked SubmitMove");
			Stage getstage = (Stage) forfeitButton.getScene().getWindow();
			Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
			
			Scene scene = new Scene(root,600,400);
			scene.getStylesheets().add(getClass().getResource("MainMenu.css").toExternalForm());
			
			getstage.setScene(scene);
			getstage.show();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
