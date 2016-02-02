package Server;

import Game.*;
import Networking.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Kyle on 2/1/2016.
 */
public class ServerThread extends Thread{
    private Socket m_socket;
    private Player m_player;
    private boolean m_quit;
    private Game m_game;
    public ServerThread(Player player, Game game){
        m_player = player;
        m_quit = false;
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
                synchronized(m_game) {
                    //This method should update the game board on the server and then send a packet to
                    //the other player updating the board.
                    m_game.ApplyMove(packet.GetID(), packet.GetMove());
                }
                break;
            case QuitGame:
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
        try (
            ObjectInputStream in = (ObjectInputStream)m_socket.getInputStream();
            ObjectOutputStream out = (ObjectOutputStream)m_socket.getOutputStream();
        ){
            while (!m_quit){
                ProcessPacket((Packet)in.readObject(), out);
            }
            m_socket.close();
        }
        catch (IOException ex){
        }
        catch (ClassNotFoundException ex){
        }
    }

}
