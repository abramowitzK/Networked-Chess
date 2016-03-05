package Game;

import java.io.*;
import java.net.Socket;

public class Player {
    private ObjectInputStream m_in;
    private ObjectOutputStream m_out;
    private int m_id;
    private Socket m_socket;
    private boolean m_joinedGame = false;
    private Thread m_thread;
    public Player( int id, ObjectInputStream in, ObjectOutputStream out, Socket socket){
        m_id = id;
        m_in = in;
        m_out = out;
        m_socket = socket;
    }
    public void SetThread(Thread t){
        m_thread = t;
    }
    public void JoinGame() throws InterruptedException {
        m_joinedGame = true;
        m_thread.join();
    }
    public boolean HasJoinedGame(){return m_joinedGame;}
    public int GetID(){
        return m_id;
    }
    public ObjectInputStream GetIn(){ return m_in; }
    public ObjectOutputStream GetOut(){
        return m_out;
    }
    public Socket GetSocket(){ return m_socket; }


}
