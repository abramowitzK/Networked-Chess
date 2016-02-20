package javafx;

import Networking.*;
import Pieces.Color;
import javafx.concurrent.*;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.Optional;

public class MainMenuController {
	@FXML private Button playGameButton;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private int port;
	private String serverIP;
    private int id;
	private Color color;
	/**
	 * Initiate pop-up to show searching and send request to Server to place player in queue
	 */
	public void SetPort(int port){
		this.port = port;
	}
	public void SetIP(String ip){
		this.serverIP = ip;
	}
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
			final Alert a = new Alert(AlertType.NONE);
			//change default icon of window to chess piece
			Image image = new Image("/images/Chancellor_Piece.png");
			Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
			stage.getIcons().add(image);
			// Add vbox and cancel button
			a.getButtonTypes().add(cancelButtonType);
			a.getDialogPane().setContent(vbox);
			a.setTitle("Play Chess");
			//Create background task to communicate with Server
			Service<Void> backgroundTask = new Service<Void>() {
				@Override
				protected Task<Void> createTask() {
					return new Task<Void>() {
						@Override
						protected Void call() throws Exception {
							//Connect to Server. Local host for now
							socket = new Socket(serverIP, port);
							out = new ObjectOutputStream(socket.getOutputStream());
							in = new ObjectInputStream(socket.getInputStream());
							//Send a join queue packet which should put us in the queue
							//The -1 signifies we're a new Client
							Packet p = new Packet(OpCode.JoinQueue, -1, null);
							out.writeObject(p);
							//Server should send a confirmation with our player id
							Packet confirm = (Packet) in.readObject();
							//Should be a joined queue packet. Let's check to make sure
							assert (confirm.GetOpCode() == OpCode.JoinedQueue);
							id = confirm.GetID();
							//Set timeout for checking cancellation
							socket.setSoTimeout(100);
							//If we got here. We joined the queue. Now we need to wait for the Server to tell us to do something
							Packet joinGame;
							while (true) {
								if (isCancelled()) {
									System.out.println("Cancelling task...");
									return null;
								}
								try {
									joinGame = (Packet) in.readObject();
									//If we don't time out. We succeeded. Break out of loop
									break;
								} catch (SocketTimeoutException e) {
									//Okay this is expected if we wait a while.
								}
							}
							socket.setSoTimeout(1000);
							//Expecting the Server to tell us to join Game. We'll block until we do. This is a thread so it won't
							//block the UI
							assert (null != joinGame && joinGame.GetOpCode() == OpCode.JoinGame);
							color = ((StartGamePacket) joinGame).GetColor();
							//If we're here we joined the Game and need to continue.
							return null;
						}
					};
				}
			};
            //Remove player from queue when Cancel button is pressed while searching
            backgroundTask.setOnCancelled(event -> {
                System.out.println("Handled Cancel");
                //TODO put disconnect code here. Have to implement that on Server first.
            });
			//Close the dialog box and transition to the Game board
			backgroundTask.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
				@Override
				public void handle(WorkerStateEvent event) {
					a.close();
					// Transitions the UI to the GameBoard and loads the GameBoard FXML and CSS file
					Stage getstage = (Stage) playGameButton.getScene().getWindow();
					Parent root;
					try {
						FXMLLoader loader = new FXMLLoader(getClass().getResource("GameBoard.fxml"));
						root = loader.load();
						GameBoardController controller = loader.getController();
						controller.setIn(in);
						controller.setOut(out);
						controller.setId(id);
						controller.setColor(color);
						if(color == Color.White){
							controller.setTurn(true);
						} else
							controller.setTurn(false);
						Scene scene = new Scene(root,800,600);
						scene.getStylesheets().add(getClass().getResource("GameBoard.css").toExternalForm());
						getstage.setScene(scene);
						getstage.show();
					} catch (Exception e) {
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
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * Terminates the Game
	 */
	public void handleQuit(){
		System.exit(0);
	}
}