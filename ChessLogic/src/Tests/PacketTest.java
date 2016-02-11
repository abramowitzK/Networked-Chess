package Tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import game.Move;
import game.Position;
import networking.OpCode;
import networking.Packet;

public class PacketTest {

    Packet p;
    OpCode o;
    Position start, end;
    Move m;
    @Before
    public void setUp() throws Exception {
        o = OpCode.JoinedQueue;
        start = new Position(2,1) ;
        end = new Position(2,6);
        m = new Move(start, end);
        
        p = new Packet(o, 1, m);
    }

    @Test
    public void getOpCodeTest() {
        assertEquals(p.GetOpCode(), OpCode.JoinedQueue);
    }

    @Test
    public void getIDTest() {
        assertEquals(p.GetID(), 1);
    }
    
    @Test
    public void getMoveTest() {
        assertEquals(p.GetMove(), m);
    }
    
    @Test
    public void toStringTest() {
        assertEquals(p.toString(), "Joined Queue");
        p = new Packet(OpCode.QuitGame, 1, null);
        assertEquals(p.toString(), "Null");
    }
    
    // OpCode Test
    @Test
    public void opCodeTest(){
        assertNotNull(OpCode.valueOf("UpdateBoard"));
        assertNotNull(OpCode.valueOf("UpdatedBoard"));
        assertNotNull(OpCode.valueOf("JoinQueue"));
        assertNotNull(OpCode.valueOf("JoinedQueue"));
        assertNotNull(OpCode.valueOf("JoinGame"));
        assertNotNull(OpCode.valueOf("JoinedGame"));
        assertNotNull(OpCode.valueOf("QuitGame"));
      
        
    }
    
    
}
