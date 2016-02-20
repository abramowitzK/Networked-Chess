package javafx;

import Game.*;
import Networking.*;
import Pieces.*;
import javafx.application.Platform;
import javafx.concurrent.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameBoardController implements Initializable {

	@FXML Button forfeitButton;
	@FXML Button submitMoveButton;
	@FXML Button resetButton;
	@FXML Label turnIndicator;
    @FXML GridPane gameBoard;
	private boolean otherPlayerQuit = false;
	private boolean weQuit = false;
	private Board boardState;
    private ObjectInputStream in;
	private ObjectOutputStream out;
	private int id;
	private Color m_color;
	private boolean m_ourTurn;
	final private Object lock = new Object();
	private Piece m_selectedPiece = null;
	private Position m_oldPosition = null;
	private Position m_newPosition = null;
	private ArrayList<Position> m_validMoves = null;
	private boolean m_hasMoved;

    public void handleClick(MouseEvent e) {
		if(!m_ourTurn)
			return;
		if(m_hasMoved)
			return;
		ImageView view = (ImageView)e.getSource();
		int i = GridPane.getRowIndex(view);
		int j = GridPane.getColumnIndex(view);
		Piece p = boardState.GetPiece(i,j);
		if(p != null && p != m_selectedPiece && p.PieceColor == m_color){
			//We selected a new non null piece that we own.
			RemoveColoring();
			m_selectedPiece = p;
			m_oldPosition = new Position(i,j);
			m_newPosition = null;
		}
		else if(m_selectedPiece != null && (p == null || p.PieceColor != m_color)){
			// We have a piece selected and we want to move it
			if(ListContainsPosition(i,j, m_validMoves)){
				m_newPosition = new Position(i, j);
				boardState.SetPiece(i, j, m_selectedPiece);
				boardState.SetPiece(m_oldPosition.GetX(), m_oldPosition.GetY(), null);
				UpdateImagesFromBoardState();
				RemoveColoring();
				m_hasMoved = true;
			}
		}
		if(null != p && p.PieceColor == m_color) {
			m_validMoves = boardState.GetValidMoves(i, j);
			if (m_validMoves != null)
                for (Position validMove : m_validMoves) {
                    ColorRegion(validMove.GetX(), validMove.GetY());
                }
		}
	}
    
	public void handleForfeitClick(){	
		try{
			weQuit = true;
			System.out.println("You clicked Forfeit");
			Stage getstage = (Stage) forfeitButton.getScene().getWindow();
			Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
			Scene scene = new Scene(root,600,400);
			scene.getStylesheets().add(getClass().getResource("MainMenu.css").toExternalForm());
			getstage.setScene(scene);
			getstage.show();
		} catch (Exception e){
			e.printStackTrace();
		}

	}
	public void handleReset(){
		boardState.SetPiece(m_oldPosition.GetX(), m_oldPosition.GetY(), m_selectedPiece);
		boardState.SetPiece(m_newPosition.GetX(),m_newPosition.GetY(), null);
		m_hasMoved = false;
		m_selectedPiece = null;
		m_newPosition = null;
		m_oldPosition = null;
		UpdateImagesFromBoardState();
	}
	public void handleSubmitMoveClick(){
		if(!m_ourTurn)
			return;
		if(!m_hasMoved)
			return;
		try{
			if(m_hasMoved) {
				Move move = new Move(m_oldPosition, m_newPosition);
				synchronized (lock) {
					//Send the move object to the Server here
					out.writeObject(new Packet(OpCode.UpdateBoard, id, move));
				}
				m_hasMoved = false;
				m_oldPosition = null;
				m_newPosition = null;
				m_ourTurn = false;
				turnIndicator.setText("Opponents turn");
			}

		} catch (SocketException ex){
			System.out.println("Socket closed");
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	public void setIn(ObjectInputStream in){
		this.in = in;
	}
	public void setOut(ObjectOutputStream out){ this.out = out; }
	public void setId(int id){ this.id = id; }
	public void setColor(Color color){ this.m_color = color;}
	public void setTurn(boolean turn){
		m_ourTurn = turn;
		if(turn){
			turnIndicator.setText("Your turn");
		} else
			turnIndicator.setText("Opponents turn");
	}
	private void processPacket(Packet p){
			switch (p.GetOpCode()) {
				case UpdateBoard:
					//The other player made a move and we need to update our board.
					synchronized (lock) {
						boardState.ApplyMove(p.GetMove());
					}
					m_ourTurn = true;
					Platform.runLater(()-> turnIndicator.setText("Your turn"));
					Platform.runLater(this::UpdateImagesFromBoardState);
					break;
				case UpdatedBoard:
					//Response packet from Server confirming that we updated the board
					break;
				case QuitGame:
					//Other player quit Game
					System.out.println("Other player quit the Game!");
                    try{
					out.writeObject(new Packet(OpCode.QuitGame, id, null));
                    } catch(IOException ex){
                        System.out.println("Caught a socket exception");
                    }
                    otherPlayerQuit = true;
                    Platform.runLater(this::HandleOtherPlayerQuit);
					break;
			}
	}
	private void HandleOtherPlayerQuit(){
		Alert A = new Alert(Alert.AlertType.ERROR, "Other player quit!", ButtonType.FINISH);
		A.showAndWait();
		Stage getstage = (Stage) forfeitButton.getScene().getWindow();
		try {
			Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
			Scene scene = new Scene(root,600,400);
			scene.getStylesheets().add(getClass().getResource("MainMenu.css").toExternalForm());
			getstage.setScene(scene);
			getstage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private Node GetByRowColumn(int i, int j){
		for(Node n : gameBoard.getChildren()){
			if(GridPane.getRowIndex(n) == i && GridPane.getColumnIndex(n) == j){
				return n;
			}
		}
		return null;
	}
	private ImageView GetImageView(int i, int j){
		return (ImageView)gameBoard.lookup("#"+i+j);
	}
	private Region GetRegion(int i, int j){
		for(Node node : gameBoard.getChildren()){
			if(GridPane.getRowIndex(node) == i && GridPane.getColumnIndex(node)== j)
				return (Region)node;
		}
		return null;
	}
	private void ColorRegion(int i, int j){
		Region r = GetRegion(i,j);
        assert r != null;
        r.setStyle("-fx-background-color:yellow");
	}
	private void UpdateImagesFromBoardState(){
		for(int i = 0; i < 8;  i++){
			for(int j = 0; j < 8; j++){
				if(boardState.GetPiece(i,j) != null)
					GetImageView(i,j).setImage(boardState.GetPiece(i,j).PieceImage);
				else
					GetImageView(i,j).setImage(null);
			}
		}
	}
	private boolean ListContainsPosition(int i, int j, ArrayList<Position> list){
		for(Position p : list){
			if(p.GetX() == i && p.GetY() == j)
				return true;
		}
		return false;
	}
	private void RemoveColoring(){
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				Region r = GetRegion(i,j);
				String color;
                assert r != null;
                if(r.getId().equals("light"))
					color = "wheat";
				else
					color = "peru";
				r.setStyle("-fx-background-color:" +  color);
			}
		}
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Initializing...");
		boardState = new Board();
		//Create background task to communicate with Server
		UpdateImagesFromBoardState();
        Service<Void> backgroundTask = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {

                return new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {
                        while (true) {
                            //TODO go back to main menu if other player quits. Also show win screen
                            if (isCancelled() || otherPlayerQuit || weQuit) {
                                System.out.println("Quitting");
                                return null;
                            }
                            try {
                                Packet p = (Packet) in.readObject();
                                processPacket(p);

                            } catch (SocketTimeoutException ex) {
                                //This is okay. Makes it so we don't hang here forever
                            } catch (EOFException ex) {
                                //Something bad happened
                                return null;
                            } catch (IOException ex) {
                                //Something bad happened
                                return null;
                            }
                        }
                    }
                };
            }
        };
		backgroundTask.setOnCancelled(event -> System.out.println("Exiting background thread"));
		//Close the dialog box and transition to the Game board
		backgroundTask.setOnSucceeded(event -> {
				try {
					System.out.println("Informing server that we quit");
					out.writeObject(new Packet(OpCode.QuitGame, id, null));
				} catch (IOException ex) {
					System.out.println("failed to inform server we quit. Panic!");
				}
		});
		backgroundTask.start();
	}
}

