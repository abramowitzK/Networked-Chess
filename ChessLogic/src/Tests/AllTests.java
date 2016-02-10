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
    BoardCellTest.class,
    PacketTest.class,
    ServerTest.class,
    BoardTest.class
    
})
public class AllTests {
    
}