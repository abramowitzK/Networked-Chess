package Networking;

import Moves.Move;

import java.io.Serializable;
/**
 * Created by Kyle on 2/1/2016.
 */
public class Packet implements Serializable {
    OpCode m_code;
    int m_playerID;
    Move m_move;

}
