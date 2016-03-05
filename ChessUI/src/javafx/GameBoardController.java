package javafx;

import Game.*;
import Networking.*;
import Pieces.*;
import javafx.application.*;
import javafx.concurrent.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class GameBoardController implements Initializable {
    private static final Logger log = Logger.getLogger(GameBoardController.class.getName());
	@FXML Button forfeitButton;
	@FXML Button submitMoveButton;
	@FXML Button resetButton;
	@FXML Label turnIndicator;
    @FXML Label checkIndicator;
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
    private int port;
    private String ip;

    //TODO change so that selection is a piece that can replace the current piece
    String selection; //variable to determine which piece was selected to redeem

	private Piece m_selectedPiece = null;
    private boolean m_selectedPieceHasMoved = false;
    private Piece m_takenPiece = null;
	private Position m_oldPosition = null;
	private Position m_newPosition = null;
	private ArrayList<Position> m_validMoves = null;
    private ArrayList<Position> m_castleMoves = null;
    private boolean m_lastMoveWasCastleLeft = false;
    private boolean m_lastMoveWasCastleRight = false;
    private boolean m_lastMoveWasEnPassant = false;
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
		if(p != null && p.PieceColor == m_color){
			//We selected a new non null piece that we own.
            m_selectedPieceHasMoved = p.HasMoved();
			RemoveColoring();
			m_selectedPiece = p;
			m_oldPosition = new Position(i,j);
			m_newPosition = null;
		}
		else if(m_selectedPiece != null){
			// We have a piece selected and we want to move it
            //Enpassant
            Position pos = boardState.GetPositionForEnPassant(m_color);
            if(p == null && pos != null && pos.GetX() == i && pos.GetY() == j && m_selectedPiece.Type == PieceType.Pawn){
                //We're moving to position to enpassant
                m_newPosition = new Position(i,j);
                boardState.SetPiece(i,j, m_selectedPiece);
                if(m_color == Color.Black){
                    m_takenPiece = boardState.GetPiece(i-1,j);
                    boardState.SetPiece(i-1, j, null);
                }else{
                    m_takenPiece = boardState.GetPiece(i+1, j);
                    boardState.SetPiece(i+1, j, null);
                }
                boardState.SetPiece(m_oldPosition.GetX(), m_oldPosition.GetY(), null);
                m_lastMoveWasEnPassant = true;
                UpdateImagesFromBoardState();
                RemoveColoring();
                m_hasMoved = true;
            } else if(ListContainsPosition(i,j, m_validMoves)){
                if(p == null){
                    m_takenPiece = null;
                }
                else{
                    m_takenPiece = p;
                }
				m_newPosition = new Position(i, j);
				boardState.SetPiece(i, j, m_selectedPiece);
				boardState.SetPiece(m_oldPosition.GetX(), m_oldPosition.GetY(), null);
				UpdateImagesFromBoardState();
				RemoveColoring();
				m_hasMoved = true;
			}else if(ListContainsPosition(i,j, m_castleMoves)){
                if(j == 2) {
                    m_lastMoveWasCastleLeft = true;
                    boardState.Castle(m_color, true);
                }
                else {
                    m_lastMoveWasCastleRight = true;
                    boardState.Castle(m_color, false);
                }
                UpdateImagesFromBoardState();
                RemoveColoring();
                m_hasMoved = true;
            }
		}
		if(null != p && p.PieceColor == m_color) {
			m_validMoves = (ArrayList<Position>)boardState.GetCheckedValidMoves(i, j);
            m_castleMoves= new ArrayList<>();
            if(p.Type == PieceType.King) {
                int rank = m_color == Color.Black ? 0 : 7;
                if (boardState.CanCastleLeft(m_color))
                    m_castleMoves.add(new Position(rank, 2));
                if (boardState.CanCastleRight(m_color))
                    m_castleMoves.add(new Position(rank, 6));
            }
			if (m_validMoves != null) {
                for (Position validMove : m_validMoves) {
                    ColorRegion(validMove.GetX(), validMove.GetY());
                }
            }
            for(Position pos : m_castleMoves){
                ColorRegion(pos.GetX(), pos.GetY());
            }
		}
	}
	public void handleForfeitClick(){
			weQuit = true;
            returnToMainMenu(false);
	}
	public void handleReset(){
        if(!m_ourTurn)
            return;
        if(!m_hasMoved)
            return;
        if(!m_lastMoveWasCastleLeft && !m_lastMoveWasCastleRight) {
            if(m_lastMoveWasEnPassant) {
                boardState.SetPiece(m_oldPosition.GetX(), m_oldPosition.GetY(), m_selectedPiece);
                boardState.SetPiece(m_newPosition.GetX(), m_newPosition.GetY(), null);
                if(m_color == Color.Black) {
                    boardState.SetPiece(m_newPosition.GetX()-1, m_newPosition.GetY(), m_takenPiece);
                }else{
                    boardState.SetPiece(m_newPosition.GetX()+1, m_newPosition.GetY(), m_takenPiece);
                }
            }else {
                boardState.SetPiece(m_oldPosition.GetX(), m_oldPosition.GetY(), m_selectedPiece);
                if (m_takenPiece == null)
                    boardState.SetPiece(m_newPosition.GetX(), m_newPosition.GetY(), null);
                else
                    boardState.SetPiece(m_newPosition.GetX(), m_newPosition.GetY(), m_takenPiece);
                if (!m_selectedPieceHasMoved)
                    m_selectedPiece.UnsetHasMoved();
            }
        } else if(m_lastMoveWasCastleLeft){
            boardState.UnCastle(m_color, true);
        } else{
            boardState.UnCastle(m_color, false);
        }
            m_lastMoveWasEnPassant = false;
            m_lastMoveWasCastleRight = false;
            m_lastMoveWasCastleLeft = false;
            m_hasMoved = false;
            m_selectedPiece = null;
            m_newPosition = null;
            m_oldPosition = null;
            m_takenPiece = null;
            m_selectedPieceHasMoved = false;
            UpdateImagesFromBoardState();
	}
    /**
     * Handles the case where we lost
     */
    private void HandleCheckmate(){
        returnToMainMenu(false);
    }
	public void handleSubmitMoveClick(){
		if(!m_ourTurn)
			return;
		try{
			if(m_hasMoved) {
                if(!m_lastMoveWasCastleLeft && !m_lastMoveWasCastleRight) {
                    Move move = new Move(m_oldPosition, m_newPosition);
                    synchronized (lock) {
                        //Send the move object to the Server here
                        out.writeObject(new Packet(OpCode.UpdateBoard, id, move));
                    }

                }else if(m_lastMoveWasCastleLeft){
                    synchronized (lock) {
                        //Send the move object to the Server here
                        out.writeObject(new CastlePacket( id ,m_color, true));
                    }
                }else{
                    synchronized (lock) {
                        //Send the move object to the Server here
                        out.writeObject(new CastlePacket(id, m_color, false));
                    }
                }
                m_lastMoveWasEnPassant = false;
                m_lastMoveWasCastleLeft = false;
                m_lastMoveWasCastleRight = false;
                m_castleMoves = null;
                m_selectedPiece = null;
                m_selectedPieceHasMoved = false;
                m_hasMoved = false;
                m_oldPosition = null;
                m_newPosition = null;
                m_ourTurn = false;
                m_takenPiece = null;
                turnIndicator.setText("Opponents turn");
                checkIndicator.setText("");
			}

		} catch (SocketException ex){
            log.log(Level.FINE, "Socket closed", ex);
		} catch (Exception e){
            log.log(Level.FINE, "General exception caught.", e);
		}
	}
	public void setIn(ObjectInputStream in){
		this.in = in;
	}
	public void setOut(ObjectOutputStream out){ this.out = out; }
	public void setId(int id){ this.id = id; }
	public void setColor(Color color){ this.m_color = color;}
    public void setIp(String ip){ this.ip = ip;}
    public void setPort(int port) {this.port = port;}
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
                    Position pos = new Position(p.GetMove().GetEndX(), p.GetMove().GetEndY());
                    if(m_color == Color.White){
                        if(pos.GetX()-1 >= 0) {
                            Piece piece = boardState.GetPiece(pos.GetX() - 1, pos.GetY());
                            if (piece != null && piece.Type == PieceType.Pawn && piece.PieceColor == Color.White) {
                                boardState.SetPiece(pos.GetX() - 1, pos.GetY(), null);
                            }
                        }
                    }else{
                        if(pos.GetX()+1 < 8) {
                            Piece piece = boardState.GetPiece(pos.GetX() + 1, pos.GetY());
                            if (piece != null && piece.Type == PieceType.Pawn && piece.PieceColor == Color.Black) {
                                boardState.SetPiece(pos.GetX() + 1, pos.GetY(), null);
                            }
                        }
                    }
                    if(boardState.IsInCheckmate(m_color)) {
                        Platform.runLater(() -> checkIndicator.setText("You are in checkmate!!!"));
                        Platform.runLater(this::HandleCheckmate);
                    }
                    if(boardState.IsInCheck(m_color))
                        Platform.runLater(() -> checkIndicator.setText("You are in check!"));
                    else
                        Platform.runLater(() -> checkIndicator.setText(""));
					m_ourTurn = true;
					Platform.runLater(() -> setTurn(true));
					Platform.runLater(this::UpdateImagesFromBoardState);
					break;
				case QuitGame:
					//Other player quit Game
                    try{
						out.writeObject(new Packet(OpCode.QuitGame, id, null));
                    } catch(IOException ex){
                        log.log(Level.FINE, "Socket exception when writing.", ex);
                    }
                    otherPlayerQuit = true;
                    Platform.runLater(this::HandleOtherPlayerQuit);
					break;
                case Castle:
                    CastlePacket packet = (CastlePacket)p;
                    boardState.Castle(packet.Col,packet.Left);
                    Platform.runLater(() -> checkIndicator.setText(""));
                    Platform.runLater(() -> setTurn(true));
                    Platform.runLater(this::UpdateImagesFromBoardState);
                    break;
                default:
                    break;
			}
	}
	private void HandleOtherPlayerQuit(){
        returnToMainMenu(true);
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
                if("light".equals(r.getId()))
					color = "wheat";
				else
					color = "peru";
				r.setStyle("-fx-background-color:" +  color);
			}
		}
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
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
                                return null;
                            }
                            try {
                                Packet p = (Packet) in.readObject();
                                if(p == null)
                                    continue;
                                processPacket(p);
                            } catch (SocketTimeoutException ex) {
                                //This is okay. Makes it so we don't hang here forever
                                log.log(Level.FINE, "Normal timeoutexception", ex);
                            } catch (EOFException ex) {
                                //Something bad happened
                                log.log(Level.FINE, "EOF exception, server died", ex);
                                return null;
                            } catch (IOException ex) {
                                //Something bad happened
                                log.log(Level.FINE, "General ioexception something bad happened", ex);
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
                    log.log(Level.FINE, "Socket already closed", ex);
				}
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
        pawnButton.setOnAction(arg0 -> selection = pawnButton.getId());

        knightButton.setId("knight");
        knightButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("../images/white_knight.png"))));
        knightButton.setOnAction(arg0 -> selection = knightButton.getId());

        bishopButton.setId("bishop");
        bishopButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("../images/white_bishop.png"))));
        bishopButton.setOnAction(arg0 -> selection = bishopButton.getId());

        queenButton.setId("queen");
        queenButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("../images/white_queen.png"))));
        queenButton.setOnAction(arg0 -> selection = queenButton.getId());

        rookButton.setId("rook");
        rookButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("../images/white_rook.png"))));
        rookButton.setOnAction(arg0 -> selection = rookButton.getId());

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
    public void returnToMainMenu(boolean win){
        otherPlayerQuit = true;
        // Create return to main menu button
        ButtonType returnButton = new ButtonType("Return to Main Menu");

        // Create label and progress indicator
        String l = win ? "Won" : "Lost";
        Label label = new Label("You " + l + "!");
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

            Stage getstage = (Stage) gameBoard.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
            Parent root = loader.load();
            MainMenuController cont = loader.getController();
            cont.SetIP(ip);
            cont.SetPort(port);
            Scene scene = new Scene(root,600,400);
            scene.getStylesheets().add(getClass().getResource("MainMenu.css").toExternalForm());

            getstage.setScene(scene);
            getstage.show();
        }
        catch (Exception e){
            log.log(Level.FINE, "promotion error.", e);
        }
    }
}

