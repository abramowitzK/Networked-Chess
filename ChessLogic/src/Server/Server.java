package Server;

import Game.Game;
import Networking.OpCode;
import Networking.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Kyle on 2/1/2016.
 */
public class Server {

    private int m_currentID;
    private ServerSocket m_serverSocket;
    /**
     * Queue representing players waiting to find match
     * */
    private Queue<Integer> m_gameQueue;
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
            m_serverSocket = new ServerSocket(4444);
        }
        catch (IOException ex) {
            System.err.println("Failed to bind server to port!");
            System.exit(-1);
        }
        finally {
            m_serverSocket.close();
        }
    }

    /**
     * Process a packet from a player. Logic in here decides what kind of packet it is and what to do with it.
     * @param packet
     */
    public void ProcessPacket(Packet packet, ObjectOutputStream out){
        try {
            switch (packet.GetOpCode()) {
                case JoinQueue:
                    //Check if client is sending a duplicate join queue packet.
                    if(!m_gameQueue.contains(packet.GetID())) {
                        m_gameQueue.add(m_currentID);
                        //Send confirmation that client is in queue
                        out.writeObject(new Packet(OpCode.JoinedQueue, m_currentID, null ));
                    }
                    else{
                        //Client already in queue and assigned an ID
                        out.writeObject(new Packet(OpCode.JoinedQueue, packet.GetID(), null ));
                    }
                    break;
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
                //Might have to do threads for each client.......

                //Accept packets. They will queue up if there is more than one at a time.
                Socket clientSocket = m_serverSocket.accept();
                //Read packet from socket. Client should send after opening socket
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                Packet receivedPacket = (Packet) in.readObject();
                //Encapsulate all packet processing
                ProcessPacket(receivedPacket, out);
                //We're done with this client. Close the socket.
                clientSocket.close();
            }
            catch (IOException ex){

            }
            catch (ClassNotFoundException ex){

            }
            finally {

            }
        }
    }




}