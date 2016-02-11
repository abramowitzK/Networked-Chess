package server;

import game.*;
import networking.OpCode;
import networking.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Kyle on 2/1/2016.
 */
public class ServerThread extends Thread{
    private Player m_player;
    private ObjectOutputStream m_out;
    private ObjectInputStream m_in;
    private boolean m_quit;
    private Game m_game;
    public ServerThread(Player player, Game game, ObjectOutputStream out, ObjectInputStream in){
        m_player = player;
        m_quit = false;
        m_out = out;
        m_in = in;
        m_game = game;
    }
    /**
     * Process a packet from a player. Logic in here decides what kind of packet it is and what to do with it.
     * @param packet packet to process
     */
    public void ProcessPacket(Packet packet, ObjectOutputStream out){
        switch (packet.GetOpCode()) {
            case UpdateBoard:
                //We are updating the board
                //Validate move here?
                //
                //Apply the move to the server board
                //This needs to be synchronized since both threads work with this game.
                System.out.println("recieved an update board packet from: " + packet.GetID());
                synchronized(m_game) {
                    //This method should update the game board on the server and then send a packet to
                    //the other player updating the board.
                    m_game.ApplyMove(packet.GetID(), packet.GetMove());
                }
                break;
            case QuitGame:
                //Set flag in game struct that lets us know game is over and can be made null in server so a new one can
                //be created if there are more people in the queue
                System.out.println("Quitting game");
                m_game.Quit(packet.GetID());
                break;
            default:
                System.err.println("Unknown packet opcode");
                break;
        }
    }

    /**
     * Runs the thread and processes traffic back and forth between m_player and the server.
     */
    @Override
    public void run(){
        try {
            while (!m_quit){
                ProcessPacket((Packet)m_in.readObject(), m_out);
            }
        }
        catch (EOFException ex){
            //Client disconnected!
            //Handle disconnection
            //Return to kill thread
            System.out.println("Client disconnected");
            System.out.println("Caught EOF");
            return;

        }
        catch (IOException ex){
            //Should be better about this here
            ex.printStackTrace();

        }
        catch (ClassNotFoundException ex){
            //Also need this? But need to handle better
            ex.printStackTrace();
        }

    }

}
