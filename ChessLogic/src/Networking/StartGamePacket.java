package Networking;

import Pieces.Color;

public class StartGamePacket extends Packet
{
    private Color m_color;
    /**
     * Always has OpCode of JoinGame
     * @param color Color that the player will be in game.
     * @param id   ID of the player sending/being sent the packet
     */
    public StartGamePacket(int id, Color color)
    {
        super(OpCode.JoinGame, id, null);
        m_color = color;
    }
    public Color GetColor(){
        return m_color;
    }
}
