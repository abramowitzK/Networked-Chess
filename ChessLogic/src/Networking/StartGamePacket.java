package Networking;

import Pieces.Color;

/**
 * Created by Kyle on 2/19/2016.
 */
public class StartGamePacket extends Packet
{
    /**
     * @param code Code describing the operation the packet is requesting
     * @param id   ID of the player sending/being sent the packet
     * @param move Possibly null. This is sent on board updates
     */
    private Color m_color;
    public StartGamePacket(OpCode code, int id, Color color)
    {
        super(code, id, null);
        m_color = color;
    }
    public Color GetColor(){
        return m_color;
    }
}
