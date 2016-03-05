package Networking;

import Pieces.Color;

public class CastlePacket extends Packet {
    public final Color Col;
    public final boolean Left;
    public CastlePacket(int id, Color color, boolean left) {
        super(OpCode.Castle, id, null);
        Col = color;
        Left = left;
    }
    public boolean isLeft() { return Left; }
}
