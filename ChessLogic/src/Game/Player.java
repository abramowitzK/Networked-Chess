package game;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Kyle on 2/1/2016.
 */
public class Player {
    private ObjectInputStream m_in;
    private ObjectOutputStream m_out;
    private int m_id;
    private Socket m_socket;
    public Player( int id, ObjectInputStream in, ObjectOutputStream out, Socket socket){
        m_id = id;
        m_in = in;
        m_out = out;
        m_socket = socket;
    }
    public int GetID(){
        return m_id;
    }
    public ObjectInputStream GetIn(){ return m_in; }
    public ObjectOutputStream GetOut(){
        return m_out;
    }
    public Socket GetSocket(){ return m_socket; }


}
