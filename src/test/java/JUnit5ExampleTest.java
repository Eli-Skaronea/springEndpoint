
import org.junit.jupiter.api.*;

//@RunWith(Greeting.class)
//@ContextConfiguration(classes = {AppConfig.class})
public class JUnit5ExampleTest{

    @Test
    public void justAnExample(){
        System.out.println("This test method ran!");
    }
    
    //Change this quick test to see what happens when Gradle clean test fails
    @Test
    public void failingTest(){
        Assertions.assertEquals(4, 4);
    }

}