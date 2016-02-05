package Game;

import javafx.geometry.Pos;

import java.io.Serializable;

/**
 * Created by Kyle on 2/1/2016.
 */
public class Move implements Serializable{
    private Position m_start;
    private Position m_end;
    public Move(Position start, Position end){
    }
    public int GetStartX(){
        return m_start.GetX();
    }
    public int GetStartY(){
        return m_start.GetY();
    }
    public int GetEndX(){
        return m_end.GetX();
    }
    public int GetEndY(){
        return m_end.GetY();
    }
}
