package Server;

import Game.*;
import Networking.*;

import java.io.*;
import java.net.SocketException;
import java.util.logging.*;
class ServerThread extends Thread{
    private static final Logger log = Logger.getLogger(ServerThread.class.getName());
    private final Object lock = new Object();
    private Server m_server;
    private Player m_player;
    private ObjectOutputStream m_out;
    private ObjectInputStream m_in;
    private boolean m_quit;
    private Game m_game;
    public ServerThread(Player player, Game game, ObjectOutputStream out, ObjectInputStream in, Server server){
        m_player = player;
        m_quit = false;
        m_out = out;
        m_in = in;
        m_game = game;
        m_server = server;
    }
    /**
     * Process a packet from a player. Logic in here decides what kind of packet it is and what to do with it.
     * @param packet packet to process
     */
    private void ProcessPacket(Packet packet, ObjectOutputStream out)
    {
        switch (packet.GetOpCode()) {
            case UpdateBoard:
                //We are updating the board
                //Apply the move to the Server board
                //This needs to be synchronized since both threads work with this Game.
                System.out.println("received an update board packet from: " + packet.GetID());
                synchronized(lock) {
                    //This method should update the Game board on the Server and then send a packet to
                    //the other player updating the board.
                    ApplyMove(packet.GetMove());
                }
                break;
            case QuitGame:
                //Set flag in Game struct that lets us know Game is over and can be made null in Server so a new one can
                //be created if there are more people in the queue
                m_server.notifyServerOfQuit(packet.GetID());
                try {
                    out.writeObject(null);
                    m_quit = true;
                    m_player.GetSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.err.println("Unknown packet opcode");
                break;
        }
    }

    /**
     * Runs the thread and processes traffic back and forth between m_player and the Server.
     */
    @Override
    public void run(){
        try {
            while (!m_quit){
                ProcessPacket((Packet)m_in.readObject(), m_out);
            }
        } catch (SocketException ex){
            log.log(Level.FINE, "Client Disconnected from inside ServerThread");
            m_server.notifyServerOfQuit(m_player.GetID());
        } catch (EOFException ex){
            log.log(Level.FINE, "Caught EOF, Client has disconnected from inside ServerThread");
        } catch (IOException | ClassNotFoundException ex){
            m_server.notifyServerOfQuit(m_player.GetID());
            log.log(Level.FINE, "Caught unknown IOException");
        }
    }
    private void ApplyMove(Move move){
        try{
            //Send move to other player
            if(!m_game.IsOver()){
                Player other = m_game.getOtherPlayer(m_player.GetID());
                other.GetOut().writeObject(new Packet(OpCode.UpdateBoard, other.GetID(), move));
                m_game.ApplyMove(move);
            } else {
                System.out.println("Game is over. Not forwarding packet");
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }

    }

}
