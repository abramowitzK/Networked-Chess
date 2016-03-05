package Server;

import Game.*;
import Networking.*;
import Pieces.Color;
import javafx.embed.swing.JFXPanel;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.*;

/**
 * TODO Implement leaving the queue.
 * Created by Kyle on 2/1/2016.
 */
public class Server {

    private static final Logger log = Logger.getLogger(Server.class.getName());
    private int m_currentID;
    private ServerSocket m_serverSocket;
    private int m_currentInQueue=0;
    private HashMap<Integer, Game> m_games;
    private int m_gameID = 0;
    /**
     * Queue representing players waiting to find match
     * */
    private ConcurrentLinkedQueue<Player> m_gameQueue;
    /**
     * Constructor
     */
    public Server(int port, String ip) {
        //Javafx won't allow us to load images without calling a javafx function first to do some
        // magic initialization...
        JFXPanel panel = new JFXPanel();
        panel.getHeight();
        m_games = new HashMap<>();
        m_currentID = 0;
        m_gameQueue = new ConcurrentLinkedQueue<>();
        try {
            m_serverSocket = new ServerSocket(port,0, InetAddress.getByName(ip));
        } catch (IOException ex) {
            log.log(Level.FINE, "Failed to bind Server to port", ex);
            System.exit(-1);
        }
    }
    /**
     * Process a packet from a player. Logic in here decides what kind of packet it is and what to do with it.
     * @param packet Packet to process
     */
    public void ProcessPacket(Packet packet, ObjectOutputStream out, ObjectInputStream in, Socket socket){
        try {
            switch (packet.GetOpCode()) {
                case JoinQueue:
                    JoinQueue(packet, out, in, socket);
                    break;
                default:
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
            } catch (IOException | ClassNotFoundException ex){
                log.log(Level.FINE, "IOException", ex);
                ex.printStackTrace();
            }
        }
    }
    private boolean Game(){
        try {
            if (m_gameQueue.size() >= 2) {
                Player p1 = m_gameQueue.remove();
                Player p2 = m_gameQueue.remove();
                m_currentInQueue -=2;
                p1.JoinGame();
                p2.JoinGame();
                p1.GetOut().writeObject(new StartGamePacket(p1.GetID(), Color.White));
                p2.GetOut().writeObject(new StartGamePacket(p2.GetID(), Color.Black));
                m_games.put(m_gameID, new Game(p1,p2));
                new ServerThread(p1, m_games.get(m_gameID), p1.GetIn(), this, m_gameID).start();
                new ServerThread(p2, m_games.get(m_gameID), p2.GetIn(), this, m_gameID).start();
                m_gameID++;
                return true;
            }
        } catch (IOException | InterruptedException ex){
            log.log(Level.FINE, "IOException in Game()", ex);
        }
        return false;
    }
    public int getQueueSize(){
        return m_gameQueue.size();
    }
    public void notifyServerOfQuit(int idOfPlayer, int gameID){
        Game game = m_games.get(gameID);
        if(game != null) {
            game.Quit(idOfPlayer);
            Player other = game.getOtherPlayer(idOfPlayer);
            try {
                //Let other player know the Game is over
                other.GetOut().writeObject(new Packet(OpCode.QuitGame, other.GetID(), null));
                other.GetSocket().close();
            } catch (IOException e) {
                log.log(Level.FINE, "IOException in notify server of quit", e);
            }
            m_games.remove(gameID);
            Game();
        }
    }
    private void JoinQueue(Packet packet,ObjectOutputStream out, ObjectInputStream in, Socket socket) throws IOException{
        //Check if Client is sending a duplicate join queue packet.
        if(packet.GetID() == -1) {
            Player p = new Player(m_currentID, in,out, socket);
            m_gameQueue.add(p);
            //Send confirmation that Client is in queue
            out.writeObject(new Packet(OpCode.JoinedQueue, m_currentID, null ));
            //Increment current ID. We don't reuse IDs
            m_currentID++;
            m_currentInQueue++;
            Thread t = new Thread(()->CheckForClientLeaving(p));
            p.SetThread(t);
            t.start();
        } else{
            //Client already in queue and assigned an ID
            out.writeObject(new Packet(OpCode.JoinedQueue, packet.GetID(), null ));
        }
    }
    private void CheckForClientLeaving(Player p){

        while(!p.HasJoinedGame())
        {
            try {
                p.GetSocket().setSoTimeout(100);
                Packet pack = (Packet) p.GetIn().readObject();
                //Let player leave queue
                if (pack.GetOpCode() == OpCode.QuitGame)
                    m_gameQueue.removeIf(i -> i.GetID() == p.GetID());
            } catch (SocketTimeoutException ex) {
                log.log(Level.FINE, "Normal", ex);
            } catch (IOException | ClassNotFoundException ex) {
                //Let player leave queue
                log.log(Level.FINE, "Exception while talking to player", ex);
                m_gameQueue.removeIf(i -> i.GetID() == p.GetID());
            }
        }
        try {
            p.GetSocket().setSoTimeout(1000000000);
        } catch (SocketException e) {
            log.log(Level.FINE, "Exception while talking to player", e);
        }
    }
}
