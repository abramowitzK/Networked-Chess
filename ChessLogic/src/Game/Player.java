package game;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Kyle on 2/1/2016.
 */
public class Player {
    private ObjectInputStream m_in;
    private ObjectOutputStream m_out;
    private int m_id;
    private boolean m_isBlack;
    public Player( int id, ObjectInputStream in, ObjectOutputStream out){
        m_id = id;
        m_in = in;
        m_out = out;
    }
    public int GetID(){
        return m_id;
    }
    public ObjectInputStream GetIn(){ return m_in; }
    public ObjectOutputStream GetOut(){
        return m_out;
    }


}
