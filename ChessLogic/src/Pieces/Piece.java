package Pieces;

import Game.Position;
import javafx.scene.image.Image;

public final class Piece {
    public final static Position[] KnightDirs = new Position[]{
            new Position(-1, 2),
            new Position(1, 2),
            new Position(2, 1),
            new Position(2, -1),
            new Position(1, -2),
            new Position(-1, -2),
            new Position(-2, 1),
            new Position(-2, -1),
    };
    public final Image PieceImage;
    public final Color PieceColor;
    public final PieceType Type;
    private boolean m_hasMoved;
    public Piece(PieceType type, Color color){
        Type = type;
        if(type != PieceType.Empty) {
            PieceImage = new ImageLoader(color).LoadPiece(type);
            PieceColor = color;
        } else {
            PieceImage = null;
            PieceColor = Color.Empty;
        }
    }
    public boolean HasMoved(){
        return m_hasMoved;
    }
    public void SetHasMoved(){
        m_hasMoved = true;
    }
    @Override
    public String toString(){
        return Type.name() + " " + PieceColor.name();
    }

}
