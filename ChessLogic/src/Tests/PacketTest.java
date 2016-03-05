package Tests;

import Game.*;
import Networking.*;
import Pieces.Color;
import Pieces.Piece;
import Pieces.PieceType;
import org.eclipse.jdt.internal.compiler.codegen.Opcodes;
import org.junit.*;

import static org.junit.Assert.*;

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
        assertNotNull(OpCode.valueOf("Promotion"));
        assertNotNull(OpCode.valueOf("Castle"));
        assertNotNull(OpCode.valueOf("EnPassant"));
    }

    @Test
    public void startGamePacketTest()
    {
        StartGamePacket sgp = new StartGamePacket(1, Color.White);
        assertEquals(Color.White, sgp.GetColor());
    }

    @Test
    public void castlePacketTest()
    {
        CastlePacket castle = new CastlePacket(1, Color.White, true);
        assertTrue(castle.isLeft());
        assertEquals(OpCode.Castle, castle.GetOpCode());
        assertEquals(1, castle.GetID());
    }


    @Test
    public void promotionPacketTest()
    {
        Move m = new Move(new Position(6,1), new Position(7,1));
        Position pos = new Position(7,1);
        PromotionPacket pp = new PromotionPacket(1, Color.Black, pos, PieceType.Queen, m);
        assertEquals(1, pp.GetID());
        assertEquals(OpCode.Promotion, pp.GetOpCode());
    }

    
}
