package Networking;

import Game.Move;

import java.io.Serializable;
public class Packet implements Serializable {
    /**
     * Describes what kind of packet this is
     */
    private OpCode m_code;
    /**
     * Player ID of the player this packet is going to/from
     */
    private int m_playerID;
    /**
     * Move object to be sent along with this only if this is a board update packet.
     */
    private Move m_move;
    /**
     *
     * @param code Code describing the operation the packet is requesting
     * @param id ID of the player sending/being sent the packet
     * @param move Possibly null. This is sent on board updates
     */
    public Packet(OpCode code, int id, Move move){
        m_code = code;
        m_playerID = id;
        m_move = move;
    }
    public OpCode GetOpCode(){
        return m_code;
    }
    public int GetID(){
        return m_playerID;
    }
    public Move GetMove(){
        return m_move;
    }
    @Override
    public String toString(){
        if(m_code == OpCode.JoinedQueue)
            return "Joined Queue";
        return "Null";
    }

}
