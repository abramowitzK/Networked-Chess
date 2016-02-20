package Server;

import Game.*;
import Networking.OpCode;
import Networking.Packet;
import Networking.StartGamePacket;
import Pieces.Color;
import javafx.embed.swing.JFXPanel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO Implement leaving the queue.
 * Created by Kyle on 2/1/2016.
 */
public class Server {

    private static final Logger log = Logger.getLogger(Server.class.getName());
    private class MyPredicate<T> implements Predicate<T>{
        T var1;
        public boolean test(T var){
            if(var1.equals(var)){
                return true;
            }
            return false;
        }
    }
    private int m_currentID;
    private ServerSocket m_serverSocket;
    /**
     * Queue representing players waiting to find match
     * */
    private ConcurrentLinkedQueue<Player> m_gameQueue;
    /**
     * Represents the current Game being played (we allow only one at a time). Is null if no Game is being played
     */
    private Game m_game;
    /**
     * Constructor
     */
    public Server() throws IOException
    {
        JFXPanel panel = new JFXPanel();
        m_currentID = 0;
        m_gameQueue = new ConcurrentLinkedQueue();
        m_game = null;
        try {
            m_serverSocket = new ServerSocket(4444,0, InetAddress.getByName("127.0.0.1"));
        }
        catch (IOException ex) {
            log.log(Level.FINE, "Failed to bind Server to port", ex);
            System.exit(-1);
        }
    }
    /**
     * Process a packet from a player. Logic in here decides what kind of packet it is and what to do with it.
     * @param packet
     */
    public void ProcessPacket(Packet packet, ObjectOutputStream out, ObjectInputStream in, Socket socket){
        try {
            switch (packet.GetOpCode()) {
                case JoinQueue:
                    //Check if Client is sending a duplicate join queue packet.
                    if(packet.GetID() == -1) {
                        System.out.println("Adding new Client to the queue!");
                        m_gameQueue.add(new Player(m_currentID, in, out, socket));
                        //Send confirmation that Client is in queue
                        out.writeObject(new Packet(OpCode.JoinedQueue, m_currentID, null ));
                        //Increment current ID. We don't reuse IDs
                        m_currentID++;
                    }
                    else{
                        //Client already in queue and assigned an ID
                        out.writeObject(new Packet(OpCode.JoinedQueue, packet.GetID(), null ));
                    }
                    break;
                case QuitGame:
                    //Let player leave queue
                    MyPredicate pred = new MyPredicate();
                    pred.var1 = packet.GetID();
                    System.out.println("Removing player with id: " + packet.GetID());
                    m_gameQueue.removeIf(pred);
                default:
                    System.err.println("Unknown packet opcode. Can only join queue from main Server thread");
                    break;
            }
        }
        catch (IOException ex){
            log.log(Level.FINE, "Caught IO exception in ProcessPacket in main Server.", ex);
        }
    }
    /**
     * Start up the Server
     */
    public void Start(){
        System.out.println("Starting Server!");
        Run();
    }
    /**
     * Run the Server
     */
    private void Run(){
        while(true){
            try {
                //Accept packets. They will queue up if there is more than one at a time.
                Socket clientSocket = m_serverSocket.accept();
                //Read packet from socket. Client should send after opening socket
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                Packet receivedPacket = (Packet) in.readObject();
                //Encapsulate all packet processing
                ProcessPacket(receivedPacket, out, in, clientSocket);
                Game();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
            catch (ClassNotFoundException ex){
                ex.printStackTrace();
            }
        }
    }
    private boolean Game(){
        try {
            if (m_gameQueue.size() >= 2 && (m_game == null)) {
                System.out.println("Starting new Game");
                Player p1 = m_gameQueue.remove();
                Player p2 = m_gameQueue.remove();
                p1.GetOut().writeObject(new StartGamePacket(OpCode.JoinGame, p1.GetID(), Color.White));
                p2.GetOut().writeObject(new StartGamePacket(OpCode.JoinGame, p2.GetID(), Color.Black));
                m_game = new Game(p1, p2);
                new ServerThread(p1, m_game, p1.GetOut(), p1.GetIn(), this).start();
                new ServerThread(p2, m_game, p2.GetOut(), p2.GetIn(), this).start();
                return true;
            } else if (null != m_game && m_game.IsOver()) {
                System.out.println("Setting Game to null");
                m_game = null;
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        return false;
    }
    public int getQueueSize(){
        return m_gameQueue.size();
    }
    public void notifyServerOfQuit(int idOfPlayer){
        if(m_game != null) {
            System.out.println("Player with ID: " + idOfPlayer + " quit. Notified by thread");
            m_game.Quit(idOfPlayer);
            Player other = m_game.getOtherPlayer(idOfPlayer);
            try {
                //Let other player know the Game is over
                other.GetOut().writeObject(new Packet(OpCode.QuitGame, other.GetID(), null));
                other.GetIn().readObject();
                other.GetSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
            System.out.println("Setting Game to null");
            m_game = null;
            Game();
        }
    }
}
