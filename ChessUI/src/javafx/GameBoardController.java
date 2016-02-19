package javafx;

import Game.Board;
import Game.Move;
import Pieces.Piece;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import Networking.OpCode;
import Networking.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class GameBoardController implements Initializable {

	@FXML Button forfeitButton;
	@FXML Button submitMoveButton;
    @FXML GridPane gameBoard;
	private boolean otherPlayerQuit = false;
	private boolean weQuit = false;
	private Board boardState;
	private Service<Void> backgroundTask;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private int id;
	private Move move = null;
	final private Object lock = new Object();

    //TODO change so that selection is a piece that can replace the current piece
    String selection; //variable to determine which piece was selected to redeem


    public void handleClick(MouseEvent e) {
    	Node source = (Node) e.getSource();
		ImageView iv = (ImageView)e.getSource();
		System.out.println(iv.toString());
		//TODO Generate moveset here.
		//TODO Also pick which move and set the move member variable
    	System.out.println( source.getId() );
		if(otherPlayerQuit){
			try {
				synchronized (lock) {
					out.writeObject(new Packet(OpCode.QuitGame, id, null));
				}
			}
			catch (IOException ex){
				System.out.println("Socket already closed by Server");
			}
			Alert a = new Alert(Alert.AlertType.INFORMATION);
			a.setTitle("Warning!");
			a.setContentText("Other Player quit Game! Returning to main menu");
			a.showAndWait();
			Stage getstage = (Stage) forfeitButton.getScene().getWindow();
			Parent root = null;
			try {
				 root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
			}
			catch (IOException ex){
				ex.printStackTrace();
			}
			Scene scene = new Scene(root, 600, 400);
			scene.getStylesheets().add(getClass().getResource("MainMenu.css").toExternalForm());

			getstage.setScene(scene);
			getstage.show();
			}
		}
    
	public void handleForfeitClick(){	
		try{
			weQuit = true;
			System.out.println("You clicked Forfeit");
			Stage getstage = (Stage) forfeitButton.getScene().getWindow();
			Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
			//tell Server we're quitting.
			synchronized (lock){
				out.writeObject(new Packet(OpCode.QuitGame, id, null));
			}
			Scene scene = new Scene(root,600,400);
			scene.getStylesheets().add(getClass().getResource("MainMenu.css").toExternalForm());
			
			getstage.setScene(scene);
			getstage.show();
			in.close();
			out.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}

	}
	
	public void handleSubmitMoveClick(){
		try{
			System.out.println("You clicked SubmitMove");
			//TODO send move object here.
			Stage getstage = (Stage) forfeitButton.getScene().getWindow();
			Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
			synchronized (lock) {
				//Send the move object to the Server here
				out.writeObject(new Packet(OpCode.UpdateBoard, id, move));
				//Expected to get an OpCode.UpdatedBoard packet here.
				in.readObject();
			}
			Scene scene = new Scene(root,600,400);
			scene.getStylesheets().add(getClass().getResource("MainMenu.css").toExternalForm());
			
			getstage.setScene(scene);
			getstage.show();
		}
		catch (SocketException ex){
			System.out.println("Socket closed");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	public void setIn(ObjectInputStream in){
		this.in = in;
	}
	public void setOut(ObjectOutputStream out){ this.out = out; }
	public void setId(int id){ this.id = id; }
	public void processPacket(Packet p){
		try {
			switch (p.GetOpCode()) {
				case UpdateBoard:
					//The other player made a move and we need to update our board.
					synchronized (lock) {
						boardState.ApplyMove(p.GetMove());
					}
					break;
				case UpdatedBoard:
					//Response packet from Server confirming that we updated the board
					break;
				case QuitGame:
					//Other player quit Game
					System.out.println("Other player quit the Game!");
					otherPlayerQuit = true;
					break;
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
	private Node GetByRowColumn(int i, int j){
		for(Node n : gameBoard.getChildren()){
			if(gameBoard.getRowIndex(n) == i && gameBoard.getColumnIndex(n) == j){
				return n;
			}
		}
		return null;
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Initialized");
		boardState = new Board();
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				Piece temp = boardState.GetBoardCell(i,j).GetPiece();
				if(null == temp)
					continue;
				temp.SetImage(((ImageView)GetByRowColumn(i,j)).getImage());
			}
		}
		//Create background task to communicate with Server
		backgroundTask = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {

				return new Task<Void>(){

					@Override
					protected Void call() throws Exception {
							while (true) {
								System.out.println("asdfa");
								//TODO go back to main menu if other player quits. Also show win screen
								if (isCancelled() || otherPlayerQuit || weQuit) {
									System.out.println("Quitting");
									return null;
								}
								try {
									Packet p = (Packet) in.readObject();
									processPacket(p);
								}
								catch (IOException ex) {
									ex.printStackTrace();
								}
							}
					}
				};
			}
		};
		backgroundTask.setOnCancelled(new EventHandler<WorkerStateEvent>(){
			@Override
			public void handle(WorkerStateEvent event)  {
				System.out.println("Handeled Cancel");
				//TODO put disconnect code here. Have to implement that on Server first.
			}
		});
		//Close the dialog box and transition to the Game board
		backgroundTask.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
			@Override
			public void handle(WorkerStateEvent event) {}

		});
		backgroundTask.start();
	}

    /**
     * Called when a pawn moves to the opponent's side of the board
     * //TODO add an argument to the method so that depending on whose turn it is, the appropriate piece colors can be shown and replaced
     */
    public void redeemPiece()
    {
        // Create ok button
        ButtonType okButton = new ButtonType("Ok");

        // Create label
        Label label = new Label("Choose a piece to redeem");
        label.setFont(Font.font("Arial", 18));

        // Create and add VBox to hold piece selector and label
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        VBox.setMargin(vbox, new Insets(5));

        //Create GridPane to hold buttons
        GridPane redeemGrid = new GridPane();

        //Create buttons
        Button pawnButton = new Button();
        Button knightButton = new Button();
        Button bishopButton = new Button();
        Button queenButton = new Button();
        Button rookButton = new Button();

        // Set piece graphic, ID, and event handler
        pawnButton.setId("pawn");
        pawnButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("../images/white_pawn.png")))); //TODO abstract this somehow
        pawnButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
                selection = pawnButton.getId();
            }
        });

        knightButton.setId("knight");
        knightButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("../images/white_knight.png"))));
        knightButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
                selection = knightButton.getId();
            }
        });

        bishopButton.setId("bishop");
        bishopButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("../images/white_bishop.png"))));
        bishopButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
                selection = bishopButton.getId();
            }
        });

        queenButton.setId("queen");
        queenButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("../images/white_queen.png"))));
        queenButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
                selection = queenButton.getId();
            }
        });

        rookButton.setId("rook");
        rookButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("../images/white_rook.png"))));
        rookButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
                selection = rookButton.getId();
            }
        });

        // add buttons to grid pane
        redeemGrid.add(pawnButton,0,0);
        redeemGrid.add(bishopButton, 1, 0);
        redeemGrid.add(knightButton, 2, 0);
        redeemGrid.add(rookButton, 3, 0);
        redeemGrid.add(queenButton, 4, 0);

        // add grid pane and label to vbox
        vbox.getChildren().addAll(label,redeemGrid);

        // Create alert box
        Alert a = new Alert(Alert.AlertType.NONE);

        // Add vbox and OK button
        a.getButtonTypes().add(okButton);
        a.getDialogPane().setContent(vbox);
        a.setTitle("Play Chess");

        //change default icon of window to chess piece
        Image image = new Image("/images/Chancellor_Piece.png");
        Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
        stage.getIcons().add(image);

        //show dialog box
        Optional<ButtonType> result = a.showAndWait();
        while (!result.isPresent() || result.get() != okButton || selection == null) {
            result = a.showAndWait();
        }
        System.out.println("You selected " + selection );
        a.close();
    }

    /**
     * TODO Change the return value to the a Piece object
     * Returns the piece that the player selected to redeem
     * @return piece
     */
    public String getSelection(){
        return selection;
    }

    /**
     * TODO Add logic to determine whether to show 'You Lose' or 'You Win'
     * Called at the end of a game and returns the player to the main menu
     */
    public void returnToMainMenu(){

        // Create return to main menu button
        ButtonType returnButton = new ButtonType("Return to Main Menu");

        // Create label and progress indicator
        Label label = new Label("You Win!");
        label.setFont(Font.font("Arial", 18));

        //Create alert box
        Alert a = new Alert(Alert.AlertType.NONE);

        // Add label
        a.getButtonTypes().add(returnButton);
        a.getDialogPane().setContent(label);
        a.setTitle("Play Chess");

        //change default icon of window to chess piece
        Image image = new Image("/images/Chancellor_Piece.png");
        Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
        stage.getIcons().add(image);

        //show dialog box until 'Return to Main Menu' is pressed
        Optional<ButtonType> result = a.showAndWait();
        while (!result.isPresent()|| result.get() != returnButton) {
            result = a.showAndWait();
        }
        // switch scene to main menu
        try{
            a.close();
            System.out.println("You clicked Return to Main Menu");

            Stage getstage = (Stage) gameBoard.getScene().getWindow();
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

