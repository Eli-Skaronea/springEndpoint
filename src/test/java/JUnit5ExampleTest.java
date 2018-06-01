
import org.junit.jupiter.api.*;

//@RunWith(Greeting.class)
//@ContextConfiguration(classes = {AppConfig.class})
public class JUnit5ExampleTest{

    @Test
    public void justAnExample(){
        System.out.println("This test method ran!");
    }
    
    @Test
    public void failingTest(){
        Assertions.assertEquals(3, 4);
    }

}