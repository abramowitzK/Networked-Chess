package server;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import game.*;
import networking.OpCode;
import networking.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import java.util.function.*;
/**
 * TODO Implement leaving the queue.
 * Created by Kyle on 2/1/2016.
 */
public class Server {


    class MyPredicate<T> implements Predicate<T>{
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
    private Queue<Player> m_gameQueue;
    /**
     * Represents the current game being played (we allow only one at a time). Is null if no game is being played
     */
    private Game m_game;
    /**
     * Constructor
     */
    public Server() throws IOException
    {
        m_currentID = 0;
        m_gameQueue = new ConcurrentLinkedQueue<>();
        m_game = null;
        try {
            m_serverSocket = new ServerSocket(4444,0, InetAddress.getByName("127.0.0.1"));
        }
        catch (IOException ex) {
            System.err.println("Failed to bind server to port!");
            System.exit(-1);
        }
    }

    /**
     * Process a packet from a player. Logic in here decides what kind of packet it is and what to do with it.
     * @param packet
     */
    public void ProcessPacket(Packet packet, ObjectOutputStream out, ObjectInputStream in){
        try {
            switch (packet.GetOpCode()) {
                case JoinQueue:
                    //Check if client is sending a duplicate join queue packet.
                    if(packet.GetID() == -1) {
                        System.out.println("Adding new client to the queue!");
                        m_gameQueue.add(new Player(m_currentID, in, out));
                        //Send confirmation that client is in queue
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
                    System.err.println("Unknown packet opcode. Can only join queue from main server thread");
                    break;
            }
        }
        catch (IOException ex){
        }
    }

    /**
     * Start up the server
     */
    public void Start(){
        System.out.println("Starting server!");
        Run();
    }

    /**
     * Run the server
     */
    public void Run(){
        while(true){
            try {
                //Accept packets. They will queue up if there is more than one at a time.
                Socket clientSocket = m_serverSocket.accept();
                //Read packet from socket. Client should send after opening socket
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                Packet receivedPacket = (Packet) in.readObject();
                //Encapsulate all packet processing
                ProcessPacket(receivedPacket, out, in);
                Game(in, out);
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
            catch (ClassNotFoundException ex){
                ex.printStackTrace();
            }
            finally {

            }
        }
    }
    public boolean Game(ObjectInputStream in, ObjectOutputStream out) throws IOException{
        if(m_gameQueue.size() >= 2 && (m_game == null)){
            System.out.println("Starting new game");
            Player p1 = m_gameQueue.remove();
            Player p2 = m_gameQueue.remove();
            p1.GetOut().writeObject(new Packet(OpCode.JoinGame, p1.GetID(), null));
            p2.GetOut().writeObject(new Packet(OpCode.JoinGame, p2.GetID(), null));
            m_game = new Game(p1, p2);
            new ServerThread(p1, m_game, p1.GetOut(), p1.GetIn()).run();
            new ServerThread(p2, m_game, p2.GetOut(), p2.GetIn()).run();
            return true;
        }
        else if (null != m_game && m_game.IsOver()) {
            m_game = null;
        }
        return false;
    }





}
