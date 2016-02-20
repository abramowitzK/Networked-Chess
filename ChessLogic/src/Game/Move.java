package Game;

import java.io.Serializable;
public class Move implements Serializable{
    private Position m_start;
    private Position m_end;
    public Move(Position start, Position end){
        m_start = start;
        m_end = end;
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
