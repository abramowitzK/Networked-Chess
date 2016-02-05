package Game;

import Pieces.Piece;
import com.sun.istack.internal.Nullable;

/**
 * Created by Kyle_Windows10 on 2/4/2016.
 */
public class BoardCell {
    /**
     * Reference to a piece object. Can be null
     */
    private Piece m_piece;
    private Position m_pos;
    public BoardCell(int x, int y, @Nullable Piece piece){
        m_pos = new Position(x, y);
        m_piece = piece;
    }
}
