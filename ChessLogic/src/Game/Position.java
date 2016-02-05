package Game;

import java.io.Serializable;

/**
 * Created by Kyle on 2/1/2016.
 */
public class Position implements Serializable {
    private int m_xPosition;
    private int m_yPosition;
    public Position(int x, int y){
        m_xPosition = x;
        m_yPosition = y;
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
}
