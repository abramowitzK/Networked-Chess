
package Tests;

import javafx.embed.swing.JFXPanel;
import org.junit.runner.*;
import org.junit.runner.notification.Failure;

public class TestRunner {
   public static void main(String[] args) {
      Result result = JUnitCore.runClasses(AllTests.class);
      for (Failure failure : result.getFailures()) {
         System.out.print(failure.toString() + "     ");
      }
      System.out.println(result.wasSuccessful());
   }
}
