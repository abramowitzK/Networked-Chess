package Server;

import Game.*;
import Networking.*;

import java.io.*;
import java.net.SocketException;
import java.util.logging.*;
public class ServerThread extends Thread{
    private static final Logger log = Logger.getLogger(ServerThread.class.getName());
    private final Object lock = new Object();
    private Server m_server;
    private Player m_player;
    private ObjectInputStream m_in;
    private boolean m_quit;
    private Game m_game;
    private int m_gameId;
    public ServerThread(Player player, Game game,ObjectInputStream in, Server server, int gameId){
        m_player = player;
        m_quit = false;
        m_in = in;
        m_game = game;
        m_server = server;
        m_gameId = gameId;
    }
    /**
     * Process a packet from a player. Logic in here decides what kind of packet it is and what to do with it.
     * @param packet packet to process
     */
    public void ProcessPacket(Packet packet)
    {
        switch (packet.GetOpCode()) {
            case UpdateBoard:
                synchronized(lock) {
                    ApplyMove(packet.GetMove());
                }
                break;
            case QuitGame:
                Quit(packet);
                break;
            case Castle:
                CastlePacket p = (CastlePacket)packet;
                Castle(p);
                break;
            default:
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
                Packet p = (Packet)(m_in.readObject());
                ProcessPacket(p);
            }
        } catch (SocketException ex){
            log.log(Level.FINE, "Client Disconnected from inside ServerThread",ex);
            m_server.notifyServerOfQuit(m_player.GetID(), m_gameId);
        } catch (EOFException ex){
            log.log(Level.FINE, "Caught EOF, Client has disconnected from inside ServerThread",ex);
        } catch (IOException | ClassNotFoundException ex){
            m_server.notifyServerOfQuit(m_player.GetID(), m_gameId);
            log.log(Level.FINE, "Caught unknown IOException",ex);
        }
    }
    private void ApplyMove(Move move){
        try{
            //Send move to other player
            if(!m_game.IsOver()){
                Player other = m_game.getOtherPlayer(m_player.GetID());
                other.GetOut().writeObject(new Packet(OpCode.UpdateBoard, other.GetID(), move));
                m_game.ApplyMove(move);
            }
        } catch (IOException ex){
            log.log(Level.FINE, "Failed to apply move", ex);
        }

    }
    private void Quit(Packet packet){
        m_server.notifyServerOfQuit(packet.GetID(), m_gameId);
        try {
            m_quit = true;
            m_player.GetSocket().close();
        } catch (IOException e) {
            log.log(Level.FINE, "Failed to close socket. Already closed", e);
        }
    }
    private void Castle(CastlePacket p){
        synchronized (lock){
            m_game.Castle(p.Col,p.Left);
            if(!m_game.IsOver()){
                Player other = m_game.getOtherPlayer(m_player.GetID());
                try {
                    other.GetOut().writeObject(new CastlePacket(other.GetID(), p.Col, p.Left));
                } catch (IOException e) {
                    log.log(Level.FINE, "IOexception on Castle Recieved in serverThread", e);
                }
            }
        }
    }

}
