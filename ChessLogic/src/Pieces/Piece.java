package Pieces;

import Game.Board;
import Game.BoardCell;
import Game.Position;
import javafx.scene.image.Image;

import java.util.ArrayList;

public abstract class Piece {
    private Image m_image;
    private String m_name;
    private BoardCell m_cell;
    private boolean m_isBlack;
    public Piece(String name, BoardCell cell, boolean isBlack){
        m_name = name;
        m_cell = cell;
        m_isBlack = isBlack;
        m_image = null;
    }
    public Piece(String name){
        m_cell = null;
        m_name = name;
    }
    public abstract ArrayList<Position> GenerateMoves(Board board);
    public String GetName(){ return m_name; }
    public BoardCell GetCell(){ return m_cell; }
    public void SetBoardCell(BoardCell cell){ m_cell = cell; }
    public Position GetPosition(){ return m_cell.GetPosition(); }
    public boolean IsBlack(){ return m_isBlack; }
    protected ArrayList<Position> RemoveOutOfRangeMoves(ArrayList<Position> moves){
        ArrayList<Position> ret = new ArrayList<>();
        for(Position pos : moves){
            if(pos.GetX() > 0 && pos.GetX() < 8){
                if(pos.GetY() > 0 && pos.GetY() < 8){
                    ret.add(new Position(pos.GetX(), pos.GetY()));
                }
            }
        }
        return ret;
    }
    public void SetImage(Image image){
        m_image = image;
    }
}
