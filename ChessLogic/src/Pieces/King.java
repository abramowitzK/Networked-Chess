package Pieces;

import Game.Board;
import Game.BoardCell;
import Game.Position;

import java.util.ArrayList;

/**
 * Created by Kyle_Windows10 on 2/16/2016.
 */
public class King extends Piece {
    public King(String name, BoardCell cell, boolean isBlack){
        super(name, cell,isBlack);
    }
    @Override
    public ArrayList<Position> GenerateMoves(Board board) {
        return null;
    }
}
