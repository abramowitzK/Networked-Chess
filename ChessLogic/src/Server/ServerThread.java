package Server;

import Game.Game;
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
    private int m_playerID;
    private boolean m_quit;
    private Game m_game;
    public ServerThread(Socket socket, int playerID, Game game){
        m_socket = socket;
        m_playerID = playerID;
        m_quit = false;
    }
    /**
     * Process a packet from a player. Logic in here decides what kind of packet it is and what to do with it.
     * @param packet
     */
    public void ProcessPacket(Packet packet){
        switch (packet.GetOpCode()) {
            case UpdateBoard:
                //We are updating the board
                m_game.ApplyMove(packet.GetID(), packet.GetMove());
                break;
            case QuitGame:
                break;
            default:
                System.err.println("Unknown packet opcode");
                break;
        }
    }
    @Override
    public void run(){
        try (
            ObjectInputStream in = (ObjectInputStream)m_socket.getInputStream();
            ObjectOutputStream out = (ObjectOutputStream)m_socket.getOutputStream();
        ){
            while (!m_quit){

            }
            m_socket.close();
        }
        catch (IOException ex){

        }
    }

}
