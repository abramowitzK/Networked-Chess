package Networking;

import Game.Move;
import Game.Position;
import Pieces.Color;
import Pieces.PieceType;

public class PromotionPacket extends Packet{
    public final Color Col;
    public final Position Pos;
    public final PieceType NewPiece;
    public PromotionPacket(int id, Color color, Position pos, PieceType newPiece, Move move){
        super(OpCode.Promotion, id, move);
        Col = color;
        Pos = pos;
        NewPiece = newPiece;
    }
}
