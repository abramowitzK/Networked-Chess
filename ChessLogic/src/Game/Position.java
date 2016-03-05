package Game;

import java.io.Serializable;

public class Position implements Serializable {
    private int m_xPosition;
    private int m_yPosition;
    public Position(int x, int y){
        m_xPosition = x;
        m_yPosition = y;
    }
    public Position (Position pos){
        m_xPosition = pos.GetX();
        m_yPosition = pos.GetY();
    }
    public int GetX() {
        return m_xPosition;
    }
    public int GetY() {
        return m_yPosition;
    }
    public void SetX(int x) {
        m_xPosition = x;
    }
    public void SetY(int y) {
        m_yPosition = y;
    }

    @Override
    public String toString()
    {
        return "(" + this.GetX() + ", " + this.GetY() + ")";
    }

    @Override
    public boolean equals(Object other)
    {
        if( other instanceof Position ) {
            if (((Integer) ((Position) other).GetX()).equals(this.GetX()) && ((Integer) ((Position) other).GetY()).equals(this.GetY())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public int hashCode(){
        return 256*m_xPosition+300*m_yPosition;
    }
}
