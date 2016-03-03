
package Tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
    
    GameTest.class, 
    PlayerTest.class, 
    PositionTest.class, 
    MoveTest.class,
    PacketTest.class,
    PieceTest.class,
    BoardTest.class,
    ServerTest.class,
    ServerThreadTest.class
    
})
public class AllTests {
    
}

